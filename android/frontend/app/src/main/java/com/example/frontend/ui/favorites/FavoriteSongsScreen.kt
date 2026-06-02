package com.example.frontend.ui.favorites

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
import androidx.compose.ui.text.style.TextAlign
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
    val duration: String,
    val color: Color,
    val imageUrl: String? = null,
    val energy: Float = 0.5f,
    val danceability: Float = 0.5f,
    val instrumentalness: Float = 0.5f,
    val acousticness: Float = 0.5f,
    val tempo: Float = 0.5f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteSongsScreen(
    onBack: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val bgColor = Color(0xFF121212)
    
    var selectedSongForDetails by remember { mutableStateOf<FavoriteSong?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }
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
                            title = item["name"]?.toString() ?: "Sin título",
                            artist = item["artist"]?.toString() ?: "Artista desconocido",
                            duration = "3:30", 
                            color = Color(0xFF9C27B0), 
                            imageUrl = null,
                            energy = (item["energy"] as? Number)?.toFloat() ?: 0.5f,
                            danceability = (item["danceability"] as? Number)?.toFloat() ?: 0.5f,
                            instrumentalness = (item["instrumentalness"] as? Number)?.toFloat() ?: 0.5f,
                            acousticness = (item["acousticness"] as? Number)?.toFloat() ?: 0.5f,
                            tempo = (item["tempo"] as? Number)?.toFloat() ?: 0.5f
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
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF913AA1).copy(alpha = 0.8f), bgColor)
                            )
                        )
                )
                
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
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(favoriteSongs) { song ->
                        FavoriteSongItem(
                            song = song,
                            onMoreClick = {
                                selectedSongForDetails = song
                                showSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showSheet && selectedSongForDetails != null) {
        ModalBottomSheet(
            onDismissRequest = { 
                showSheet = false
                selectedSongForDetails = null
            },
            sheetState = sheetState,
            containerColor = Color(0xFF1E1E1E),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            selectedSongForDetails?.let { SongDetailsSheet(it) }
        }
    }
}

@Composable
fun FavoriteSongItem(
    song: FavoriteSong,
    onMoreClick: () -> Unit = {}
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
            .fillMaxWidth()
            .clickable { /* Play song */ },
        color = Color.Transparent,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
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
                    text = song.artist,
                    color = Color.Gray,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = song.duration,
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                Icon(
                    Icons.Default.Favorite, 
                    contentDescription = "Favorito", 
                    tint = Color(0xFF9C27B0),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(onClick = onMoreClick) {
                    Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun SongDetailsSheet(song: FavoriteSong) {
    val paletteBlue = Color(0xFF5D7A8C)
    val paletteGreen = Color(0xFF6B8C6D)
    val palettePurple = Color(0xFF8C73AD)
    val paletteRed = Color(0xFFAD7373)
    val paletteCyan = Color(0xFF738C8C)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Análisis Musical",
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        AttributeBar("Energía", song.energy, palettePurple)
        AttributeBar("Bailabilidad", song.danceability, paletteGreen)
        AttributeBar("Instrumental", song.instrumentalness, paletteBlue)
        AttributeBar("Acústico", song.acousticness, paletteRed)
        AttributeBar("Tempo / Ritmo", song.tempo, paletteCyan)
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "Basado en tus preferencias de Symphonix",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun AttributeBar(label: String, value: Float, color: Color) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, color = Color.LightGray, fontSize = 14.sp)
            Text("${(value * 100).toInt()}%", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { value },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(CircleShape),
            color = color,
            trackColor = Color(0xFF2A2A2A)
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun FavoriteSongsScreenPreview() {
    FavoriteSongsScreen()
}
