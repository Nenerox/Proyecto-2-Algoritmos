package com.example.frontend.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.functions.FirebaseFunctions

@Composable
fun HomeScreen(
    favoriteGenres: List<String> = listOf("Pop", "Rock", "Reggaetón", "Heavy Metal"),
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMoodFormClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onSongClick: () -> Unit = {},
    recomendaciones: List<Map<String, Any>>,
    onRecomendacionesLoaded: (List<Map<String, Any>>) -> Unit,
    onLike: (Map<String, Any>) -> Unit = {}
) {
    val bgColor = Color(0xFF121212)
    val white = Color(0xFFFFFFFF)
    val currentUser = FirebaseAuth.getInstance().currentUser
    val context = LocalContext.current

    val username =
        currentUser?.displayName
            ?: currentUser?.email?.substringBefore("@")
            ?: "Usuario"

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf("Todo") }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF000000).copy(alpha = 0.9f))
                    .navigationBarsPadding()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                SymphonixBottomBar(
                    selectedIndex = selectedTab,
                    onTabSelected = {
                        selectedTab = it
                        when (it) {
                            0 -> { /* Home */ }
                            1 -> onMoodFormClick()
                            2 -> onFavoritesClick()
                            3 -> onProfileClick()
                        }
                    },
                    white = white
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 32.dp) // Alineado con MoodFormScreen
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp), // Margen estándar de 24dp
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "¡Hola, $username!",
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Tu ritmo de hoy",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
                
                Row {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Categories
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Todo") + favoriteGenres
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1E1E1E),
                            labelColor = Color.Gray,
                            selectedContainerColor = Color(0xFFFFFFFF),
                            selectedLabelColor = Color.Black
                        ),
                        border = null,
                        shape = RoundedCornerShape(16.dp)
                    )
                }
            }

            LaunchedEffect(selectedCategory) {
                val data = if (selectedCategory == "Todo") {
                    hashMapOf("limit" to 20)
                } else {
                    hashMapOf(
                        "genre" to selectedCategory.lowercase(),
                        "limit" to 20
                    )
                }
                
                FirebaseFunctions.getInstance().getHttpsCallable("getRecommendations").call(data)
                    .addOnSuccessListener { result ->
                        val dataList = result.data as? List<Map<String, Any>>
                        if (dataList != null) {
                            onRecomendacionesLoaded(dataList)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("RECOMMENDATIONS_ERROR", e.message ?: "Error de recomendaciones", e)
                    }
            }

            Spacer(Modifier.height(16.dp))

            when (selectedCategory) {
                "Todo" -> {
                    Banner()
                    Spacer(modifier = Modifier.height(32.dp))
                    SectionHeader("Canciones para ti")
                }
                else -> {
                    SectionHeader("Canciones de $selectedCategory")
                }
            }

            // Recomendaciones
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                if (recomendaciones.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFB582C7))
                    }
                } else {
                    recomendaciones.forEach { song ->
                        RecommendedCard(
                            spotifyId = song["id"].toString(),
                            title = song["name"].toString(),
                            artist = song["artist"].toString(),
                            album = song["album"].toString(),
                            imageUrl = song["image_url"]?.toString(),
                            tint = Color(0xFFB582C7),
                            context = context,
                            onClick = onSongClick,
                            onLikeClick = { onLike(song) }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
    )
}

@Composable
fun Banner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF913AA1), Color(0xFFB582C7))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Symphonix Mix",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                lineHeight = 28.sp
            )

            Text(
                text = "Tu dosis diaria\nde música nueva",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }
        Icon(
            Icons.Default.MusicNote,
            null,
            tint = Color.White.copy(alpha = 0.2f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(120.dp)
                .offset(x = 20.dp, y = 20.dp)
        )
    }
}

@Composable
fun PlaylistRow(title: String, artist: String, info: String, onClick: () -> Unit = {}) {
    val displayInfo = info // Uso ficticio para evitar error
    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFF1E1E1E)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Album, null, tint = Color.DarkGray, modifier = Modifier.size(64.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        Text(artist, color = Color.Gray, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun RecommendedCard(
    spotifyId: String,
    title: String,
    artist: String,
    album: String,
    imageUrl: String? = null,
    tint: Color,
    context: Context,
    onClick: () -> Unit,
    onLikeClick: () -> Unit = {}
) {
    // LLAVE: Usamos title, artist y imageUrl como claves para que el estado se resetee
    var resolvedImageUrl by remember(title, artist, imageUrl) { mutableStateOf(imageUrl) }

    LaunchedEffect(title, artist, imageUrl) {
        if (imageUrl == null) {
            kotlin.concurrent.thread {
                try {
                    val query = Uri.encode("$artist $title")
                    val searchUrl = "https://itunes.apple.com/search?term=$query&entity=song&limit=1"
                    val response = java.net.URL(searchUrl).readText()
                    val match = "\"artworkUrl100\":\"(.*?)\"".toRegex().find(response)
                    val url = match?.groupValues?.get(1)?.replace("100x100bb", "600x600bb")
                    if (url != null) {
                        resolvedImageUrl = url
                    }
                } catch (e: Exception) {
                    Log.e("ITUNES_ERROR", "Error fetching image: ${e.message}")
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onClick() },
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(110.dp)
                    .background(tint.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                if (resolvedImageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(resolvedImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Album Art",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Arte Generativo basado en el ID
                    val hash = spotifyId.hashCode()
                    val color1 = Color(0xFF000000 or (hash and 0xFFFFFF).toLong())
                    val color2 = Color(0xFF000000 or ((hash shr 8) and 0xFFFFFF).toLong())
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(color1, color2))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title.take(1).uppercase(),
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1
                )
                Text(
                    "$artist • $album",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Row(
                modifier = Modifier.padding(end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = Color.White)
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(Color.White.copy(alpha = 0.05f), CircleShape)
                        .clickable {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                                "https://open.spotify.com/track/$spotifyId"
                            ))
                            context.startActivity(intent)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.White)
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        recomendaciones = emptyList(),
        onRecomendacionesLoaded = {}
    )
}
