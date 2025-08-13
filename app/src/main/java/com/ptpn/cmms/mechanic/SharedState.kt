// File: SharedState.kt
package com.ptpn.cmms.mechanic

import androidx.compose.runtime.mutableStateMapOf

/** menyimpan evidence file path per item id (local cache path) */
val evidenceMap = mutableStateMapOf<Int, String?>()

/** menyimpan status done per item id (true = sudah dikerjakan) */
val doneState = mutableStateMapOf<Int, Boolean>()
