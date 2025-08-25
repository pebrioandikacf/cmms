// File: app/src/main/java/com/ptpn/cmms/mechanic/ViewQRMechanic.kt
package com.ptpn.cmms.mechanic

import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import com.ptpn.cmms.ui.theme.CmmsTheme
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image

class ViewQRMechanic : ComponentActivity() {

    companion object {
        const val EXTRA_URL = "extra_url"     // expected: URL or identifier text for QR
        const val EXTRA_LABEL = "extra_label" // optional: label to show under QR
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Read extras (fallbacks)
        val text = intent?.getStringExtra(EXTRA_URL)?.takeIf { it.isNotBlank() } ?: "asset://unknown"
        val label = intent?.getStringExtra(EXTRA_LABEL) ?: text

        setContent {
            CmmsTheme { // use app theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ViewQRScreen(qrText = text, label = label, onBack = { finish() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ViewQRScreen(qrText: String, label: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QR Code", fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(18.dp))

            // QR image area
            val bitmapState = remember(qrText) { runCatching { generateQrBitmap(qrText, 640) }.getOrNull() }

            if (bitmapState != null) {
                Image(
                    bitmap = bitmapState.asImageBitmap(),
                    contentDescription = "QR Code",
                    modifier = Modifier
                        .size(280.dp)
                )
            } else {
                // fallback UI if generation failed
                Text(
                    text = "Gagal membuat QR\n(cek format teks)",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(12.dp),
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = qrText,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Simple ZXing-based QR generator -> Bitmap */
private fun generateQrBitmap(text: String, size: Int = 512): Bitmap {
    val writer = MultiFormatWriter()
    val bitMatrix: BitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, null)
    val bmp = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val black = android.graphics.Color.BLACK
    val white = android.graphics.Color.WHITE
    for (x in 0 until size) {
        for (y in 0 until size) {
            bmp.setPixel(x, y, if (bitMatrix[x, y]) black else white)
        }
    }
    return bmp
}
