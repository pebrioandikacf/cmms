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
import com.ptpn.cmms.mechanic.DetailMechanicDashboard
import com.ptpn.cmms.mechanic.MechanicDashboard
import com.ptpn.cmms.ui.theme.CmmsTheme
import com.ptpn.cmms.unit.UnitDashboard

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CmmsTheme {
                val navController = rememberNavController()
                NavHost(navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen { role ->
                            if (role == "mekanik") {
                                navController.navigate("mechanic_dashboard")
                            } else {
                                navController.navigate("unit_dashboard") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    }
                    composable("unit_dashboard") {
                        UnitDashboard(
                            onLogout = { navController.navigate("login") { popUpTo("unit_dashboard")
                            { inclusive = true }}
                            }
                        )
                    }

                    composable("mechanic_dashboard") {
                        MechanicDashboard(
                            onLogout = { navController.navigate("login") { popUpTo("login") } },
                            onViewDetail = { itemId ->
                                navController.navigate("detail/$itemId")
                            }
                        )
                    }
                    composable(
                        "detail/{itemId}",
                        arguments = listOf(navArgument("itemId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        DetailMechanicDashboard(
                            itemId = backStackEntry.arguments!!.getInt("itemId"),
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
