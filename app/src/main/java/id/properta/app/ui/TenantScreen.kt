package id.properta.app.ui

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun TenantScreen(
    viewModel: TenantViewModel,
    modifier: Modifier = Modifier
) {
    val tenants by viewModel.tenants.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var showForm by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var depositText by remember { mutableStateOf("0") }
    var identityType by remember { mutableStateOf("KTP") }
    var identityNumber by remember { mutableStateOf("") }
    var identityAddress by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var photoMessage by remember { mutableStateOf("Belum ada foto dipilih") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var savingPhoto by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                savingPhoto = true
                errorMessage = null

                val savedPath = withContext(Dispatchers.IO) {
                    saveIdentityPhoto(context, uri)
                }

                savingPhoto = false
                if (savedPath != null) {
                    photoPath = savedPath
                    photoMessage = "Foto identitas tersimpan di perangkat"
                } else {
                    errorMessage = "Foto gagal disimpan. Silakan coba pilih foto lain."
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Penyewa")
                Text("${tenants.size} penyewa terdaftar")
            }

            Button(onClick = {
                fullName = ""
                phone = ""
                depositText = "0"
                identityType = "KTP"
                identityNumber = ""
                identityAddress = ""
                notes = ""
                photoPath = null
                photoMessage = "Belum ada foto dipilih"
                errorMessage = null
                showForm = true
            }) {
                Text("Tambah")
            }
        }

        if (tenants.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("Belum ada penyewa")
                    Text("Tambahkan penyewa untuk mulai mengelola data sewa dan identitas.")
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tenants, key = { it.id }) { tenant ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            Text(tenant.fullName)

                            if (tenant.phone.isNotBlank()) {
                                Text("HP/WhatsApp: ${tenant.phone}")
                            }
                            if (tenant.identityType.isNotBlank()) {
                                Text("Jenis identitas: ${tenant.identityType}")
                            }
                            if (tenant.identityNumber.isNotBlank()) {
                                Text("Nomor identitas: ${tenant.identityNumber}")
                            }
                            if (tenant.identityAddress.isNotBlank()) {
                                Text("Alamat: ${tenant.identityAddress}")
                            }
                            if (tenant.identityPhotoPath != null) {
                                Text("Foto identitas tersimpan di perangkat")
                            }

                            TextButton(
                                onClick = { viewModel.deleteTenant(tenant) }
                            ) {
                                Text("Hapus penyewa")
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        AlertDialog(
            onDismissRequest = { showForm = false },
            title = { Text("Tambah penyewa") },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 440.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = { Text("Nama lengkap *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Nomor HP/WhatsApp") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = depositText,
                        onValueChange = { depositText = it.filter(Char::isDigit) },
                        label = { Text("Deposit (Rp)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = identityType,
                        onValueChange = { identityType = it },
                        label = { Text("Jenis identitas (KTP/SIM/Paspor)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = identityNumber,
                        onValueChange = { identityNumber = it },
                        label = { Text("Nomor identitas") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = identityAddress,
                        onValueChange = { identityAddress = it },
                        label = { Text("Alamat sesuai identitas") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { photoPicker.launch("image/*") },
                        enabled = !savingPhoto
                    ) {
                        Text(if (savingPhoto) "Menyimpan foto..." else "Pilih foto identitas")
                    }

                    Text(photoMessage)

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (errorMessage != null) {
                        Text(errorMessage!!)
                    }

                    Spacer(Modifier.height(4.dp))
                }
            },
            confirmButton = {
                Button(
                    enabled = fullName.isNotBlank() &&
                        (depositText.toLongOrNull() ?: -1L) >= 0L &&
                        !savingPhoto,
                    onClick = {
                        viewModel.addTenant(
                            fullName = fullName.trim(),
                            phone = phone.trim(),
                            deposit = depositText.toLongOrNull() ?: 0L,
                            notes = notes.trim(),
                            identityType = identityType.trim(),
                            identityNumber = identityNumber.trim(),
                            identityAddress = identityAddress.trim(),
                            identityPhotoPath = photoPath
                        )
                        showForm = false
                    }
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showForm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

private fun saveIdentityPhoto(context: Context, uri: Uri): String? {
    var destination: File? = null

    return try {
        val directory = File(context.filesDir, "tenant_identity")
        if (!directory.exists() && !directory.mkdirs()) return null

        val mimeType = context.contentResolver.getType(uri)
        val extension = when (mimeType?.lowercase()) {
            "image/png" -> "png"
            "image/webp" -> "webp"
            "image/gif" -> "gif"
            else -> "jpg"
        }

        destination = File(
            directory,
            "identity_${System.currentTimeMillis()}.$extension"
        )

        val input = context.contentResolver.openInputStream(uri)
            ?: return null

        input.use { source ->
            destination.outputStream().use { output ->
                source.copyTo(output)
            }
        }

        destination.absolutePath
    } catch (_: Exception) {
        destination?.delete()
        null
    }
}
