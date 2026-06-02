package com.example.frontend

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
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
import com.example.frontend.ui.favorites.FavoriteSongsScreen
import com.example.frontend.ui.dislikes.DislikeSongsScreen
import com.example.frontend.ui.song.SongScreen
import com.example.frontend.ui.song.SwipeableSong
import com.example.frontend.ui.form.MoodFormScreen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.functions.getHttpsCallable

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
    val context = LocalContext.current
    val navController = rememberNavController()
    val auth = FirebaseAuth.getInstance()
    
    // Lista de géneros favoritos compartida
    var favoriteGenres by remember { mutableStateOf(listOf<String>()) }

    //lista de canciones recomendadas de la ultima pagina de generos visitada
    var recomendaciones by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }

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
                            val profileUpdates = UserProfileChangeRequest.Builder()
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
                            navController.navigate("mood_form")
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
                        } else {
                            favoriteGenres = genres.map { it.replaceFirstChar { c -> c.uppercase() } }
                        }
                    }
            }
            HomeScreen(
                favoriteGenres = favoriteGenres,
                recomendaciones = recomendaciones,
                onRecomendacionesLoaded = { nuevasRecomendaciones: List<Map<String, Any>> -> recomendaciones = nuevasRecomendaciones },
                onLike = { song ->
                    val data = hashMapOf("trackId" to song["id"].toString())
                    FirebaseFunctions.getInstance()
                        .getHttpsCallable("addFavorite")
                        .call(data)
                        .addOnSuccessListener {
                            Log.d("FAVORITES", "Canción agregada a favoritos desde Home")
                            Toast.makeText(context, "Agregada a favoritos", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FAVORITES", "Error al agregar a favoritos", e)
                        }
                },
                onProfileClick = { navController.navigate("profile") },
                onSearchClick = { navController.navigate("search") },
                onMoodFormClick = { navController.navigate("mood_form") },
                onFavoritesClick = { navController.navigate("favorites") },
                onSongClick = { navController.navigate("song_discover") }
            )
        }
        composable("song_discover") {
            SongScreen(
                recomendaciones = recomendaciones,
                onLike = { song: SwipeableSong ->
                    val data = hashMapOf("trackId" to song.id)
                    FirebaseFunctions.getInstance().getHttpsCallable("addFavorite").call(data)
                        .addOnSuccessListener {
                            Log.d("FAVORITES", "Canción agregada a favoritos: ${song.title}")
                            Toast.makeText(context, "Agregada a favoritos", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("FAVORITES", "Error al agregar a favoritos", e)
                        }
                },
                onDislike = { song: SwipeableSong ->
                    val data = hashMapOf("trackId" to song.id)
                    FirebaseFunctions.getInstance().getHttpsCallable("addDislike").call(data)
                        .addOnSuccessListener {
                            Log.d("DISLIKE", "Cancion agregada a dislikes: ${song.title}")
                            Toast.makeText(context, "Agregada a dislikes", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Log.e("DISLIKE", "Error al marcar como dislike", e)
                        }
                },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("mood_form")
                        2 -> navController.navigate("favorites")
                        3 -> navController.navigate("profile")
                    }
                }
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
                        1 -> { /* Ya estamos aquí */ }
                        2 -> navController.navigate("favorites")
                        3 -> navController.navigate("profile")
                    }
                }
            )
        }
        composable("favorites") {
            FavoriteSongsScreen(
                onBack = { navController.popBackStack() },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("mood_form")
                        2 -> { /* Ya estamos aquí */ }
                        3 -> navController.navigate("profile")
                    }
                }
            )
        }
        composable("dislikes") {
            DislikeSongsScreen(
                onBack = { navController.popBackStack() },
                onTabSelected = { index ->
                    when (index) {
                        0 -> navController.navigate("home")
                        1 -> navController.navigate("mood_form")
                        2 -> navController.navigate("favorites")
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
                onMoodFormClick = { navController.navigate("mood_form") },
                onFavoritesClick = { navController.navigate("favorites") },
                onDislikesClick = { navController.navigate("dislikes") },
                onAboutUsClick = { navController.navigate("about_us") },
                onEditGenresClick = { navController.navigate("genre") },
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
