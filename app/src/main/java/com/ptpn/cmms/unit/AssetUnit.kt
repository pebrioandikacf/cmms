package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign

// --- Data ---
data class AssetUnitItem(
    val no: Int,
    val tanggal: String,
    val judul: String,
    val deskripsi: String,
    val status: String
)

val sampleassetunit = listOf(
    AssetUnitItem(no = 1, tanggal = "11 Juni 2025", judul = "Wheel Loader Cat 914 G", deskripsi = "Preventive Maintenance", status = "Sudah Dikerjakan"),
    AssetUnitItem(no = 2, tanggal = "22 Oktober 2024", judul = "Gledorr", deskripsi = "Preventive Maintenance", status = "Sudah Dikerjakan") ,
    AssetUnitItem(no = 3, tanggal = "22 Oktober 2024", judul = "Gledorr", deskripsi = "Preventive Maintenance", status = "Sudah Dikerjakan"),
    AssetUnitItem(no = 4, tanggal = "22 Oktober 2024", judul = "Gledorr", deskripsi = "Preventive Maintenance", status = "Sudah Dikerjakan")
)

// --- AssetUnit composable (improved UI while keeping Card-centered design) ---
@Composable
fun AssetUnit(
    items: List<AssetUnitItem>,
    modifier: Modifier = Modifier,
    // Top-grid callback (index starts from 1)
    onBoxClick: (Int) -> Unit = {},
    // optional callbacks for actions on each item
    onViewClick: (AssetUnitItem) -> Unit = {},
    onUpdateClick: (AssetUnitItem) -> Unit = {},
    onQrClick: (AssetUnitItem) -> Unit = {}
) {
    var showCount by remember { mutableStateOf(10) }
    var query by remember { mutableStateOf("") }

    // lightweight filtering
    val filtered = remember(items, query) {
        if (query.isBlank()) items else items.filter {
            it.judul.contains(query, ignoreCase = true) || it.deskripsi.contains(query, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header: title + subtle actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Asset",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = { /* refresh */ }) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)) {

                    // --- Grid of actions (kept as cards) ---
                    GridBoxes(
                        labels = listOf(
                            "Tambah Data Peralatan",
                            "Import Data Peralatan",
                            "Import Data List Pemeliharaan",
                            "Import Data Perbaikan",
                            "Import Data History Perbaikan"
                        ),
                        columns = 3,
                        onBoxClick = onBoxClick
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // controls: show + search
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text("Show", fontSize = 13.sp)
                        Spacer(Modifier.width(8.dp))

                        // simple dropdown emulation
                        Box(modifier = Modifier
                            .border(1.dp, Color.LightGray, shape = RoundedCornerShape(6.dp))
                            .height(36.dp)
                            .width(72.dp), contentAlignment = Alignment.Center) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text(showCount.toString(), fontSize = 13.sp)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Text("entries", fontSize = 13.sp)

                        Spacer(modifier = Modifier.width(16.dp))

                        OutlinedTextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("Cari judul / deskripsi") },
                            singleLine = true,
                            modifier = Modifier
                                .height(36.dp)
                                .widthIn(min = 220.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // list of items as LazyColumn inside Card (keeps card look while being efficient)
                    LazyColumn(modifier = Modifier.fillMaxWidth(), content = {
                        itemsIndexed(filtered) { index, item ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFBFBFD))
                            ) {
                                Column(modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)) {

                                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                        // number badge
                                        Box(modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFEEF7FF)), contentAlignment = Alignment.Center) {
                                            Text("${item.no}", fontWeight = FontWeight.SemiBold)
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = item.judul,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 15.sp,
                                                maxLines = 2,
                                                overflow = TextOverflow.Ellipsis
                                            )

                                            Spacer(modifier = Modifier.height(6.dp))

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(item.tanggal, fontSize = 12.sp, color = Color.Gray)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = item.deskripsi,
                                        fontSize = 13.sp,
                                        color = Color.DarkGray,
                                        maxLines = 3,
                                        overflow = TextOverflow.Ellipsis
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                        // action icons vertical stack
                                        Column(horizontalAlignment = Alignment.End) {
                                            IconButton(onClick = { onQrClick(item) }) {
                                                Box(modifier = Modifier
                                                    .size(36.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF4CAF50)), contentAlignment = Alignment.Center) {
                                                    Icon(Icons.Default.QrCode, contentDescription = "QR", tint = Color.White)
                                                }
                                            }
                                        }
                                        Spacer(Modifier.width(50.dp))

                                        TextButton(onClick = { onViewClick(item) }) {
                                            Icon(Icons.Default.Visibility, contentDescription = null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Lihat")
                                        }

                                        Spacer(Modifier.width(8.dp))

                                        Button(onClick = { onUpdateClick(item) }, shape = RoundedCornerShape(8.dp)) {
                                            Icon(Icons.Default.Edit, contentDescription = null)
                                            Spacer(Modifier.width(6.dp))
                                            Text("Update")
                                        }
                                    }
                                }
                            }
                        }

                        // footer spacing to ensure pagination visible
                        item {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    })

                    Spacer(modifier = Modifier.height(12.dp))

                    // Showing / pagination area
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Showing 1 to ${filtered.size.coerceAtLeast(1)} of ${filtered.size} entries", fontSize = 13.sp)

                        // simple text pagination (kept as before)
                        val pageSize = showCount
                        val totalPages = ((filtered.size + pageSize - 1) / pageSize).coerceAtLeast(1)
                        var currentPage by remember { mutableStateOf(1) }

                        SimpleTextPaginationAssetUnit(
                            totalPages = totalPages,
                            currentPage = currentPage,
                            onPageSelected = { newPage -> currentPage = newPage },
                            modifier = Modifier
                        )
                    }
                }
            }
        }
    }
}


