// File: AppDrawer.kt
package com.ptpn.cmms

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptpn.cmms.ui.theme.CmmsTheme
import kotlinx.coroutines.launch

/** Dummy Data for Dashboard cards */
private val dummyStats = listOf(
    Pair("Stasiun", 11),
    Pair("Peralatan", 233)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawer(onLogout: () -> Unit = {}) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDataMasterSub by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                DrawerHeader()
                Spacer(Modifier.height(8.dp))

                // Dashboard biasa
                DrawerItem("Dashboard", Icons.Filled.Home) { /* TODO */ }

                // Data Master dengan icon panah
                NavigationDrawerItem(
                    label = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Data Master")
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = if (showDataMasterSub)
                                    Icons.Filled.KeyboardArrowUp
                                else
                                    Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                    selected = showDataMasterSub,
                    onClick = { showDataMasterSub = !showDataMasterSub },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                // Submenu: Data Mekanik (indentasi)
                if (showDataMasterSub) {
                    DrawerItem(
                        title = "Data Mekanik",
                        icon = Icons.Filled.Person,
                        modifier = Modifier.padding(start = 32.dp)
                    ) { /* TODO */ }
                }

                // Item lain
                DrawerItem("Assets", Icons.AutoMirrored.Filled.List) { /* TODO */ }
                DrawerItem("Jam Jalan Peralatan", Icons.Filled.Schedule) { /* TODO */ }
                DrawerItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) { onLogout() }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard CMMS") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) {
            DashboardContent(modifier = Modifier.padding(it))
        }
    }
}

@Composable
fun DrawerHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .padding(16.dp)
    ) {
        Text("Navigasi", color = Color.White, style = MaterialTheme.typography.headlineSmall)
    }
}

@Composable
fun DrawerItem(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(title) },
        icon = { Icon(icon, contentDescription = null) },
        selected = false,
        onClick = onClick,
        modifier = modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
    )
}

@Composable
fun DashboardContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // scroll untuk seluruh screen
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header & status cards (tidak berubah)
        Text("Dashboard", style = MaterialTheme.typography.titleLarge)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard("11", "Stasiun", Color(0xFFE53935), Modifier.weight(1f).height(100.dp))
            StatusCard("233", "Peralatan", Color(0xFF00ACC1), Modifier.weight(1f).height(100.dp))
        }

        // Semua section akan selalu dirender (scrollable)
        Section("List Pemeliharaan Asset Belum Dikerjakan", dummyMaintenanceData.filter { !it.sudah })
        Section("List Pemeliharaan Asset Sudah Dikerjakan", dummyMaintenanceData.filter { it.sudah })
        Section("List Perbaikan Asset Belum Dikerjakan", dummyRepairData.filter { !it.sudah })
        Section("List Perbaikan Asset Sudah Dikerjakan", dummyRepairData.filter { it.sudah })
    }
}

@Composable
fun StatusCard(count: String, label: String, bgColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(count, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, color = Color.White)
        }
    }
}

@Composable
fun Section(title: String, data: List<AssetData>) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    MaintenanceTable(data)
}

@Composable
fun MaintenanceTable(data: List<AssetData>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        // Header row
        Box(modifier = Modifier.horizontalScroll(scrollState)) {
            Row(
                modifier = Modifier
                    .width(580.dp)
                    .background(Color(0xFFB0BEC5))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TableCell("No", 40.dp, isHeader = true)
                TableCell("Tanggal", 100.dp, isHeader = true)
                TableCell("Deskripsi", 200.dp, isHeader = true)
                TableCell("Status", 120.dp, isHeader = true)
                TableCell("Action", 120.dp, isHeader = true)
            }
        }

        // Data rows
        data.forEach { item ->
            Box(modifier = Modifier.horizontalScroll(scrollState)) {
                Row(
                    modifier = Modifier
                        .width(580.dp)
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TableCell(item.no.toString(), 40.dp)
                    TableCell(item.tanggal, 100.dp)
                    TableCell(item.deskripsi, 200.dp)
                    TableCell(
                        text = if (item.sudah) "Sudah Dikerjakan" else "Belum Dikerjakan",
                        width = 120.dp,
                        color = if (item.sudah) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )
                    Row(
                        modifier = Modifier
                            .width(120.dp)
                            .height(56.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { /* lihat */ },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1))
                        ) {
                            Text("Lihat", fontSize = 12.sp)
                        }
                        Button(
                            onClick = { /* hapus */ },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight(),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                        ) {
                            Text("Hapus", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TableCell(text: String, width: Dp, color: Color = Color.Unspecified, isHeader: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color.Black else color
    )
}

data class AssetData(val no: Int, val tanggal: String, val deskripsi: String, val sudah: Boolean)

// Dummy Data
private val dummyMaintenanceData = listOf(
    AssetData(1, "01 Juni 2025", "Preventive WHEEL LOADER CAT 914 G", false),
    AssetData(2, "02 Juni 2025", "Preventive Screw Press No:2", false),
    AssetData(3, "03 Juni 2025", "Preventive Screw Press No:3", true),
    AssetData(4, "04 Juni 2025", "Preventive WHEEL LOADER CAT 914 G", true)
)

private val dummyRepairData = listOf(
    AssetData(1, "05 Juni 2025", "Perbaikan Gearbox Press A", false),
    AssetData(2, "06 Juni 2025", "Perbaikan Conveyor Loader", true)
)

@Preview(showBackground = true)
@Composable
fun AppDrawerPreview() {
    CmmsTheme {
        AppDrawer()
    }
}
