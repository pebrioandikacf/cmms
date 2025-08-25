package com.ptpn.cmms.unit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Alternative attractive mobile layout for Import Data Peralatan
 * - Gradient header with floating card
 * - Drag-drop style upload area (simulated)
 * - File chip + success state + subtle animations
 */

@Composable
fun ImportDataPeralatanAttractive(
    modifier: Modifier = Modifier,
    onChooseFile: (() -> Unit)? = null,
    onSave: ((String?) -> Unit)? = null
) {
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var uploaded by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(true) }

    // gradient header brush
    val headerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF2ECFDB), Color(0xFF1FA6A2)),
        start = Offset(0f, 0f),
        end = Offset(1000f, 1000f),
        tileMode = TileMode.Clamp
    )

    Column(modifier = modifier
        .fillMaxSize()
        .background(Color(0xFFF6F7FB))) {

        // Header
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(headerBrush)) {
            Column(modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), verticalArrangement = Arrangement.Center) {
                Text(text = "Import Data Peralatan", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Unggah data .xlsx atau .csv untuk menambahkan asset", color = Color(0x88FFFFFF), fontSize = 13.sp)
            }

            // Floating card overlapping header
            Card(modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = 92.dp)
                .shadow(6.dp, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp)) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)) {
                    Text(text = "Tambahkan Data Asset", fontWeight = FontWeight.SemiBold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Upload area
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF7FBFB))
                        .border(width = 1.dp, color = Color(0xFFE6F3F3), shape = RoundedCornerShape(8.dp))
                        .clickable {
                            // simulate opening file chooser
                            if (onChooseFile != null) onChooseFile()
                            else {
                                // simulate selecting file
                                selectedFileName = "data_peralatan.xlsx"
                                uploaded = true
                            }
                        }
                        , contentAlignment = Alignment.Center) {

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color(0xFF1FA6A2))
                            Spacer(modifier = Modifier.height(6.dp))
                            if (selectedFileName == null) {
                                Text(text = "Tarik dan taruh file di sini atau ketuk untuk memilih", color = Color(0xFF6B6B6B), fontSize = 13.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                TextButton(onClick = {
                                    if (onChooseFile != null) onChooseFile()
                                    else {
                                        selectedFileName = "assets_template.xlsx"
                                        uploaded = true
                                    }
                                }) {
                                    Text(text = "Pilih File", color = Color(0xFF1FA6A2))
                                }
                            } else {
                                // show file chip & status
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
                                        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = selectedFileName ?: "", maxLines = 1, overflow = TextOverflow.Ellipsis)
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    AnimatedVisibility(visible = uploaded, enter = fadeIn(animationSpec = tween(400)), exit = fadeOut()) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2ECFDB))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "Siap diunggah", color = Color(0xFF2ECFDB))
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // note + save
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Maks: 2MB — Format .xlsx atau .csv", color = Color(0xFF9E9E9E), fontSize = 13.sp)
                            if (showHint) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "Tip: Unduh template jika kolom tidak sesuai", color = Color(0xFF707070), fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(onClick = { onSave?.invoke(selectedFileName) }, modifier = Modifier.height(44.dp)) {
                            Text(text = "Simpan & Import")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ImportDataPeralatanAttractivePreview() {
    MaterialTheme {
        ImportDataPeralatanAttractive()
    }
}
