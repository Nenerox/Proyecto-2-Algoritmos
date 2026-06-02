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
                    onTabSelected = { index ->
                        selectedTab = index
                        when (index) {
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
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
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
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, null, tint = Color.White)
                    }
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                }
            }

            // Categories
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val categories = listOf("Todo", "Música", "Podcasts", "Estado de Ánimo")
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Color(0xFF1E1E1E),
                            labelColor = Color.Gray,
                            selectedContainerColor = Color(0xFFB582C7),
                            selectedLabelColor = Color.Black
                        ),
                        border = null
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Featured / Banner
            DiscoverWeeklyBanner()

            Spacer(Modifier.height(32.dp))

            SectionHeader("Canciones para ti")

            LaunchedEffect(selectedCategory) {
                val data = hashMapOf(
                    "limit" to 20,
                    "genre" to if (selectedCategory == "Todo") null else selectedCategory.lowercase()
                )
                FirebaseFunctions.getInstance().getHttpsCallable("getRecommendations").call(data)
                    .addOnSuccessListener { result ->
                        val dataList = result.data as? List<Map<String, Any>>
                        if (dataList != null) {
                            onRecomendacionesLoaded(dataList)
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("HOME", "Error loading recommendations", e)
                    }
            }

            // Recomendaciones
            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                if (recomendaciones.isEmpty()) {
                    Box(Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFFB582C7))
                    }
                } else {
                    recomendaciones.take(5).forEach { song ->
                        RecommendedCard(
                            id = song["id"].toString(),
                            title = song["name"].toString(),
                            artist = song["artist"].toString(),
                            album = song["album"].toString(),
                            imageUrl = null, // Se resuelve dinámicamente en el componente
                            color = Color(0xFFB582C7),
                            context = context,
                            onPlayClick = onSongClick,
                            onLikeClick = { onLike(song) }
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            SectionHeader("Basado en tus géneros")
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(favoriteGenres) { genre ->
                    PlaylistRow(
                        title = genre,
                        artist = "Mix personalizado",
                        info = "Actualizado hoy"
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
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
fun DiscoverWeeklyBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF913AA1), Color(0xFFB582C7))
                )
            )
            .clickable { /* Acción banner */ }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(24.dp)
        ) {
            Text("Symphonix Mix", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text("Tu dosis diaria\nde música nueva", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
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
fun PlaylistRow(title: String, artist: String, info: String) {
    val displayInfo = info // Uso ficticio para evitar error
    Column(modifier = Modifier.width(160.dp)) {
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
    id: String,
    title: String,
    artist: String,
    album: String,
    imageUrl: String?,
    color: Color,
    context: Context,
    onPlayClick: () -> Unit,
    onLikeClick: () -> Unit = {}
) {
    val currentAlbum = album // Uso ficticio
    var resolvedImageUrl by remember(id) { mutableStateOf(imageUrl) }

    LaunchedEffect(title, artist) {
        if (resolvedImageUrl == null) {
            kotlin.concurrent.thread {
                try {
                    val query = Uri.encode("$artist $title")
                    val searchUrl = "https://itunes.apple.com/search?term=$query&entity=song&limit=1"
                    val response = java.net.URL(searchUrl).readText()
                    val match = "\"artworkUrl100\":\"(.*?)\"".toRegex().find(response)
                    val url = match?.groupValues?.get(1)?.replace("100x100bb", "400x400bb")
                    if (url != null) { resolvedImageUrl = url }
                } catch (e: Exception) { }
            }
        }
    }

    Surface(
        onClick = onPlayClick,
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (resolvedImageUrl != null) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(resolvedImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Default.MusicNote, null, tint = color)
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(artist, color = Color.Gray, fontSize = 14.sp)
            }

            IconButton(onClick = onLikeClick) {
                Icon(Icons.Default.FavoriteBorder, null, tint = Color.White)
            }

            IconButton(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/track/$id"))
                context.startActivity(intent)
            }) {
                Icon(Icons.Default.PlayCircle, null, tint = Color.White, modifier = Modifier.size(32.dp))
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
