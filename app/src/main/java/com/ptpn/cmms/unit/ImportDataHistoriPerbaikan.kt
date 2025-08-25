package com.ptpn.cmms.unit

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportDataHistoriPerbaikanScreen(
    onSave: (ImportHistoriPerbaikanData) -> Unit = {}
) {
    val context = LocalContext.current

    var pickedFile by remember { mutableStateOf<Uri?>(null) }
    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        pickedFile = uri
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {

            Text(text = "Import Data Histori Perbaikan", fontSize = 20.sp, modifier = Modifier.padding(bottom = 12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {

                    Text(text = "Tambah Data Histori Peralatan", style = MaterialTheme.typography.titleSmall, modifier = Modifier.padding(bottom = 8.dp))

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Masukkan Data", modifier = Modifier.weight(0.25f))

                        OutlinedButton(
                            onClick = { filePicker.launch(arrayOf("*/*")) },
                            modifier = Modifier
                                .weight(0.75f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.UploadFile, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = pickedFile?.lastPathSegment ?: "Pilih file (.xls/.csv)")
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (pickedFile == null) {
                                Toast.makeText(context, "Pilih file terlebih dahulu", Toast.LENGTH_SHORT).show()
                                return@Button
                            }

                            val data = ImportHistoriPerbaikanData(pickedFile)
                            onSave(data)
                            Toast.makeText(context, "File diimpor (simulasi)", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.Start)
                    ) {
                        Text(text = "Simpan")
                    }

                }
            }

        }
    }
}

// simple data holder
data class ImportHistoriPerbaikanData(
    val fileUri: Uri?
)

@Preview(showBackground = true)
@Composable
fun PreviewImportDataHistoriPerbaikan() {
    ImportDataHistoriPerbaikanScreen()
}
