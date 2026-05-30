package com.example.frontend

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
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
import com.example.frontend.ui.profile.AboutUsScreen
import com.example.frontend.ui.form.MoodFormScreen
import com.example.frontend.ui.form.MoodFormData
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
    
    // Lista de géneros favoritos compartida
    var favoriteGenres by remember { mutableStateOf(listOf<String>()) }

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
                onResetPassword = { email ->
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                navController.navigate("login")
                            } else {
                                Log.e("AUTH",task.exception?.message ?: "Error")
                            }
                        }
                },
                onBack = {
                    navController.popBackStack()
                }
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

                                    navController.navigate("home") {
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
                onContinue = { selected ->
                    favoriteGenres = selected
                    val data = hashMapOf(
                        "genres" to selected.map { it.lowercase() }
                    )

                    FirebaseFunctions.getInstance()
                        .getHttpsCallable("savePreferences").call(data)

                        .addOnSuccessListener {
                            Log.d("GENRES","Se guardaron los generos")
                            navController.navigate("home")
                        }
                        .addOnFailureListener {
                            Log.e("GENRES", it.message ?: "Error guardando generos")
                        }
                }
            )
        }
        composable("home") {
            LaunchedEffect(Unit) {
                FirebaseFunctions.getInstance().getHttpsCallable("getUserGenres").call()
                    .addOnSuccessListener { result ->
                        val genres =
                            (result.data as List<*>)
                                .map { it.toString() }
                        if (genres.isEmpty()) {
                            navController.navigate("genre")
                        }
                    }
            }
            HomeScreen(
                onProfileClick = { navController.navigate("profile") },
                onSearchClick = { navController.navigate("search") },
                onMoodFormClick = { navController.navigate("mood_form") }
            )
        }
        composable("mood_form") {
            MoodFormScreen(
                onFinish = { form ->
                    FirebaseFunctions.getInstance()
                        .getHttpsCallable("saveDailyMood")
                        .call(
                            hashMapOf(
                                "valence" to form.valence,
                                "energy" to form.energy,
                                "danceability" to form.danceability,
                                "instrumentalness" to form.instrumentalness,
                                "acousticness" to form.acousticness,
                                "tempo" to form.tempo,
                                "wantNewMusic" to form.wantNewMusic
                            )
                        )

                        .addOnSuccessListener {
                            Log.d("MOOD", "Mood guardado correctamente")
                            navController.navigate("home")
                        }
                        .addOnFailureListener {
                            Log.e("MOOD", it.message ?: "Error"
                            )
                        }
                },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home")
                        3 -> navController.navigate("profile")
                    }
                }
            )
        }
        composable("search") {
            SearchScreen(
                onBack = { navController.popBackStack() }
            )
        }
        composable("profile") {

            val user = FirebaseAuth.getInstance().currentUser

            LaunchedEffect(Unit) {
                FirebaseFunctions.getInstance().getHttpsCallable("getUserGenres").call()
                    .addOnSuccessListener { result ->

                        favoriteGenres =
                            (result.data as List<*>)
                                .map { it.toString().replaceFirstChar { c -> c.uppercase() } }
                    }
            }

            ProfileScreen(
                username = user?.displayName ?: "Usuario",
                email = user?.email ?: "Sin correo",
                favoriteGenres = favoriteGenres,
                onBack = { navController.popBackStack() },
                onHomeClick = { navController.navigate("home") },
                onAboutUsClick = { navController.navigate("about_us") },
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("about_us") {
            AboutUsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
