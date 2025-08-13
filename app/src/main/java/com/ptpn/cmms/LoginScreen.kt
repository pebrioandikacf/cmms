// File: LoginScreen.kt
package com.ptpn.cmms

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ptpn.cmms.ui.theme.CmmsTheme

/**
 * @param onLoginSuccess Dipanggil dengan role yang di‐login, misal "unit" atau "mekanik"
 */
@Composable
fun LoginScreen(
    onLoginSuccess: (role: String) -> Unit = {}
) {
    val context = LocalContext.current
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Tampilkan logo jika ada
        runCatching {
            Image(
                painter = painterResource(id = R.drawable.logocmms),
                contentDescription = "Logo CMMS",
                modifier = Modifier
                    .height(120.dp)
                    .padding(bottom = 24.dp)
            )
        }

        Text(
            text = "Sign In",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isNotBlank() && password.isNotBlank()) {
                    // Dummy role determination
                    val role = if (username.lowercase().startsWith("mek")) "mekanik" else "unit"
                    Toast.makeText(context, "Login sukses sebagai $role", Toast.LENGTH_SHORT).show()
                    onLoginSuccess(role)
                } else {
                    Toast.makeText(context, "Username & Password wajib diisi", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Sign In")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    CmmsTheme {
        LoginScreen() // sekarang aman tanpa perlu argumen
    }
}
