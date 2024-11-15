@file:Suppress("DEPRECATION")

package com.jamesthang.salerecords

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.jamesthang.salerecords.main.MainScreen
import com.jamesthang.salerecords.model.DTOSaleRecord
import com.jamesthang.salerecords.model.DTOSaleRecordNavType
import com.jamesthang.salerecords.record_detail.RecordDetailScreen
import com.jamesthang.salerecords.ui.theme.SaleRecordsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SaleRecordsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

enum class AppScreen(val route: String) {
    Login("login"),
    SignUp("signup"),
    MainScreen("MainScreen"),
    RecordDetailScreen("RecordDetailScreen"),
    Settings("settings");

    companion object {
        fun fromRoute(route: String?): AppScreen =
            when (route?.substringBefore("/")) {
                Login.route -> Login
                SignUp.route -> SignUp
                Settings.route -> Settings
                MainScreen.route -> MainScreen
                RecordDetailScreen.route -> RecordDetailScreen
                null -> throw IllegalArgumentException("Route cannot be null")
                else -> throw IllegalArgumentException("Unknown route: $route")
            }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val isLogin = remember { FirebaseAuth.getInstance().currentUser != null }

    LaunchedEffect(isLogin) {
        if (isLogin) {
            navController.navigate(AppScreen.MainScreen.route) {
                popUpTo(AppScreen.Login.route) { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (isLogin) AppScreen.MainScreen.route else AppScreen.Login.route
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(AppScreen.SignUp.route) {
            SignUpScreen(navController = navController)
        }

        composable(AppScreen.Settings.route) {
            SettingScreen(navController = navController)
        }

        composable(AppScreen.MainScreen.route) {
            MainScreen(navController)
        }

        composable(
            "RecordDetailScreen/{saleRecord}",
            arguments = listOf(navArgument("saleRecord") { type = DTOSaleRecordNavType() })
        ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.arguments?.getParcelable("saleRecord", DTOSaleRecord::class.java)
            } else {
                it.arguments?.getParcelable("saleRecord")
            }?.let {
                RecordDetailScreen(navController = navController, dtoSaleRecord = it)
            }
        }
    }
}