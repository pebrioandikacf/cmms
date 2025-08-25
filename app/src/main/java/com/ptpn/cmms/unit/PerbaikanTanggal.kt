package com.ptpn.cmms.unit

import android.app.DatePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

/**
 * PerbaikanTanggal:
 * - useUtc = false -> gunakan waktu perangkat (lokal)
 * - useUtc = true  -> gunakan waktu UTC (jam dunia)
 */
@Composable
fun PerbaikanTanggal(
    modifier: Modifier = Modifier,
    useUtc: Boolean = false
) {
    val konteks = LocalContext.current

    // manualOverride = true ketika user memilih tanggal manual lewat DatePicker
    var manualOverride by remember { mutableStateOf(false) }

    // state untuk tanggal yang ditampilkan (format: "dd MMMM yyyy")
    var tanggalDipilih by remember { mutableStateOf(currentFormattedDate(useUtc)) }

    // Auto-update: jika tidak manualOverride, update tepat setelah pergantian hari
    LaunchedEffect(useUtc, manualOverride) {
        if (manualOverride) return@LaunchedEffect
        while (!manualOverride) {
            val wait = millisUntilNextMidnight(useUtc)
            // safety: jika perhitungan negatif atau 0, tunggu 1 detik dulu
            val safeWait = if (wait > 0L) wait else 1000L
            delay(safeWait + 1000L) // tambahkan 1 detik supaya sudah melewati tengah malam
            if (!manualOverride) {
                tanggalDipilih = currentFormattedDate(useUtc)
            }
        }
    }

    Card(
        modifier = modifier
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            // Judul
            Text(
                text = "List Perbaikan Asset Tanggal $tanggalDipilih",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Input tanggal (read-only) + DatePicker
            OutlinedTextField(
                value = tanggalDipilih,
                onValueChange = {},
                label = { Text("Tanggal") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = "Pilih Tanggal",
                        modifier = Modifier.clickable {
                            val kalender = Calendar.getInstance(
                                if (useUtc) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
                            )
                            val dialogTanggal = DatePickerDialog(
                                konteks,
                                { _, tahun, bulan, hari ->
                                    // format manual agar tetap konsisten (Indonesia)
                                    val daftarBulan = listOf(
                                        "Januari", "Februari", "Maret", "April", "Mei", "Juni",
                                        "Juli", "Agustus", "September", "Oktober", "November", "Desember"
                                    )
                                    tanggalDipilih = "$hari ${daftarBulan[bulan]} $tahun"
                                    manualOverride = true // user memilih manual -> hentikan auto-update
                                },
                                kalender.get(Calendar.YEAR),
                                kalender.get(Calendar.MONTH),
                                kalender.get(Calendar.DAY_OF_MONTH)
                            )
                            dialogTanggal.show()
                        }
                    )
                },
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Tombol Submit (dummy) + Tombol Hari Ini (reset ke sekarang & enable auto-update)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { /* aksi submit jika perlu */ },
                    modifier = Modifier
                ) {
                    Text("Submit")
                }

                Spacer(Modifier.width(12.dp))

                TextButton(onClick = {
                    // reset ke hari ini dan izinkan auto-update lagi
                    tanggalDipilih = currentFormattedDate(useUtc)
                    manualOverride = false
                }) {
                    Text("Hari Ini")
                }

                Spacer(Modifier.weight(1f))

                // Toggle untuk gunakan UTC atau tidak (opsional visual)
                // Jika tidak ingin toggle, bisa dihapus; disini hanya informasi kecil.
                Text(
                    text = if (useUtc) "UTC time" else "Local time",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider()

            // Tabel Header
            Row(
                Modifier
                    .background(Color(0xFFE0E0E0))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("#", modifier = Modifier.weight(0.3f))
                Text("Deskripsi", modifier = Modifier.weight(1f))
                Text("Status", modifier = Modifier.weight(1f))
                Text("Aksi", modifier = Modifier.weight(1f))
            }

            // Tabel Kosong
            Text(
                "Belum ada data untuk ditampilkan.",
                modifier = Modifier.padding(vertical = 16.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

/** Helper: format tanggal sekarang sesuai locale ID */
private fun currentFormattedDate(useUtc: Boolean): String {
    val tz = if (useUtc) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
    val now = Calendar.getInstance(tz).time
    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id"))
    sdf.timeZone = tz
    return sdf.format(now)
}

/** Helper: berapa millis sampai tengah malam berikutnya (mengikuti useUtc flag) */
private fun millisUntilNextMidnight(useUtc: Boolean): Long {
    val tz = if (useUtc) TimeZone.getTimeZone("UTC") else TimeZone.getDefault()
    val now = Calendar.getInstance(tz)
    val next = Calendar.getInstance(tz).apply {
        timeInMillis = now.timeInMillis
        add(Calendar.DAY_OF_YEAR, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return next.timeInMillis - now.timeInMillis
}

@Preview(showBackground = true)
@Composable
fun PreviewPerbaikanTanggal() {
    PerbaikanTanggal(modifier = Modifier.fillMaxWidth(), useUtc = false)
}
