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
import com.ptpn.cmms.unit.Detail_Pemeliharaan
import com.ptpn.cmms.unit.UnitDashboard

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CmmsTheme(dynamicColor = false) {
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
                            },
                            navController = navController
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
                                navController.navigate("asset_mechanic")
                            },
                            onViewDetail = { itemId ->
                                navController.navigate("detail/$itemId")
                            }
                        )
                    }

                    // Mechanic Asset list
                    composable("asset_mechanic") {
                        AssetMechanicScreen(
                            onBack = {
                                val popped = navController.popBackStack("mechanic_dashboard", false)
                                if (!popped) {
                                    navController.navigate("mechanic_dashboard") {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onViewAsset = { assetId ->
                                navController.navigate("detail/$assetId")
                            },
                            onUpdateAsset = { assetId ->
                                // navController.navigate("asset_edit/$assetId")
                            }
                        )
                    }

                    // Detail Mekanik Dashboard
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

                    // Detail Pemeliharaan Unit
                    composable(
                        route = "detail_pemeliharaan/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id")
                        Detail_Pemeliharaan(id = id)
                    }
                }
            }
        }
    }
}
