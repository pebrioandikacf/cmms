// File: DashboardActivity.kt
package com.ptpn.cmms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ptpn.cmms.mechanic.AssetMechanicScreen
import com.ptpn.cmms.mechanic.DetailMechanicDashboard
import com.ptpn.cmms.mechanic.MechanicDashboard
import com.ptpn.cmms.ui.theme.CmmsTheme
import com.ptpn.cmms.unit.UnitDashboard

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CmmsTheme(dynamicColor = false) { // gunakan tema CMMS tanpa dynamic color
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "login") {

                    // Login Screen
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

                    // Unit Dashboard
                    composable("unit_dashboard") {
                        UnitDashboard(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("unit_dashboard") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Mechanic Dashboard
                    composable("mechanic_dashboard") {
                        MechanicDashboard(
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("mechanic_dashboard") { inclusive = true }
                                }
                            },
                            onAssetsClick = {
                                // navigate to asset list screen
                                navController.navigate("asset_mechanic")
                            },
                            onViewDetail = { itemId ->
                                // navigate to detail screen with integer argument
                                navController.navigate("detail/$itemId")
                            }
                        )
                    }

                    // Mechanic Asset list (screen yang kamu buat: AssetMechanicScreen)
                    composable("asset_mechanic") {
                        AssetMechanicScreen(
                            onBack = {
                                // coba pop sampai mechanic_dashboard (jika ada di back stack)
                                val popped = navController.popBackStack("mechanic_dashboard", false)
                                if (!popped) {
                                    // jika tidak ada, navigasikan ke mechanic_dashboard
                                    navController.navigate("mechanic_dashboard") {
                                        // hindari duplikat di back stack
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onViewAsset = { assetId ->
                                // arahkan mis. ke same detail route
                                navController.navigate("detail/$assetId")
                            },
                            onUpdateAsset = { assetId ->
                                // contoh: navigasi ke edit screen (opsional)
                                // navController.navigate("asset_edit/$assetId")
                            }
                        )
                    }

                    // Detail Mekanik Dashboard (mengharapkan itemId Int)
                    composable(
                        "detail/{itemId}",
                        arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("itemId") ?: 0
                        DetailMechanicDashboard(
                            itemId = id,
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
