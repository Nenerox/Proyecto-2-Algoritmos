package com.example.frontend

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.ui.login.LoginScreen
import com.example.frontend.ui.signin.SignInScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SymphonixApp()
        }
    }
}

@Composable
fun SymphonixApp() {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onRegister = { navController.navigate("signin") },
                onForgotPassword = { /* TODO */ }
            )
        }
        composable("signin") {
            SignInScreen(
                onLoginClick = { navController.popBackStack() }
            )
        }
    }
}
