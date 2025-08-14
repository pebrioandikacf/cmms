package com.ptpn.cmms.unit

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class MaintenanceRow(val description: String, val status: String)

@Composable
fun Detail_Pemeliharaan(
    id: Int? = null,                          // optional id dari nav
    titleMain: String = "",                  // default value
    titleBold: String = "",                  // default value
    date: String = "",                       // default value
    mechanicName: String = "",               // default value
    tasks: List<MaintenanceRow> = emptyList() // default empty list
) {
    // Data contoh sementara berdasarkan id (ganti dengan fetch nanti)
    data class DetailData(
        val titleMain: String,
        val titleBold: String,
        val date: String,
        val mechanicName: String,
        val tasks: List<MaintenanceRow>
    )

    val sampleDataMap = remember {
        mapOf(
            3 to DetailData(
                titleMain = "DETAIL PEMELIHARAAN ",
                titleBold = "OVERHAUL BOILER No.1",
                date = "28 Mei 2025",
                mechanicName = "SUPARNO",
                tasks = listOf(
                    MaintenanceRow("Bongkar bagian A", "Selesai"),
                    MaintenanceRow("Ganti gasket", "Selesai"),
                    MaintenanceRow("Test tekanan", "Selesai")
                )
            ),
            4 to DetailData(
                titleMain = "DETAIL PEMELIHARAAN ",
                titleBold = "PENGGANTIAN BELT CONVEYOR",
                date = "29 Mei 2025",
                mechanicName = "SUDIRMAN",
                tasks = listOf(
                    MaintenanceRow("Lepas belt lama", "Selesai"),
                    MaintenanceRow("Pasang belt baru", "Selesai")
                )
            )
            // Tambahkan mapping lain sesuai kebutuhan
        )
    }

    // Jika semua parameter kosong tapi id ada -> ambil dari sampleDataMap
    val resolved = remember(id, titleMain, titleBold, date, mechanicName, tasks) {
        val isAllEmpty =
            titleMain.isBlank() && titleBold.isBlank() && date.isBlank() && mechanicName.isBlank() && tasks.isEmpty()

        if (isAllEmpty && id != null) {
            sampleDataMap[id] ?: DetailData(titleMain, titleBold, date, mechanicName, tasks)
        } else {
            DetailData(titleMain, titleBold, date, mechanicName, tasks)
        }
    }

    val borderColor = Color(0xFFBDBDBD)
    val rowAlt = Color(0xFFF5F5F5)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Judul (pakai resolved)
            Text(
                text = buildAnnotatedString {
                    append(resolved.titleMain)
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) { append(resolved.titleBold) }
                },
                fontSize = 14.sp
            )
            Divider(color = borderColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

            // Tampilkan tanggal & mekanik secara terpisah supaya tidak kosong di UI
            Text(
                text = "TANGGAL",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (resolved.date.isNotBlank()) resolved.date else "-",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "MEKANIK YANG MENGERJAKAN :",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = if (resolved.mechanicName.isNotBlank()) resolved.mechanicName else "-",
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Divider(color = borderColor, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
            Spacer(modifier = Modifier.height(8.dp))

            // TableRow reusable
            @Composable
            fun TableRow(
                desc: String,
                status: String,
                bgColor: Color,
                isHeader: Boolean = false
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor)
                        .height(IntrinsicSize.Min) // tinggi seragam
                ) {
                    // Kolom 1: Uraian
                    Box(
                        modifier = Modifier
                            .weight(1.8f)
                            .fillMaxHeight()
                            .border(1.dp, borderColor) // border penuh cell
                            .padding(8.dp),
                        contentAlignment = if (isHeader) Alignment.Center else Alignment.CenterStart
                    ) {
                        Text(
                            text = desc,
                            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            textAlign = if (isHeader) TextAlign.Center else TextAlign.Start
                        )
                    }

                    // Kolom 2: Status
                    Box(
                        modifier = Modifier
                            .weight(0.6f)
                            .fillMaxHeight()
                            .border(1.dp, borderColor) // border penuh cell
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status,
                            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp,
                            color = if (!isHeader) Color(0xFF00BFFF) else Color.Unspecified,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Header
            TableRow("Uraian Pekerjaan", "Status", Color.White, isHeader = true)

            // Data: pakai resolved.tasks
            if (resolved.tasks.isEmpty()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {
                    Text("Tidak ada detail pekerjaan.", color = Color.Gray)
                }
            } else {
                resolved.tasks.forEachIndexed { index, task ->
                    val bgColor = if (index % 2 == 1) rowAlt else Color.White
                    TableRow(task.description, task.status, bgColor)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDetailPemeliharaanView() {
    val sampleData = remember {
        listOf(
            MaintenanceRow("PREVENTIVE WHELL LOADER 50 HM", "Selesai"),
            MaintenanceRow("BUANG KOTORAN DLM TANGKI PD SIST BHN BKR", "Selesai"),
            MaintenanceRow("PERIKSA AIR BATTERY PD SIST LISTRIK", "Selesai"),
            MaintenanceRow("PERIKSA KONEKTOR PD SIST LISTRIK", "Selesai")
        )
    }
    Detail_Pemeliharaan(
        titleMain = "DETAIL PEMELIHARAAN ",
        titleBold = "WHELL LOADER CAT 914 G PKS SPA",
        date = "02 FEBRUARI 2024",
        mechanicName = "SUPARNO",
        tasks = sampleData
    )
}
