package com.jamesthang.salerecords.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings

sealed class BottomNavItem(
    var title: String,
    var icon: androidx.compose.ui.graphics.vector.ImageVector,
    var route: String
) {
    data object Menu : BottomNavItem("Menu", Icons.Filled.Home, "menu")
    data object Records : BottomNavItem("Records", Icons.Filled.List, "records")
    data object Settings : BottomNavItem("Settings", Icons.Filled.Settings, "settings")
}