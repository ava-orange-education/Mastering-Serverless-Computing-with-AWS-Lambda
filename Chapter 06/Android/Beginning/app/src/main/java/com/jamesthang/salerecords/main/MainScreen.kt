package com.jamesthang.salerecords.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.jamesthang.salerecords.SettingScreen
import com.jamesthang.salerecords.menu.MenuScreen
import com.jamesthang.salerecords.records.RecordsScreen

@Composable
fun MainScreen(navController: NavHostController) {
    val mainNavController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = mainNavController) }
    ) { paddingValues ->

        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = mainNavController,
            startDestination = BottomNavItem.Menu.route
        ) {
            composable(BottomNavItem.Menu.route) {
                MenuScreen()
            }
            composable(BottomNavItem.Records.route) {
                RecordsScreen(navController = navController)
            }
            composable(BottomNavItem.Settings.route) {
                SettingScreen(navController = navController)
            }
        }
    }
}

