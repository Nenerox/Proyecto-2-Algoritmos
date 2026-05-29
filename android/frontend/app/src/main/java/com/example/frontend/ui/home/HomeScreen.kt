package com.example.frontend.ui.home

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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {},
    onSearchClick: () -> Unit = {},
    onMoodFormClick: () -> Unit = {}
) {
    val bgColor = Color(0xFF121212) // Un negro más profundo y moderno
    val white = Color(0xFFFFFFFF)
    val accentColor = Color(0xFFB582C7)
    val currentUser = FirebaseAuth.getInstance().currentUser

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
                            3 -> onProfileClick()
                            else -> { /* TODO: Other tabs */ }
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
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            // Header con Estilo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bienvenido,",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = username,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Buscar", tint = Color.White)
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Color(0xFF1DB954), Color(0xFF913AA1))))
                            .padding(2.dp)
                            .clickable { onProfileClick() }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color(0xFF252525)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, null, tint = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filtros Modernos
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val filters = listOf("Todo", "Favoritos", "Top", "Artistas", "Géneros")
                items(filters) { filter ->
                    val isSelected = filter == selectedCategory
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { selectedCategory = filter },
                        color = if (isSelected) white else Color(0xFF2A2D2B),
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = filter,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                            color = if (isSelected) Color.Black else Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            DiscoverWeeklyBanner()

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader("Canciones para ti")

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedCategory) {
                "Todo" -> {
                    RecommendedCard("Summertime Sadness", "Lana del Rey", Color(0xFFB582C7))
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendedCard("Perfect", "Ed Sheeran", Color(0xFF1DB954))
                }
                "Favoritos" -> {
                    RecommendedCard("Flowers", "Miley Cyrus", Color(0xFFFF4081))
                }
                "Top" -> {
                    RecommendedCard("As It Was", "Harry Styles", Color(0xFF03A9F4))
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendedCard("Blinding Lights", "The Weeknd", Color(0xFFFFC107))
                }
                "Artistas" -> {
                    RecommendedCard("Lana del Rey", "Artista", Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendedCard("Ed Sheeran", "Artista", Color.DarkGray)
                }
                "Géneros" -> {
                    RecommendedCard("Pop", "Género", Color.DarkGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    RecommendedCard("Rock", "Género", Color.DarkGray)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            SectionHeader("Listas de reproducción")

            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PlaylistRow("Starlit Reverie", "Budiarti", "8 Canciones")
                PlaylistRow("Midnight Confessions", "Lana del Rey", "12 Canciones")
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Ver más",
            color = Color(0xFF1DB954),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DiscoverWeeklyBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(Color(0xFFB582C7), Color(0xFF7B1FA2))
                )
            )
    ) {
        // Decoración abstracta
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color.White.copy(alpha = 0.1f),
                center = Offset(size.width * 0.9f, size.height * 0.2f),
                radius = 100.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .padding(28.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Descubre\nSemanalmente",
                color = Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                lineHeight = 28.sp
            )

            Text(
                text = "Personalizado para tus oídos",
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Escuchar ahora", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.FavoriteBorder, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun PlaylistRow(title: String, artist: String, info: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF2A2A2A)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MusicNote, null, tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("De $artist • $info", color = Color.Gray, fontSize = 12.sp)
            }
            Icon(Icons.Default.MoreVert, null, tint = Color.Gray)
        }
    }
}

@Composable
fun RecommendedCard(title: String, artist: String, tint: Color) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
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
                Icon(
                    Icons.Default.MusicVideo,
                    null,
                    tint = tint,
                    modifier = Modifier.size(40.dp)
                )
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
                    artist,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
                    .size(44.dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.PlayArrow, null, tint = Color.White)
            }
        }
    }
}

@Composable
fun SymphonixBottomBar(
    selectedIndex: Int,
    onTabSelected: (Int) -> Unit,
    white: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val icons = listOf(
            Icons.Default.Home,
            Icons.Default.MusicNote,
            Icons.Default.LibraryMusic,
            Icons.Default.Person
        )

        icons.forEachIndexed { index, icon ->
            val isSelected = selectedIndex == index
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onTabSelected(index) }
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color(0xFF1DB954) else Color.Gray,
                    modifier = Modifier.size(26.dp)
                )
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .padding(top = 4.dp)
                            .size(4.dp)
                            .background(Color(0xFF1DB954), CircleShape)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
