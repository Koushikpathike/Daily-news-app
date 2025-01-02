package com.example.dailynews.screens

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class SignUpScreen : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SignUpScreen(
                onSignUp = { email, password -> signUpUser(email, password) },
                onLoginRedirect = { startActivity(Intent(this, LoginActivity::class.java)) }
            )
        }
    }

    private fun signUpUser(email: String, password: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show()
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnCompleteListener { verificationTask ->
                            if (verificationTask.isSuccessful) {
                                Toast.makeText(
                                    this,
                                    "Sign up successful! Please verify your email before logging in.",
                                    Toast.LENGTH_LONG
                                ).show()
                                auth.signOut()
                                startActivity(Intent(this, LoginActivity::class.java))
                                finish()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Failed to send verification email: ${verificationTask.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(this, "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(onSignUp: (String, String) -> Unit, onLoginRedirect: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Create Your Account",
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6A1B9A)),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                leadingIcon = {
                    Icon(Icons.Filled.Email, contentDescription = "Email Icon")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6A1B9A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                leadingIcon = {
                    Icon(Icons.Filled.Lock, contentDescription = "Password Icon")
                },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6A1B9A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Button(
                onClick = { onSignUp(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Sign Up",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            }

            TextButton(onClick = onLoginRedirect) {
                Text(
                    text = "Already have an account? Log In",
                    style = TextStyle(color = Color(0xFF6A1B9A), fontWeight = FontWeight.Medium)
                )
            }
        }
    }
}