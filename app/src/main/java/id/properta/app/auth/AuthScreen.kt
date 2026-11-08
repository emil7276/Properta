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
fun AuthScreen(language: String = "id", onAuthenticated: () -> Unit) {
    val context = LocalContext.current
    val auth = remember(context) { LocalAuth(context) }
    val isEnglish = language == "en"

    var introFinished by remember { mutableStateOf(false) }
    var isSetup by remember { mutableStateOf(!auth.isSetup()) }
    var username by remember { mutableStateOf(auth.getRememberedUsername()) }
    var rememberMe by remember { mutableStateOf(auth.getRememberedUsername().isNotBlank()) }
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
                Brush.linearGradient(
                    listOf(
                        Color(0xFF020817),
                        Color(0xFF06172F),
                        Color(0xFF0B2C55),
                        Color(0xFF06152E)
                    )
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
                    listOf(Color(0x662684FF), Color(0x3322D3EE), Color.Transparent),
                    center = Offset(w * 0.82f, h * 0.24f),
                    radius = w * 0.72f
                ),
                radius = w * 0.72f,
                center = Offset(w * 0.82f, h * 0.24f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x4422D3EE), Color.Transparent),
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

            // Premium lens bloom: bright core, wide soft halo and restrained anamorphic streak.
            val flare = Offset(w * 0.82f, h * 0.22f)
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(
                        Color(0x66E8FCFF),
                        Color(0x4422D3EE),
                        Color(0x1C2684FF),
                        Color.Transparent
                    ),
                    center = flare,
                    radius = w * 0.24f
                ),
                radius = w * 0.24f,
                center = flare
            )
            drawCircle(Color(0xBBDDFBFF), 2.5.dp.toPx(), flare)
            drawCircle(Color(0x3322D3EE), 9.dp.toPx(), flare)

            drawLine(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0x1822D3EE),
                        Color(0x55E8FCFF),
                        Color(0x222684FF),
                        Color.Transparent
                    ),
                    startX = w * 0.42f,
                    endX = w * 1.04f
                ),
                start = Offset(w * 0.42f, h * 0.22f),
                end = Offset(w * 1.04f, h * 0.22f),
                strokeWidth = 1.2.dp.toPx()
            )

            // Defocused bokeh points and tiny light cores.
            listOf(
                Triple(Offset(w * 0.16f, h * 0.25f), 3.0f, 0x4422D3EE),
                Triple(Offset(w * 0.84f, h * 0.37f), 2.2f, 0x5522D3EE),
                Triple(Offset(w * 0.72f, h * 0.79f), 2.6f, 0x332684FF),
                Triple(Offset(w * 0.24f, h * 0.84f), 1.8f, 0x3322D3EE)
            ).forEach { (point, radius, tint) ->
                drawCircle(Color(tint), radius.dp.toPx(), point)
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
                    if (isEnglish) "Manage Properties, Achieve More." else "Kelola Properti, Raih Lebih Banyak.",
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

            // PROPERTA_PREMIUM_HEADER_V2
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(top = 24.dp, start = 28.dp, end = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(94.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0x5535BFFF),
                                    Color(0x222684FF),
                                    Color.Transparent
                                )
                            ),
                            RoundedCornerShape(50)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    PropertaLogo(Modifier.size(86.dp))
                }

                Text(
                    text = "PROPERTA",
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp
                )

                Text(
                    text = if (isEnglish)
                        "Manage Properties, Achieve More."
                    else
                        "Kelola Properti, Raih Lebih Banyak.",
                    color = Color(0xFF75DFFF),
                    fontSize = 12.sp,
                    letterSpacing = 0.5.sp,
                    textAlign = TextAlign.Center
                )

                PropertaLoginBuildings(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(132.dp)
                )
            }

            Spacer(Modifier.height(8.dp))
Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 18.dp),
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
                        if (isSetup) if (isEnglish) "Create administrator account" else "Buat akun administrator"
                        else if (isEnglish) "Welcome back" else "Selamat datang kembali",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isSetup)
                            if (isEnglish) "Create an account to secure PROPERTA data on this device." else "Buat akun untuk mengamankan data PROPERTA di perangkat ini."
                        else
                            if (isEnglish) "Sign in to continue managing your properties." else "Masuk untuk melanjutkan pengelolaan properti Anda.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text(if (isEnglish) "Username" else "Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = {
                            Text(if (isEnglish) "Password" else "Kata sandi")
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
                                Text(if (showPassword) if (isEnglish) "Hide" else "Sembunyikan" else if (isEnglish) "Show" else "Lihat")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (!isSetup) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = rememberMe,
                                onCheckedChange = { rememberMe = it }
                            )
                            Text(
                                if (isEnglish) "Remember me" else "Ingat saya",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    if (isSetup) {
                        OutlinedTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = { Text(if (isEnglish) "Confirm password" else "Ulangi kata sandi") },
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
                                message = if (isEnglish) "Username and password are required." else "Username dan kata sandi wajib diisi."
                                return@Button
                            }

                            if (isSetup) {
                                if (password != confirmPassword) {
                                    message = if (isEnglish) "Passwords do not match." else "Konfirmasi kata sandi belum cocok."
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
                                    message = if (isEnglish) "Account created. Please sign in." else "Akun dibuat. Silakan login."
                                } else {
                                    message = if (isEnglish) "Could not create account. Please try again." else "Akun gagal dibuat. Coba lagi."
                                }
                            } else {
                                busy = true
                                val valid = auth.verify(
                                    username,
                                    password.toCharArray()
                                )
                                busy = false

                                if (valid) {
                                    auth.setRememberMe(username.trim(), rememberMe)
                                    password = ""
                                    onAuthenticated()
                                } else {
                                    message = if (isEnglish) "Incorrect username or password." else "Username atau kata sandi salah."
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
                            if (busy) if (isEnglish) "Processing..." else "Memproses..."
                            else if (isSetup) if (isEnglish) "Create account" else "Buat akun"
                            else if (isEnglish) "Sign in" else "Masuk",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        if (isEnglish) "Account data is stored locally on this device." else "Data akun tersimpan secara lokal di perangkat ini.",
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


/*
 * Ilustrasi arsitektur PROPERTA.
 * Seluruh bentuk digambar dengan Compose, bukan gambar screenshot.
 */
@Composable
private fun PropertaLoginBuildings(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val base = h * 0.91f

        // Cahaya lembut di belakang skyline.
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0x5533BFFF),
                    Color(0x222684FF),
                    Color.Transparent
                ),
                center = Offset(w * 0.50f, h * 0.48f),
                radius = w * 0.48f
            ),
            radius = w * 0.48f,
            center = Offset(w * 0.50f, h * 0.48f)
        )

        val glow = Color(0xFF36BDF5)
        val buildingDark = Color(0xFF102D50)
        val buildingMid = Color(0xFF17436B)
        val buildingLight = Color(0xFF205982)
        val window = Color(0xFF7DDAFF)

        // Gedung kiri.
        drawRoundRect(
            color = buildingDark,
            topLeft = Offset(w * 0.08f, h * 0.40f),
            size = Size(w * 0.22f, base - h * 0.40f),
            cornerRadius = CornerRadius(5.dp.toPx())
        )

        // Gedung tengah sebagai bangunan utama.
        drawRoundRect(
            color = buildingMid,
            topLeft = Offset(w * 0.34f, h * 0.13f),
            size = Size(w * 0.31f, base - h * 0.13f),
            cornerRadius = CornerRadius(6.dp.toPx())
        )

        // Atap gedung utama.
        drawLine(
            color = glow,
            start = Offset(w * 0.495f, h * 0.04f),
            end = Offset(w * 0.495f, h * 0.13f),
            strokeWidth = 2.dp.toPx()
        )

        // Gedung kanan.
        drawRoundRect(
            color = buildingLight,
            topLeft = Offset(w * 0.70f, h * 0.31f),
            size = Size(w * 0.22f, base - h * 0.31f),
            cornerRadius = CornerRadius(5.dp.toPx())
        )

        // Jendela gedung kiri.
        for (row in 0..3) {
            for (col in 0..2) {
                drawRoundRect(
                    color = window.copy(alpha = 0.72f),
                    topLeft = Offset(
                        w * (0.115f + col * 0.065f),
                        h * (0.47f + row * 0.105f)
                    ),
                    size = Size(w * 0.025f, h * 0.045f),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        // Jendela gedung tengah.
        for (row in 0..5) {
            for (col in 0..3) {
                drawRoundRect(
                    color = window.copy(alpha = if (row % 2 == 0) 0.9f else 0.55f),
                    topLeft = Offset(
                        w * (0.375f + col * 0.065f),
                        h * (0.22f + row * 0.105f)
                    ),
                    size = Size(w * 0.027f, h * 0.052f),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        // Jendela gedung kanan.
        for (row in 0..4) {
            for (col in 0..2) {
                drawRoundRect(
                    color = window.copy(alpha = 0.72f),
                    topLeft = Offset(
                        w * (0.735f + col * 0.060f),
                        h * (0.39f + row * 0.105f)
                    ),
                    size = Size(w * 0.024f, h * 0.047f),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        // Garis dasar kota dan aksen neon.
        drawLine(
            color = glow.copy(alpha = 0.85f),
            start = Offset(w * 0.035f, base),
            end = Offset(w * 0.965f, base),
            strokeWidth = 1.5.dp.toPx()
        )

        drawLine(
            color = glow.copy(alpha = 0.35f),
            start = Offset(w * 0.18f, base + 4.dp.toPx()),
            end = Offset(w * 0.82f, base + 4.dp.toPx()),
            strokeWidth = 1.dp.toPx()
        )
    }
}
