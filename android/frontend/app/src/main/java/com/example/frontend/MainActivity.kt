package com.example.frontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.frontend.ui.login.LoginScreen
import com.example.frontend.ui.login.ForgotPasswordScreen
import com.example.frontend.ui.signin.SignInScreen
import com.example.frontend.ui.genre.GenreSelectionScreen
import com.example.frontend.ui.home.HomeScreen
import com.example.frontend.ui.home.SearchScreen
import com.example.frontend.ui.profile.ProfileScreen
import androidx.compose.material3.Text
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast

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
    val auth = FirebaseAuth.getInstance()
    val currentUser = auth.currentUser

    val startDestination =
        if (auth.currentUser != null) "home"
        else "login"
    
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onLogin = { email, password ->
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Log.e("AUTH", task.exception?.message ?: "Login Error")
                        }
                    }
                },
                onRegister = { navController.navigate("signin") },
                onForgotPassword = { navController.navigate("forgot_password") }
            )
        }
        composable("forgot_password") {
            ForgotPasswordScreen(
                onResetPassword = { _ -> navController.navigate("login") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("signin") {
            SignInScreen(
                onLoginClick = { navController.popBackStack() },
                onSignIn = { username, email, password ->
                    auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val profileUpdates =
                                com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                    .setDisplayName(username)
                                    .build()

                            auth.currentUser
                                ?.updateProfile(profileUpdates)
                                ?.addOnCompleteListener {

                                    navController.navigate("genre") {
                                        popUpTo("signin") {
                                            inclusive = true
                                        }
                                    }
                                }
                        } else {
                            Log.e("AUTH", task.exception?.message ?: "Register Error")
                        }
                    }
                }
            )
        }
        composable("genre") {
            GenreSelectionScreen(
                onContinue = { navController.navigate("home") }
            )
        }
        composable("home") {
            HomeScreen(
                onProfileClick = { navController.navigate("profile") },
                onSearchClick = { navController.navigate("search") }
            )
        }
        composable("search") {
            SearchScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {

            val user = FirebaseAuth.getInstance().currentUser

            Log.d("PROFILE", "DisplayName: ${user?.displayName}")
            Log.d("PROFILE", "Email: ${user?.email}")

            ProfileScreen(
                username = user?.displayName ?: "Usuario",
                email = user?.email ?: "Sin correo",

                onBack = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },

                onLogout = {
                    auth.signOut()

                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
