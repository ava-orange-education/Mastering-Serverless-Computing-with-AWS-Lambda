package com.jamesthang.salerecords

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.MutableState
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val emailError = remember { mutableStateOf(false) }
    val passwordError = remember { mutableStateOf(false) }
    val confirmPasswordError = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Back") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { it: PaddingValues ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Create new Account",
                fontSize = 24.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
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
                label = { Text("Email") },
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                isError = emailError.value,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start // Align content to the start
                ) {
                    Text("Enter a valid email address", color = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError.value = !isValidPassword(it)
                                },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                isError = passwordError.value,
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start // Align content to the start
                ) {
                    Text("Password must be at least 6 characters", color = Color.Red)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError.value = (password != it)
                                },
                label = { Text("Re-enter Password") },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                isError = confirmPasswordError.value,
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmPasswordError.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start // Align content to the start
                ) {
                    Text("Passwords do not match", color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    performSignUp(email, password, confirmPassword, emailError, passwordError, confirmPasswordError, navController)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("SIGN UP")
            }
        }
    }
}

private fun performSignUp(
    email: String,
    password: String,
    confirmPassword: String,
    emailErrorState: MutableState<Boolean>,
    passwordErrorState: MutableState<Boolean>,
    confirmPasswordErrorState: MutableState<Boolean>,
    navController: NavController
) {
    val isEmailValid = isValidEmail(email)
    val isPasswordValid = isValidPassword(password)
    val isConfirmPasswordValid = password == confirmPassword

    emailErrorState.value = !isEmailValid
    passwordErrorState.value = !isPasswordValid
    confirmPasswordErrorState.value = !isConfirmPasswordValid

    if (isEmailValid && isPasswordValid && isConfirmPasswordValid) {
        // Perform sign-up action
        val auth = Firebase.auth
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SignUpSuccess", "signUpWithEmail:success")
                    navController.navigate(AppScreen.Settings.route)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("SignUpFail", "signUpWithEmail:failure", task.exception)
                }
            }

    }
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}

private fun isValidPassword(password: String): Boolean {
    return password.length >= 6
}