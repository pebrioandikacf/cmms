package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDataListPemeliharaan() {
    var selectedPeralatan by remember { mutableStateOf("") }
    var selectedTanggal by remember { mutableStateOf("") }
    var selectedMekanik by remember { mutableStateOf("") }
    var fileName by remember { mutableStateOf("Belum ada file dipilih") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Import Data List Pemeliharaan",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF7F9FC))
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        "Tambah Data List Pemeliharaan",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )

                    // Upload File
                    OutlinedButton(
                        onClick = {
                            fileName = "contoh_data.csv" // simulasi upload
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Pilih File")
                    }
                    Text(
                        text = fileName,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    // Dropdown Peralatan
                    OutlinedTextField(
                        value = selectedPeralatan,
                        onValueChange = { selectedPeralatan = it },
                        label = { Text("Peralatan") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Tanggal
                    OutlinedTextField(
                        value = selectedTanggal,
                        onValueChange = { selectedTanggal = it },
                        label = { Text("Tanggal (dd/mm/yyyy)") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = Color(0xFF2196F3)
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Mekanik
                    OutlinedTextField(
                        value = selectedMekanik,
                        onValueChange = { selectedMekanik = it },
                        label = { Text("Mekanik") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Tombol Simpan
                    Button(
                        onClick = { /* Simpan Data */ },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00BCD4)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simpan", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "ImportData - Light", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PreviewImportDataListPemeliharaan_Light() {
    MaterialTheme {
        Surface {
            ImportDataListPemeliharaan()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(name = "ImportData - Dark", showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, widthDp = 360, heightDp = 800)
@Composable
fun PreviewImportDataListPemeliharaan_Dark() {
    MaterialTheme {
        Surface {
            ImportDataListPemeliharaan()
        }
    }
}