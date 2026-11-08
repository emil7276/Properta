package id.properta.app

import android.os.Bundle
import id.properta.app.data.backup.PropertaBackup
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.compose.ui.platform.LocalContext
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.properta.app.auth.AuthScreen
import id.properta.app.auth.LocalAuth
import id.properta.app.ui.TenantScreen
import id.properta.app.ui.FinanceScreen
import id.properta.app.data.PropertaRepository
import id.properta.app.data.local.PropertaDatabase
import id.properta.app.ui.PropertyViewModel
import id.properta.app.ui.TenantViewModel
import id.properta.app.ui.theme.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = PropertaDatabase.getInstance(applicationContext)
        val repository = PropertaRepository(
            database.propertyDao(),
            database.rentalUnitDao(),
            database.tenantDao()
        )

        val auth = LocalAuth(applicationContext)
        val settings = getSharedPreferences("properta_settings", MODE_PRIVATE)

        setContent {
            var selectedSkinId by remember {
                mutableStateOf(settings.getString("accent_skin", "ocean") ?: "ocean")
            }
            val selectedSkin = PropertaAccentSkins.all.firstOrNull {
                it.id == selectedSkinId
            } ?: PropertaAccentSkins.default

            var selectedLanguage by remember {
                mutableStateOf(
                    settings.getString("app_language", "id") ?: "id"
                )
            }

            PropertaTheme(accentSkin = selectedSkin) {
                val propertyViewModel: PropertyViewModel = viewModel(
                    factory = PropertyViewModel.Factory(repository)
                )
                val tenantViewModel: TenantViewModel = viewModel(
                    factory = TenantViewModel.Factory(repository)
                )
                var authenticated by remember { mutableStateOf(auth.hasRememberedSession()) }
                if (authenticated) {
                    PropertaApp(
                        propertyViewModel,
                        tenantViewModel,
                        selectedSkin = selectedSkin,
                        selectedLanguage = selectedLanguage,
                        onLanguageSelected = { language ->
                            selectedLanguage = language
                            settings.edit()
                                .putString("app_language", language)
                                .apply()
                        },
                        onSkinSelected = { skin ->
                            settings.edit()
                                .putString("accent_skin", skin.id)
                                .apply()
                            selectedSkinId = skin.id
                        },
                        onLogout = {
                            auth.clearSession()
                            authenticated = false
                        }
                    )
                } else {
                    AuthScreen(
                        language = selectedLanguage,
                        onAuthenticated = { authenticated = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun PropertaApp(
    viewModel: PropertyViewModel,
    tenantViewModel: TenantViewModel,
    selectedSkin: AccentSkin,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onSkinSelected: (AccentSkin) -> Unit,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Beranda", "Unit", "Penyewa", "Transaksi", "Lainnya")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Apartment,
        Icons.Default.People,
        Icons.Default.Payments,
        Icons.Default.MoreHoriz
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF030B1B),
                        Color(0xFF071A38),
                        Color(0xFF0A2852),
                        Color(0xFF06152E)
                    )
                )
            )
            .drawBehind {
                // Soft abstract glow: electric blue upper-right, cyan lower-left.
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x9926BFFF),
                            Color(0x442B8CFF),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.94f, size.height * 0.10f),
                        radius = size.width * 0.78f
                    ),
                    radius = size.width * 0.78f,
                    center = Offset(size.width * 0.94f, size.height * 0.10f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0x6622D3EE),
                            Color(0x332684FF),
                            Color.Transparent
                        ),
                        center = Offset(size.width * 0.04f, size.height * 0.88f),
                        radius = size.width * 0.72f
                    ),
                    radius = size.width * 0.72f,
                    center = Offset(size.width * 0.04f, size.height * 0.88f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0x332B8CFF), Color.Transparent),
                        center = Offset(size.width * 0.58f, size.height * 0.52f),
                        radius = size.width * 0.58f
                    ),
                    radius = size.width * 0.58f,
                    center = Offset(size.width * 0.58f, size.height * 0.52f)
                )
            },
        containerColor = Color.Transparent,
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xF5FFFFFF),
                tonalElevation = 8.dp
            ) {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = {
                            Icon(
                                icons[index],
                                contentDescription = title
                            )
                        },
                        label = { Text(title, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = RoyalBlue,
                            selectedTextColor = RoyalBlue,
                            unselectedIconColor = MutedText,
                            unselectedTextColor = MutedText,
                            indicatorColor = Color(0xFFDCEBFF)
                        )
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> Dashboard(
                modifier = Modifier.padding(padding),
                selectedSkin = selectedSkin,
                selectedLanguage = selectedLanguage,
                onLanguageSelected = onLanguageSelected,
                onSkinSelected = onSkinSelected,
                onLogout = onLogout
            )
            1 -> PropertyScreen(viewModel, Modifier.padding(padding))
            2 -> TenantScreen(tenantViewModel, Modifier.padding(padding))
            3 -> FinanceScreen(Modifier.padding(padding))
            4 -> OtherScreen(
                Modifier.padding(padding),
                selectedLanguage,
                onLanguageSelected
            )
        }
    }
}



