@file:OptIn(ExperimentalMaterial3Api::class)
package com.ptpn.cmms.mechanic

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.RemoveCircle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ptpn.cmms.ui.theme.CmmsTheme

// -------------------------------
// Data Class (tidak diubah)
// -------------------------------
data class AssetItemMechanic(
    val id: Long,
    var name: String,
    var merk: String,
    var kapasitas: String,
    var tahun: String,
    var nilai: String,
    var kode: String,
    var lokasi: String,
    var status: String = "Baik",
    var fotoUri: String? = null,
    var sopLink: String? = null,
    var grafik: String? = null
)

// -------------------------------
// Activity Utama (Dummy Demo)
// -------------------------------
class EditAssetActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CmmsTheme { CMMSDummyApp() } }
    }
}

// -------------------------------
// Root App Dummy
// -------------------------------
@Composable
fun CMMSDummyApp() {
    val assets = remember {
        mutableStateListOf(
            AssetItemMechanic(
                id = 1000110001,
                name = "Mesin Example 1",
                merk = "Brand A",
                kapasitas = "10 HP",
                tahun = "2010",
                nilai = "1.000.000",
                kode = "1000110001",
                lokasi = "Area Bengkel Mekanik",
                status = "Baik"
            ),
            AssetItemMechanic(
                id = 1000110002,
                name = "Mesin Example 2",
                merk = "Brand B",
                kapasitas = "5 HP",
                tahun = "2012",
                nilai = "800.000",
                kode = "1000110002",
                lokasi = "Pabrik A",
                status = "Butuh Perbaikan"
            ),
            AssetItemMechanic(
                id = 1000110003,
                name = "Mesin Example 3",
                merk = "Brand C",
                kapasitas = "7 HP",
                tahun = "2015",
                nilai = "900.000",
                kode = "1000110003",
                lokasi = "Gudang",
                status = "Dalam Pengawasan"
            )
        )
    }

    var editingAssetId by remember { mutableStateOf<Long?>(null) }
    val ctx = LocalContext.current

    Surface(color = MaterialTheme.colorScheme.background) {
        if (editingAssetId == null) {
            AssetListScreen(
                assets = assets,
                onView = { asset ->
                    Toast.makeText(
                        ctx,
                        "Lihat: ${asset.name}\nKode: ${asset.kode}\nLokasi: ${asset.lokasi}",
                        Toast.LENGTH_LONG
                    ).show()
                },
                onUpdate = { id -> editingAssetId = id }
            )
        } else {
            val asset = assets.find { it.id == editingAssetId }
            if (asset != null) {
                EditAssetScreen(
                    asset = asset,
                    onCancel = { editingAssetId = null },
                    onSave = { updated ->
                        val idx = assets.indexOfFirst { it.id == updated.id }
                        if (idx >= 0) assets[idx] = updated
                        Toast.makeText(ctx, "Berhasil diupdate", Toast.LENGTH_SHORT).show()
                        editingAssetId = null
                    }
                )
            } else editingAssetId = null
        }
    }
}

