package com.example.frontend.ui.song

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.frontend.ui.home.SymphonixBottomBar
import kotlinx.coroutines.launch

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

@Composable
fun SongScreen(
    onTabSelected: (Int) -> Unit = {},
    onLike: (SwipeableSong) -> Unit = {},
    onDislike: (SwipeableSong) -> Unit = {},
    recomendaciones: List<Map<String, Any>> = emptyList(),
) {
    val bgColor = Color(0xFF121212)
    
    val songs = remember(recomendaciones) {
        if (recomendaciones.isEmpty()) {
            mutableStateListOf(
                SwipeableSong("1", "Perfect", "Ed Sheeran", "Divide", "0tgVpS3mghpB7Yv3tobP1Z", null, 0.4f, 0.3f, 0.0f, 0.7f, 0.3f),
                SwipeableSong("2", "Starboy", "The Weeknd", "Starboy", "7MXVkv9YvqcS2SQU97S4S7", null, 0.7f, 0.8f, 0.0f, 0.1f, 0.9f)
            )
        } else {
            recomendaciones.reversed().map {
                SwipeableSong(
                    id = it["id"].toString(),
                    title = it["name"].toString(),
                    artist = it["artist"].toString(),
                    album = it["album"].toString(),
                    spotifyId = it["id"].toString(),
                    energy = (it["energy"] as? Number)?.toFloat() ?: 0.5f,
                    danceability = (it["danceability"] as? Number)?.toFloat() ?: 0.5f,
                    instrumentalness = (it["instrumentalness"] as? Number)?.toFloat() ?: 0.5f,
                    acousticness = (it["acousticness"] as? Number)?.toFloat() ?: 0.5f,
                    tempo = ((it["tempo"] as? Number)?.toFloat() ?: 120f) / 200f
                )
            }.toMutableStateList()
        }
    }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().background(Color.Black.copy(alpha = 0.9f)).navigationBarsPadding().padding(vertical = 8.dp)) {
                SymphonixBottomBar(selectedIndex = -1, onTabSelected = onTabSelected, white = Color.White)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
            if (songs.isNotEmpty()) {
                songs.forEachIndexed { index, song ->
                    key(song.id) {
                        SwipeCardContainer(
                            song = song,
                            isTopCard = index == songs.lastIndex,
                            onSwipeLeft = { onDislike(song); songs.removeAt(index) },
                            onSwipeRight = { onLike(song); songs.removeAt(index) }
                        )
                    }
                }
            } else {
                Text("¡No hay más recomendaciones!", color = Color.Gray)
            }
        }
    }
}

@Composable
fun SwipeCardContainer(
    song: SwipeableSong,
    isTopCard: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val rotation = (offsetX.value / 10f).coerceIn(-15f, 15f)
    val scope = rememberCoroutineScope()
    
    // Estado para el volteo (Flip)
    var isFlipped by remember { mutableStateOf(false) }
    val flipRotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth(0.8f) // Reducido para parecerse a la imagen
            .aspectRatio(0.6f) // Más largo que ancho
            .graphicsLayer {
                translationX = offsetX.value
                rotationZ = rotation
                rotationY = flipRotation
                cameraDistance = 12f * density
            }
            .pointerInput(isTopCard) {
                if (!isTopCard || isFlipped) return@pointerInput
                detectDragGestures(
                    onDragEnd = {
                        if (offsetX.value > 400f) onSwipeRight()
                        else if (offsetX.value < -400f) onSwipeLeft()
                        else scope.launch { offsetX.animateTo(0f, spring(stiffness = Spring.StiffnessLow)) }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        scope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
                    }
                )
            }
            .clip(RoundedCornerShape(32.dp))
            .background(Color(0xFF1E1E1E))
            .clickable { isFlipped = !isFlipped }
            .shadow(16.dp)
    ) {
        if (flipRotation <= 90f || flipRotation >= 270f) {
            // Cara Frontal (Imagen y Datos)
            SongCardFront(song, onSwipeLeft, onSwipeRight, offsetX.value)
        } else {
            // Cara Trasera (Análisis Musical)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                SongAnalysisContent(song)
            }
        }
    }
}

