package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ptpn.cmms.ui.theme.CmmsTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import androidx.annotation.RequiresApi
import android.os.Build
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility

// Tambahkan id pada MaintenanceItem supaya bisa dinavigasi
data class MaintenanceItem(
    val id: Int,
    val tanggal: String,
    val deskripsi: String,
    val status: String
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDashboard(onLogout: () -> Unit = {}, navController: NavController? = null) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showDataMasterSub by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                UnitDrawerHeader()
                Spacer(Modifier.height(8.dp))

                UnitDrawerItem("Dashboard", Icons.Filled.Home) {}

                // Submenu Data Master
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
                    ) {}
                }

                UnitDrawerItem("Assets", Icons.AutoMirrored.Filled.List) {}
                UnitDrawerItem("Jam Jalan Peralatan", Icons.Filled.Schedule) {}
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
            UnitDashboardContent(modifier = Modifier.padding(it), navController = navController)
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
fun UnitDashboardContent(modifier: Modifier = Modifier, navController: NavController? = null) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(4.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header dashboard
        Text("Dashboard", style = MaterialTheme.typography.titleLarge)

        // Status cards
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
                modifier = Modifier.weight(1f).height(110.dp)
            )

            StatusCard(
                count = "233",
                label = "Peralatan",
                icon = Icons.Filled.Download,
                footerColor = Color(0xFF00ACC1),
                changeText = "%change",
                modifier = Modifier.weight(1f).height(110.dp)
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // MaintenanceByDateSection1 tetap di atas (search by date untuk user)
            MaintenanceByDateSection1()
        }

        // Dummy data (contoh ada id sekarang)
        val belumItems = listOf(
            MaintenanceItem(1, "01 Juni 2025", "Preventive Maintenance WHELL LOADER CAT 914 G PKS SPA", "Belum Dikerjakan"),
            MaintenanceItem(2, "02 Juni 2025", "Preventive Maintenance Screw Press No : 2", "Belum Dikerjakan"),
        )

        val selesaiItems = listOf(
            MaintenanceItem(3, "28 Mei 2025", "Overhaul Boiler No. 1", "Selesai"),
            MaintenanceItem(4, "29 Mei 2025", "Penggantian Belt Conveyor", "Selesai")
        )

        // Tabel Pemeliharaan Belum Dikerjakan
        TabelPemeliharaanBelum(items = belumItems)

        // Tabel Pemeliharaan Sudah Dikerjakan (kirim navController yang didapat dari DashboardActivity)
        TabelPemeliharaanSelesai(
            items = selesaiItems,
            navController = navController
        )

        // Pindahkan MaintenanceByDateSection2 supaya muncul di BAWAH semua tabel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MaintenanceByDateSection2()
        }

        // Jika ada fungsi TabelPerbaikanBelum/TabelPerbaikanSelesai eksternal, hapus/diperbaiki.
        // Di sini kita sudah menggunakan TabelPemeliharaanBelum / TabelPemeliharaanSelesai yang didefinisikan di file ini.
    }
}

@Composable
fun StatusCard(
    count: String,
    label: String,
    icon: ImageVector,
    footerColor: Color,
    changeText: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp), // Sudut sedikit membulat
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Bagian atas isi utama
            Row(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = count,
                        color = footerColor,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = label,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Bagian bawah (footer berwarna)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(footerColor)
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = changeText, // Contoh: "+12%"
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}


