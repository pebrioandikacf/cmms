// File: app/src/main/java/com/ptpn/cmms/mechanic/AssetMechanic.kt
package com.ptpn.cmms.mechanic

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
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
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptpn.cmms.ui.component.CmmsCard
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

private val VIEW_COLOR = Color(0xFF1AA39D)
private val UPDATE_COLOR = Color(0xFFF28C6F)

data class AssetItem(val id: Int, val kode: String, val nama: String, val lokasi: String, val status: String)

// NOTE: made public so NavGraph / other screens can reuse same dummy data if needed
val dummyAssets = List(233) { i ->
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
    // states (compact names)
    var q by rememberSaveable { mutableStateOf("") }
    var perSel by rememberSaveable { mutableIntStateOf(10) }
    var showEntries by remember { mutableStateOf(false) }
    var page by rememberSaveable { mutableIntStateOf(1) }
    val entryOptions = listOf(10, 25, 50, 100)

    val cfg = LocalConfiguration.current
    val isLandscape = cfg.orientation == Configuration.ORIENTATION_LANDSCAPE

    // filter & paging
    val filtered = remember(q) {
        if (q.isBlank()) dummyAssets
        else {
            val s = q.lowercase()
            dummyAssets.filter { a ->
                a.nama.lowercase().contains(s) ||
                        a.kode.lowercase().contains(s) ||
                        a.lokasi.lowercase().contains(s) ||
                        a.status.lowercase().contains(s)
            }
        }
    }

    val perPage = max(1, perSel)
    val total = filtered.size
    val totalPages = max(1, ceil(total.toDouble() / perPage.toDouble()).toInt())

    LaunchedEffect(perPage, total) { page = page.coerceIn(1, totalPages) }

    val from = (page - 1) * perPage
    val displayed = remember(filtered, page, perPage) { if (filtered.isEmpty()) listOf() else filtered.drop(from).take(perPage) }
    val showingStart = if (total == 0) 0 else from + 1
    val showingEnd = if (total == 0) 0 else from + displayed.size

    // layout sizes
    val cNo = 56.dp; val cKat = 120.dp; val cNom = 120.dp; val cMes = 220.dp; val cArea = 120.dp; val cAct = 420.dp
    val tableW = cNo + cKat + cNom + cMes + cArea + cAct + 24.dp
    val hScroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Mekanik",
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = MaterialTheme.colorScheme.onPrimary) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Text("Data Assets", style = MaterialTheme.typography.titleLarge) }

            item {
                CmmsCard {
                    Column(Modifier.fillMaxWidth().animateContentSize()) {
                        // controls
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Show", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.width(8.dp))
                                Box {
                                    OutlinedButton(onClick = { showEntries = !showEntries }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(8.dp)) {
                                        Text("$perSel", fontSize = 13.sp); Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                    }
                                    DropdownMenu(expanded = showEntries, onDismissRequest = { showEntries = false }) {
                                        entryOptions.forEach { n -> DropdownMenuItem(text = { Text(n.toString()) }, onClick = { perSel = n; page = 1; showEntries = false }) }
                                    }
                                }
                                Spacer(Modifier.width(8.dp)); Text("entries", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            OutlinedTextField(
                                value = q,
                                onValueChange = { q = it; page = 1 },
                                placeholder = { Text("Cari nama / kode / lokasi...") },
                                singleLine = true,
                                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                                modifier = Modifier.height(44.dp).widthIn(min = 200.dp, max = 480.dp),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        Spacer(Modifier.height(12.dp))

                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text("Showing $showingStart to $showingEnd of $total entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { /* refresh */ }) { Icon(Icons.Default.Refresh, contentDescription = "Refresh") }
                            IconButton(onClick = { /* export */ }) { Icon(Icons.Default.Download, contentDescription = "Export") }
                        }

                        DividerSpacer()

                        // responsive area
                        BoxWithConstraints {
                            val wide = 900.dp; val phone = 420.dp; val useTable = maxWidth > phone
                            when {
                                !useTable -> {
                                    Column { displayed.forEachIndexed { i, a -> AssetCard("${showingStart + i}", a, onViewAsset, onUpdateAsset, isLandscape) }; if (displayed.isEmpty()) EmptyNotFoundAsset() }
                                }
                                maxWidth > wide -> {
                                    Column {
                                        HeaderRow(cNo, cKat, cNom, cMes, cArea, cAct)
                                        Column {
                                            displayed.forEachIndexed { i, a ->
                                                RowItem(showingStart + i, a, cNo, cKat, cNom, cMes, cArea, cAct, 240.dp, onViewAsset, onUpdateAsset)
                                                DividerSpacer()
                                            }
                                            if (displayed.isEmpty()) EmptyNotFound()
                                        }
                                    }
                                }
                                else -> {
                                    Column {
                                        Box(Modifier.horizontalScroll(hScroll)) { HeaderScrollable(tableW, cNo, cKat, cNom, cMes, cArea, cAct) }
                                        Box(Modifier.horizontalScroll(hScroll)) { Column(Modifier.width(tableW)) { displayed.forEachIndexed { i, a -> RowItem(showingStart + i, a, cNo, cKat, cNom, cMes, cArea, cAct, 220.dp, onViewAsset, onUpdateAsset) }; if (displayed.isEmpty()) EmptyNotFoundAsset() } }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item { DividerSpacer() }

            item {
                TableFooterCompact(
                    currentPage = page,
                    totalPages = totalPages,
                    perPage = perPage,
                    totalItems = total,
                    onPrevious = { if (page > 1) page-- },
                    onNext = { if (page < totalPages) page++ },
                    onPageClick = { p -> page = p }
                )
            }
        }
    }
}

@Composable private fun DividerSpacer() = HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = DividerDefaults.Thickness, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))

@Composable
private fun HeaderRow(cNo: Dp, cKat: Dp, cNom: Dp, cMes: Dp, cArea: Dp, cAct: Dp) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No", modifier = Modifier.width(cNo), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Kategori", modifier = Modifier.width(cKat), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Nomor Peralatan", modifier = Modifier.width(cNom), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Mesin", modifier = Modifier.width(cMes), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Area Mesin", modifier = Modifier.width(cArea), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(12.dp))
        Text("Aksi", modifier = Modifier.width(cAct), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun HeaderScrollable(tableW: Dp, cNo: Dp, cKat: Dp, cNom: Dp, cMes: Dp, cArea: Dp, cAct: Dp) {
    Row(
        modifier = Modifier
            .width(tableW)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("No", modifier = Modifier.width(cNo), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Kategori", modifier = Modifier.width(cKat), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Nomor Peralatan", modifier = Modifier.width(cNom), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Mesin", modifier = Modifier.width(cMes), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Text("Area Mesin", modifier = Modifier.width(cArea), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.width(12.dp))
        Text("Aksi", modifier = Modifier.width(cAct), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}


@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun RowItem(index: Int, asset: AssetItem, colNo: Dp, colKat: Dp, colNom: Dp, colMes: Dp, colArea: Dp, colAct: Dp, compactTh: Dp, onView: (Int)->Unit, onUpdate: (Int)->Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(index.toString(), modifier = Modifier.width(colNo), fontSize = 13.sp)
        Text("Mesin Produksi", modifier = Modifier.width(colKat), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(asset.kode, modifier = Modifier.width(colNom), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(asset.nama, modifier = Modifier.width(colMes), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(asset.lokasi, modifier = Modifier.width(colArea), fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        BoxWithConstraints(modifier = Modifier.width(colAct).padding(start = 12.dp)) {
            val compact = maxWidth < compactTh
            ActionButtons(compact, asset.id, onView, onUpdate)
        }
    }
}

@Composable
private fun ActionButtons(compact: Boolean, id: Int, onView: (Int)->Unit, onUpdate: (Int)->Unit) {
    val ctx = LocalContext.current

    // try to find asset info from dummyAssets for nicer URL (fallback to id)
    val asset = dummyAssets.find { it.id == id }
    val identifier = asset?.kode ?: id.toString()
    // build dummy URL — ganti sesuai endpoint server-mu nanti
    val qrUrl = "https://118.97.163.52:8296/index.php/peralatan/viewQR/$identifier"

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        if (compact) {
            // compact: icon-only
            IconButton(onClick = {
                // open WebView activity to show QR/page
                val intent = Intent(ctx, ViewQRMechanic::class.java).apply {
                    putExtra(ViewQRMechanic.EXTRA_URL, qrUrl)
                    putExtra(ViewQRMechanic.EXTRA_LABEL, identifier)
                }
                ctx.startActivity(intent)
            }, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Default.QrCode, contentDescription = "QR")
            }

            IconButton(onClick = { onView(id) }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Visibility, contentDescription = "Lihat") }
            IconButton(onClick = { onUpdate(id) }, modifier = Modifier.size(40.dp)) { Icon(Icons.Default.Edit, contentDescription = "Update") }
        } else {
            // normal: labeled buttons
            FilledTonalButton(onClick = {
                val intent = Intent(ctx, ViewQRMechanic::class.java).apply {
                    putExtra(ViewQRMechanic.EXTRA_URL, qrUrl)
                    putExtra(ViewQRMechanic.EXTRA_LABEL, identifier)
                }
                ctx.startActivity(intent)
            }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 80.dp)) {
                Icon(Icons.Default.QrCode, contentDescription = "QR"); Spacer(Modifier.width(6.dp)); Text("QR Code")
            }

            Button(onClick = { onView(id) }, modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 64.dp), colors = ButtonDefaults.buttonColors(containerColor = VIEW_COLOR)) {
                Icon(Icons.Default.Visibility, contentDescription = "Lihat"); Spacer(Modifier.width(6.dp)); Text("Lihat")
            }

            Button(
                onClick = { onUpdate(id) },
                modifier = Modifier.height(40.dp).defaultMinSize(minWidth = 84.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UPDATE_COLOR)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Update")
                Spacer(Modifier.width(6.dp))
                Text("Update")
            }
        }
    }
}



@Composable
private fun EmptyNotFoundAsset() = Box(Modifier.fillMaxWidth().padding(36.dp), contentAlignment = Alignment.Center) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        Text("Tidak ada item ditemukan", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun AssetCard(
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
                val ctx = LocalContext.current
                IconButton(onClick = {
                    val identifier = asset.kode.ifBlank { asset.id.toString() }
                    val qrUrl = "https://118.97.163.52:8296/index.php/peralatan/viewQR/$identifier"
                    val intent = Intent(ctx, ViewQRMechanic::class.java).apply {
                        putExtra(ViewQRMechanic.EXTRA_URL, qrUrl)
                        putExtra(ViewQRMechanic.EXTRA_LABEL, identifier)
                    }
                    ctx.startActivity(intent)
                }, modifier = Modifier.size(40.dp)) {
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

                // UPDATE button standardized to your requested style
                Button(
                    onClick = { onUpdate(asset.id) },
                    modifier = Modifier
                        .height(40.dp)
                        .defaultMinSize(minWidth = 84.dp),
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
private fun TableFooterCompact(currentPage: Int, totalPages: Int, perPage: Int, totalItems: Int, onPrevious: ()->Unit, onNext: ()->Unit, onPageClick: (Int)->Unit) {
    val start = if (totalItems == 0) 0 else (currentPage - 1) * perPage + 1
    val end = min(totalItems, currentPage * perPage)
    Column(Modifier.fillMaxWidth().padding(top = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Text("Showing $start to $end of $totalItems entries", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
        val footerScroll = rememberScrollState()
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Row(Modifier.horizontalScroll(footerScroll), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onPrevious, enabled = currentPage > 1, modifier = Modifier.height(36.dp)) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous"); Spacer(Modifier.width(6.dp)); Text("Previous", fontSize = 12.sp) }
                val pages = remember(currentPage, totalPages) { val w = 5; val s = max(1, currentPage - w / 2); val e = min(totalPages, s + w - 1); (s..e).toList() }
                pages.forEach { p -> val sel = p == currentPage; Button(onClick = { if (!sel) onPageClick(p) }, colors = if (sel) ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) else ButtonDefaults.outlinedButtonColors(), modifier = Modifier.height(36.dp).width(36.dp), contentPadding = PaddingValues(0.dp)) { Text(p.toString(), fontSize = 12.sp, color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface) } }
                OutlinedButton(onClick = onNext, enabled = currentPage < totalPages, modifier = Modifier.height(36.dp)) { Text("Next", fontSize = 12.sp) }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360) @Composable fun AssetMechanicPreviewPhone() = AssetMechanicScreen()
@Preview(showBackground = true, widthDp = 1000) @Composable fun AssetMechanicPreviewWide() = AssetMechanicScreen()
