package com.example.frontend.ui.home

import android.media.Image
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R

@Composable
fun HomeScreen(
    onProfileClick: () -> Unit = {}
) {
    val bgColor = Color(0xFF252525)
    val white = Color(0xFFFFFFFF)
    val recommendColor = Color(0xFF4CAF50)

    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = bgColor,
        bottomBar = {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161616))
                    .navigationBarsPadding()
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {

                SymphonixBottomBar(
                    selectedIndex = selectedTab,
                    onTabSelected = { 
                        selectedTab = it
                        if (it == 3) onProfileClick()
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
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: User
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.Gray)
                    ) {
                         Icon(Icons.Default.Person, null, Modifier.align(Alignment.Center), tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Hola, Usuario",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Search, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Icon(Icons.Default.FavoriteBorder, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Filtros Estilo Pastel Grisáceo
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", "Favorites", "Top", "Artist", "Genres")
                items(filters) { filter ->
                    val isSelected = filter == "All"
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isSelected) white else Color(0xFF2A2D2B))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (isSelected) Color.Black else Color.Gray,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            DiscoverWeeklyBanner()

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                SectionTitle("Canciones para ti")
                Text("Ver más", color = recommendColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Recomendaciones con el Template Solicitado
            RecommendedCard("Summertime Sadness", "Lana del Rey")
            Spacer(modifier = Modifier.height(12.dp))
            RecommendedCard("Perfect", "Ed Sheeran")

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                SectionTitle("Playlists para ti")
                Text("Ver más", color = recommendColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                PlaylistRow("Starlit Reverie", "Budiarti", "8 Songs")
                PlaylistRow("Midnight Confessions", "Lana del Rey", "12 Songs")
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold
    )
}
@Composable
fun DiscoverWeeklyBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFFB582C7)) // Color solicitado: #B582C7
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxHeight()
        ) {
            Text(
                text = "Descubre semanalmente",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Text(
                text = "Según tus gustos y estados \nde ánimo",
                color = Color.Black.copy(alpha = 0.7f),
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // BOTÓN PLAY
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape
                        )
                        .background(
                            Color.Black.copy(alpha = 0.75f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // CORAZÓN
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = CircleShape
                        )
                        .background(
                            Color.Black.copy(alpha = 0.75f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaylistRow(title: String, artist: String, info: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF2A2A2A))
        ) {
            Icon(Icons.Default.MusicNote, null, Modifier.align(Alignment.Center), tint = Color.Gray)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text("By $artist  •  $info", color = Color.Gray, fontSize = 12.sp)
        }
        IconButton(onClick = { }) {
            Icon(Icons.Default.PlayArrow, null, tint = Color.White)
        }
    }
}

@Composable
fun RecommendedCard(title: String, artist: String) {

    Box(
        modifier = Modifier
            .fillMaxWidth() // ← ocupa todo el ancho disponible
            .height(112.dp)
            .shadow(
                elevation = 4.dp,
                spotColor = Color(0x40000000),
                ambientColor = Color(0x40000000)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1A1A1A).copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {

            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .background(Color.DarkGray.copy(alpha = 0.4f))
            ) {
                Icon(
                    Icons.Default.MusicVideo,
                    null,
                    Modifier.align(Alignment.Center),
                    tint = Color.LightGray
                )
            }

            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .weight(1f)
            ) {
                Text(
                    title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    artist,
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(end = 16.dp)
                    .size(40.dp)
                    .background(
                        Color.Black.copy(alpha = 0.5f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    null,
                    tint = Color.White
                )
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(72.dp),
        contentAlignment = Alignment.Center
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            val icons = listOf(
                Icons.Default.Home,
                Icons.Default.MusicNote,
                Icons.Default.List,
                Icons.Default.Person
            )

            icons.forEachIndexed { index, icon ->

                val isSelected = selectedIndex == index

                Box(
                    modifier = Modifier
                        .width(58.dp)
                        .height(42.dp)
                        .clip(RoundedCornerShape(100.dp))
                        .background(
                            if (isSelected)
                                Color.White
                            else
                                Color.Transparent
                        )
                        .clickable {
                            onTabSelected(index)
                        },
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isSelected)
                            Color.Black
                        else
                            Color.White,
                        modifier = Modifier.size(22.dp)
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
