// File: MechanicDashboard.kt
package com.ptpn.cmms.mechanic

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
                    Text("Navigasi", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                }
                Spacer(Modifier.height(8.dp))

                // Ganti bagian listOf(...) lama dengan ini:
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
                                    // Dashboard -> ...
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
                    title = { Text("Dashboard Mekanik") },
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
        MechanicTable(data, onViewDetail)
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun MechanicTable(data: List<MechanicAssetData>, onViewDetail: (Int) -> Unit) {
    var search by rememberSaveable { mutableStateOf("") }
    var entries by rememberSaveable { mutableIntStateOf(10) }
    var page by rememberSaveable { mutableIntStateOf(1) }

    val filtered = data.filter { it.deskripsi.contains(search, true) || it.tanggal.contains(search, true) || it.statusText().contains(search, true) }
    val totalPages = max(1, (filtered.size + entries - 1) / entries)
    if (page > totalPages) page = totalPages
    val start = (page - 1) * entries
    val pageItems = filtered.drop(start).take(entries)
    val tableWidth = 660.dp
    val actionCol = 120.dp

    val scrollState = rememberScrollState()

    CmmsCard {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(Modifier.horizontalScroll(scrollState)) {
                Row(
                    Modifier
                        .width(tableWidth)
                        .background(Color(0xFFE0E0E0))
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    tableCell("No", 48.dp, header = true)
                    tableCell("Tanggal", 120.dp, header = true)
                    tableCell("Deskripsi", 260.dp, header = true, alignStart = true)
                    tableCell("Status", 120.dp, header = true)
                    tableCell("Action", actionCol, header = true)
                }
            }

            pageItems.forEach { item ->
                Box(Modifier.horizontalScroll(scrollState)) {
                    Row(
                        Modifier
                            .width(tableWidth)
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        tableCell(item.no.toString(), 48.dp)
                        tableCell(item.tanggal, 120.dp)
                        tableCell(item.deskripsi, 260.dp, alignStart = true)
                        tableCell(item.statusText(), 120.dp, color = if (item.sudah) Color(0xFF2E7D32) else Color(0xFFD32F2F))
                        Row(Modifier.width(actionCol), horizontalArrangement = Arrangement.Center) {
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

            TableFooter(
                page, totalPages, if (filtered.isEmpty()) 0 else start + 1, filtered.size,
                { if (page > 1) page-- }, { if (page < totalPages) page++ }, { p -> page = p }
            )
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

@Preview(showBackground = true, widthDp = 800)
@Composable
fun MechanicDashboardPreview() {
    // preview: berikan lambda dummy agar preview bisa dibuild
    MechanicDashboard(onLogout = {}, onAssetsClick = {}, onViewDetail = {})
}
