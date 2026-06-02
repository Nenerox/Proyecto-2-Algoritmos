package com.example.frontend.ui.favorites

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.frontend.ui.home.SymphonixBottomBar
import com.google.firebase.functions.FirebaseFunctions

data class FavoriteSong(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val color: Color,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSongsScreen(
    onBack: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val bgColor = Color(0xFF121212)

    var isLoading by remember { mutableStateOf(true) }

    var favoriteSongs by remember { mutableStateOf(listOf<FavoriteSong>()) }

    LaunchedEffect(Unit) {
        FirebaseFunctions.getInstance()
            .getHttpsCallable("getFavorites")
            .call()
            .addOnSuccessListener { result ->
                val data = result.data as? List<*>
                if (data != null) {
                    favoriteSongs = data.filterIsInstance<Map<String, Any>>().map { item ->
                        FavoriteSong(
                            id = item["id"]?.toString() ?: "",
                            title = item["name"]?.toString() ?: "",
                            artist = item["artist"]?.toString() ?: "",
                            album = item["album"]?.toString() ?: "",
                            color = Color(0xFF9C27B0),
                            imageUrl = null
                        )
                    }
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("FAVORITES", "Error al cargar favoritos", e)
                isLoading = false
            }
    }

    Scaffold(
        containerColor = bgColor,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Regresar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .navigationBarsPadding()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                SymphonixBottomBar(
                    selectedIndex = 2, // Library/Favorites
                    onTabSelected = onTabSelected,
                    white = Color.White
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header con Gradiente Dinámico
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = "Tus Favoritos",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${favoriteSongs.size} canciones guardadas",
                        color = Color.LightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // Lista de canciones
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF9C27B0))
                }
            } else if (favoriteSongs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aún no tienes canciones favoritas", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp), // Alineado a 24dp
                    verticalArrangement = Arrangement.spacedBy(16.dp), // Multiplo de 8
                    contentPadding = PaddingValues(bottom = 32.dp) // Multiplo de 8
                ) {
                    items(favoriteSongs) { song ->
                        FavoriteSongItem(
                            song = song,
                            onRemoveClick = {
                                favoriteSongs = favoriteSongs.filter { it.id != song.id }
                                FirebaseFunctions.getInstance()
                                    .getHttpsCallable("removeFavorite")
                                    .call(hashMapOf("trackId" to song.id))
                                    .addOnFailureListener {
                                        Log.e("FAVORITES", "Error al eliminar")
                                    }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteSongItem(
    song: FavoriteSong,
    onRemoveClick: () -> Unit = {}
) {
    val context = LocalContext.current
    var resolvedImageUrl by remember(song.id, song.imageUrl) { mutableStateOf(song.imageUrl) }

    LaunchedEffect(song.id, song.title, song.artist, song.imageUrl) {
        if (song.imageUrl == null) {
            kotlin.concurrent.thread {
                try {
                    val query = android.net.Uri.encode("${song.artist} ${song.title}")
                    val searchUrl = "https://itunes.apple.com/search?term=$query&entity=song&limit=1"
                    val response = java.net.URL(searchUrl).readText()
                    val match = "\"artworkUrl100\":\"(.*?)\"".toRegex().find(response)
                    val url = match?.groupValues?.get(1)?.replace("100x100bb", "400x400bb")
                    if (url != null) {
                        resolvedImageUrl = url
                    }
                } catch (e: Exception) {
                    Log.e("IMAGE_RESOLVER", "Error resolving image", e)
                }
            }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {

                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(
                        "https://open.spotify.com/track/${song.id}"))
                context.startActivity(intent)
            },
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp) // Multiplo de 8
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp) // Multiplo de 8
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp) // Multiplo de 8
                    .clip(RoundedCornerShape(12.dp))
                    .background(song.color.copy(alpha = 0.2f)),
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
                    val hash = song.id.hashCode()
                    val color1 = Color(0xFF000000 or (hash and 0xFFFFFF).toLong())
                    val color2 = Color(0xFF000000 or ((hash shr 8) and 0xFFFFFF).toLong())
                    
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Brush.linearGradient(listOf(color1, color2))),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = song.title.take(1).uppercase(),
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${song.artist} • ${song.album}",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onRemoveClick) {
                    Icon(
                        Icons.Default.Favorite, 
                        contentDescription = "Quitar de favoritos", 
                        tint = Color(0xFF1DB954),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun FavoriteSongsScreenPreview() {
    FavoriteSongsScreen()
}
