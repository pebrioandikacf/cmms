// File: DetailMechanicDashboard.kt
package com.ptpn.cmms.mechanic

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Photo
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.ptpn.cmms.ui.theme.CmmsTheme
import com.ptpn.cmms.ui.component.CmmsCard
import java.io.File

private data class MechanicDetail(
    val id: Int, val title: String, val date: String, val mechanic: String,
    val description: String, val status: String, val materialDesc: String, val workResult: String
)

private val dummyDetails = listOf(
    MechanicDetail(1, "BOLLARD CAPSTAND NO. 1", "16 Juni 2025", "Kelik Sudarto", "Rusak, aus. Estimasi 1 hari", "Belum Selesai", "Bearing 32010 SKF", ""),
    MechanicDetail(2, "SCREW PRESS NO. 2", "02 Juni 2025", "Dewi Lestari", "Rem pengaman aus", "Selesai", "Seal Kit Press", "Tersedia gambar hasil perbaikan")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailMechanicDashboard(itemId: Int, onBack: () -> Unit = {}) {
    val detail = remember(itemId) { dummyDetails.firstOrNull { it.id == itemId } }
    val context = LocalContext.current

    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> hasCameraPermission = granted }

    var lastSavedFilePath by remember { mutableStateOf<String?>(null) }
    var imageBitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    var showImageDialog by remember { mutableStateOf(false) }

    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            lastSavedFilePath?.let { path ->
                evidenceMap[itemId] = path
                doneState[itemId] = true
                imageBitmap = runCatching { BitmapFactory.decodeFile(path) }.getOrNull()
            }
        } else lastSavedFilePath = null
    }

    LaunchedEffect(evidenceMap[itemId]) {
        imageBitmap = evidenceMap[itemId]?.let { p -> runCatching { BitmapFactory.decodeFile(p) }.getOrNull() }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Perbaikan", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { padding ->
        if (detail == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Data tidak ditemukan.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyLarge)
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionHeader(title = "DETAIL PERBAIKAN")

            // gunakan CmmsCard (reusable)
            CmmsCard {
                InfoRow("Asset", detail.title)
                ThinDivider()
                InfoRow("Tanggal", detail.date)
                ThinDivider()
                InfoRow("Mekanik", detail.mechanic)
                ThinDivider()
                InfoRow("Keterangan", detail.description)
                ThinDivider()
                val isDone = doneState[itemId] == true
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Status", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    StatusPill(text = if (isDone) "Sudah Dikerjakan" else detail.status, success = isDone)
                }
            }

            SectionHeader(title = "Deskripsi Material")
            CmmsCard { Text(detail.materialDesc, style = MaterialTheme.typography.bodyMedium) }

            SectionHeader(title = "Hasil Pekerjaan")
            CmmsCard {
                if (imageBitmap != null) {
                    Image(
                        bitmap = imageBitmap!!.asImageBitmap(),
                        contentDescription = "Bukti Foto",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showImageDialog = true }
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("Foto bukti tersimpan", style = MaterialTheme.typography.bodyMedium)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Photo, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(detail.workResult.ifEmpty { "Tidak ada gambar" }, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Buttons area
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                val existingPath = evidenceMap[itemId]
                if (existingPath != null) {
                    Text("Bukti sudah tersimpan (hubungi admin untuk perubahan)", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f).padding(end = 12.dp))
                    Button(onClick = {}, enabled = false, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ambil & Simpan Bukti")
                    }
                } else {
                    Button(onClick = {
                        if (!hasCameraPermission) { permissionLauncher.launch(Manifest.permission.CAMERA); return@Button }
                        val cacheFile = try {
                            File.createTempFile("evidence_${itemId}_", ".jpg", context.cacheDir)
                        } catch (_: Exception) {
                            File(context.cacheDir, "evidence_${itemId}_${System.currentTimeMillis()}.jpg").also { runCatching { it.createNewFile() } }
                        }
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", cacheFile)
                        lastSavedFilePath = cacheFile.absolutePath
                        takePictureLauncher.launch(uri)
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ambil & Simpan Bukti")
                    }
                }
            }
        }

        // full-screen image preview
        if (showImageDialog && imageBitmap != null) {
            Dialog(onDismissRequest = { showImageDialog = false }) {
                Surface(shape = RoundedCornerShape(12.dp), tonalElevation = 8.dp) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Image(bitmap = imageBitmap!!.asImageBitmap(), contentDescription = "Preview", modifier = Modifier.fillMaxWidth().heightIn(max = 600.dp).clip(RoundedCornerShape(8.dp)))
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                            TextButton(onClick = { showImageDialog = false }) { Text("Tutup") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ThinDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f)
    )
}

@Composable
private fun InfoRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = valueColor, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
private fun StatusPill(text: String, success: Boolean) {
    Surface(shape = RoundedCornerShape(50), color = if (success) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)) {
        Text(text, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.bodySmall, color = if (success) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
    }
}

@Preview(showBackground = true)
@Composable
fun DetailMechanicDashboardPreview() {
    CmmsTheme { DetailMechanicDashboard(itemId = 1) }
}
