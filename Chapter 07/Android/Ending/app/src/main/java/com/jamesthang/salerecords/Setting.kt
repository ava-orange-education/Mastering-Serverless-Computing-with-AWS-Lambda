package com.jamesthang.salerecords

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(navController: NavController) {

    val currentUser = FirebaseAuth.getInstance().currentUser
    val authMethod = getCurrentUserAuthMethod(currentUser)
    val showReauthenPassword = remember { mutableStateOf(false) }

    // For Google Authentication
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            Log.d("ReauthenticatSuccess", "ReauthenticatWithGoogle:success")
            deleteAccount(currentUser, navController)
        },
        onAuthError = { error ->
            Log.w("ReauthenticateFail", "ReauthenticatWithGoogle:failure$error")
        }
    )
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setting") }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                    onClick = {
                        when (authMethod) {
                            AuthMethod.EMAIL -> {
                                println("User signed in using Email")
                                // Handle email authenticated user
                                showReauthenPassword.value = true
                            }

                            AuthMethod.GOOGLE -> {
                                println("User signed in using Google")
                                // Handle Google authenticated user
                                performGoogleReauthentication(launcher, context)
                            }

                            AuthMethod.NONE -> {
                                println("No user is signed in")
                                // Handle case where no user is signed in
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("DELETE ACCOUNT", color = Color.White)
                }
            }

            if (showReauthenPassword.value) {
                ReauthenticationEmailPassWord(
                    showDialog = showReauthenPassword,
                    onReauthenticated = {
                        // Handle successful reauthentication, e.g., navigate, show message
                        println("Reauthentication successful")
                        deleteAccount(currentUser, navController)
                    },
                    onError = { errorMsg ->
                        // Handle error, e.g., show error message
                        println("Reauthentication error: $errorMsg")
                        showReauthenPassword.value = false
                    }
                )
            }

        }
    }
}

private fun performLogout(navController: NavController) {
    Firebase.auth.signOut()
    navController.navigate(AppScreen.Login.route) {
        popUpTo(AppScreen.MainScreen.route) { inclusive = true }
    }
}

private fun deleteAccount(user: FirebaseUser?, navController: NavController) {
    user?.let {
        it.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("User delete success", "User account deleted.")
                    navController.popBackStack(route = AppScreen.Login.route, inclusive = false)
                }
            }
    }
}

enum class AuthMethod {
    EMAIL,
    GOOGLE,
    NONE   // Use this when no user is logged in or others method found
}

private fun getCurrentUserAuthMethod(user: FirebaseUser?): AuthMethod {
    user?.let {
        for (profile in it.providerData) {
            Log.d("User providerId", profile.providerId)
            return when (profile.providerId) {
                "password" -> AuthMethod.EMAIL
                "google.com" -> AuthMethod.GOOGLE
                else -> continue
            }
        }
    }
    return AuthMethod.NONE // Return this if no user is logged in or no provider data found
}

@Composable
private fun ReauthenticationEmailPassWord(
    showDialog: MutableState<Boolean>,
    onReauthenticated: () -> Unit,
    onError: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val error = remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = {
                showDialog.value = false
            },
            title = { Text("Reauthenticate") },
            text = {
                Column {
                    if (error.value.isNotEmpty()) {
                        Text(error.value, color = Color.Red)
                    }
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") }
                    )
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val user = FirebaseAuth.getInstance().currentUser
                        val credential = EmailAuthProvider.getCredential(email, password)
                        user?.reauthenticate(credential)
                            ?.addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    showDialog.value = false
                                    onReauthenticated()
                                } else {
                                    error.value =
                                        task.exception?.message ?: "Reauthentication failed"
                                    onError(error.value)
                                }
                            }
                    }
                ) {
                    Text("Reauthenticate")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog.value = false }) {
                    Text("Cancel")
                }
            },
            properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
        )
    }
}

private fun performGoogleReauthentication(
    launcher: ManagedActivityResultLauncher<Intent, ActivityResult>,
    context: Context
) {
    val token = "314960024202-bpv1d3uuvicphnhs91lsulipedug12rc.apps.googleusercontent.com"
    val gso =
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(token)
            .requestEmail()
            .build()
    val googleSignInClient = GoogleSignIn.getClient(context, gso)
    launcher.launch(googleSignInClient.signInIntent)
}

@Composable
private fun rememberFirebaseAuthLauncher(
    onAuthComplete: (AuthResult) -> Unit,
    onAuthError: (ApiException) -> Unit
): ManagedActivityResultLauncher<Intent, ActivityResult> {
    val scope = rememberCoroutineScope()
    return rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)!!
            val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
            scope.launch {
                val authResult = Firebase.auth.signInWithCredential(credential).await()
                onAuthComplete(authResult)
            }
        } catch (e: ApiException) {
            onAuthError(e)
        }
    }
}