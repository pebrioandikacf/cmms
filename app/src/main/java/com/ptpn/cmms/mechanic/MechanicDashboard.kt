// File: MechanicDashboard.kt
package com.ptpn.cmms.mechanic

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptpn.cmms.unit.StatusCard
import com.ptpn.cmms.ui.component.CmmsCard
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min

data class MechanicAssetData(val no: Int, val tanggal: String, val deskripsi: String, val sudah: Boolean)
private fun MechanicAssetData.statusText() = if (sudah) "Sudah Dikerjakan" else "Belum Dikerjakan"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicDashboard(
    onLogout: () -> Unit = {},
    onAssetsClick: () -> Unit = {},
    onViewDetail: (Int) -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(16.dp)
                ) {
                    Text(
                        "NAVIGASI", color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.height(8.dp))

                val items = listOf(
                    "Dashboard" to Icons.Filled.Home,
                    "Assets" to Icons.AutoMirrored.Filled.List,
                    "Logout" to Icons.AutoMirrored.Filled.ExitToApp
                )

                items.forEach { (t, i) ->
                    NavigationDrawerItem(
                        label = { Text(t) },
                        icon = { Icon(i, contentDescription = null) },
                        selected = false,
                        onClick = {
                            scope.launch {
                                drawerState.close()
                                when (t) {
                                    "Assets" -> onAssetsClick()
                                    "Logout" -> onLogout()
                                }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "CMMS",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
                )
            }
        ) { padding ->
            MechanicDashboardContent(Modifier.padding(padding), onViewDetail)
        }
    }
}

@Composable
fun MechanicDashboardContent(
    modifier: Modifier = Modifier,
    onViewDetail: (Int) -> Unit,
    maintenanceItems: List<MechanicAssetData> = dummyMaintenanceData,
    repairItems: List<MechanicAssetData> = dummyRepairData
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.titleLarge)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatusCard(
                "11",
                "Stasiun",
                Icons.Filled.CalendarToday,
                footerColor = Color(0xFFE53935),
                changeText = "%change",
                modifier = Modifier.weight(1f).height(110.dp)
            )
            StatusCard(
                "233",
                "Peralatan",
                Icons.Filled.Download,
                footerColor = Color(0xFF00ACC1),
                changeText = "%change",
                modifier = Modifier.weight(1f).height(110.dp)
            )
        }

        MechanicSection("List Pemeliharaan Asset Belum Dikerjakan", maintenanceItems.filter { !it.sudah }, onViewDetail)
        MechanicSection("List Perbaikan Asset Belum Dikerjakan", repairItems.filter { !it.sudah }, onViewDetail)
    }
}

