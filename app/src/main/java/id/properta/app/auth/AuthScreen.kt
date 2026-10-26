package id.properta.app.auth

import id.properta.app.ui.components.PropertaLogo

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay

private val AuthNavy = Color(0xFF061630)
private val AuthBlue = Color(0xFF155EEF)
private val AuthCyan = Color(0xFF22D3EE)

@Composable
fun AuthScreen(onAuthenticated: () -> Unit) {
    val context = LocalContext.current
    val auth = remember(context) { LocalAuth(context) }

    var introFinished by remember { mutableStateOf(false) }
    var isSetup by remember { mutableStateOf(!auth.isSetup()) }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(2200)
        introFinished = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(AuthNavy, Color(0xFF082A58), AuthBlue)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Cahaya abstrak biru dan cyan.
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x332684FF), Color(0x1122D3EE), Color.Transparent),
                    center = Offset(w * 0.82f, h * 0.24f),
                    radius = w * 0.72f
                ),
                radius = w * 0.72f,
                center = Offset(w * 0.82f, h * 0.24f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x2222D3EE), Color.Transparent),
                    center = Offset(w * 0.12f, h * 0.72f),
                    radius = w * 0.58f
                ),
                radius = w * 0.58f,
                center = Offset(w * 0.12f, h * 0.72f)
            )

            // Garis lengkung tipis seperti aksen neon pada desain web.
            val line1 = Path().apply {
                moveTo(-w * 0.12f, h * 0.30f)
                cubicTo(w * 0.30f, h * 0.12f, w * 0.68f, h * 0.43f, w * 1.12f, h * 0.20f)
            }
            val line2 = Path().apply {
                moveTo(-w * 0.10f, h * 0.78f)
                cubicTo(w * 0.30f, h * 0.60f, w * 0.65f, h * 0.93f, w * 1.10f, h * 0.70f)
            }
            val line3 = Path().apply {
                moveTo(w * 0.78f, -h * 0.05f)
                cubicTo(w * 0.55f, h * 0.18f, w * 0.92f, h * 0.32f, w * 0.72f, h * 0.48f)
            }

            listOf(line1, line2, line3).forEachIndexed { index, path ->
                drawPath(
                    path = path,
                    color = if (index == 1) Color(0x3322D3EE) else Color(0x302684FF),
                    style = Stroke(
                        width = if (index == 2) 1.0.dp.toPx() else 1.4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                )
            }

            // Titik cahaya kecil sebagai detail futuristik.
            listOf(
                Offset(w * 0.16f, h * 0.25f),
                Offset(w * 0.84f, h * 0.37f),
                Offset(w * 0.72f, h * 0.79f),
                Offset(w * 0.24f, h * 0.84f)
            ).forEach { point ->
                drawCircle(
                    color = Color(0x5522D3EE),
                    radius = 2.2.dp.toPx(),
                    center = point
                )
            }
        }

        if (!introFinished) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                PropertaLogo(Modifier.size(94.dp))
                Text(
                    "PROPERTA",
                    color = Color.White,
                    fontSize = 29.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 3.sp
                )
                Text(
                    "Kelola Properti, Raih Lebih Banyak.",
                    color = AuthCyan,
                    textAlign = TextAlign.Center,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(12.dp))
                CircularProgressIndicator(
                    color = AuthCyan,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        if (isSetup) "Buat akun administrator"
                        else "Selamat datang kembali",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isSetup)
                            "Buat akun untuk mengamankan data PROPERTA di perangkat ini."
                        else
                            "Masuk untuk melanjutkan pengelolaan properti Anda.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text("Kata sandi")
                        },
                        singleLine = true,
                        visualTransformation = if (showPassword)
                            VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password
                        ),
                        trailingIcon = {
                            TextButton(onClick = { showPassword = !showPassword }) {
                                Text(if (showPassword) "Sembunyikan" else "Lihat")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (isSetup) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text("Ulangi kata sandi") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (message.isNotBlank()) {
                        Text(
                            message,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = {
                            if (busy) return@Button
                            message = ""

                            if (username.isBlank() || password.isBlank()) {
                                message = "Username dan kata sandi wajib diisi."
                                return@Button
                            }

                            if (isSetup) {
                                if (password != confirmPassword) {
                                    message = "Konfirmasi kata sandi belum cocok."
                                    return@Button
                                }

                                busy = true
                                val created = auth.createAccount(
                                    username,
                                    password.toCharArray()
                                )
                                busy = false

                                if (created) {
                                    isSetup = false
                                    password = ""
                                    confirmPassword = ""
                                    message = "Akun dibuat. Silakan login."
                                } else {
                                    message = "Akun gagal dibuat. Coba lagi."
                                }
                            } else {
                                busy = true
                                val valid = auth.verify(
                                    username,
                                    password.toCharArray()
                                )
                                busy = false

                                if (valid) {
                                    onAuthenticated()
                                } else {
                                    message = "Username atau kata sandi salah."
                                    password = ""
                                }
                            }
                        },
                        enabled = !busy,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AuthBlue
                        )
                    ) {
                        Text(
                            if (busy) "Memproses..."
                            else if (isSetup) "Buat akun"
                            else "Masuk",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        "Data akun tersimpan secara lokal di perangkat ini.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