@Composable
private fun Dashboard(
    modifier: Modifier = Modifier,
    selectedSkin: AccentSkin,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onSkinSelected: (AccentSkin) -> Unit,
    onLogout: () -> Unit = {}
) {
    val english = selectedLanguage == "en"
    Column(
        modifier = modifier.fillMaxSize().background(LightBackground)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("PROPERTA", color = DeepNavy, fontSize = 21.sp,
                    fontWeight = FontWeight.ExtraBold, letterSpacing = 1.2.sp)
                Text(if (english) "Manage Properties, Achieve More."
                    else "Kelola Properti, Raih Lebih Banyak.",
                    color = MutedText, fontSize = 11.sp)
            }
            Surface(shape = CircleShape, color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE2EAF5))) {
                Row(Modifier.padding(horizontal = 4.dp, vertical = 2.dp)) {
                    TextButton(onClick = { onLanguageSelected("id") },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("ID", color = if (!english) RoyalBlue else MutedText,
                            fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    TextButton(onClick = { onLanguageSelected("en") },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)) {
                        Text("EN", color = if (english) RoyalBlue else MutedText,
                            fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PropertaAccentSkins.all, key = { it.id }) { skin ->
                val active = skin.id == selectedSkin.id
                Surface(onClick = { onSkinSelected(skin) },
                    shape = RoundedCornerShape(50),
                    color = if (active) Color(0xFFE7F0FF) else Color.White,
                    border = BorderStroke(1.dp,
                        if (active) skin.primary else Color(0xFFE3EAF4))) {
                    Row(Modifier.padding(horizontal = 11.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        Box(Modifier.size(12.dp).clip(CircleShape)
                            .background(Brush.linearGradient(listOf(skin.primary, skin.glow))))
                        Text(skin.name, color = DeepNavy, fontSize = 10.sp,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium)
                    }
                }
            }
        }

        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))
            .background(Brush.linearGradient(listOf(
                Color(0xFF061A3A), Color(0xFF0755B8), Color(0xFF12A8E8)
            ))).padding(20.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(if (english) "PROPERTY OVERVIEW" else "RINGKASAN PROPERTI",
                        color = Color.White.copy(alpha = .72f), fontSize = 10.sp,
                        letterSpacing = 1.2.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(7.dp))
                    Text(if (english) "Manage smarter." else "Kelola lebih mudah.",
                        color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    Text(if (english) "Your property business at a glance"
                        else "Semua aktivitas properti dalam satu tempat",
                        color = Color.White.copy(alpha = .78f), fontSize = 11.sp)
                }
                Surface(shape = RoundedCornerShape(17.dp),
                    color = Color.White.copy(alpha = .14f)) {
                    Icon(Icons.Default.Apartment, contentDescription = null,
                        tint = Color.White, modifier = Modifier.padding(13.dp).size(28.dp))
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(if (english) "MONTHLY INCOME" else "PENDAPATAN BULAN INI",
                color = Color.White.copy(alpha = .72f), fontSize = 10.sp,
                letterSpacing = 1.sp)
            Spacer(Modifier.height(5.dp))
            Text("—", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
            Text(if (english) "Finance data appears when transactions are recorded"
                else "Data pendapatan tampil setelah transaksi dicatat",
                color = Color.White.copy(alpha = .78f), fontSize = 10.sp)
        }

        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(if (english) "Your portfolio" else "Portofolio Anda",
                    color = DeepNavy, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(if (english) "A clear view of your property data"
                    else "Pantau data properti dengan lebih rapi",
                    color = MutedText, fontSize = 11.sp)
            }
            TextButton(onClick = onLogout) {
                Text(if (english) "Sign out" else "Keluar", color = MutedText, fontSize = 11.sp)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            SummaryCard(if (english) "Properties" else "Properti", "—",
                if (english) "Registered" else "Terdaftar", RoyalBlue, Modifier.weight(1f))
            SummaryCard(if (english) "Rental units" else "Unit sewa", "—",
                if (english) "Total units" else "Total unit", CyanGlow, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
            SummaryCard(if (english) "Tenants" else "Penyewa", "—",
                if (english) "Active tenants" else "Penyewa aktif",
                SuccessGreen, Modifier.weight(1f))
            SummaryCard(if (english) "Outstanding" else "Tunggakan", "—",
                if (english) "Unpaid bills" else "Tagihan belum lunas",
                WarningAmber, Modifier.weight(1f))
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
            Row(Modifier.fillMaxWidth().padding(17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(13.dp)) {
                Surface(shape = RoundedCornerShape(14.dp), color = Color(0xFFEAF2FF)) {
                    Icon(Icons.Default.Payments, contentDescription = null, tint = RoyalBlue,
                        modifier = Modifier.padding(12.dp).size(23.dp))
                }
                Column(Modifier.weight(1f)) {
                    Text(if (english) "Payment schedule" else "Jadwal pembayaran",
                        color = DeepNavy, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(if (english) "Upcoming bills and payment status"
                        else "Pantau jatuh tempo dan status pembayaran",
                        color = MutedText, fontSize = 10.sp)
                }
            }
        }
        Text(if (english) "Choose a menu below to manage your properties."
            else "Pilih menu di bawah untuk mulai mengelola properti Anda.",
            color = MutedText, fontSize = 10.sp,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp))
    }
}

@Composable
private fun PropertyScreen(
    viewModel: PropertyViewModel,
    modifier: Modifier = Modifier
) {
    val properties by viewModel.properties.collectAsState()
    var showForm by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Properti", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                Text("${properties.size} properti terdaftar", color = MutedText)
            }
            FilledTonalIconButton(onClick = { showForm = true }) {
                Icon(Icons.Default.Add, contentDescription = "Tambah properti")
            }
        }

        if (properties.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.fillMaxWidth().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Apartment, contentDescription = null,
                        tint = RoyalBlue, modifier = Modifier.size(42.dp))
                    Spacer(Modifier.height(12.dp))
                    Text("Belum ada properti", fontWeight = FontWeight.Bold)
                    Text("Tambahkan properti pertama Anda.",
                        color = MutedText, fontSize = 12.sp)
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = { showForm = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Tambah properti")
                    }
                }
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(properties, key = { it.id }) { property ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.fillMaxWidth().padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(property.name, fontWeight = FontWeight.Bold,
                                fontSize = 16.sp)
                            if (property.address.isNotBlank()) {
                                Text(property.address, color = MutedText,
                                    fontSize = 13.sp)
                            }
                            if (property.notes.isNotBlank()) {
                                Text(property.notes, fontSize = 12.sp)
                            }
                            TextButton(
                                onClick = { viewModel.deleteProperty(property) }
                            ) {
                                Text("Hapus properti",
                                    color = MaterialTheme.colorScheme.error)
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
            title = { Text("Tambah properti") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nama properti *") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Alamat") }
                    )
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Catatan") }
                    )
                }
            },
            confirmButton = {
                Button(
                    enabled = name.isNotBlank(),
                    onClick = {
                        viewModel.addProperty(name.trim(), address.trim(), notes.trim())
                        name = ""
                        address = ""
                        notes = ""
                        showForm = false
                    }
                ) { Text("Simpan") }
            },
            dismissButton = {
                TextButton(onClick = { showForm = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun PlaceholderScreen(title: String, modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
        Text("Modul ini akan dikembangkan berikutnya.", color = MutedText)
    }
}



@Composable
private fun SummaryCard(
    title: String,
    value: String,
    subtitle: String,
    accent: Color,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, shape = RoundedCornerShape(21.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(Modifier.fillMaxWidth().padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(12.dp))
                .background(accent.copy(alpha = .12f)),
                contentAlignment = Alignment.Center) {
                Box(Modifier.size(10.dp).clip(CircleShape).background(accent))
            }
            Text(title, color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Medium)
            Text(value, color = DeepNavy, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MutedText, fontSize = 9.sp)
        }
    }
}


