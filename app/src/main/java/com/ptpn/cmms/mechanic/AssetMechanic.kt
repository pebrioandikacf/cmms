// File: app/src/main/java/com/ptpn/cmms/mechanic/AssetMechanic.kt
package com.ptpn.cmms.mechanic

import android.annotation.SuppressLint
import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptpn.cmms.ui.component.CmmsCard
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private val VIEW_COLOR = Color(0xFF1AA39D)
private val UPDATE_COLOR = Color(0xFFF28C6F)

data class AssetItem(val id: Int, val kode: String, val nama: String, val lokasi: String, val status: String)

private val dummyAssets = List(233) { i ->
    AssetItem(
        id = i + 1,
        kode = "1000110%03d".format(i + 1),
        nama = "Mesin Example ${i + 1}",
        lokasi = listOf("AREA BENGKEL MEKANIK", "Pabrik A", "Gudang")[i % 3],
        status = listOf("Baik", "Butuh Perbaikan", "Dalam Pengawasan")[i % 3]
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetMechanicScreen(
    onBack: () -> Unit = {},
    onViewAsset: (Int) -> Unit = {},
    onUpdateAsset: (Int) -> Unit = {}
) {
    // state
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var selectedEntries by rememberSaveable { mutableIntStateOf(10) }
    var showDropdown by remember { mutableStateOf(false) }
    val entriesOptions = listOf(10, 25, 50, 100)
    var currentPage by rememberSaveable { mutableIntStateOf(1) }

    // deteksi orientasi layar (untuk penyesuaian kalau perlu)
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // filter + paging
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) dummyAssets
        else {
            val q = searchQuery.lowercase()
            dummyAssets.filter { a ->
                a.nama.lowercase().contains(q) ||
                        a.kode.lowercase().contains(q) ||
                        a.lokasi.lowercase().contains(q) ||
                        a.status.lowercase().contains(q)
            }
        }
    }

    val perPage = max(1, selectedEntries)
    val totalFiltered = filtered.size
    val totalPages = max(1, ceil(totalFiltered.toDouble() / perPage.toDouble()).toInt())

    LaunchedEffect(perPage, totalFiltered) {
        if (currentPage > totalPages) currentPage = totalPages
        if (currentPage < 1) currentPage = 1
    }

    val fromIndex = (currentPage - 1) * perPage
    val displayed = remember(filtered, currentPage, perPage) {
        if (filtered.isEmpty()) listOf() else filtered.drop(fromIndex).take(perPage)
    }
    val showingStart = if (totalFiltered == 0) 0 else fromIndex + 1
    val showingEnd = if (totalFiltered == 0) 0 else fromIndex + displayed.size

    // layout constants
    val colNo = 56.dp
    val colKategori = 120.dp
    val colNomor = 120.dp
    val colMesin = 220.dp
    val colArea = 120.dp
    val colAction = 420.dp // <-- diperbesar agar ruang aksi lebih longgar
    val tableWidth = colNo + colKategori + colNomor + colMesin + colArea + colAction + 24.dp

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Mekanik", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                // removed manual view mode toggle: UI is now always auto (responsive)
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // Use a single LazyColumn so the whole screen scrolls vertically (header + table + footer)
        val tableHScroll = rememberScrollState() // shared horizontal ScrollState for header + rows
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text("Data Assets", style = MaterialTheme.typography.titleLarge)
            }

            item {
                CmmsCard {
                    Column(Modifier.fillMaxWidth().animateContentSize()) {
                        // controls
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Show", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(8.dp))
                                Box {
                                    OutlinedButton(onClick = { showDropdown = !showDropdown }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(8.dp)) {
                                        Text("$selectedEntries", fontSize = 13.sp)
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Open entries")
                                    }
                                    DropdownMenu(expanded = showDropdown, onDismissRequest = { showDropdown = false }) {
                                        entriesOptions.forEach { n ->
                                            DropdownMenuItem(text = { Text(n.toString()) }, onClick = {
                                                selectedEntries = n
                                                currentPage = 1
                                                showDropdown = false
                                            })
                                        }
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Text("entries", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it; currentPage = 1 },
                                placeholder = { Text("Cari nama / kode / lokasi...") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                modifier = Modifier.height(44.dp).widthIn(min = 200.dp, max = 480.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Showing $showingStart to $showingEnd of $totalFiltered entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { /* refresh */ }) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }
                            IconButton(onClick = { /* export */ }) { Icon(Icons.Default.Download, contentDescription = "Export") }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 10.dp),
                            thickness = DividerDefaults.Thickness,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                        )

                        // responsive area: always auto (no manual selection)
                        BoxWithConstraints {
                            val wideBreakpoint = 900.dp
                            val phoneBreakpoint = 420.dp

                            // auto behavior: small screens -> cards, others -> table
                            val useTable = maxWidth > phoneBreakpoint

                            if (useTable) {
                                if (maxWidth > wideBreakpoint) {
                                    // wide: header + rows (no horizontal scroll)
                                    Column {
                                        Row(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 12.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text("No", modifier = Modifier.width(colNo), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Kategori", modifier = Modifier.width(colKategori), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Nomor Peralatan", modifier = Modifier.width(colNomor), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Mesin", modifier = Modifier.width(colMesin), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Text("Area Mesin", modifier = Modifier.width(colArea), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            Spacer(Modifier.width(12.dp))
                                            Text("Aksi", modifier = Modifier.width(colAction), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                        }

                                        Column {
                                            displayed.forEachIndexed { idx, asset ->
                                                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                    Text("${showingStart + idx}", modifier = Modifier.width(colNo), fontSize = 13.sp)
                                                    Text("Mesin Produksi", modifier = Modifier.width(colKategori), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(asset.kode, modifier = Modifier.width(colNomor), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(asset.nama, modifier = Modifier.width(colMesin), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                    Text(asset.lokasi, modifier = Modifier.width(colArea), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                                                    BoxWithConstraints(modifier = Modifier.width(colAction).padding(start = 12.dp)) {
                                                        val compact = maxWidth < 240.dp // <-- threshold diturunkan
                                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                            if (compact) {
                                                                IconButton(onClick = { /* QR */ }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.QrCode, contentDescription = "QR") }
                                                                IconButton(onClick = { onViewAsset(asset.id) }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Visibility, contentDescription = "Lihat") }
                                                                IconButton(onClick = { onUpdateAsset(asset.id) }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Edit, contentDescription = "Update") }
                                                            } else {
                                                                FilledTonalButton(onClick = { /* QR */ }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 80.dp)) {
                                                                    Icon(Icons.Default.QrCode, contentDescription = "QR")
                                                                    Spacer(Modifier.width(6.dp))
                                                                    Text("QR Code")
                                                                }
                                                                Button(onClick = { onViewAsset(asset.id) }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 64.dp), colors = ButtonDefaults.buttonColors(containerColor = VIEW_COLOR)) {
                                                                    Icon(Icons.Default.Visibility, contentDescription = "Lihat")
                                                                    Spacer(Modifier.width(6.dp))
                                                                    Text("Lihat")
                                                                }
                                                                // pastikan teks 'Update' penuh
                                                                Button(onClick = { onUpdateAsset(asset.id) }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 84.dp), colors = ButtonDefaults.buttonColors(containerColor = UPDATE_COLOR)) {
                                                                    Icon(Icons.Default.Edit, contentDescription = "Update")
                                                                    Spacer(Modifier.width(6.dp))
                                                                    Text("Update")
                                                                }
                                                            }
                                                        }
                                                    }
                                                }

                                                HorizontalDivider(
                                                    Modifier,
                                                    DividerDefaults.Thickness,
                                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                                                )
                                            }

                                            if (displayed.isEmpty()) {
                                                Box(modifier = Modifier.fillMaxWidth().padding(36.dp), contentAlignment = Alignment.Center) {
                                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                        Spacer(Modifier.height(8.dp))
                                                        Text("Tidak ada item ditemukan", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    // tablet / narrow: header + rows both use the same horizontal scroll state
                                    Column {
                                        Box(modifier = Modifier.horizontalScroll(tableHScroll)) {
                                            Row(modifier = Modifier.width(tableWidth).background(MaterialTheme.colorScheme.surfaceVariant).padding(vertical = 12.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                Text("No", modifier = Modifier.width(colNo), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                Text("Kategori", modifier = Modifier.width(colKategori), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                Text("Nomor Peralatan", modifier = Modifier.width(colNomor), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                Text("Mesin", modifier = Modifier.width(colMesin), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                Text("Area Mesin", modifier = Modifier.width(colArea), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                                Spacer(Modifier.width(12.dp))
                                                Text("Aksi", modifier = Modifier.width(colAction), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                            }
                                        }

                                        Box(modifier = Modifier.horizontalScroll(tableHScroll)) {
                                            Column(modifier = Modifier.width(tableWidth)) {
                                                displayed.forEachIndexed { idx, asset ->
                                                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                        Text("${showingStart + idx}", modifier = Modifier.width(colNo), fontSize = 13.sp)
                                                        Text("Mesin Produksi", modifier = Modifier.width(colKategori), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text(asset.kode, modifier = Modifier.width(colNomor), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text(asset.nama, modifier = Modifier.width(colMesin), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                                        Text(asset.lokasi, modifier = Modifier.width(colArea), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)

                                                        // action cell: fixed width and responsive (icon-only if really narrow)
                                                        rememberScrollState()
                                                        BoxWithConstraints(modifier = Modifier.width(colAction).padding(start = 8.dp)) {
                                                            val compact = maxWidth < 220.dp // <-- threshold diturunkan
                                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                                                if (compact) {
                                                                    IconButton(onClick = { /* QR */ }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.QrCode, contentDescription = "QR") }
                                                                    IconButton(onClick = { onViewAsset(asset.id) }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Visibility, contentDescription = "Lihat") }
                                                                    IconButton(onClick = { onUpdateAsset(asset.id) }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Edit, contentDescription = "Update") }
                                                                } else {
                                                                    FilledTonalButton(onClick = { /* QR */ }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 88.dp)) {
                                                                        Icon(Icons.Default.QrCode, contentDescription = "QR")
                                                                        Spacer(Modifier.width(8.dp))
                                                                        Text("QR Code")
                                                                    }
                                                                    Button(onClick = { onViewAsset(asset.id) }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 68.dp), colors = ButtonDefaults.buttonColors(containerColor = VIEW_COLOR)) {
                                                                        Icon(Icons.Default.Visibility, contentDescription = "Lihat")
                                                                        Spacer(Modifier.width(8.dp))
                                                                        Text("Lihat")
                                                                    }
                                                                    // tetap tampil lengkap (icon + "Update")
                                                                    Button(onClick = { onUpdateAsset(asset.id) }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 88.dp), colors = ButtonDefaults.buttonColors(containerColor = UPDATE_COLOR)) {
                                                                        Icon(Icons.Default.Edit, contentDescription = "Update")
                                                                        Spacer(Modifier.width(8.dp))
                                                                        Text("Update")
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                // CARD mode for small screens
                                Column {
                                    displayed.forEachIndexed { idx, asset ->
                                        AssetCard(indexLabel = "${showingStart + idx}", asset = asset, onView = onViewAsset, onUpdate = onUpdateAsset, isLandscape = isLandscape)
                                    }

                                    if (displayed.isEmpty()) {
                                        Box(modifier = Modifier.fillMaxWidth().padding(36.dp), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                                Spacer(Modifier.height(8.dp))
                                                Text("Tidak ada item ditemukan", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }
                        } // BoxWithConstraints
                    } // Column inside CmmsCard
                } // CmmsCard
            }

            item {
                HorizontalDivider(
                    Modifier,
                    DividerDefaults.Thickness,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
                )
            }

            // pagination footer as item so it scrolls with page
            item {
                TableFooterCompact(
                    currentPage = currentPage,
                    totalPages = totalPages,
                    perPage = perPage,
                    totalItems = totalFiltered,
                    onPrevious = { if (currentPage > 1) currentPage -= 1 },
                    onNext = { if (currentPage < totalPages) currentPage += 1 },
                    onPageClick = { page -> currentPage = page }
                )
            }
        } // LazyColumn
    } // Scaffold
}

@Composable
private fun AssetCard(
    indexLabel: String,
    asset: AssetItem,
    onView: (Int) -> Unit,
    onUpdate: (Int) -> Unit,
    isLandscape: Boolean = false
) {
    // NOTE: harus @Composable karena menggunakan MaterialTheme.colorScheme
    @Composable
    fun statusColor(status: String): Color = when (status.lowercase()) {
        "baik" -> Color(0xFF2ECC71)
        "butuh perbaikan" -> Color(0xFFF39C12)
        "dalam pengawasan" -> Color(0xFF3498DB)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // baris atas: index, nama (judul), status chip
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(indexLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.width(8.dp))

                Text(
                    asset.nama,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(8.dp))

                // status chip kecil
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .background(color = statusColor(asset.status), shape = RoundedCornerShape(9.dp))
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(asset.status, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
            }

            // baris info: kode + lokasi
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Kode: ${asset.kode}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Lokasi: ${asset.lokasi}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // actions: QR | Lihat | Update (rata kanan)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { /* QR action */ }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Default.QrCode, contentDescription = "QR")
                }

                Spacer(Modifier.width(6.dp))

                FilledTonalButton(
                    onClick = { onView(asset.id) },
                    modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 76.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Lihat")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lihat")
                }

                Spacer(Modifier.width(8.dp))

                Button(
                    onClick = { onUpdate(asset.id) },
                    modifier = Modifier.height(40.dp).defaultMinSize(minWidth = if (isLandscape) 110.dp else 96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UPDATE_COLOR)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Update")
                    Spacer(Modifier.width(8.dp))
                    Text("Update")
                }
            }
        }
    }
}

@Composable
private fun TableFooterCompact(
    currentPage: Int,
    totalPages: Int,
    perPage: Int,
    totalItems: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPageClick: (Int) -> Unit
) {
    val start = if (totalItems == 0) 0 else (currentPage - 1) * perPage + 1
    val end = min(totalItems, currentPage * perPage)

    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // baris atas: teks "Showing ... of ..."
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("Showing $start to $end of $totalItems entries", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // baris bawah: pagination (scrollable, diratakan ke kanan)
        val footerScroll = rememberScrollState()
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(1f)) // dorong pagination ke kanan
            Row(
                modifier = Modifier.horizontalScroll(footerScroll),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(onClick = onPrevious, enabled = currentPage > 1, modifier = Modifier.height(36.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous")
                    Spacer(Modifier.width(6.dp))
                    Text("Previous", fontSize = 12.sp)
                }

                val pages = remember(currentPage, totalPages) {
                    val window = 5
                    val startPage = max(1, currentPage - window / 2)
                    val endPage = min(totalPages, startPage + window - 1)
                    (startPage..endPage).toList()
                }

                pages.forEach { p ->
                    val selected = p == currentPage
                    Button(
                        onClick = { if (!selected) onPageClick(p) },
                        colors = if (selected) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.height(36.dp).width(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(p.toString(), fontSize = 12.sp, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
                    }
                }

                OutlinedButton(onClick = onNext, enabled = currentPage < totalPages, modifier = Modifier.height(36.dp)) {
                    Text("Next", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
fun AssetMechanicPreviewPhone() {
    AssetMechanicScreen(onBack = {}, onViewAsset = {}, onUpdateAsset = {})
}

@Preview(showBackground = true, widthDp = 1000)
@Composable
fun AssetMechanicPreviewWide() {
    AssetMechanicScreen(onBack = {}, onViewAsset = {}, onUpdateAsset = {})
}
