package com.example.frontend.ui.genre

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun GenreSelectionScreen(
    onContinue: (List<String>) -> Unit = {}
) {
    val genres = listOf(
        "Acoustic", "Afrobeat", "Alt-rock","Alternative","Ambient","Anime",
        "Black-metal","Bluegrass","Blues","Brazil","Breakbeat","British",
        "Cantopop","Chicago-house","Children","Chill","Classical","Club","Comedy",
        "Country","Dance","Dancehall","Death-metal","Deep-house","Detroit-techno","Cisco",
        "Disney","Drum-and-bass","Dub","Dubstep","Edm","Electro","Electronic",
        "Emo","Folk","Forro","French","Funk","Garage","German","Gospel","Goth",
        "Grindcore","Groove","Grunge","Guitar","Happy","Hard-rock","Hardcore",
        "Hardstyle","Heavy-metal","Hip-hop","Honky-tonk","House","Idm","Indian",
        "Indie","Indie-pop","Industrial","Iranian","J-dance","J-idol","J-pop",
        "J-rock","Jazz","K-pop","Kids","Latin","Latino","Malay","Mandopop","Metal",
        "Metalcore","Minimal-techno","Mpb","New-age","Opera","Pagode","Party","Piano",
        "Pop","Pop-film","Power-pop","Progressive-house","Psych-rock","Punk","Punk-rock",
        "r-n-b","Reggae","Reggaeton","Rock","Rock-n-Roll","Rockabilly", "Romance",
        "Sad","Salsa","Samba","Sertanejo","Show-tunes","Singer-songwriter",
        "Ska","Sleep","Songwriter","Soul","Spanish","Study","Swedish","Synth-pop",
        "Tango","Techno","Trance","Trip-hop","Turkish","World-music"
    )
    var searchText by remember { mutableStateOf("") }

    val filteredGenres = genres.filter {
        it.contains(searchText, ignoreCase = true)
    }

    val selectedGenres = remember { mutableStateListOf<String>() }

    val bgColor = Color(0xFF0D1612)
    val greenColor = Color(0xFF1DB954)
    val purpleColor = Color(0xFF913AA1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Radial Gradients (Mismo estilo que Login/SignIn)
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(greenColor.copy(alpha = 0.3f), Color.Transparent),
                    center = Offset(size.width * 0.0f, size.height * 0.5f),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width * 0.0f, size.height * 0.5f),
                radius = size.width * 0.9f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(purpleColor.copy(alpha = 0.25f), Color.Transparent),
                    center = Offset(size.width * 1.0f, size.height * 0.4f),
                    radius = size.width * 0.8f
                ),
                center = Offset(size.width * 1.0f, size.height * 0.4f),
                radius = size.width * 0.8f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_symphonix_logo),
                contentDescription = "Symphonix Logo",
                modifier = Modifier
                    .width(260.dp)
                    .padding(bottom = 24.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
                    .border(width = 2.dp, color = Color(0xFF383838), shape = RoundedCornerShape(size = 24.dp))
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .heightIn(max = 516.dp)
                    .background(color = Color(0xFF201F1F).copy(alpha = 0.5f), shape = RoundedCornerShape(size = 24.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "¿Qué tipo de música\nte gusta escuchar?",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    lineHeight = 28.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Buscar género...",
                            color = Color.Gray
                        )
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF913AA1),
                        unfocusedBorderColor = Color(0xFF383838),
                        cursorColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(filteredGenres.size) { index ->
                        val genre = filteredGenres[index]
                        val isSelected = selectedGenres.contains(genre)
                        GenreCard(
                            genre = genre,
                            index = index,
                            isSelected = isSelected,
                            onClick = {
                                if (isSelected) selectedGenres.remove(genre)
                                else selectedGenres.add(genre)
                            },
                            greenColor = greenColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onContinue(selectedGenres.toList()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .shadow(12.dp, RoundedCornerShape(18.dp)),
                    shape = RoundedCornerShape(18.dp),
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
                        Text("Continuar", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun GenreCard(
    genre: String,
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    greenColor: Color
) {
    val mutedPalettes = listOf(
        listOf(Color(0xFF5D7A8C), Color(0xFF4A5D6A)), // Azul acero
        listOf(Color(0xFF6B8C6D), Color(0xFF536A55)), // Verde bosque suave
        listOf(Color(0xFF8C73AD), Color(0xFF6D5A8C)), // Púrpura suave
        listOf(Color(0xFFAD7373), Color(0xFF8C5A5A)), // Terracota suave
        listOf(Color(0xFF738C8C), Color(0xFF5A6D6D)), // Cian grisáceo
        listOf(Color(0xFF8C8473), Color(0xFF6D685A))  // Arena / Vintage
    )
    
    val currentPalette = mutedPalettes[index % mutedPalettes.size]
    
    // Color oscuro original para cuando NO está seleccionado
    val unselectedBrush = Brush.verticalGradient(
        listOf(Color(0xFF2A2A2A), Color(0xFF2A2A2A))
    )
    
    // Degradado sutil para cuando está seleccionado
    val selectedBrush = Brush.verticalGradient(
        listOf(currentPalette[0].copy(alpha = 0.8f), currentPalette[1].copy(alpha = 0.9f))
    )

    Box(
        modifier = Modifier
            .aspectRatio(1.5f)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) selectedBrush else unselectedBrush)
            .border(
                width = 2.dp,
                color = if (isSelected) Color.White.copy(alpha = 0.5f) else Color(0xFF383838),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = genre,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun GenreSelectionScreenPreview() {
    MaterialTheme {
        GenreSelectionScreen()
    }
}
