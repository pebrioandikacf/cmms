package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

data class PemeliharaanBelumItem(
    val no: Int,
    val tanggal: String,
    val judul: String,
    val deskripsi: String,
    val status: String
)

val samplepemeliharaanbelum = listOf(
    PemeliharaanBelumItem(no = 1, tanggal = "01 Juni 2025", judul = "Wheel Loader Cat 914 G", deskripsi = "Preventive Maintenance", status = "Belum Dikerjakan"),
    PemeliharaanBelumItem(no = 2, tanggal = "21 Oktober 2024", judul = "Gledor", deskripsi = "Preventive Maintenance", status = "Belum Dikerjakan")
)

@Composable
fun PemeliharaanBelum(
    items: List<PemeliharaanBelumItem>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(0.8.dp, Color(0xFFEEEEEE), shape = MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Judul
                Text(
                    text = "List Pemeliharaan Asset Belum Dikerjakan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // show + search (tetap ada)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show", fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color(0xFFCCCCCC), shape = RoundedCornerShape(6.dp))
                            .height(36.dp)
                            .width(64.dp)
                            .padding(horizontal = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("10", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("entries", fontSize = 13.sp)

                    Spacer(Modifier.width(16.dp))

                    // Search
                    Text("Search:", fontSize = 13.sp, modifier = Modifier.padding(end = 8.dp))
                    Box(
                        modifier = Modifier
                            .height(36.dp)
                            .width(220.dp)
                            .border(1.dp, Color(0xFFDDDDDD), shape = RoundedCornerShape(8.dp))
                            .background(Color(0xFFF7F8FB)),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("Cari...", modifier = Modifier.padding(start = 12.dp), fontSize = 13.sp, color = Color(0xFF9E9E9E))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- SELALU TAMPILKAN CARD LIST (hapus branch tabel) ---
                items.forEachIndexed { idx, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(0.5.dp, Color(0xFFEEEEEE), shape = RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFDFDFE))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                // No
                                Text(
                                    text = "${item.no}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.width(30.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.width(10.dp))

                                // Judul dan tanggal
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.judul,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 15.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        softWrap = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(item.tanggal, fontSize = 12.sp, color = Color(0xFF757575))
                                }

                                // Status badge
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadgePemBelum(status = item.status)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // deskripsi multiline
                            Text(
                                item.deskripsi,
                                fontSize = 13.sp,
                                color = Color(0xFF616161),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // aksi (Lihat + Hapus) di kanan
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { /* lihat */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1976D2), contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "Lihat", modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Lihat", fontSize = 14.sp)
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { /* hapus */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F), contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Hapus", fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Showing text
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)) {
                    Text("Showing 1 to ${items.size.coerceAtLeast(1)} of ${items.size} entries", fontSize = 13.sp, color = Color(0xFF616161))
                }

                // Pagination (tetap dipertahankan)
                val pageSize = 10
                val totalPages = ((items.size + pageSize - 1) / pageSize).coerceAtLeast(1)
                var currentPage by remember { mutableStateOf(1) }

                SimpleTextPaginationPemBelum(
                    totalPages = totalPages,
                    currentPage = currentPage,
                    onPageSelected = { newPage -> currentPage = newPage },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)
                )
            }
        }
    }
}


// ---------- StatusBadge (pills with dot) ----------
@Composable
fun StatusBadgePemBelum(status: String) {
    val color = when {
        status.contains("Belum", ignoreCase = true) -> Color(0xFFE53935)
        status.contains("Baik", ignoreCase = true) -> Color(0xFF2E7D32)
        else -> Color(0xFFF57F17)
    }

    Surface(
        color = color.copy(alpha = 0.10f),
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 0.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = status, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ---------- Pagination helper (sekaligus mengatasi error inference) ----------
private sealed class PageItemPemBelum {
    data class Page(val number: Int) : PageItemPemBelum()
    object Ellipsis : PageItemPemBelum()
}

private fun computePageItems(totalPages: Int, currentPage: Int): List<PageItemPemBelum> {
    if (totalPages <= 7) {
        return (1..totalPages).map { PageItemPemBelum.Page(it) }
    }

    val items = mutableListOf<PageItemPemBelum>()
    items += PageItemPemBelum.Page(1)

    val start = (currentPage - 1).coerceAtLeast(2)
    val end = (currentPage + 1).coerceAtMost(totalPages - 1)

    if (start > 2) {
        items += PageItemPemBelum.Ellipsis
    } else {
        for (i in 2..start) items += PageItemPemBelum.Page(i)
    }

    for (i in start..end) {
        if (i in 2 until totalPages) items += PageItemPemBelum.Page(i)
    }

    if (end < totalPages - 1) {
        items += PageItemPemBelum.Ellipsis
    } else {
        for (i in (end + 1) until totalPages) items += PageItemPemBelum.Page(i)
    }

    items += PageItemPemBelum.Page(totalPages)

    // remove accidental duplicates
    return items.fold(mutableListOf<PageItemPemBelum>()) { acc, it ->
        val keep = when {
            acc.isEmpty() -> true
            acc.last() is PageItemPemBelum.Page && it is PageItemPemBelum.Page -> (acc.last() as PageItemPemBelum.Page).number != (it as PageItemPemBelum.Page).number
            acc.last() is PageItemPemBelum.Ellipsis && it is PageItemPemBelum.Ellipsis -> false
            else -> true
        }
        if (keep) acc += it
        acc
    }
}

@Composable
fun SimpleTextPaginationPemBelum(
    totalPages: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = Color.Gray
) {
    val total = totalPages.coerceAtLeast(1)
    val cur = currentPage.coerceIn(1, total)
    val items = computePageItems(total, cur)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Sebelumnya",
            modifier = Modifier
                .clickable(enabled = cur > 1) { if (cur > 1) onPageSelected(cur - 1) }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            color = if (cur > 1) inactiveColor else inactiveColor.copy(alpha = 0.4f)
        )

        Spacer(Modifier.width(6.dp))

        items.forEach { item ->
            when (item) {
                is PageItemPemBelum.Page -> {
                    val isActive = item.number == cur
                    Text(
                        text = item.number.toString(),
                        modifier = Modifier
                            .padding(horizontal = 6.dp, vertical = 6.dp)
                            .clickable(enabled = !isActive) { onPageSelected(item.number) },
                        color = if (isActive) activeColor else inactiveColor,
                        fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
                PageItemPemBelum.Ellipsis -> {
                    Text(
                        text = "...",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp),
                        color = inactiveColor
                    )
                }
            }
        }

        Spacer(Modifier.width(6.dp))

        Text(
            text = "Selanjutnya",
            modifier = Modifier
                .clickable(enabled = cur < total) { if (cur < total) onPageSelected(cur + 1) }
                .padding(horizontal = 8.dp, vertical = 6.dp),
            color = if (cur < total) inactiveColor else inactiveColor.copy(alpha = 0.4f)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPemeliharaanBelum() {
    MaterialTheme {
        PemeliharaanBelum(items = samplepemeliharaanbelum, modifier = Modifier.fillMaxWidth())
    }
}
