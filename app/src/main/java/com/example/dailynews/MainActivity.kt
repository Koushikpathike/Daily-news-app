package com.example.dailynews

import android.content.Intent
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.dailynews.screens.LoginActivity
import com.example.dailynews.ui.theme.DailyNewsTheme
import com.google.firebase.auth.FirebaseAuth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseAuth.getInstance()

        enableEdgeToEdge()
        val newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        installSplashScreen()

        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        } else {
            setContent {

                val navController = rememberNavController()

                DailyNewsTheme {
                    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                        Column(
                            modifier = Modifier.padding(innerPadding)
                                .fillMaxSize()
                        ) {
                            NavHost(
                                navController = navController,
                                startDestination = HomePageScreen
                            ) {
                                composable<HomePageScreen> {
                                    HomePage(newsViewModel, navController)
                                }

                                composable<NewsArticleScreen> {
                                    val args = it.toRoute<NewsArticleScreen>()
                                    NewsArticlePage(args.url)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}