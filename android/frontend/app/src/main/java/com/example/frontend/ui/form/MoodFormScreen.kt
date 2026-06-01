package com.example.frontend.ui.form

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.SentimentVeryDissatisfied
import androidx.compose.material.icons.outlined.SentimentVerySatisfied
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.home.SymphonixBottomBar
import androidx.compose.ui.tooling.preview.Preview
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MoodFormScreen(
    onFinish: (MoodFormData) -> Unit = {},
    onTabSelected: (Int) -> Unit = {}
) {
    val bgColor = Color(0xFF121212)
    val accentColor = Color(0xFF1DB954)
    
    // Paletas extraídas de GenreSelectionScreen
    val paletteBlue = Color(0xFF5D7A8C)
    val paletteGreen = Color(0xFF6B8C6D)
    val palettePurple = Color(0xFF8C73AD)
    val paletteRed = Color(0xFFAD7373)
    val paletteCyan = Color(0xFF738C8C)

    var moodValue by remember { mutableIntStateOf(3) }
    var energy by remember { mutableFloatStateOf(5f) }
    var danceability by remember { mutableFloatStateOf(5f) }
    var wantNewMusic by remember { mutableStateOf<Boolean?>(null) }
    var instrumentalness by remember { mutableFloatStateOf(5f) }
    var acousticness by remember { mutableFloatStateOf(5f) }
    var tempoPreference by remember { mutableFloatStateOf(5f) }

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
                    selectedIndex = 1, // Musical Note
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Header estilo Home
            Text(
                text = "Descubre ahora",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Cuéntanos cómo te sientes hoy y obtiene la mejor recomendación",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Formulario en tarjetas estilo moderno
            FormSection(title = "¿Cómo te sientes hoy?") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.SentimentVeryDissatisfied,
                        contentDescription = "Sad",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    (1..5).forEach { value ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    color = if (moodValue == value) accentColor else Color.Gray.copy(alpha = 0.5f),
                                    shape = CircleShape
                                )
                                .background(
                                    if (moodValue == value) accentColor.copy(alpha = 0.2f) else Color.Transparent
                                )
                                .clickable { moodValue = value },
                            contentAlignment = Alignment.Center
                        ) {
                            if (moodValue == value) {
                                Box(
                                    modifier = Modifier
                                        .size(12.dp)
                                        .clip(CircleShape)
                                        .background(accentColor)
                                )
                            }
                        }
                    }

                    Icon(
                        imageVector = Icons.Outlined.SentimentVerySatisfied,
                        contentDescription = "Happy",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Text(
                    text = when(moodValue) {
                        1 -> "Muy Triste"
                        2 -> "Triste"
                        3 -> "Neutral"
                        4 -> "Feliz"
                        else -> "Muy Feliz"
                    },
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "Nivel de energía") {
                Column {
                    Slider(
                        value = energy,
                        onValueChange = { energy = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = palettePurple,
                            inactiveTrackColor = Color(0xFF2A2A2A)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0", color = Color.Gray, fontSize = 12.sp)
                        Text("5", color = Color.Gray, fontSize = 12.sp)
                        Text("10", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Ganas de bailar?") {
                Column {
                    Slider(
                        value = danceability,
                        onValueChange = { danceability = it },
                        valueRange = 0f..10f,
                        steps = 9,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = paletteGreen,
                            inactiveTrackColor = Color(0xFF2A2A2A)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0", color = Color.Gray, fontSize = 12.sp)
                        Text("5", color = Color.Gray, fontSize = 12.sp)
                        Text("10", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Prefieres instrumental o voces?") {

                Slider(
                    value = instrumentalness,
                    onValueChange = { instrumentalness = it },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = paletteBlue,
                        inactiveTrackColor = Color(0xFF2A2A2A)
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Voces", color = Color.Gray, fontSize = 12.sp)
                    Text("Indiferente", color = Color.Gray, fontSize = 12.sp)
                    Text("Instrumental", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Prefieres acústico o electrónico?") {

                Slider(
                    value = acousticness,
                    onValueChange = { acousticness = it },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = paletteRed,
                        inactiveTrackColor = Color(0xFF2A2A2A)
                    )
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Electrónico", color = Color.Gray, fontSize = 12.sp)
                    Text("Indiferente", color = Color.Gray, fontSize = 12.sp)
                    Text("Acústico", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Qué ritmo buscas hoy?") {

                Slider(
                    value = tempoPreference,
                    onValueChange = { tempoPreference = it },
                    valueRange = 0f..10f,
                    steps = 9,
                    colors = SliderDefaults.colors(
                        thumbColor = Color.White,
                        activeTrackColor = paletteCyan,
                        inactiveTrackColor = Color(0xFF2A2A2A)
                    )
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Relajado", color = Color.Gray, fontSize = 12.sp)
                    Text("Indiferente", color = Color.Gray, fontSize = 12.sp)
                    Text("Rápido", color = Color.Gray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Quieres descubrir algo nuevo?") {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    SelectableChip(
                        text = "Si",
                        isSelected = wantNewMusic == true,
                        onClick = { wantNewMusic = true },
                        modifier = Modifier.weight(1f)
                    )
                    SelectableChip(
                        text = "No",
                        isSelected = wantNewMusic == false,
                        onClick = { wantNewMusic = false },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Botón de acción principal con el degradado de la app
            Button(
                onClick = {
                    val valence = when(moodValue) {
                        1 -> 0.1f
                        2 -> 0.3f
                        3 -> 0.5f
                        4 -> 0.7f
                        else -> 0.9f
                    }
                    onFinish(MoodFormData(
                        valence = valence,
                        energy = energy / 10f,
                        danceability = danceability / 10f,
                        instrumentalness = instrumentalness / 10f,
                        acousticness = acousticness / 10f,
                        tempo = 60f + ((tempoPreference / 10f) * 140f),
                        wantNewMusic = wantNewMusic ?: false
                    ))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(8.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
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
                    Text(
                        "Listo",
                        color = Color.White, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun FormSection(title: String, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}

@Composable
fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isSelected) Color(0xFF1DB954) else Color(0xFF252525),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (isSelected) Color.White.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.Black else Color.LightGray,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
            textAlign = TextAlign.Center
        )
    }
}


data class MoodFormData(
    val valence: Float,
    val energy: Float,
    val danceability: Float,
    val instrumentalness: Float,
    val acousticness: Float,
    val tempo: Float,
    val wantNewMusic: Boolean?
)

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun MoodFormScreenPreview() {
    MaterialTheme {
        MoodFormScreen()
    }
}