@Composable
fun SongCardFront(song: SwipeableSong, onSwipeLeft: () -> Unit, onSwipeRight: () -> Unit, offsetX: Float) {
    val context = LocalContext.current
    var resolvedImageUrl by remember(song.id) { mutableStateOf(song.imageUrl) }

    LaunchedEffect(song.title, song.artist) {
        if (resolvedImageUrl == null) {
            kotlin.concurrent.thread {
                try {
                    val query = Uri.encode("${song.artist} ${song.title}")
                    val response = java.net.URL("https://itunes.apple.com/search?term=$query&entity=song&limit=1").readText()
                    val match = "\"artworkUrl100\":\"(.*?)\"".toRegex().find(response)
                    val url = match?.groupValues?.get(1)?.replace("100x100bb", "600x600bb")
                    if (url != null) resolvedImageUrl = url
                } catch (e: Exception) { }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Imagen principal (Sin degradado de fondo, como pediste)
        if (resolvedImageUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(resolvedImageUrl).crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(Modifier.fillMaxSize().background(Color(0xFF252525)), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.MusicNote, null, tint = Color.Gray, modifier = Modifier.size(64.dp))
            }
        }

        // Overlay sutil para legibilidad del texto abajo
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)), startY = 600f)))

        // Contenido
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(song.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(song.artist, color = Color.LightGray, fontSize = 16.sp)
            
            Spacer(Modifier.height(24.dp))
            
            // Botones: X (izq), Play (centro), Corazón (der)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                // DISLIKE
                IconButton(
                    onClick = onSwipeLeft,
                    modifier = Modifier.size(56.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) { Icon(Icons.Default.Close, null, tint = Color.White) }

                // PLAY
                IconButton(
                    onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://open.spotify.com/track/${song.spotifyId}"))) },
                    modifier = Modifier.size(72.dp).background(Color.White, CircleShape)
                ) { Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(40.dp)) }

                // LIKE
                IconButton(
                    onClick = onSwipeRight,
                    modifier = Modifier.size(56.dp).background(Color.Black.copy(alpha = 0.4f), CircleShape).border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                ) { Icon(Icons.Default.Favorite, null, tint = Color(0xFF1DB954)) }
            }
        }
        
        // Indicadores visuales de Swipe
        if (offsetX > 100f) {
            Box(Modifier.align(Alignment.TopStart).padding(24.dp).border(4.dp, Color(0xFF1DB954), RoundedCornerShape(8.dp)).padding(8.dp)) {
                Text("LIKE", color = Color(0xFF1DB954), fontWeight = FontWeight.Black, fontSize = 24.sp)
            }
        } else if (offsetX < -100f) {
            Box(Modifier.align(Alignment.TopEnd).padding(24.dp).border(4.dp, Color.Red, RoundedCornerShape(8.dp)).padding(8.dp)) {
                Text("NOPE", color = Color.Red, fontWeight = FontWeight.Black, fontSize = 24.sp)
            }
        }
    }
}

@Composable
fun SongAnalysisContent(song: SwipeableSong) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Análisis Musical", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 24.dp))
        AttributeBar("Energía", song.energy, Color(0xFF8C73AD))
        AttributeBar("Bailabilidad", song.danceability, Color(0xFF6B8C6D))
        AttributeBar("Instrumental", song.instrumentalness, Color(0xFF5D7A8C))
        AttributeBar("Acústico", song.acousticness, Color(0xFFAD7373))
        AttributeBar("Tempo", song.tempo, Color(0xFF738C8C))

        Spacer(Modifier.height(32.dp))
        Text("Toca para volver", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
    }
}

@Composable
fun AttributeBar(
    label: String,
    value: Float,
    color: Color
) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                label,
                color = Color.LightGray,
                fontSize = 14.sp
            )

            Text(
                "${(value * 100).toInt()}%",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
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

@Preview
@Composable
fun SongScreenPreview() { SongScreen() }