// ---------- GridBoxes (slightly tweaked style but same API) ----------
@Composable
fun GridBoxes(
    labels: List<String>,
    columns: Int = 3,
    modifier: Modifier = Modifier,
    onBoxClick: (Int) -> Unit = {}
) {
    require(columns >= 1) { "columns must be >= 1" }

    val rows = (labels.size + columns - 1) / columns
    val chunks = labels.chunked(columns)

    Column(modifier = modifier.fillMaxWidth()) {
        for (rowIndex in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                val rowItems = chunks.getOrNull(rowIndex) ?: emptyList()
                rowItems.forEachIndexed { colIndex, label ->
                    val index = rowIndex * columns + colIndex // 0-based
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(90.dp)
                            .padding(4.dp)
                            .clickable { onBoxClick(index + 1) },
                        shape = RoundedCornerShape(10.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC))
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            Text(
                                text = label,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (rowItems.size < columns) {
                    val missing = columns - rowItems.size
                    repeat(missing) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}


// ---------- Pagination helper (reused) ----------
private sealed class PageItemAssetUnit {
    data class Page(val number: Int) : PageItemAssetUnit()
    object Ellipsis : PageItemAssetUnit()
}

private fun computePageItems(totalPages: Int, currentPage: Int): List<PageItemAssetUnit> {
    if (totalPages <= 7) {
        return (1..totalPages).map { PageItemAssetUnit.Page(it) }
    }

    val items = mutableListOf<PageItemAssetUnit>()
    items += PageItemAssetUnit.Page(1)

    val start = (currentPage - 1).coerceAtLeast(2)
    val end = (currentPage + 1).coerceAtMost(totalPages - 1)

    if (start > 2) {
        items += PageItemAssetUnit.Ellipsis
    } else {
        for (i in 2..start) items += PageItemAssetUnit.Page(i)
    }

    for (i in start..end) {
        if (i in 2 until totalPages) items += PageItemAssetUnit.Page(i)
    }

    if (end < totalPages - 1) {
        items += PageItemAssetUnit.Ellipsis
    } else {
        for (i in (end + 1) until totalPages) items += PageItemAssetUnit.Page(i)
    }

    items += PageItemAssetUnit.Page(totalPages)

    // remove accidental duplicates
    return items.fold(mutableListOf<PageItemAssetUnit>()) { acc, it ->
        val keep = when {
            acc.isEmpty() -> true
            acc.last() is PageItemAssetUnit.Page && it is PageItemAssetUnit.Page -> (acc.last() as PageItemAssetUnit.Page).number != (it as PageItemAssetUnit.Page).number
            acc.last() is PageItemAssetUnit.Ellipsis && it is PageItemAssetUnit.Ellipsis -> false
            else -> true
        }
        if (keep) acc += it
        acc
    }
}

@Composable
fun SimpleTextPaginationAssetUnit(
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
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Previous button
        TextButton(
            onClick = { if (cur > 1) onPageSelected(cur - 1) },
            enabled = cur > 1,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("‹ Sebelumnya", fontSize = 14.sp)
        }

        Spacer(Modifier.width(8.dp))

        // Page numbers — active pill + outlined for others
        items.forEach { item ->
            when (item) {
                is PageItemAssetUnit.Page -> {
                    val isActive = item.number == cur
                    if (isActive) {
                        Surface(
                            tonalElevation = 0.dp,
                            shape = RoundedCornerShape(12.dp),
                            color = activeColor.copy(alpha = 0.12f),
                            modifier = Modifier.padding(horizontal = 4.dp)
                        ) {
                            Text(
                                text = item.number.toString(),
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                color = activeColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = { onPageSelected(item.number) },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .height(36.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = inactiveColor)
                        ) {
                            Text(item.number.toString())
                        }
                    }
                }

                PageItemAssetUnit.Ellipsis -> {
                    Text(
                        text = "…",
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = inactiveColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Next button
        TextButton(
            onClick = { if (cur < total) onPageSelected(cur + 1) },
            enabled = cur < total,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.height(36.dp)
        ) {
            Text("Selanjutnya ›", fontSize = 14.sp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAssetUnit() {
    MaterialTheme {
        AssetUnit(items = sampleassetunit, modifier = Modifier.fillMaxWidth())
    }
}
