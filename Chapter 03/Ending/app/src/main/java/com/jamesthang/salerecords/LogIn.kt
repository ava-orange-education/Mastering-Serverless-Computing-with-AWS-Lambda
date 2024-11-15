package com.jamesthang.salerecords

import android.app.Instrumentation
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val launcher = rememberFirebaseAuthLauncher(
        onAuthComplete = { result ->
            Log.d("SignInSuccess", "signInWithGoogle:success")
            navController.navigate(AppScreen.Settings.route)
        },
        onAuthError = { error ->
            Log.w("SignInFail", "signInWithGoogle:failure$error")
        }
    )
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
        Text(
            text = "Mastering Firebase",
            fontSize = 24.sp,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Replace with the actual image resource
        Image(
            painter = painterResource(id = R.drawable.firebase),
            contentDescription = "Firebase Logo"
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError.value = !isValidEmail(it)
            },
            label = { Text("Enter email") },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            isError = emailError.value,
            modifier = Modifier.fillMaxWidth()
        )
        if (emailError.value) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align content to the start
            ) {
                Text(
                    "Enter a valid email address",
                    color = Color.Red
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError.value = !isValidPassword(it)
            },
            label = { Text("Enter password") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            isError = passwordError.value,
            modifier = Modifier.fillMaxWidth()
        )
        if (passwordError.value) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start // Align content to the start
            ) {
                Text(
                    "Password must be at least 6 characters",
                    color = Color.Red
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { performLogin(email, password, emailError, passwordError, navController) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("LOG IN")
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Doesn't have account?")
            TextButton(onClick = { performSignUp(navController) }) {
                Text("SIGN UP", color = Color.Blue)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("or")
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { performGoogleAuthentication(launcher, context) },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue with Google", color = Color.White)
        }
    }
}

private fun performLogin(
    email: String,
    password: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    navController: NavController
) {
    val isEmailValid = isValidEmail(email)
    val isPasswordValid = isValidPassword(password)

    emailErrorState.value = !isEmailValid
    passwordErrorState.value = !isPasswordValid

    if (isEmailValid && isPasswordValid) {
        // Implement actual login logic here
        val auth = Firebase.auth
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignInSuccess", "signInWithEmail:success")
                    navController.navigate(AppScreen.Settings.route)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignInFail", "signInWithEmail:failure", task.exception)
                }
            }
    }
}

private fun performGoogleAuthentication(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>, context: Context) {
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

private fun performSignUp(navController: NavController) {
    // Here you would also handle the logic to actually log the user out from your backend
    // For now, it will just clear the back stack and navigate to the login screen
    navController.navigate(AppScreen.SignUp.route)
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}