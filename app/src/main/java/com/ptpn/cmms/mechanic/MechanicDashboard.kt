// File: MechanicDashboard.kt
package com.ptpn.cmms.mechanic

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.ptpn.cmms.unit.StatusCard
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MechanicDashboard(onLogout: () -> Unit = {}) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                MechanicDrawerHeader()
                Spacer(Modifier.height(8.dp))

                MechanicDrawerItem("Dashboard", Icons.Filled.Home) { /* TODO */ }
                MechanicDrawerItem("Assets", Icons.AutoMirrored.Filled.List) { /* TODO */ }
                MechanicDrawerItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) { onLogout() }
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
                    }
                )
            }
        ) {
            MechanicDashboardContent(modifier = Modifier.padding(it))
        }
    }
}

@Composable
fun MechanicDrawerHeader() {
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
fun MechanicDrawerItem(
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
fun MechanicDashboardContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text("Dashboard", style = MaterialTheme.typography.titleLarge)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatusCard(
                count = "11",
                label = "Stasiun",
                icon = Icons.Filled.CalendarToday,
                footerColor = Color(0xFFE53935),
                changeText = "%change",
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
            )

            StatusCard(
                count = "233",
                label = "Peralatan",
                icon = Icons.Filled.Download,
                footerColor = Color(0xFF00ACC1),
                changeText = "%change",
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
            )
        }

        MechanicSection("List Pemeliharaan Asset Belum Dikerjakan", dummyMaintenanceData.filter { !it.sudah })
        MechanicSection("List Perbaikan Asset Belum Dikerjakan", dummyRepairData.filter { !it.sudah })
    }
}

@Composable
fun MechanicStatusCard(count: String, label: String, bgColor: Color, modifier: Modifier = Modifier) {
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
fun MechanicSection(title: String, data: List<MechanicAssetData>) {
    Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    MechanicTable(data)
}

@Composable
fun MechanicTable(data: List<MechanicAssetData>) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        Box(modifier = Modifier.horizontalScroll(scrollState)) {
            Row(
                modifier = Modifier
                    .width(580.dp)
                    .background(Color(0xFFB0BEC5))
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MechanicTableCell("No", 40.dp, isHeader = true)
                MechanicTableCell("Tanggal", 100.dp, isHeader = true)
                MechanicTableCell("Deskripsi", 200.dp, isHeader = true)
                MechanicTableCell("Status", 120.dp, isHeader = true)
                MechanicTableCell("Action", 120.dp, isHeader = true)
            }
        }

        data.forEach { item ->
            Box(modifier = Modifier.horizontalScroll(scrollState)) {
                Row(
                    modifier = Modifier
                        .width(580.dp)
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MechanicTableCell(item.no.toString(), 40.dp)
                    MechanicTableCell(item.tanggal, 100.dp)
                    MechanicTableCell(item.deskripsi, 200.dp)
                    MechanicTableCell(
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
fun MechanicTableCell(text: String, width: Dp, color: Color = Color.Unspecified, isHeader: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = if (isHeader) Color.Black else color
    )
}

// Dummy data shared
data class MechanicAssetData(val no: Int, val tanggal: String, val deskripsi: String, val sudah: Boolean)

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

@Preview(showBackground = true)
@Composable
fun MechanicDashboardPreview() {
    CmmsTheme {
        MechanicDashboard()
    }
}