// Awal List Search Box Pertanggal
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaintenanceByDateSection1() {
    val today = remember { LocalDate.now() }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val formattedDate = today.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "List Pemeliharaan Asset Tanggal $formattedDate",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text("Tanggal", fontSize = 16.sp, modifier = Modifier.width(80.dp))
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Tanggal")
                },
                modifier = Modifier.width(220.dp)
            )
        }

        Button(
            onClick = { /* ambil data nanti */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF17C0EB)),
            modifier = Modifier.width(100.dp),
            shape= RectangleShape
        ) {
            Text("Submit", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray)
        Spacer(modifier = Modifier.height(12.dp))

        // Header kolom
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#", modifier = Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
            Text("Deskripsi", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Status", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
            Text("Action", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Teks placeholder sementara kosong
        Text(
            "Belum ada data untuk ditampilkan.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
// Akhir List Search Box Pertanggal


//-->Awal list Pemeliharaan Asset belum di kerjakan<--

@Composable
fun ShowEntriesSection(
    selectedEntries: Int,
    onSelect: (Int) -> Unit,
    showDropdown: Boolean,
    onShowDropdownChange: (Boolean) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Show", fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))

        Box {
            OutlinedButton(
                onClick = { onShowDropdownChange(true) },
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier
                    .height(36.dp)
                    .width(60.dp),
                shape = RoundedCornerShape(0.dp) // kotak persegi
            ) {
                Text(selectedEntries.toString(), fontSize = 12.sp)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }

            DropdownMenu(
                expanded = showDropdown,
                onDismissRequest = { onShowDropdownChange(false) }
            ) {
                listOf(10, 25, 50, 100).forEach { number ->
                    DropdownMenuItem(
                        text = { Text(number.toString(), fontSize = 12.sp) },
                        onClick = {
                            onSelect(number)
                            onShowDropdownChange(false)
                        }
                    )
                }
            }
        }

        Spacer(Modifier.width(6.dp))
        Text("entries", fontSize = 12.sp)
    }
}

@Composable
fun SearchSection(
    searchQuery: String,
    onSearchChange: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("Search:", fontSize = 12.sp)
        Spacer(Modifier.width(6.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .width(180.dp)
                .height(36.dp),
            textStyle = LocalTextStyle.current.copy(fontSize = 12.sp),
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun TabelPemeliharaanBelum(items: List<MaintenanceItem>) {
    val cellBorderColor = Color(0xFFBDBDBD)

    var searchQuery by remember { mutableStateOf("") }
    var selectedEntries by remember { mutableStateOf(10) }
    var showDropdown by remember { mutableStateOf(false) }

    val filteredItems = items.filter {
        it.deskripsi.contains(searchQuery, ignoreCase = true) ||
                it.status.contains(searchQuery, ignoreCase = true) ||
                it.tanggal.contains(searchQuery, ignoreCase = true)
    }.take(selectedEntries)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Judul
        Text(
            text = "List Pemeliharaan Asset Belum Dikerjakan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Show + Search responsif
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            val isWide = this.maxWidth > 500.dp

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShowEntriesSection(
                        selectedEntries = selectedEntries,
                        onSelect = { selectedEntries = it },
                        showDropdown = showDropdown,
                        onShowDropdownChange = { showDropdown = it }
                    )

                    SearchSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it }
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShowEntriesSection(
                        selectedEntries = selectedEntries,
                        onSelect = { selectedEntries = it },
                        showDropdown = showDropdown,
                        onShowDropdownChange = { showDropdown = it }
                    )

                    SearchSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

// Header tabel adaptif
        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            val isSmallScreen = this.maxWidth < 400.dp // batas layar kecil

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
            ) {
                listOf(
                    "No",                                     // Tetap "No"
                    "Tanggal",  // Ubah kalau kecil
                    "Deskripsi",
                    "Status",
                    "Aksi"
                ).forEachIndexed { index, title ->
                    val weight = when (index) {
                        0 -> 0.5f
                        1 -> 0.9f
                        2 -> 1.6f
                        3 -> 1.1f
                        4 -> 1.0f
                        else -> 1f
                    }
                    Box(
                        modifier = Modifier
                            .weight(weight)
                            .fillMaxHeight()
                            .border(0.5.dp, cellBorderColor)
                            .background(Color(0xFFE0E0E0))
                            .padding(6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                    }
                }
            }
        }

        // Isi tabel
        filteredItems.forEachIndexed { index, item ->
            val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(bgColor)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${index + 1}", fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.tanggal, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1.6f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(item.deskripsi, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.status, fontSize = 12.sp, color = Color(0xFFE53935))
                }
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(), // biar bisa di-center secara vertikal
                        verticalArrangement = Arrangement.Center, // posisi vertikal di tengah
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { /* Lihat */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 2.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Visibility,
                                contentDescription = "Lihat",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Lihat", color = Color.White, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { /* Hapus */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 2.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Hapus",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text("Hapus", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
        val currentPage = 1
        val totalPages = 5

        TableFooter(
            currentPage = currentPage,
            totalPages = totalPages,
            onPrevious = { /* aksi previous */ },
            onNext = { /* aksi next */ },
            onPageClick = { page -> /* aksi klik halaman */ }
        )
    }
}

@Composable
fun TableFooter(
    currentPage: Int,
    totalPages: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPageClick: (Int) -> Unit
) {
    val cellBorderColor = Color.Gray
    val cellHeight = 28.dp
    val navWidth = 60.dp
    val pageWidth = 28.dp
    val fontSizeNav = 10.sp
    val fontSizePage = 9.sp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
    ) {
        // Baris pertama: Showing di kiri
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Showing ${(currentPage - 1) * 10 + 1} to ${minOf(currentPage * 10, totalPages * 10)} of ${totalPages * 10} entries",
                fontSize = 12.sp
            )
        }

        // Baris kedua: Pagination di kanan
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous
            Box(
                modifier = Modifier
                    .border(1.dp, cellBorderColor)
                    .height(cellHeight)
                    .width(navWidth)
                    .clickable(enabled = currentPage > 1) { onPrevious() },
                contentAlignment = Alignment.Center
            ) {
                Text("Previous", fontSize = fontSizeNav)
            }

            // Hitung halaman
            val pageNumbers = mutableListOf<Int>()
            if (totalPages <= 2) {
                for (i in 1..totalPages) pageNumbers.add(i)
            } else {
                val startPage = maxOf(1, currentPage - 1)
                val endPage = minOf(totalPages, startPage + 1)
                for (i in startPage..endPage) pageNumbers.add(i)
            }

            // Nomor halaman
            pageNumbers.forEach { page ->
                val isSelected = page == currentPage
                Box(
                    modifier = Modifier
                        .border(1.dp, cellBorderColor)
                        .background(if (isSelected) Color(0xFF00AFAF) else Color.Transparent)
                        .height(cellHeight)
                        .width(pageWidth)
                        .clickable { onPageClick(page) },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        page.toString(),
                        fontSize = fontSizePage,
                        color = if (isSelected) Color.White else Color.Black
                    )
                }
            }

            // Next
            Box(
                modifier = Modifier
                    .border(1.dp, cellBorderColor)
                    .height(cellHeight)
                    .width(navWidth)
                    .clickable(enabled = currentPage < totalPages) { onNext() },
                contentAlignment = Alignment.Center
            ) {
                Text("Next", fontSize = fontSizeNav)
            }
        }

    }
}
//-->Akhir list Asset belum di kerjakan<--


//-->Awal List Asset yang sudah di kerjakan<--

@Composable
fun TabelPemeliharaanSelesai(items: List<MaintenanceItem>, navController: NavController?) {
    val cellBorderColor = Color(0xFFBDBDBD)

    var searchQuery by remember { mutableStateOf("") }
    var selectedEntries by remember { mutableStateOf(10) }
    var showDropdown by remember { mutableStateOf(false) }

    val filteredItems = items.filter {
        it.deskripsi.contains(searchQuery, ignoreCase = true) ||
                it.status.contains(searchQuery, ignoreCase = true) ||
                it.tanggal.contains(searchQuery, ignoreCase = true)
    }.take(selectedEntries)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        // Judul
        Text(
            text = "List Pemeliharaan Asset Sudah Dikerjakan",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Show + Search responsif (sama dengan list Belum Dikerjakan)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            val isWide = this.maxWidth > 500.dp

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShowEntriesSection(
                        selectedEntries = selectedEntries,
                        onSelect = { selectedEntries = it },
                        showDropdown = showDropdown,
                        onShowDropdownChange = { showDropdown = it }
                    )

                    SearchSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it }
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ShowEntriesSection(
                        selectedEntries = selectedEntries,
                        onSelect = { selectedEntries = it },
                        showDropdown = showDropdown,
                        onShowDropdownChange = { showDropdown = it }
                    )

                    SearchSection(
                        searchQuery = searchQuery,
                        onSearchChange = { searchQuery = it }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Header tabel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
        ) {
            listOf("No", "Tanggal", "Deskripsi", "Status", "Aksi").forEachIndexed { index, title ->
                val weight = when (index) {
                    0 -> 0.5f
                    1 -> 0.9f
                    2 -> 1.6f
                    3 -> 1.1f
                    4 -> 1.0f
                    else -> 1f
                }
                Box(
                    modifier = Modifier
                        .weight(weight)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .background(Color(0xFFE0E0E0))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1)
                }
            }
        }

        // Isi tabel
        filteredItems.forEachIndexed { index, item ->
            val bgColor = if (index % 2 == 0) Color.White else Color(0xFFF5F5F5)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .background(bgColor)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.5f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("${index + 1}", fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.tanggal, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1.6f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(item.deskripsi, fontSize = 12.sp)
                }
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(item.status, fontSize = 12.sp, color = Color(0xFF4CAF50))
                }
                Box(
                    modifier = Modifier
                        .weight(1.0f)
                        .fillMaxHeight()
                        .border(0.5.dp, cellBorderColor)
                        .padding(4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { navController?.navigate("detail_pemeliharaan/${item.id}") },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 2.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = "Lihat", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Lihat", color = Color.White, fontSize = 11.sp)
                        }
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { /* Hapus */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(vertical = 2.dp),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Hapus", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Footer
        TableFooter(
            currentPage = 1,
            totalPages = 2,
            onPrevious = { /* prev */ },
            onNext = { /* next */ },
            onPageClick = { /* page */ }
        )
    }
}
//-->Akhir List Asset yang sudah di kerjakan<--

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MaintenanceByDateSection2() {
    val today = remember { LocalDate.now() }
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val formattedDate = today.format(formatter)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "List Perbaikan Asset Tanggal $formattedDate",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            Text("Tanggal", fontSize = 16.sp, modifier = Modifier.width(80.dp))
            OutlinedTextField(
                value = formattedDate,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    Icon(Icons.Default.DateRange, contentDescription = "Tanggal")
                },
                modifier = Modifier.width(220.dp)
            )
        }

        Button(
            onClick = { /* ambil data nanti */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF17C0EB)),
            modifier = Modifier.width(100.dp),
            shape= RectangleShape
        ) {
            Text("Submit", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Divider(color = Color.LightGray)
        Spacer(modifier = Modifier.height(12.dp))

        // Header kolom
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0E0E0))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#", modifier = Modifier.weight(0.2f), fontWeight = FontWeight.Bold)
            Text("Deskripsi", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("Status", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
            Text("Action", modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Teks placeholder sementara kosong
        Text(
            "Belum ada data untuk ditampilkan.",
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UnitDashboardPreview() {
    CmmsTheme {
        // untuk preview kita tidak punya NavController, jadi panggil tanpa navController
        UnitDashboard()
    }
}
