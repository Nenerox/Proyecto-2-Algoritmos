package com.example.frontend.ui.dislikes

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

data class DislikedSong(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val color: Color,
    val imageUrl: String? = null
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DislikeSongsScreen(
    onBack: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val bgColor = Color(0xFF121212)
    var isLoading by remember { mutableStateOf(true) }
    var dislikedSongs by remember { mutableStateOf(listOf<DislikedSong>()) }

    LaunchedEffect(Unit) {
        FirebaseFunctions.getInstance()
            .getHttpsCallable("getDislikes")
            .call()
            .addOnSuccessListener { result ->
                val data = result.data as? List<*>
                if (data != null) {
                    dislikedSongs = data.mapNotNull { item ->
                        val map = item as? Map<*, *> ?: return@mapNotNull null

                        DislikedSong(
                            id = map["id"]?.toString() ?: "",
                            title = map["name"]?.toString() ?: "Sin título",
                            artist = map["artist"]?.toString() ?: "Artista desconocido",
                            album = map["album"]?.toString() ?: "Álbum desconocido",
                            color = Color(0xFFF44336),
                            imageUrl = null
                        )
                    }
                }
                isLoading = false
            }
            .addOnFailureListener { e ->
                Log.e("DISLIKES", "Error al cargar dislikes", e)
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
                    selectedIndex = -1,
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
                        text = "Lista de Dislikes",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${dislikedSongs.size} canciones marcadas",
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
            } else if (dislikedSongs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No tienes canciones con dislike", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    items(dislikedSongs) { song ->
                        DislikedSongItem(
                            song = song,
                            onRemoveClick = {
                                dislikedSongs = dislikedSongs.filter { it.id != song.id }
                                FirebaseFunctions.getInstance()
                                    .getHttpsCallable("removeDislike")
                                    .call(hashMapOf("trackId" to song.id))
                                    .addOnFailureListener {
                                        Log.e("DISLIKES", "Error al eliminar dislike")
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
fun DislikedSongItem(
    song: DislikedSong,
    onRemoveClick: () -> Unit = {}
) {
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
            .fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
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

            IconButton(onClick = onRemoveClick) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Quitar dislike",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun DislikeSongsScreenPreview() {
    DislikeSongsScreen()
}