@Composable
private fun MechanicSection(title: String, data: List<MechanicAssetData>, onViewDetail: (Int) -> Unit) {
    Column(Modifier.fillMaxWidth()) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        MechanicCardList(data, onViewDetail)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MechanicCardList(data: List<MechanicAssetData>, onViewDetail: (Int) -> Unit) {
    var q by rememberSaveable { mutableStateOf("") }
    var entries by rememberSaveable { mutableIntStateOf(10) }
    var showEntries by remember { mutableStateOf(false) }
    var page by rememberSaveable { mutableIntStateOf(1) }

    val filtered = data.filter { it.deskripsi.contains(q, true) || it.tanggal.contains(q, true) || it.statusText().contains(q, true) }
    val perPage = max(1, entries)
    val total = filtered.size
    val totalPages = max(1, (total + perPage - 1) / perPage)
    if (page > totalPages) page = totalPages
    val startIndex = (page - 1) * perPage
    val pageItems = filtered.drop(startIndex).take(perPage)
    val showingFrom = if (total == 0) 0 else startIndex + 1

    CmmsCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            // responsive controls: stack on narrow screens
            BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                val isPhone = maxWidth < 480.dp

                if (isPhone) {
                    // stacked layout for phone
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Show", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Box {
                                OutlinedButton(onClick = { showEntries = !showEntries }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(8.dp)) {
                                    Text("$entries", fontSize = 13.sp); Spacer(Modifier.width(6.dp)); Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(expanded = showEntries, onDismissRequest = { showEntries = false }) {
                                    listOf(5, 10, 25, 50).forEach { n -> DropdownMenuItem(text = { Text(n.toString()) }, onClick = { entries = n; page = 1; showEntries = false }) }
                                }
                            }
                            Spacer(Modifier.width(8.dp))
                            Text("entries", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        OutlinedTextField(
                            value = q,
                            onValueChange = { q = it; page = 1 },
                            placeholder = { Text("Cari deskripsi / tanggal / status...") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            modifier = Modifier
                                .height(52.dp)
                                .fillMaxWidth(),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                } else {
                    // wide layout: controls on one row
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Show", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.width(8.dp))
                            Box {
                                OutlinedButton(onClick = { showEntries = !showEntries }, modifier = Modifier.height(40.dp), shape = RoundedCornerShape(8.dp)) {
                                    Text("$entries", fontSize = 13.sp); Spacer(Modifier.width(6.dp)); Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                                DropdownMenu(expanded = showEntries, onDismissRequest = { showEntries = false }) {
                                    listOf(5, 10, 25, 50).forEach { n -> DropdownMenuItem(text = { Text(n.toString()) }, onClick = { entries = n; page = 1; showEntries = false }) }
                                }
                            }
                            Spacer(Modifier.width(8.dp)); Text("entries", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        OutlinedTextField(
                            value = q,
                            onValueChange = { q = it; page = 1 },
                            placeholder = { Text("Cari deskripsi / tanggal / status...") },
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                            modifier = Modifier.height(52.dp).widthIn(min = 200.dp, max = 480.dp),
                            textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                Modifier.padding(vertical = 8.dp),
                DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f)
            )

            // Card list area (cards fill available width)
            if (pageItems.isEmpty()) {
                EmptyNotFound()
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    pageItems.forEachIndexed { idx, item ->
                        MechanicAssetCard("${showingFrom + idx}", item, onViewDetail)
                    }
                }
            }

            // Footer / Pagination (reuse TableFooter)
            TableFooter(
                page,
                totalPages,
                showingFrom,
                total,
                { if (page > 1) page-- },
                { if (page < totalPages) page++ },
                { p -> page = p }
            )
        }
    }
}

@Composable
fun EmptyNotFound() = Box(
    Modifier.fillMaxWidth().padding(36.dp),
    contentAlignment = Alignment.Center
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Tidak ada item ditemukan",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun MechanicAssetCard(indexLabel: String, item: MechanicAssetData, onViewDetail: (Int) -> Unit) {
    // status color small function
    @Composable
    fun statusColor(sudah: Boolean): Color = if (sudah) Color(0xFF2ECC71) else Color(0xFFD32F2F)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(indexLabel, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.deskripsi, style = MaterialTheme.typography.titleSmall, maxLines = 1)
                    Spacer(Modifier.height(4.dp))
                    Text(item.tanggal, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(Modifier.width(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(14.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Box(modifier = Modifier.size(9.dp).background(color = statusColor(item.sudah), shape = RoundedCornerShape(9.dp)))
                    Spacer(Modifier.width(8.dp))
                    Text(item.statusText(), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { onViewDetail(item.no) },
                    modifier = Modifier.height(36.dp),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = null, tint = Color.White)
                    Spacer(Modifier.width(6.dp))
                    Text("Lihat", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun tableCell(text: String, width: Dp, color: Color = Color.Unspecified, header: Boolean = false, alignStart: Boolean = false) {
    Box(Modifier.width(width).padding(horizontal = 6.dp), contentAlignment = if (alignStart) Alignment.CenterStart else Alignment.Center) {
        Text(
            text,
            fontSize = 12.sp,
            fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
            color = if (color == Color.Unspecified) Color.Black else color
        )
    }
}

@Composable
private fun TableFooter(
    currentPage: Int, totalPages: Int, showingFrom: Int,
    totalEntries: Int, onPrevious: () -> Unit, onNext: () -> Unit, onPageClick: (Int) -> Unit
) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text("Showing $showingFrom of $totalEntries entries", fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.border(1.dp, Color.Gray).height(28.dp).width(80.dp).clickable(enabled = currentPage > 1) { onPrevious() }, color = Color.Transparent) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Previous", fontSize = 12.sp) }
            }
            Spacer(Modifier.width(8.dp))
            val pages = run { val s = max(1, currentPage - 1); val e = min(totalPages, s + 2); (s..e).toList() }
            pages.forEach { p ->
                val selected = p == currentPage
                Surface(modifier = Modifier.border(1.dp, Color.Gray).background(if (selected) Color(0xFF00AFAF) else Color.Transparent).height(28.dp).width(32.dp).clickable { onPageClick(p) }, color = Color.Transparent) {
                    Box(Modifier.fillMaxSize(), Alignment.Center) { Text("$p", fontSize = 12.sp, color = if (selected) Color.White else Color.Black) }
                }
                Spacer(Modifier.width(6.dp))
            }
            Surface(modifier = Modifier.border(1.dp, Color.Gray).height(28.dp).width(80.dp).clickable(enabled = currentPage < totalPages) { onNext() }, color = Color.Transparent) {
                Box(Modifier.fillMaxSize(), Alignment.Center) { Text("Next", fontSize = 12.sp) }
            }
        }
    }
}

private val dummyMaintenanceData = listOf(
    MechanicAssetData(1, "01 Juni 2025", "Preventive WHEEL LOADER CAT 914 G", false),
    MechanicAssetData(2, "02 Juni 2025", "Preventive Screw Press No:2", false),
    MechanicAssetData(3, "03 Juni 2025", "Preventive Screw Press No:3", true),
    MechanicAssetData(4, "04 Juni 2025", "Preventive WHEEL LOADER CAT 914 G", true)
)

private val dummyRepairData = listOf(
    MechanicAssetData(1, "05 Juni 2025", "Perbaikan Gearbox Press A", false),
    MechanicAssetData(2, "06 Juni 2025", "Perbaikan Conveyor Loader", true)
)

@Preview(showBackground = true, widthDp = 360)
@Composable
fun MechanicDashboardPreviewPhone() {
    MechanicDashboard(onLogout = {}, onAssetsClick = {}, onViewDetail = {})
}

@Preview(showBackground = true, widthDp = 800)
@Composable
fun MechanicDashboardPreviewWide() {
    MechanicDashboard(onLogout = {}, onAssetsClick = {}, onViewDetail = {})
}
