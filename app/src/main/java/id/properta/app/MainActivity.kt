package id.properta.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import id.properta.app.auth.AuthScreen
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

        setContent {
            PropertaTheme {
                val propertyViewModel: PropertyViewModel = viewModel(
                    factory = PropertyViewModel.Factory(repository)
                )
                val tenantViewModel: TenantViewModel = viewModel(
                    factory = TenantViewModel.Factory(repository)
                )
                var authenticated by remember { mutableStateOf(false) }
                if (authenticated) {
                    PropertaApp(propertyViewModel, tenantViewModel)
                } else {
                    AuthScreen(onAuthenticated = { authenticated = true })
                }
            }
        }
    }
}

@Composable
private fun PropertaApp(
    viewModel: PropertyViewModel,
    tenantViewModel: TenantViewModel
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Beranda", "Properti", "Penyewa", "Keuangan")
    val icons = listOf(
        Icons.Default.Home,
        Icons.Default.Apartment,
        Icons.Default.People,
        Icons.Default.Payments
    )

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title, fontSize = 10.sp) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            0 -> Dashboard(Modifier.padding(padding))
            1 -> PropertyScreen(viewModel, Modifier.padding(padding))
            2 -> TenantScreen(tenantViewModel, Modifier.padding(padding))
            else -> PlaceholderScreen(tabs[selectedTab], Modifier.padding(padding))
        }
    }
}

@Composable
private fun Dashboard(modifier: Modifier = Modifier) {
    Column(
        modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Column(Modifier.padding(top = 24.dp)) {
            Text(
                "PROPERTA",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "Kelola properti dengan lebih mudah",
                color = MutedText,
                fontSize = 12.sp
            )
        }

        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(26.dp))
                .background(Brush.linearGradient(listOf(DeepNavy, Navy, RoyalBlue)))
                .padding(22.dp)
        ) {
            Text("Kelola Properti,", color = Color.White.copy(alpha = 0.8f))
            Text(
                "Raih Lebih Banyak.",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(22.dp))
            Text(
                "TOTAL PEMASUKAN BULAN INI",
                color = Color.White.copy(alpha = 0.75f),
                fontSize = 10.sp,
                letterSpacing = 1.sp
            )
            Spacer(Modifier.height(6.dp))
            Text("Rp 0", color = Color.White, fontSize = 32.sp,
                fontWeight = FontWeight.Bold)
            Text(
                "Pemasukan tercatat akan muncul di sini",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 11.sp
            )
        }

        Text(
            "Ringkasan properti",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Properti", "0", "Terdaftar", RoyalBlue, Modifier.weight(1f))
            SummaryCard("Unit", "0", "Total kamar", CyanGlow, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Penyewa aktif", "0", "Sedang menyewa",
                SuccessGreen, Modifier.weight(1f))
            SummaryCard("Tunggakan", "Rp 0", "Belum dibayar",
                WarningAmber, Modifier.weight(1f))
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Pembayaran berikutnya", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Belum ada jadwal pembayaran", color = MutedText,
                    fontSize = 12.sp)
            }
        }
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
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp)) {
        Column(
            Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                Modifier.size(9.dp).clip(RoundedCornerShape(50)).background(accent)
            )
            Text(title, color = MutedText, fontSize = 11.sp)
            Text(value, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = MutedText, fontSize = 10.sp)
        }
    }
}