// -------------------------------
// List Screen (styled to use theme)
// -------------------------------
@Composable
fun AssetListScreen(
    assets: List<AssetItemMechanic>,
    onView: (AssetItemMechanic) -> Unit,
    onUpdate: (Long) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Mekanik") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                scrollBehavior = null
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(inner)
                .padding(12.dp)
        ) {
            Text("Data Assets", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                assets.forEach { asset ->
                    AssetCard(
                        asset = asset,
                        onView = { onView(asset) },
                        onUpdate = { onUpdate(asset.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun AssetCard(asset: AssetItemMechanic, onView: () -> Unit, onUpdate: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(asset.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(4.dp))
                    Text("Kode: ${asset.kode}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                    Text("Lokasi: ${asset.lokasi}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                }
                StatusPill(status = asset.status)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onView,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Lihat")
                }
                Spacer(Modifier.width(12.dp))
                Button(
                    onClick = onUpdate,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Update")
                }
            }
        }
    }
}

@Composable
fun StatusPill(status: String) {
    val bg = when (status) {
        "Baik" -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.12f)
        "Butuh Perbaikan" -> MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
        "Dalam Pengawasan" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
    }
    val textColor = when (status) {
        "Baik" -> MaterialTheme.colorScheme.onSecondary
        "Butuh Perbaikan" -> MaterialTheme.colorScheme.onError
        "Dalam Pengawasan" -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(status, color = textColor, style = MaterialTheme.typography.bodyMedium)
    }
}

// -------------------------------
// Edit Screen (themed) — Material3
// -------------------------------
@Composable
fun EditAssetScreen(asset: AssetItemMechanic, onCancel: () -> Unit, onSave: (AssetItemMechanic) -> Unit) {
    var name by remember { mutableStateOf(asset.name) }
    var merk by remember { mutableStateOf(asset.merk) }
    var kapasitas by remember { mutableStateOf(asset.kapasitas) }
    var tahun by remember { mutableStateOf(asset.tahun) }
    var nilai by remember { mutableStateOf(asset.nilai) }
    var sopLink by remember { mutableStateOf(asset.sopLink ?: "") }
    var grafik by remember { mutableStateOf(asset.grafik ?: "") }
    var status by remember { mutableStateOf(asset.status) }
    var fotoUriStr by remember { mutableStateOf(asset.fotoUri) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { fotoUriStr = it.toString() }
    }

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Data Asset") },
                navigationIcon = {
                    // jangan beri tint manual: biarkan TopAppBar atur contentColor
                    IconButton(onClick = onCancel) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back") }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .imePadding(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onCancel, shape = RoundedCornerShape(8.dp)) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                Toast.makeText(context, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            onSave(
                                asset.copy(
                                    name = name,
                                    merk = merk,
                                    kapasitas = kapasitas,
                                    tahun = tahun,
                                    nilai = nilai,
                                    sopLink = sopLink.ifBlank { null },
                                    grafik = grafik.ifBlank { null },
                                    status = status,
                                    fotoUri = fotoUriStr
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Update")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("FORM INVENTORY", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Mesin/Peralatan") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = merk,
                onValueChange = { merk = it },
                label = { Text("Merk/Type") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = kapasitas,
                onValueChange = { kapasitas = it },
                label = { Text("Kapasitas Terpasang") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = tahun,
                onValueChange = { tahun = it },
                label = { Text("Tahun Perolehan") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = nilai,
                onValueChange = { nilai = it },
                label = { Text("Nilai Perolehan") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                ),
                shape = RoundedCornerShape(8.dp)
            )

            Text("Foto Peralatan", style = MaterialTheme.typography.titleSmall)
            if (fotoUriStr != null) {
                AsyncImage(
                    model = fotoUriStr,
                    contentDescription = "Foto",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) { Text("Belum ada foto", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("Pilih Foto", color = MaterialTheme.colorScheme.onPrimary) }

                OutlinedButton(onClick = { fotoUriStr = null }, shape = RoundedCornerShape(8.dp)) {
                    Text("Hapus Foto")
                }
            }

            OutlinedTextField(
                value = sopLink,
                onValueChange = { sopLink = it },
                label = { Text("SOP/IK Peralatan (link)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = grafik,
                onValueChange = { grafik = it },
                label = { Text("Grafik Peralatan") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Status:", modifier = Modifier.width(80.dp), style = MaterialTheme.typography.bodyMedium)
                DropdownStatus(status, onSelect = { status = it })
            }

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}


// -------------------------------
// Dropdown Status (themed)
// -------------------------------
@Composable
fun DropdownStatus(status: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Baik", "Butuh Perbaikan", "Dalam Pengawasan")
    Box {
        Button(
            onClick = { expanded = true },
            modifier = Modifier.height(40.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Text(status, color = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { s ->
                DropdownMenuItem(onClick = { onSelect(s); expanded = false }) {
                    Text(s, color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

// -------------------------------
// Preview
// -------------------------------
@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun Preview_EditAssetScreen() {
    CmmsTheme {
        val sampleAsset = AssetItemMechanic(
            id = 1000110001,
            name = "Mesin Example 1",
            merk = "Brand A",
            kapasitas = "10 HP",
            tahun = "2010",
            nilai = "1.000.000",
            kode = "1000110001",
            lokasi = "Area Bengkel Mekanik",
            status = "Baik",
            fotoUri = null,
            sopLink = "https://drive.google.com/example",
            grafik = "Grafik A"
        )
        EditAssetScreen(asset = sampleAsset, onCancel = {}, onSave = {})
    }
}
