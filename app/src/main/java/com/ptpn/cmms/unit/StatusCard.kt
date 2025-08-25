package com.ptpn.cmms.unit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StatusCard(
    count: String = "0",
    label: String = "Label",
    icon: ImageVector = Icons.Filled.Info,
    footerColor: Color = Color.Gray,
    modifier: Modifier = Modifier,
    changeText: String = "%change",
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier.then(
            if (onClick != null) Modifier.clickable { onClick() } else Modifier
        ),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // Bagian atas (angka, label, ikon)
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
                        fontSize = 24.sp
                    )
                    Text(
                        text = label,
                        color = Color(0xFF9E9E9E),
                        fontSize = 14.sp
                    )
                }

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF9E9E9E),
                    modifier = Modifier.size(28.dp)
                )
            }

            // Footer berwarna (%change + trending)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(footerColor)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = changeText,
                    color = Color.White,
                    fontSize = 12.sp
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

// ✅ Data class biar gampang buat list
data class StatusData(
    val count: String,
    val label: String,
    val icon: ImageVector,
    val footerColor: Color,
    val changeText: String
)

// ✅ Helper Composable buat nampilin banyak StatusCard
@Composable
fun StatusCardList(
    items: List<StatusData>,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        items.forEach { data ->
            StatusCard(
                count = data.count,
                label = data.label,
                icon = data.icon,
                footerColor = data.footerColor,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                changeText = data.changeText
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusCardPreview() {
    val sample = listOf(
        StatusData("11", "Stasiun", Icons.Filled.CalendarToday, Color(0xFFE53935), "+4%"),
        StatusData("233", "Peralatan", Icons.Filled.Download, Color(0xFF00ACC1), "-2%")
    )
    StatusCardList(items = sample, modifier = Modifier.padding(12.dp))
}
