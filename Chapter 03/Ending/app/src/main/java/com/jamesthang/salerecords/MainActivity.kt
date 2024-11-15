package com.jamesthang.salerecords

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.jamesthang.salerecords.ui.theme.SaleRecordsTheme

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
    Settings("settings");

    companion object {
        fun fromRoute(route: String?): AppScreen =
            when (route?.substringBefore("/")) {
                Login.route -> Login
                SignUp.route -> SignUp
                Settings.route -> Settings
                null -> throw IllegalArgumentException("Route cannot be null")
                else -> throw IllegalArgumentException("Unknown route: $route")
            }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppScreen.Login.route
    ) {
        composable(AppScreen.Login.route) {
            LoginScreen(navController)
        }
        composable(AppScreen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(AppScreen.Settings.route) {
            SettingScreen(navController)
        }
    }
    val currentUser = Firebase.auth.currentUser
    if (currentUser != null) {
        navController.navigate(AppScreen.Settings.route)
    }
}