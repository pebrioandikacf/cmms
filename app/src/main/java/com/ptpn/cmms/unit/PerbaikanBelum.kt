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

data class PerbaikanBelumItem(
    val no: Int,
    val tanggal: String,
    val judul: String,
    val deskripsi: String,
    val status: String
)

val sampleperbaikanbelum = listOf(
    PerbaikanBelumItem(no = 1, tanggal = "01 Juni 2025", judul = "Wheel Loader Cat 914 G", deskripsi = "Preventive Maintenance", status = "Belum Dikerjakan"),
    PerbaikanBelumItem(no = 2, tanggal = "21 Oktober 2024", judul = "Gledor", deskripsi = "Preventive Maintenance", status = "Belum Dikerjakan")
)

@Composable
fun PerbaikanBelum(
    items: List<PerbaikanBelumItem>,
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
                .border(0.8.dp, Color(0xFFDDDDDD), shape = MaterialTheme.shapes.medium),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Judul
                Text(
                    text = "List Perbaikan Asset Belum Dikerjakan",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                // show + search (tetap ada)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show", fontSize = 13.sp)
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .border(1.dp, Color.LightGray)
                            .height(32.dp)
                            .width(56.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("10", fontSize = 13.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("entries", fontSize = 13.sp)

                    Spacer(Modifier.width(16.dp))

                    // Search
                    Text("Search:", fontSize = 13.sp, modifier = Modifier.padding(end = 8.dp))
                    Box(
                        modifier = Modifier
                            .height(34.dp)
                            .width(200.dp)
                            .border(1.dp, Color.LightGray),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text("", modifier = Modifier.padding(start = 8.dp), fontSize = 13.sp, color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // --- SELALU TAMPILKAN CARD LIST (hapus branch tabel) ---
                items.forEachIndexed { idx, item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFD))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                                // No
                                Text(
                                    text = "${item.no}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    modifier = Modifier.width(28.dp)
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                // Judul dan tanggal
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.judul,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        maxLines = Int.MAX_VALUE,
                                        overflow = TextOverflow.Clip,
                                        softWrap = true
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(item.tanggal, fontSize = 12.sp, color = Color.Gray)
                                }

                                // Status badge
                                Column(horizontalAlignment = Alignment.End) {
                                    StatusBadgePerBelum(status = item.status)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // deskripsi multiline
                            Text(
                                item.deskripsi,
                                fontSize = 13.sp,
                                color = Color.DarkGray,
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
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3), contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Visibility, contentDescription = "Lihat", modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Lihat")
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = { /* hapus */ },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935), contentColor = Color.White),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(36.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Hapus", modifier = Modifier.size(18.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Hapus")
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Showing text
                Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp)) {
                    Text("Showing 1 to ${items.size.coerceAtLeast(1)} of ${items.size} entries", fontSize = 13.sp)
                }

                // Pagination (tetap dipertahankan)
                val pageSize = 10
                val totalPages = ((items.size + pageSize - 1) / pageSize).coerceAtLeast(1)
                var currentPage by remember { mutableStateOf(1) }

                SimpleTextPaginationPerBelum(
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
fun StatusBadgePerBelum(status: String) {
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
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = status, fontSize = 12.sp, color = color, fontWeight = FontWeight.SemiBold)
        }
    }
}

// ---------- Pagination helper (sekaligus mengatasi error inference) ----------
private sealed class PageItemPerBelum {
    data class Page(val number: Int) : PageItemPerBelum()
    object Ellipsis : PageItemPerBelum()
}

private fun computePageItems(totalPages: Int, currentPage: Int): List<PageItemPerBelum> {
    if (totalPages <= 7) {
        return (1..totalPages).map { PageItemPerBelum.Page(it) }
    }

    val items = mutableListOf<PageItemPerBelum>()
    items += PageItemPerBelum.Page(1)

    val start = (currentPage - 1).coerceAtLeast(2)
    val end = (currentPage + 1).coerceAtMost(totalPages - 1)

    if (start > 2) {
        items += PageItemPerBelum.Ellipsis
    } else {
        for (i in 2..start) items += PageItemPerBelum.Page(i)
    }

    for (i in start..end) {
        if (i in 2 until totalPages) items += PageItemPerBelum.Page(i)
    }

    if (end < totalPages - 1) {
        items += PageItemPerBelum.Ellipsis
    } else {
        for (i in (end + 1) until totalPages) items += PageItemPerBelum.Page(i)
    }

    items += PageItemPerBelum.Page(totalPages)

    // remove accidental duplicates
    return items.fold(mutableListOf<PageItemPerBelum>()) { acc, it ->
        val keep = when {
            acc.isEmpty() -> true
            acc.last() is PageItemPerBelum.Page && it is PageItemPerBelum.Page -> (acc.last() as PageItemPerBelum.Page).number != (it as PageItemPerBelum.Page).number
            acc.last() is PageItemPerBelum.Ellipsis && it is PageItemPerBelum.Ellipsis -> false
            else -> true
        }
        if (keep) acc += it
        acc
    }
}

@Composable
fun SimpleTextPaginationPerBelum(
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
                is PageItemPerBelum.Page -> {
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
                PageItemPerBelum.Ellipsis -> {
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
fun PreviewPerbaikanBelum() {
    MaterialTheme {
        PerbaikanBelum(items = sampleperbaikanbelum, modifier = Modifier.fillMaxWidth())
    }
}
