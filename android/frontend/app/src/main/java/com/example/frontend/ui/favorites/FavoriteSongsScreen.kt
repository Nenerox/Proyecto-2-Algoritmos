package com.example.frontend.ui.favorites

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.home.SymphonixBottomBar

data class FavoriteSong(
    val id: String,
    val title: String,
    val artist: String,
    val duration: String,
    val color: Color,
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
    val accentColor = Color(0xFF9C27B0)
    
    var selectedSongForDetails by remember { mutableStateOf<FavoriteSong?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showSheet by remember { mutableStateOf(false) }

    val favoriteSongs = remember {
        listOf(
            FavoriteSong("1", "Summertime Sadness", "Lana Del Rey", "4:25", Color(0xFFB582C7), 0.6f, 0.4f, 0.1f, 0.3f, 0.4f),
            FavoriteSong("2", "Blinding Lights", "The Weeknd", "3:20", Color(0xFFFFC107), 0.8f, 0.7f, 0.0f, 0.1f, 0.8f),
            FavoriteSong("3", "Starboy", "The Weeknd", "3:50", Color(0xFFE91E63), 0.7f, 0.8f, 0.0f, 0.1f, 0.9f),
            FavoriteSong("4", "Perfect", "Ed Sheeran", "4:23", Color(0xFF1DB954), 0.4f, 0.3f, 0.0f, 0.7f, 0.3f),
            FavoriteSong("5", "Flowers", "Miley Cyrus", "3:21", Color(0xFFFF4081), 0.7f, 0.7f, 0.0f, 0.2f, 0.6f),
            FavoriteSong("6", "As It Was", "Harry Styles", "2:47", Color(0xFF03A9F4), 0.7f, 0.8f, 0.0f, 0.2f, 0.8f),
            FavoriteSong("7", "Bohemian Rhapsody", "Queen", "5:55", Color(0xFF9C27B0), 0.5f, 0.3f, 0.1f, 0.4f, 0.4f),
            FavoriteSong("8", "Rolling in the Deep", "Adele", "3:48", Color(0xFF795548), 0.7f, 0.5f, 0.0f, 0.2f, 0.5f),
            FavoriteSong("9", "Yellow", "Coldplay", "4:29", Color(0xFFFFEB3B), 0.5f, 0.3f, 0.0f, 0.4f, 0.4f),
            FavoriteSong("10", "Circles", "Post Malone", "3:35", Color(0xFF607D8B), 0.6f, 0.7f, 0.0f, 0.3f, 0.5f)
        )
    }

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

            // Acciones Rápidas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botón Play All con degradado
                Button(
                    onClick = { /* Play all */ },
                    modifier = Modifier
                        .height(48.dp)
                        .weight(1f)
                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF5A107C), Color(0xFF913AA1))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.Black)
                            Spacer(Modifier.width(8.dp))
                            Text("Reproducir todo", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(Modifier.width(16.dp))
                
                IconButton(
                    onClick = { /* Shuffle */ },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF252525), CircleShape)
                ) {
                    Icon(Icons.Default.Shuffle, null, tint = accentColor)
                }
            }

            // Lista de canciones
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

    if (showSheet && selectedSongForDetails != null) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color(0xFF1E1E1E),
            dragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray) }
        ) {
            SongDetailsSheet(selectedSongForDetails!!)
        }
    }
}

@Composable
fun FavoriteSongItem(
    song: FavoriteSong,
    onMoreClick: () -> Unit = {}
) {
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
            // Portada de Álbum estilizada
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(song.color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.MusicNote, 
                    null, 
                    tint = song.color,
                    modifier = Modifier.size(28.dp)
                )
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
