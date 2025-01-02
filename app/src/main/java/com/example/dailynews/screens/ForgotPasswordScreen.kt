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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordScreen : ComponentActivity() {
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ForgotPasswordScreenContent(
                onResetPassword = { email -> resetPassword(email) },
                onBackToLogin = { navigateBackToLogin() }
            )
        }
    }

    private fun resetPassword(email: String) {
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            return
        }

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateBackToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreenContent(onResetPassword: (String) -> Unit, onBackToLogin: () -> Unit) {
    var email by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F4F8)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .background(Color.White, shape = RoundedCornerShape(24.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password",
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color(0xFF6A1B9A),
                    unfocusedBorderColor = Color.Gray
                )
            )

            Button(
                onClick = { onResetPassword(email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Reset Password",
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            }

            TextButton(onClick = onBackToLogin) {
                Text(
                    text = "Back to Login",
                    style = TextStyle(fontSize = 16.sp, color = Color(0xFF6A1B9A))
                )
            }
        }
    }
}