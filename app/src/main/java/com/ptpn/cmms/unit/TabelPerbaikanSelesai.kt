import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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

@Composable
fun TabelPemeliharaanSelesai(items: List<MaintenanceItem>) {
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
                            onClick = { /* Lihat */ },
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