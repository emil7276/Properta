package id.properta.app.auth

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

/**
 * Local authentication for PROPERTA.
 *
 * Keeps the existing PBKDF2 password_hash/salt preference keys compatible.
 * The remembered session is protected with an AES-GCM key held by Android Keystore.
 * Passwords are never stored in plaintext.
 */
class LocalAuth(context: Context) {
    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(
        "properta_local_auth",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_USERNAME = "username"
        private const val KEY_SALT = "salt"
        private const val KEY_PASSWORD_HASH = "password_hash"
        private const val KEY_REMEMBERED_USERNAME = "remembered_username"
        private const val KEY_SESSION_IV = "session_iv"
        private const val KEY_SESSION_DATA = "session_data"
        private const val KEYSTORE_ALIAS = "properta_remember_me_v1"
        private const val PBKDF2_ITERATIONS = 120_000
        private const val HASH_BITS = 256
        private const val AES_TRANSFORMATION = "AES/GCM/NoPadding"
    }

    init {
        // First launch account as explicitly specified for this project.
        if (!isSetup()) {
            createAccount("admin", charArrayOf('1', '2', '3', '4'))
        }
    }

    fun isSetup(): Boolean = prefs.contains(KEY_PASSWORD_HASH)

    fun getRememberedUsername(): String =
        prefs.getString(KEY_REMEMBERED_USERNAME, "") ?: ""

    fun saveRememberedUsername(username: String, remember: Boolean) {
        prefs.edit().apply {
            if (remember) putString(KEY_REMEMBERED_USERNAME, username.trim())
            else remove(KEY_REMEMBERED_USERNAME)
        }.apply()
    }

    /**
     * Creates the initial local account. The caller's char array is cleared.
     */
    @Synchronized
    fun createAccount(username: String, password: CharArray): Boolean {
        if (isSetup() || username.isBlank() || password.isEmpty()) {
            password.fill('\u0000')
            return false
        }

        return try {
            val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
            val hash = derive(password, salt)
            prefs.edit()
                .putString(KEY_USERNAME, username.trim())
                .putString(KEY_SALT, encode(salt))
                .putString(KEY_PASSWORD_HASH, encode(hash))
                .remove(KEY_SESSION_IV)
                .remove(KEY_SESSION_DATA)
                .commit()
        } catch (_: Exception) {
            false
        } finally {
            password.fill('\u0000')
        }
    }

    /**
     * Verifies credentials. The password array is cleared before returning.
     */
    @Synchronized
    fun verify(username: String, password: CharArray): Boolean {
        return try {
            val savedUsername = prefs.getString(KEY_USERNAME, null)
            val saltText = prefs.getString(KEY_SALT, null)
            val hashText = prefs.getString(KEY_PASSWORD_HASH, null)
            if (savedUsername == null || saltText == null || hashText == null) {
                false
            } else {
                val salt = decode(saltText)
                val expected = decode(hashText)
                val actual = derive(password, salt)
                username.trim() == savedUsername &&
                    MessageDigest.isEqual(expected, actual)
            }
        } catch (_: Exception) {
            false
        } finally {
            password.fill('\u0000')
        }
    }

    /**
     * Call only after verify() succeeds. If remember is false, no persistent
     * session is created; the app must require login after process restart.
     */
    @Synchronized
    fun setRememberMe(username: String, remember: Boolean) {
        saveRememberedUsername(username, remember)
        clearSession()
        if (!remember) return

        try {
            val token = ByteArray(32).also { SecureRandom().nextBytes(it) }
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKeystoreKey())
            val encrypted = cipher.doFinal(token)
            val saved = prefs.edit()
                .putString(KEY_SESSION_IV, encode(cipher.iv))
                .putString(KEY_SESSION_DATA, encode(encrypted))
                .commit()
            token.fill(0)
            if (!saved) clearSession()
        } catch (_: Exception) {
            clearSession()
        }
    }

    /**
     * True only when a previously encrypted session token can be authenticated
     * by the Android Keystore key on this device.
     */
    @Synchronized
    fun hasRememberedSession(): Boolean {
        val ivText = prefs.getString(KEY_SESSION_IV, null) ?: return false
        val dataText = prefs.getString(KEY_SESSION_DATA, null) ?: return false
        return try {
            val cipher = Cipher.getInstance(AES_TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                getOrCreateKeystoreKey(),
                GCMParameterSpec(128, decode(ivText))
            )
            val token = cipher.doFinal(decode(dataText))
            val valid = token.size == 32
            token.fill(0)
            valid
        } catch (_: Exception) {
            clearSession()
            false
        }
    }

    @Synchronized
    fun clearSession() {
        prefs.edit()
            .remove(KEY_SESSION_IV)
            .remove(KEY_SESSION_DATA)
            .apply()
    }

    /**
     * Password change requires the current password. Existing remembered
     * sessions are invalidated after a successful change.
     */
    @Synchronized
    fun changePassword(currentPassword: CharArray, newPassword: CharArray): Boolean {
        val username = prefs.getString(KEY_USERNAME, null) ?: run {
            currentPassword.fill('\u0000')
            newPassword.fill('\u0000')
            return false
        }

        val verified = verify(username, currentPassword)
        if (!verified || newPassword.size < 4) {
            newPassword.fill('\u0000')
            return false
        }

        return try {
            val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
            val hash = derive(newPassword, salt)
            val saved = prefs.edit()
                .putString(KEY_SALT, encode(salt))
                .putString(KEY_PASSWORD_HASH, encode(hash))
                .remove(KEY_SESSION_IV)
                .remove(KEY_SESSION_DATA)
                .commit()
            saved
        } catch (_: Exception) {
            false
        } finally {
            newPassword.fill('\u0000')
            clearSession()
        }
    }

    private fun derive(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, PBKDF2_ITERATIONS, HASH_BITS)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }

    private fun getOrCreateKeystoreKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(KEYSTORE_ALIAS, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        generator.init(
            KeyGenParameterSpec.Builder(
                KEYSTORE_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return generator.generateKey()
    }

    private fun encode(bytes: ByteArray): String =
        android.util.Base64.encodeToString(bytes, android.util.Base64.NO_WRAP)

    private fun decode(value: String): ByteArray =
        android.util.Base64.decode(value, android.util.Base64.NO_WRAP)
}
