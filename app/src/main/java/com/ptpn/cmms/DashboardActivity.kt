// File: MainActivity.kt
package com.ptpn.cmms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ptpn.cmms.mechanic.MechanicDashboard
import com.ptpn.cmms.ui.theme.CmmsTheme
import com.ptpn.cmms.unit.UnitDashboard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CmmsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "login"
                ) {
                    // 1) Login
                    composable("login") {
                        LoginScreen { role ->
                            if (role == "mekanik") {
                                navController.navigate("mechanic_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                    }
                            } else {
                                navController.navigate("unit_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }

                    // 2) Unit Dashboard
                    composable("unit_dashboard") {
                        UnitDashboard(onLogout = {
                            navController.navigate("login") {
                                popUpTo("unit_dashboard") { inclusive = true }
                            }
                        })
                    }

                    // 3) Mechanic Dashboard
                    composable("mechanic_dashboard") {
                        MechanicDashboard(onLogout = {
                            navController.navigate("login") {
                                popUpTo("mechanic_dashboard") { inclusive = true }
                            }
                        })
                    }
                }
            }
        }
    }
}
