package id.properta.app.auth

import android.content.Context
import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

class LocalAuth(context: Context) {
    private val prefs = context.getSharedPreferences(
        "properta_local_auth",
        Context.MODE_PRIVATE
    )

    init {
        if (!isSetup()) {
            createAccount("admin", charArrayOf('1', '2', '3', '4'))
        }
    }

    fun isSetup(): Boolean = prefs.contains("password_hash")

    fun getRememberedUsername(): String =
        prefs.getString("remembered_username", "") ?: ""

    fun saveRememberedUsername(username: String, remember: Boolean) {
        prefs.edit().apply {
            if (remember) {
                putString("remembered_username", username.trim())
            } else {
                remove("remembered_username")
            }
        }.apply()
    }

    fun createAccount(username: String, password: CharArray): Boolean {
        if (isSetup() || username.isBlank() || password.isEmpty()) {
            password.fill('\u0000')
            return false
        }

        val salt = ByteArray(16)
        SecureRandom().nextBytes(salt)

        val hash = try {
            derive(password, salt)
        } finally {
            password.fill('\u0000')
        }

        return prefs.edit()
            .putString("username", username.trim())
            .putString("salt", Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString("password_hash", Base64.encodeToString(hash, Base64.NO_WRAP))
            .commit()
    }

    fun verify(username: String, password: CharArray): Boolean {
        val savedUsername = prefs.getString("username", null)
        val saltText = prefs.getString("salt", null)
        val hashText = prefs.getString("password_hash", null)

        if (savedUsername == null || saltText == null || hashText == null) {
            password.fill('\u0000')
            return false
        }

        return try {
            val salt = Base64.decode(saltText, Base64.NO_WRAP)
            val expected = Base64.decode(hashText, Base64.NO_WRAP)
            val actual = derive(password, salt)

            username.trim() == savedUsername &&
                MessageDigest.isEqual(expected, actual)
        } finally {
            password.fill('\u0000')
        }
    }

    private fun derive(password: CharArray, salt: ByteArray): ByteArray {
        val spec = PBEKeySpec(password, salt, 120_000, 256)
        return try {
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                .generateSecret(spec)
                .encoded
        } finally {
            spec.clearPassword()
        }
    }
}
