package com.example.frontend.ui.song

import android.net.Uri
import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.frontend.ui.home.SymphonixBottomBar
import com.example.frontend.ui.favorites.AttributeBar
import kotlin.math.roundToInt

data class SwipeableSong(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val spotifyId: String = "",
    val imageUrl: String? = null,
    val energy: Float = 0.5f,
    val danceability: Float = 0.5f,
    val instrumentalness: Float = 0.5f,
    val acousticness: Float = 0.5f,
    val tempo: Float = 0.5f
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongScreen(
    onTabSelected: (Int) -> Unit = {},
    onLike: (SwipeableSong) -> Unit = {},
    onDislike: (SwipeableSong) -> Unit = {},
    recomendaciones: List<Map<String, Any>>,
) {
    val bgColor = Color(0xFF121212)
    
    // Lista de canciones
    val songs = remember(recomendaciones) {
        recomendaciones.reversed().map {
            SwipeableSong(
                id = it["id"].toString(),
                title = it["name"].toString(),
                artist = it["artist"].toString(),
                album = it["album"].toString(),
                spotifyId = it["id"].toString(),

                energy = (it["energy"] as Number).toFloat(),
                danceability = (it["danceability"] as Number).toFloat(),
                instrumentalness = (it["instrumentalness"] as Number).toFloat(),
                acousticness = (it["acousticness"] as Number).toFloat(),
                tempo = ((it["tempo"] as Number).toFloat() / 250f).coerceIn(0f, 1f)
            )
        }.toMutableStateList()
    }

    var showAnalysis by remember { mutableStateOf(false) }
    val currentSong = songs.lastOrNull()

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .navigationBarsPadding()
                    .padding(top = 8.dp, bottom = 8.dp)
            ) {
                SymphonixBottomBar(
                    selectedIndex = -1, // Ninguna seleccionada por defecto o ajustar según lógica
                    onTabSelected = onTabSelected,
                    white = Color.White
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            if (songs.isNotEmpty()) {
                songs.forEachIndexed { index, song ->
                    key(song.id) {
                        SongCard(
                            song = song,
                            isTopCard = index == songs.lastIndex,
                            onSwipeLeft = {
                                onDislike(song)
                                songs.removeAt(index)
                            },
                            onSwipeRight = {
                                onLike(song)
                                songs.removeAt(index)
                            },
                            onClick = { showAnalysis = true }
                        )
                    }
                }
            } else {
                Text("¡No hay más recomendaciones por ahora!", color = Color.Gray, textAlign = TextAlign.Center)
            }
        }
    }

    if (showAnalysis && currentSong != null) {
        ModalBottomSheet(
            onDismissRequest = { showAnalysis = false },
            containerColor = Color(0xFF1E1E1E),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            SongAnalysisContent(currentSong)
        }
    }
}

@Composable
fun SongCard(
    song: SwipeableSong,
    isTopCard: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onClick: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val rotation = (offsetX.value / 10f).coerceIn(-15f, 15f)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var resolvedImageUrl by remember(song.id) { mutableStateOf(song.imageUrl) }

    LaunchedEffect(song.title, song.artist) {
        if (resolvedImageUrl == null) {
            kotlin.concurrent.thread {
                try {
                    val query = Uri.encode("${song.artist} ${song.title}")
                    val searchUrl = "https://itunes.apple.com/search?term=$query&entity=song&limit=1"
                    val response = java.net.URL(searchUrl).readText()
                    val match = "\"artworkUrl100\":\"(.*?)\"".toRegex().find(response)
                    val url = match?.groupValues?.get(1)?.replace("100x100bb", "600x600bb")
                    if (url != null) { resolvedImageUrl = url }
                } catch (e: Exception) { }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(0.65f)
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = rotation
            }
            .pointerInput(isTopCard) {
                if (!isTopCard) return@pointerInput
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX.value > 400f) {
                            onSwipeRight()
                        } else if (offsetX.value < -400f) {
                            onSwipeLeft()
                        } else {
                            scope.launch {
                                offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessLow))
                            }
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val newX = offsetX.value + dragAmount.x
                        scope.launch {
                            offsetX.snapTo(newX)
                        }
                    }
                )
            }
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { onClick() }
            .shadow(12.dp)
    ) {
        // Imagen de fondo (Ocupa casi todo)
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
            val hash = song.id.hashCode()
            val color1 = Color(0xFF000000 or (hash and 0xFFFFFF).toLong())
            val color2 = Color(0xFF000000 or ((hash shr 8) and 0xFFFFFF).toLong())
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(color1, color2))))
        }

        // Overlay gradiente para texto (Más pronunciado abajo)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(
                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.9f)),
                    startY = 300f
                ))
        )

        // Botón Play en el centro
        IconButton(
            onClick = {
                val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://open.spotify.com/track/${song.spotifyId}"))
                context.startActivity(intent)
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(80.dp)
                .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.White, modifier = Modifier.size(48.dp))
        }

        // Contenido Inferior (Info + Botones Manuales)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(song.title, color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(song.artist, color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Medium)
                Text(song.album, color = Color.Gray, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Botones Manuales
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón DISLIKE (X)
                IconButton(
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(-800f, tween(300))
                            onSwipeLeft()
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, Color.Red.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Close, null, tint = Color.Red, modifier = Modifier.size(32.dp))
                }

                // Botón LIKE (Corazón)
                IconButton(
                    onClick = {
                        scope.launch {
                            offsetX.animateTo(800f, tween(300))
                            onSwipeRight()
                        }
                    },
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, Color(0xFF1DB954).copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.Default.Favorite, null, tint = Color(0xFF1DB954), modifier = Modifier.size(32.dp))
                }
            }
        }
        
        // Indicadores visuales de Swipe (LIKE / NOPE)
        if (offsetX.value > 100f) {
            Box(
                Modifier.align(Alignment.TopStart).padding(24.dp).graphicsLayer { rotationZ = -15f }
                .border(4.dp, Color(0xFF1DB954), RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("LIKE", color = Color(0xFF1DB954), fontWeight = FontWeight.Black, fontSize = 32.sp)
            }
        } else if (offsetX.value < -100f) {
            Box(
                Modifier.align(Alignment.TopEnd).padding(24.dp).graphicsLayer { rotationZ = 15f }
                .border(4.dp, Color.Red, RoundedCornerShape(8.dp)).padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("NOPE", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 32.sp)
            }
        }
    }
}

@Composable
fun SongAnalysisContent(song: SwipeableSong) {
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
            text = "Análisis de la Pista",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        AttributeBar("Energía", song.energy, palettePurple)
        AttributeBar("Bailabilidad", song.danceability, paletteGreen)
        AttributeBar("Instrumental", song.instrumentalness, paletteBlue)
        AttributeBar("Acústico", song.acousticness, paletteRed)
        AttributeBar("Tempo", song.tempo, paletteCyan)
        
        Spacer(Modifier.height(16.dp))
        Text(
            "Toca para cerrar o desliza para decidir",
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SongScreenPreview() {
    SongScreen(recomendaciones = emptyList())
}
