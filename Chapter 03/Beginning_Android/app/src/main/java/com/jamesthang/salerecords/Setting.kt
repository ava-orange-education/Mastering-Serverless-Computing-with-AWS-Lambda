package com.jamesthang.salerecords

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setting") }
            )
        }
    ) {it: PaddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // This spacer will push the buttons to the bottom as it takes up all available space
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { performLogout(navController) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("LOG OUT", color = Color.Black)
            }
            Button(
                onClick = { /* TODO: Add delete account functionality */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("DELETE ACCOUNT", color = Color.White)
            }
        }
    }

}

private fun performLogout(navController: NavController) {
    // Here you would also handle the logic to actually log the user out from your backend
    // For now, it will just clear the back stack and navigate to the login screen
    navController.popBackStack(route = AppScreen.Login.route, inclusive = false)
}