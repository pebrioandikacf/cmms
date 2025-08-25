// File: app/src/main/java/com/ptpn/cmms/mechanic/DetailAssetMechanic.kt
@file:Suppress("DEPRECATION")

package com.ptpn.cmms.mechanic

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptpn.cmms.ui.component.CmmsCard
import java.text.NumberFormat
import java.util.Locale

// Models (sesuaikan dengan model backend Anda)
data class SubAsset(val uraian: String, val jumlah: Int, val satuan: String, val harga: String)
data class AssetDetail(
    val id: Int,
    val kode: String,
    val nama: String,
    val lokasi: String,
    val kategori: String,
    val merk: String?,
    val kapasitas: String?,
    val tahun: String?,
    val nomorPeralatan: String?,
    val nilaiPerolehan: String?,
    val totalJamJalan: String?,
    val images: List<String> = emptyList(),
    val subAssets: List<SubAsset> = emptyList()
)

// helpers
private fun parseCurrencyToLong(s: String?): Long {
    if (s.isNullOrBlank()) return 0L
    val digits = s.replace("[^0-9]".toRegex(), "")
    return try { digits.ifBlank { "0" }.toLong() } catch (_: Exception) { 0L }
}
private fun formatCurrency(value: Long): String =
    NumberFormat.getCurrencyInstance(Locale("id", "ID")).format(value)

// utility small composables
@Composable
private fun InfoRow(title: String, value: String?, weight: Float = 1f) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.Top) {
        Text(title, modifier = Modifier.width(140.dp), fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value ?: "-", modifier = Modifier.weight(weight), maxLines = 3, overflow = TextOverflow.Ellipsis, fontSize = 13.sp)
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
}

@Composable
private fun TwoColumnInfo(leftContent: @Composable ColumnScope.() -> Unit, rightContent: @Composable ColumnScope.() -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Column(modifier = Modifier.weight(1f), content = leftContent)
        Column(modifier = Modifier.weight(1f), content = rightContent)
    }
}

// Image gallery (safe placeholder, clickable to call onViewImage).
// If you want to load real remote images, replace inner Icon with Coil's AsyncImage (add coil-compose dependency).
@Composable
private fun ImageGallery(images: List<String>, onViewImage: (String) -> Unit = {}) {
    if (images.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("No image", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(horizontal = 6.dp)
    ) {
        itemsIndexed(images) { _, img ->
            Card(
                modifier = Modifier
                    .size(width = 320.dp, height = 180.dp)
                    .clickable(enabled = img.isNotBlank()) { if (img.isNotBlank()) onViewImage(img) },
                shape = RoundedCornerShape(6.dp),
                elevation = CardDefaults.cardElevation()
            ) {
                // Placeholder icon for now (safe for all devices)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = if (img.isBlank()) "No image" else "Asset image",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// Sub-assets table simplified
@Composable
private fun SubAssetsTable(items: List<SubAsset>) {
    CmmsCard {
        Column(Modifier.fillMaxWidth()) {
            Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(12.dp)) {
                Text("Uraian", modifier = Modifier.weight(2f), fontWeight = FontWeight.SemiBold)
                Text("Jumlah", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text("Satuan", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
                Text("Harga", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold)
            }
            if (items.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No sub-assets")
                }
            } else {
                items.forEach { s ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                        Text(s.uraian, modifier = Modifier.weight(2f))
                        Text("${s.jumlah}", modifier = Modifier.weight(1f))
                        Text(s.satuan, modifier = Modifier.weight(1f))
                        Text(s.harga, modifier = Modifier.weight(1f))
                    }
                }
                val total = items.sumOf { parseCurrencyToLong(it.harga) }
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.End) {
                    Text("Total Biaya", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.width(24.dp))
                    Text(formatCurrency(total), fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

/**
 * Main detail screen composable.
 * onBack -> navigate back
 * onViewImage -> open image viewer (optional)
 */
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun DetailAssetMechanicScreen(
    asset: AssetDetail,
    onBack: () -> Unit = {},
    onViewImage: (String) -> Unit = {}
) {
    val scroll = rememberScrollState()
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Detail Asset",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold) },
            navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
        )
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header with logo + company address (mimic screenshot)
            CmmsCard {
                Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.Top) {
                    // safe logo placeholder (use Icon instead of system drawable)
                    Box(modifier = Modifier.size(120.dp).background(Color.White, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Company logo",
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(Modifier.width(18.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text("PT. PERKEBUNAN NUSANTARA IV (Regional 3)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        Spacer(Modifier.height(6.dp))
                        Text("Jl. Rambutan No. 43 Pekanbaru - Riau 28294")
                        Spacer(Modifier.height(6.dp))
                        Text("(+62-761) 66665")
                    }
                }
            }

            // main info two-column
            CmmsCard {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("DATA RINCIAN :")
                    TwoColumnInfo(
                        leftContent = {
                            InfoRow("PKS", asset.lokasi)
                            InfoRow("Kategori", asset.kategori)
                            InfoRow("Mesin", asset.nama)
                        },
                        rightContent = {
                            InfoRow("Merk", asset.merk)
                            InfoRow("Kapasitas", asset.kapasitas)
                            InfoRow("Tahun", asset.tahun)
                        }
                    )
                }
            }

            // perolehan
            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("DATA PEROLEHAN :")
                    InfoRow("Nomor Peralatan", asset.nomorPeralatan)
                    InfoRow("Nilai Perolehan", asset.nilaiPerolehan)
                    InfoRow("Total Jam Jalan", asset.totalJamJalan)
                }
            }

            // image gallery + action buttons
            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionTitle("FOTO KONDISI PERALATAN")
                    ImageGallery(asset.images, onViewImage)
                }
            }

            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("GAMBAR KERJA PERALATAN")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { /* open gambar kerja */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF44336))) { Text("Lihat Gambar Kerja") }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("IK PERALATAN")
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { /* open sop */ }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B5998))) { Text("Lihat SOP IK") }
                    }
                    Spacer(Modifier.height(8.dp))
                }
            }

            // tables (history / sub-assets) simplified
            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("DATA JAM JALAN")
                    Text("Showing 0 to 0 of 0 entries", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                }
            }

            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("DATA HISTORY PEMELIHARAAN")
                    Text("Showing 0 to 0 of 0 entries", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                }
            }

            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("DATA HISTORY PERBAIKAN")
                    Text("Showing 0 to 0 of 0 entries", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 13.sp)
                    Spacer(Modifier.height(8.dp))
                }
            }

            CmmsCard {
                Column(Modifier.fillMaxWidth().padding(12.dp)) {
                    SectionTitle("DAFTAR SUB ASSETS")
                    SubAssetsTable(asset.subAssets)
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

// preview with dummy data
@Preview(showBackground = true, widthDp = 1000)
@Composable
fun DetailAssetPreview() {
    val dummy = remember {
        AssetDetail(
            id = 1,
            kode = "1000110001",
            nama = "Mesin Bubut Besar",
            lokasi = "PKS Sei Pagar",
            kategori = "Mesin Produksi",
            merk = "ST SUNDER ELECTRICAL",
            kapasitas = "10 T",
            tahun = "1995",
            nomorPeralatan = "1000110648",
            nilaiPerolehan = "Rp 454.786.763",
            totalJamJalan = "0 Jam",
            images = listOf("", "", ""), // URLs or leave empty
            subAssets = listOf(SubAsset("Mesin Bubut Besar", 1, "unit", "Rp 454.786.763"))
        )
    }
    DetailAssetMechanicScreen(asset = dummy, onBack = {})
}
