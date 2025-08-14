import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ptpn.cmms.unit.MaintenanceItem
import com.ptpn.cmms.unit.SearchSection
import com.ptpn.cmms.unit.ShowEntriesSection
import com.ptpn.cmms.unit.TableFooter

//-->Awal list Perbaikan Asset belum di kerjakan<--

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
fun TabelPerbaikanBelum(items: List<MaintenanceItem>) {
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
            text = "List Perbaikan Asset Belum Dikerjakan",
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
//-->Akhir list Perbaikan Asset belum di kerjakan<--