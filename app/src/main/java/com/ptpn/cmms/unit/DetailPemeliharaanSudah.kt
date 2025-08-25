// DetailPemeliharaanSudah.kt
package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.zIndex
import androidx.compose.ui.platform.LocalDensity

data class DetailPemeliharaanRow(
    val uraian: String,
    val status: String
)

@Composable
fun DetailPemeliharaanSudah(
    assetName: String,
    mekanik: String,
    tanggal: String,
    items: List<DetailPemeliharaanRow>,
    modifier: Modifier = Modifier
) {
    val borderColor = Color(0xFFE6E6E6)
    val separatorColor = Color(0xFFECECEC)
    val verticalLineColor = Color(0xFFECECEC)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
        ) {

            // Judul kecil
            Text(
                text = "DETAIL PEMELIHARAAN",
                fontSize = 13.sp,
                color = Color.Gray
            )

            // Asset name (bold)
            Text(
                text = assetName,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp, bottom = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // asset name + date (bold parts)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "TANGGAL", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = tanggal, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            // Mekanik (label biasa + nama bold)
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
            ) {
                Text(text = "MEKANIK YANG MENGERJAKAN : ", color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = mekanik, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))


            // Tabel dengan overlay vertical line (BoxWithConstraints digunakan untuk posisi overlay)
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
            ) {
                val totalWidth = this.maxWidth
                // Hitungan posisi overlay (sesuaikan jika padding/statusWidth berubah)
                val overlayX = totalWidth - 145.dp

                // state untuk menyimpan tinggi konten (px)
                var contentHeightPx by remember { mutableStateOf(0) }
                val density = LocalDensity.current
                val contentHeightDp = with(density) { contentHeightPx.toDp() }

                // Konten tabel (header + baris) — ukur tinggi dengan onGloballyPositioned
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { layoutCoordinates ->
                            // simpan tinggi konten (dalam pixel)
                            contentHeightPx = layoutCoordinates.size.height
                        }
                ) {
                    // Header (tanpa vertical divider di sini). Header teks di-center.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7F7F8))
                            .padding(horizontal = 12.dp, vertical = 10.dp)
                            .height(IntrinsicSize.Min),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Uraian Pekerjaan",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Box(modifier = Modifier.width(120.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "Status",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Baris konten
                    items.forEachIndexed { index, row ->
                        val bg = if (index % 2 == 0) Color(0xFFFDFDFD) else Color(0xFFF6F6F6)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(bg)
                                .padding(horizontal = 12.dp, vertical = 14.dp)
                                .height(IntrinsicSize.Min),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopStart) {
                                Text(
                                    text = row.uraian,
                                    fontSize = 13.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            // Status sekarang di-center di kolom
                            Box(
                                modifier = Modifier
                                    .width(120.dp)
                                    .padding(end = 0.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                StatusPill(text = row.status)
                            }
                        }

                        // Separator horizontal penuh (akan tertutup oleh overlay vertical line di atas)
                        Divider(color = separatorColor, thickness = 1.dp)
                    }
                }

                // Overlay vertical line — tinggi sesuai contentHeightDp sehingga berhenti di akhir data
                if (contentHeightDp > 0.dp) {
                    Box(
                        modifier = Modifier
                            .offset(x = overlayX)
                            .height(contentHeightDp)
                            .width(1.dp)
                            .background(verticalLineColor)
                            .zIndex(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusPill(text: String) {
    // warna sederhana: Selesai -> teal/green, lainnya -> amber
    val (bg, txtColor) = when {
        text.contains("Selesai", ignoreCase = true) -> Pair(Color(0xFFE8F6F4), Color(0xFF00796B))
        text.contains("Belum", ignoreCase = true) -> Pair(Color(0xFFFFF3E0), Color(0xFFEF6C00))
        else -> Pair(Color(0xFFEFF7FF), Color(0xFF1565C0))
    }

    Surface(
        color = bg,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .wrapContentWidth()
            .height(32.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(horizontal = 12.dp)
                .fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = txtColor, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailPemeliharaanSudah() {
    val sample = listOf(
        DetailPemeliharaanRow("PREVENTIVE WHELL LOADER 50 HM", "Selesai"),
        DetailPemeliharaanRow("BUANG KOTORAN DLM TANGKI PD SIST BHN BKR", "Selesai"),
        DetailPemeliharaanRow("PERIKSA AIR BATTERY PD SIST LISTRIK", "Selesai"),
        DetailPemeliharaanRow("PERIKSA KONEKTOR PD SIST LISTRIK", "Selesai")
    )

    MaterialTheme {
        DetailPemeliharaanSudah(
            assetName = "WHELL LOADER CAT 914 G PKS SPA",
            mekanik = "SUPARNO",
            tanggal = "02 FEBRUARI 2024",
            items = sample,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
