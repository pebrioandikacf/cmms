package com.ptpn.cmms.unit

import android.app.DatePickerDialog
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDataPerbaikanScreen(
    onSave: (ImportPerbaikanData) -> Unit = {}
) {
    val context = LocalContext.current

    // File pickers
    var importFileUri by remember { mutableStateOf<Uri?>(null) }
    var buktiFileUri by remember { mutableStateOf<Uri?>(null) }

    val importPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        importFileUri = uri
    }

    val buktiPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        buktiFileUri = uri
    }

    // Form state
    var selectedPeralatan by remember { mutableStateOf("") }
    var peralatanExpanded by remember { mutableStateOf(false) }
    val peralatanOptions = listOf("Pompa Air", "Generator", "Kompresor", "Conveyor")

    var selectedSifat by remember { mutableStateOf("") }
    var sifatExpanded by remember { mutableStateOf(false) }
    val sifatOptions = listOf("Darurat", "Rutin", "Inspeksi")

    var tanggal by remember { mutableStateOf("") }
    var mekanik by remember { mutableStateOf("") }
    var keterangan by remember { mutableStateOf("") }

    // Date picker
    val calendar = Calendar.getInstance()
    val datePicker = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            tanggal = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text(
                text = "Import Data Perbaikan",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    // Import File
                    Text(text = "Import Data", modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedButton(
                        onClick = { importPicker.launch(arrayOf("*/*")) },
                        modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = importFileUri?.lastPathSegment ?: "Pilih file (xls/csv)")
                    }

                    Spacer(Modifier.height(12.dp))

                    // Peralatan dropdown
                    Text(text = "Peralatan", modifier = Modifier.padding(bottom = 6.dp))
                    ExposedDropdownMenuBox(
                        expanded = peralatanExpanded,
                        onExpandedChange = { peralatanExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedPeralatan,
                            onValueChange = { selectedPeralatan = it },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            placeholder = { Text("Pilih Peralatan") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.clickable { peralatanExpanded = true }
                                )
                            },
                            readOnly = true
                        )

                        ExposedDropdownMenu(
                            expanded = peralatanExpanded,
                            onDismissRequest = { peralatanExpanded = false }
                        ) {
                            peralatanOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedPeralatan = selectionOption
                                        peralatanExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Sifat perbaikan
                    Text(text = "Sifat Perbaikan", modifier = Modifier.padding(bottom = 6.dp))
                    ExposedDropdownMenuBox(
                        expanded = sifatExpanded,
                        onExpandedChange = { sifatExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedSifat,
                            onValueChange = { selectedSifat = it },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            placeholder = { Text("Pilih Sifat Perbaikan") },
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    modifier = Modifier.clickable { sifatExpanded = true }
                                )
                            },
                            readOnly = true
                        )

                        ExposedDropdownMenu(
                            expanded = sifatExpanded,
                            onDismissRequest = { sifatExpanded = false }
                        ) {
                            sifatOptions.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(selectionOption) },
                                    onClick = {
                                        selectedSifat = selectionOption
                                        sifatExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Upload Bukti
                    Text(text = "Upload Data Bukti Perbaikan", modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedButton(
                        onClick = { buktiPicker.launch(arrayOf("image/*", "application/pdf")) },
                        modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.UploadFile, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(text = buktiFileUri?.lastPathSegment ?: "Pilih file bukti")
                    }

                    Spacer(Modifier.height(12.dp))

                    // Tanggal
                    Text(text = "Tanggal", modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = tanggal,
                        onValueChange = { tanggal = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { datePicker.show() },
                        placeholder = { Text(text = "dd/mm/yyyy") },
                        trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = null) },
                        readOnly = true
                    )

                    Spacer(Modifier.height(12.dp))

                    // Mekanik
                    Text(text = "Mekanik", modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = mekanik,
                        onValueChange = { mekanik = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Pilih Mekanik") }
                    )

                    Spacer(Modifier.height(12.dp))

                    // Keterangan
                    Text(text = "Keterangan", modifier = Modifier.padding(bottom = 6.dp))
                    OutlinedTextField(
                        value = keterangan,
                        onValueChange = { keterangan = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 96.dp),
                        placeholder = { Text("Keterangan singkat perbaikan") },
                        singleLine = false,
                        maxLines = 6
                    )

                    Spacer(Modifier.height(18.dp))

                    // Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        TextButton(onClick = {
                            // clear
                            importFileUri = null
                            buktiFileUri = null
                            selectedPeralatan = ""
                            selectedSifat = ""
                            tanggal = ""
                            mekanik = ""
                            keterangan = ""
                            Toast.makeText(context, "Form dibersihkan", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Bersihkan")
                        }

                        Button(onClick = {
                            if (selectedPeralatan.isBlank()) {
                                Toast.makeText(context, "Pilih peralatan terlebih dahulu", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val data = ImportPerbaikanData(
                                importFile = importFileUri,
                                peralatan = selectedPeralatan,
                                sifat = selectedSifat,
                                buktiFile = buktiFileUri,
                                tanggal = tanggal,
                                mekanik = mekanik,
                                keterangan = keterangan
                            )

                            onSave(data)
                            Toast.makeText(context, "Data disimpan (simulasi)", Toast.LENGTH_SHORT).show()
                        }) {
                            Text("Simpan")
                        }
                    }

                }
            }
        }
    }
}

// Simple data holder
data class ImportPerbaikanData(
    val importFile: Uri?,
    val peralatan: String,
    val sifat: String,
    val buktiFile: Uri?,
    val tanggal: String,
    val mekanik: String,
    val keterangan: String
)

@Preview(showBackground = true)
@Composable
fun PreviewImportDataPerbaikan() {
    ImportDataPerbaikanScreen()
}
