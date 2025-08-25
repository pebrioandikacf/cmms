package com.ptpn.cmms.unit

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// File: DetailPerbaikanSudah.kt
// Deskripsi: Composable untuk meniru tampilan detail perbaikan (lihat screenshot yang diberikan).
// Catatan: ganti `R.drawable.sample_photo` pada preview dengan resource gambar milikmu,
// atau panggil `DetailPerbaikanSudah` dengan parameter `imagePainter` yang sesuai.

@Composable
fun DetailPerbaikanSudah(
    modifier: Modifier = Modifier,
    imagePainter: Painter? = null // beri null jika tidak ingin menampilkan gambar
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(color = Color(0xFFF6F7F8))
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Header
                Text(
                    text = "DETAIL PERBAIKAN BOILER NO. 2 TANGGAL 20 JANUARI 2024",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))
                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "MEKANIK YANG MENGERJAKAN :",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                    )
                    Text(
                        text = "ANGGA SAPUTRA",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                ) {
                    Text(
                        text = "KETERANGAN PERBAIKAN :",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                    )
                    Text(
                        text = "RUSAK. ESTIMASI 1 HARI",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    )
                }

                Divider()

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "STATUS PENGERJAAN :",
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 13.sp)
                    )
                    Text(
                        text = "SUDAH SELESAI",
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF1E8E4F) // hijau mirip screenshot
                        )
                    )
                }

                Divider()

                Spacer(modifier = Modifier.height(12.dp))

                // Deskripsi Material box
                Column {
                    Text(
                        text = "Deskripsi Material",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(4.dp)),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Box(modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF2F2F2))
                            .padding(12.dp)
                        ) {
                            Text(text = "SEMEN MORTAR @ 50 Kg", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Hasil Pekerjaan header
        Text(
            text = "HASIL PEKERJAAN",
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Gambar hasil pekerjaan dalam card dengan border dan padding seperti di screenshot
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .padding(top = 4.dp),
            shape = RoundedCornerShape(6.dp)
        ) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
            ) {
                if (imagePainter != null) {
                    Image(
                        painter = imagePainter,
                        contentDescription = "Hasil pekerjaan",
                        modifier = Modifier
                            .fillMaxSize()
                    )
                } else {
                    // Placeholder box kalau tidak ada gambar
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(1.dp, Color(0xFFE0E0E0), shape = RoundedCornerShape(4.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "[GAMBAR HASIL PEKERJAAN]", color = Color.Gray)
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewDetailPerbaikanSudah() {
    Surface {
        // Untuk preview gunakan drawable bawaan Android supaya tidak unresolved.
        // Ganti android.R.drawable.ic_menu_gallery -> R.drawable.sample_photo (projectmu)
        // setelah kamu pastikan drawable itu memang ada.
        val samplePainter: Painter = painterResource(id = android.R.drawable.ic_menu_gallery)

        DetailPerbaikanSudah(imagePainter = samplePainter)
    }
}
