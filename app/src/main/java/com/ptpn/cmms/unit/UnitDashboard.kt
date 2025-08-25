package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ptpn.cmms.ui.theme.CmmsTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDashboard(onLogout: () -> Unit = {}) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDataMasterSub by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                UnitDrawerHeader()
                Spacer(Modifier.height(8.dp))

                UnitDrawerItem("Dashboard", Icons.Filled.Home) { /* TODO */ }

                // Menu dengan submenu
                NavigationDrawerItem(
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Data Master")
                            Spacer(Modifier.weight(1f))
                            Icon(
                                imageVector = if (showDataMasterSub) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                                contentDescription = null
                            )
                        }
                    },
                    icon = { Icon(Icons.Filled.Dashboard, contentDescription = null) },
                    selected = showDataMasterSub,
                    onClick = { showDataMasterSub = !showDataMasterSub },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )

                if (showDataMasterSub) {
                    UnitDrawerItem(
                        title = "Data Mekanik",
                        icon = Icons.Filled.Person,
                        modifier = Modifier.padding(start = 32.dp)
                    ) { /* TODO */ }
                }

                UnitDrawerItem("Assets", Icons.AutoMirrored.Filled.List) { /* TODO */ }
                UnitDrawerItem("Jam Jalan Peralatan", Icons.Filled.Schedule) { /* TODO */ }
                UnitDrawerItem("Logout", Icons.AutoMirrored.Filled.ExitToApp) { onLogout() }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Dashboard CMMS - Unit") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Filled.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) {
            UnitDashboardContent(modifier = Modifier.padding(it))
        }
    }
}

@Composable
fun UnitDrawerHeader() {
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
fun UnitDrawerItem(
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
fun UnitDashboardContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()) // scroll untuk seluruh screen
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header
        Text("Dashboard", style = MaterialTheme.typography.titleLarge)

        //===========================================================================
        // --- tampilkan StatusCard yang sudah kamu punya ---
        val statusItems = listOf(
            StatusData("11", "Stasiun", Icons.Filled.CalendarToday, Color(0xFFE53935), "+4%"),
            StatusData("233", "Peralatan", Icons.Filled.Download, Color(0xFF00ACC1), "-2%")
        )

        // Jika kamu ingin tampilkan **vertikal (stacked)**:
        StatusCardList(
            items = statusItems,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        //===========================================================================

        //Pemeliharaan Tanggal
        PemeliharaanTanggal(modifier = Modifier.fillMaxWidth())

        //===========================================================================

        //Pemeliharaan Belum
        PemeliharaanBelum(
            items = samplepemeliharaanbelum,
            modifier = Modifier.fillMaxWidth()
        )

        //===========================================================================

        //Pemeliharaan Sudah
        PemeliharaanSudah(
            items = samplepemeliharaansudah,
            modifier = Modifier.fillMaxWidth()
        )

        //Perbaikan Tanggal
        PerbaikanTanggal(modifier = Modifier.fillMaxWidth())

        //===========================================================================

        //Pemeliharaan Sudah
        PerbaikanBelum(
            items = sampleperbaikanbelum,
            modifier = Modifier.fillMaxWidth()
        )

        //===========================================================================

        //Pemeliharaan Sudah
        PerbaikanSudah(
            items = sampleperbaikansudah,
            modifier = Modifier.fillMaxWidth()
        )
    }
}



@Preview(showBackground = true)
@Composable
fun UnitDashboardPreview() {
    CmmsTheme {
        UnitDashboard()
    }
}
