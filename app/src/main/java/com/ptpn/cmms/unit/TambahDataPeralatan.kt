package com.ptpn.cmms.unit

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Versi UI yang "diperbagus" untuk form TambahDataAsset.
 * - Lebih rapi: jarak, grup kolom, section header, icon, helperText, tombol tersusun
 * - Preview-safe (enablePickers = false akan men-disable launcher)
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TambahDataAsset(
    modifier: Modifier = Modifier,
    enablePickers: Boolean = true
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val kategoriOptions = listOf(
        "Pilih Kategori",
        "Mesin Berat",
        "Alat Ringan",
        "Elektronik",
        "Lainnya"
    )

    // state form
    var kategori by remember { mutableStateOf(kategoriOptions.first()) }
    var kategoriExpanded by remember { mutableStateOf(false) }

    var mesin by remember { mutableStateOf("") }
    var merk by remember { mutableStateOf("") }
    var kapasitas by remember { mutableStateOf("") }
    var tahun by remember { mutableStateOf("") }
    var nomorMaterial by remember { mutableStateOf("") }
    var nilaiPerolehan by remember { mutableStateOf("") }
    var sopIk by remember { mutableStateOf("") }
    var grafik by remember { mutableStateOf("") }

    var fotoKondisiUri by remember { mutableStateOf<Uri?>(null) }
    var gambarKerjaUri by remember { mutableStateOf<Uri?>(null) }

    val pickImageLauncher = if (enablePickers) {
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            fotoKondisiUri = uri
        }
    } else null

    val pickAnyLauncher = if (enablePickers) {
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            gambarKerjaUri = uri
        }
    } else null

    // local validation helper
    fun validate(): List<String> {
        val errors = mutableListOf<String>()
        if (kategori == kategoriOptions.first()) errors += "Pilih kategori"
        if (mesin.isBlank()) errors += "Isi mesin/peralatan"
        if (merk.isBlank()) errors += "Isi merk/type"
        if (tahun.isNotBlank()) {
            if (tahun.length != 4 || tahun.any { !it.isDigit() }) errors += "Tahun perolehan harus 4 digit angka"
        }
        return errors
    }

    fun formatCurrencyInput(input: String): String {
        // simple sanitization: allow digits, dot, comma
        return input.filter { it.isDigit() || it == '.' || it == ',' }
    }

    fun submit() {
        val errors = validate()
        if (errors.isEmpty()) {
            Toast.makeText(context, "Data berhasil dikirim (simulasi)", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, errors.joinToString("\n"), Toast.LENGTH_LONG).show()
        }
    }

    // ---- UI ----
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Inventory,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "Form input data peralatan", style = MaterialTheme.typography.headlineSmall)
            }
            ElevatedButton(onClick = { /* optional: reset form */
                kategori = kategoriOptions.first(); mesin = ""; merk = ""; kapasitas = ""; tahun = ""; nomorMaterial = ""; nilaiPerolehan = ""; sopIk = ""; grafik = ""; fotoKondisiUri = null; gambarKerjaUri = null
            }) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Reset")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = "Detail Peralatan", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(12.dp))

                // Kategori (dropdown)
                Text(text = "Kategori", style = MaterialTheme.typography.labelSmall)
                ExposedDropdownMenuBox(
                    expanded = kategoriExpanded,
                    onExpandedChange = { kategoriExpanded = !kategoriExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = kategori,
                        onValueChange = { /* read only */ },
                        readOnly = true,
                        trailingIcon = {
                            Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = "drop")
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = kategoriExpanded,
                        onDismissRequest = { kategoriExpanded = false }
                    ) {
                        kategoriOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    kategori = option
                                    kategoriExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // two-column row: Mesin / Merk
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = mesin,
                        onValueChange = { mesin = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Mesin / Peralatan") },
                        leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = merk,
                        onValueChange = { merk = it },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        label = { Text("Merk / Type") },
                        leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // two-column row: Kapasitas / Tahun
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = kapasitas,
                        onValueChange = { kapasitas = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        singleLine = true,
                        label = { Text("Kapasitas Terpasang") },
                        leadingIcon = { Icon(Icons.Default.Speed, contentDescription = null) }
                    )

                    OutlinedTextField(
                        value = tahun,
                        onValueChange = {
                            val filtered = it.filter { ch -> ch.isDigit() }.take(4)
                            tahun = filtered
                        },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 56.dp),
                        singleLine = true,
                        label = { Text("Tahun Perolehan") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nomorMaterial,
                    onValueChange = { nomorMaterial = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nomor Material") },
                    leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = nilaiPerolehan,
                    onValueChange = { input -> nilaiPerolehan = formatCurrencyInput(input) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Nilai Perolehan") },
                    supportingText = { Text("Masukkan angka. Gunakan titik atau koma sebagai pemisah desimal") },
                    leadingIcon = { Icon(Icons.Default.Savings, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // File pickers row
                Text(text = "Dokumen & Foto", style = MaterialTheme.typography.titleSmall)
                Spacer(modifier = Modifier.height(8.dp))

                // Foto card (full width)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)) {
                        Text(text = "Foto Kondisi Peralatan", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(onClick = {
                                if (pickImageLauncher != null) pickImageLauncher.launch("image/*")
                                else Toast.makeText(context, "Picker disabled (preview mode)", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Pilih Foto")
                            }

                            // filename + clear
                            Column(modifier = Modifier.weight(1f)) {
                                val filename = fotoKondisiUri?.lastPathSegment ?: "Belum ada file"
                                Text(
                                    text = filename,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (fotoKondisiUri != null) {
                                    Text(text = "Ketuk ikon untuk preview", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            AnimatedVisibility(visible = fotoKondisiUri != null) {
                                IconButton(onClick = { fotoKondisiUri = null }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus foto")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gambar kerja card (di bawah foto, full width)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)) {
                        Text(text = "Gambar Kerja Peralatan", style = MaterialTheme.typography.labelSmall)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilledTonalButton(onClick = {
                                if (pickAnyLauncher != null) pickAnyLauncher.launch("*/*")
                                else Toast.makeText(context, "Picker disabled (preview mode)", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.UploadFile, contentDescription = null)
                                Spacer(Modifier.width(6.dp))
                                Text("Pilih File")
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                val filename = gambarKerjaUri?.lastPathSegment ?: "Belum ada file"
                                Text(
                                    text = filename,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                if (gambarKerjaUri != null) {
                                    Text(text = "Tipe file: ${'$'}{gambarKerjaUri?.scheme ?:}", style = MaterialTheme.typography.bodySmall)
                                }
                            }

                            AnimatedVisibility(visible = gambarKerjaUri != null) {
                                IconButton(onClick = { gambarKerjaUri = null }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Hapus file")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = sopIk,
                    onValueChange = { sopIk = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("SOP / IK Peralatan") },
                    leadingIcon = { Icon(Icons.Default.Rule, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = grafik,
                    onValueChange = { grafik = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Grafik Peralatan (opsional)") },
                    leadingIcon = { Icon(Icons.Default.ShowChart, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action row
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    ElevatedButton(onClick = { submit() }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Tambah")
                    }

                    OutlinedButton(onClick = {
                        // go back or clear
                        Toast.makeText(context, "Batal", Toast.LENGTH_SHORT).show()
                    }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Batal")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Small footer card with summary / hint
        Card(shape = RoundedCornerShape(8.dp), modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(2.dp)) {
            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Column {
                    Text("Tips: Lengkapi data penting seperti merk, tahun, dan nomor material untuk mempermudah pelacakan.")
                    Text("File yang dipilih hanya disimulasikan pada preview.", style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTambahDataAsset() {
    TambahDataAsset(enablePickers = false)
}
