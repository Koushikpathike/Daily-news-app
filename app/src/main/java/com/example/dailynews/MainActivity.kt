package com.example.dailynews

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.example.dailynews.ui.theme.DailyNewsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val newsViewModel = ViewModelProvider(this)[NewsViewModel :: class.java]
        installSplashScreen()
        setContent {
            DailyNewsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier.padding(innerPadding)
                            .fillMaxSize()
                    ) {
//                        Text(text = "Daily News App",
//                            modifier = Modifier.align(Alignment.CenterHorizontally),
//                            color = Color.Gray,
//                            fontSize = 25.sp,
//                            fontFamily = FontFamily.SansSerif
//                            )
                        HomePage(newsViewModel)
                    }
                }
            }
        }
    }
}

