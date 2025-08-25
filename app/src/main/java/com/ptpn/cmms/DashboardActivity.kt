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
import com.ptpn.cmms.mechanic.*
import com.ptpn.cmms.ui.theme.CmmsTheme
import com.ptpn.cmms.unit.UnitDashboard

class DashboardActivity : ComponentActivity() {

    // Kamu bisa ekstrak route ini ke object/const agar tidak typo saat gunakan lagi
    private object Routes {
        const val LOGIN = "login"
        const val UNIT_DASH = "unit_dashboard"
        const val MECHANIC_DASH = "mechanic_dashboard"
        const val ASSET_MECHANIC = "asset_mechanic"
        const val DETAIL = "detail/{itemId}"
        const val ASSET_DETAIL = "assetDetail/{id}"
        const val ASSET_EDIT = "asset_edit/{id}"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CmmsTheme(dynamicColor = false) {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Routes.LOGIN) {

                    /** ------------------- LOGIN ------------------- */
                    composable(Routes.LOGIN) {
                        LoginScreen { role ->
                            when (role) {
                                "mekanik" -> {
                                    navController.navigate(Routes.MECHANIC_DASH) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                                else -> {
                                    navController.navigate(Routes.UNIT_DASH) {
                                        popUpTo(Routes.LOGIN) { inclusive = true }
                                    }
                                }
                            }
                        }
                    }

                    /** ------------------- UNIT DASHBOARD ------------------- */
                    composable(Routes.UNIT_DASH) {
                        UnitDashboard(
                            onLogout = {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.UNIT_DASH) { inclusive = true }
                                }
                            }
                        )
                    }

                    /** ------------------- MECHANIC DASHBOARD ------------------- */
                    composable(Routes.MECHANIC_DASH) {
                        MechanicDashboard(
                            onLogout = {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.MECHANIC_DASH) { inclusive = true }
                                }
                            },
                            onAssetsClick = {
                                navController.navigate(Routes.ASSET_MECHANIC)
                            },
                            onViewDetail = { itemId ->
                                navController.navigate("detail/$itemId")
                            }
                        )
                    }

                    /** ------------------- LIST ASET MEKANIK ------------------- */
                    composable(Routes.ASSET_MECHANIC) {
                        AssetMechanicScreen(
                            onBack = {
                                val popped = navController.popBackStack(Routes.MECHANIC_DASH, false)
                                if (!popped) {
                                    navController.navigate(Routes.MECHANIC_DASH) {
                                        launchSingleTop = true
                                    }
                                }
                            },
                            onViewAsset = { assetId ->
                                navController.navigate("assetDetail/$assetId")
                            },
                            onUpdateAsset = { assetId ->
                                navController.navigate("asset_edit/$assetId")
                            }
                        )
                    }

                    /** ------------------- DETAIL MEKANIK (Dashboard) ------------------- */
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

                    /** ------------------- DETAIL ASET (AssetMechanic 'Lihat') ------------------- */
                    composable(
                        "assetDetail/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0

                        // Dummy AssetDetail (sesuaikan jika model asli berbeda)
                        val asset = AssetDetail(
                            id = id,
                            kode = "1000110%03d".format(id),
                            nama = "Mesin Example $id",
                            lokasi = "—",
                            kategori = "Mesin Produksi",
                            merk = "Merk Dummy",
                            kapasitas = "200 kW",
                            tahun = "2023",
                            nomorPeralatan = "EQ-$id",
                            nilaiPerolehan = "10.000.000",
                            totalJamJalan = "1200",
                            images = emptyList(),
                            subAssets = emptyList()
                        )

                        DetailAssetMechanicScreen(
                            asset = asset,
                            onBack = { navController.popBackStack() },
                            onViewImage = { /* optional image viewer nav */ }
                        )
                    }

                    /** ------------------- EDIT ASET ------------------- */
                    composable(
                        "asset_edit/{id}",
                        arguments = listOf(navArgument("id") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val id = backStackEntry.arguments?.getInt("id") ?: 0

                        // => Sesuaikan dengan definisi AssetItemMechanic di EditAssetScreen (versi panjang)
                        val asset = AssetItemMechanic(
                            id = id.toLong(),
                            name = "Mesin $id",
                            merk = "BrandX",
                            kapasitas = "50 kW",
                            tahun = "2023",
                            nilai = "1000000",
                            kode = "A-${1000 + id}",
                            lokasi = "Lokasi $id",
                            // status, fotoUri, sopLink, grafik akan memakai nilai default dari data class
                        )

                        EditAssetScreen(
                            asset = asset,
                            onCancel = { navController.popBackStack() },
                            onSave = { updatedAsset ->
                                // sementara cukup log saja
                                println("Updated Asset: $updatedAsset")
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