@Composable
private fun OtherScreen(
    modifier: Modifier = Modifier,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val database = remember(context) {
        PropertaDatabase.getInstance(context)
    }
    val scope = rememberCoroutineScope()
    val english = selectedLanguage == "en"

    var busy by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("") }
    var pendingRestore by remember { mutableStateOf<String?>(null) }

    val createBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                busy = true
                status = if (english) "Creating backup..." else "Membuat backup..."

                try {
                    val json = PropertaBackup.export(database)

                    withContext(Dispatchers.IO) {
                        val output = context.contentResolver.openOutputStream(uri)
                            ?: error("Tidak dapat membuka file tujuan.")

                        output.bufferedWriter().use { it.write(json) }
                    }

                    status = if (english) {
                        "Backup successfully saved."
                    } else {
                        "Backup berhasil disimpan."
                    }
                } catch (e: Exception) {
                    status = (if (english) "Backup failed: " else "Backup gagal: ") +
                        (e.message ?: "Unknown error")
                } finally {
                    busy = false
                }
            }
        }
    }

    val openBackup = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                busy = true
                status = if (english) "Checking backup file..." else "Memeriksa file backup..."

                try {
                    val json = withContext(Dispatchers.IO) {
                        context.contentResolver.openInputStream(uri)
                            ?.bufferedReader()
                            ?.use { it.readText() }
                            ?: error("Tidak dapat membaca file.")
                    }

                    withContext(Dispatchers.IO) {
                        PropertaBackup.validate(json)
                    }

                    pendingRestore = json
                    status = if (english) {
                        "Backup file is valid. Confirm before restoring."
                    } else {
                        "File backup valid. Konfirmasikan sebelum memulihkan."
                    }
                } catch (e: Exception) {
                    status = (if (english) "Invalid backup: " else "Backup tidak valid: ") +
                        (e.message ?: "Unknown error")
                } finally {
                    busy = false
                }
            }
        }
    }

    if (pendingRestore != null) {
        AlertDialog(
            onDismissRequest = { pendingRestore = null },
            title = {
                Text(if (english) "Restore PROPERTA?" else "Pulihkan PROPERTA?")
            },
            text = {
                Text(
                    if (english) {
                        "All current property, unit, tenant, and finance records will be replaced by the backup data. This action cannot be undone."
                    } else {
                        "Seluruh data properti, unit, penyewa, dan keuangan saat ini akan diganti dengan data dari backup. Tindakan ini tidak dapat dibatalkan."
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val json = pendingRestore ?: return@TextButton
                        pendingRestore = null

                        scope.launch {
                            busy = true
                            status = if (english) "Restoring data..." else "Memulihkan data..."

                            try {
                                PropertaBackup.restore(database, json)
                                status = if (english) {
                                    "Restore completed successfully."
                                } else {
                                    "Restore berhasil. Data telah dipulihkan."
                                }
                            } catch (e: Exception) {
                                status = (if (english) "Restore failed: " else "Restore gagal: ") +
                                    (e.message ?: "Unknown error")
                            } finally {
                                busy = false
                            }
                        }
                    }
                ) {
                    Text(if (english) "Restore" else "Pulihkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingRestore = null }) {
                    Text(if (english) "Cancel" else "Batal")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = if (english) "More" else "Lainnya",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    if (english) "Language / Bahasa" else "Bahasa / Language",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FilterChip(
                        selected = selectedLanguage == "id",
                        onClick = { onLanguageSelected("id") },
                        label = { Text("Indonesia") }
                    )
                    FilterChip(
                        selected = selectedLanguage == "en",
                        onClick = { onLanguageSelected("en") },
                        label = { Text("English") }
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    if (english) "Backup & Restore" else "Backup & Pemulihan",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Text(
                    if (english) {
                        "Save all eight PROPERTA data tables to a JSON file, or restore them from a previous backup."
                    } else {
                        "Simpan seluruh delapan tabel data PROPERTA ke file JSON, atau pulihkan dari file backup sebelumnya."
                    },
                    color = MutedText
                )

                Button(
                    onClick = {
                        createBackup.launch("PROPERTA-backup.json")
                    },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (english) "Create Backup" else "Buat Backup")
                }

                OutlinedButton(
                    onClick = {
                        openBackup.launch(
                            arrayOf("application/json", "application/octet-stream", "*/*")
                        )
                    },
                    enabled = !busy,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (english) "Restore from File" else "Pulihkan dari File")
                }

                if (busy) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }

                if (status.isNotBlank()) {
                    Text(
                        text = status,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(Modifier.padding(20.dp)) {
                Text(
                    "PROPERTA",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
                Text(
                    if (english) "Property Management Application" else
                        "Aplikasi Manajemen Properti",
                    color = MutedText,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    "Version 0.1.0",
                    color = MutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}
