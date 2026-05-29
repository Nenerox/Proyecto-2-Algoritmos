package com.example.frontend.ui.form

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
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
    val purpleAccent = Color(0xFFB582C7)

    var moodValue by remember { mutableIntStateOf(3) }
    var energy by remember { mutableFloatStateOf(5f) }
    var danceability by remember { mutableFloatStateOf(5f) }
    var wantNewMusic by remember { mutableStateOf<Boolean?>(null) }
    var environment by remember { mutableStateOf("") }

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
                text = "Tu Registro Diario",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "Cuéntanos cómo te sientes hoy",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Calendario sutil
            CalendarRow()

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
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = purpleAccent,
                            inactiveTrackColor = Color(0xFF2A2A2A)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1", color = Color.Gray, fontSize = 12.sp)
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
                        valueRange = 1f..10f,
                        steps = 8,
                        colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = accentColor,
                            inactiveTrackColor = Color(0xFF2A2A2A)
                        )
                    )
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("1", color = Color.Gray, fontSize = 12.sp)
                        Text("10", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Quieres descubrir algo nuevo?") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
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

            Spacer(modifier = Modifier.height(32.dp))

            FormSection(title = "¿Qué estás haciendo?") {
                val contexts = listOf(
                    "Estudiando", "Entrenando", "Descansando", "Fiesta", "Viajando",
                    "Trabajando", "Cocinando", "Limpiando", "Meditando", "En el tráfico"
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    contexts.forEach { item ->
                        SelectableChip(
                            text = item,
                            isSelected = environment == item,
                            onClick = { environment = item }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Botón de acción principal con el degradado de la app
            Button(
                onClick = { 
                    val moodStr = when(moodValue) {
                        1 -> "Muy Triste"
                        2 -> "Triste"
                        3 -> "Neutral"
                        4 -> "Feliz"
                        else -> "Muy Feliz"
                    }
                    onFinish(MoodFormData(moodStr, energy, danceability, wantNewMusic, environment))
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

@Composable
fun CalendarRow() {
    val days = remember {
        (0..6).map { i ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -i)
            cal
        }.reversed()
    }
    
    // Simulación de conteo de recomendaciones por día
    val recommendationCounts = remember {
        val map = mutableMapOf<String, Int>()
        val fmt = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val cal3 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -2) }
        val cal4 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -4) }
        
        map[fmt.format(cal1.time)] = 3
        map[fmt.format(cal2.time)] = 1
        map[fmt.format(cal3.time)] = 5
        map[fmt.format(cal4.time)] = 2
        map
    }
    val todayStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Calendar.getInstance().time)

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        userScrollEnabled = false
    ) {
        items(days) { dateCal ->
            val dateStr = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(dateCal.time)
            val isToday = dateStr == todayStr
            val count = recommendationCounts[dateStr] ?: 0
            val dayName = SimpleDateFormat("E", Locale.forLanguageTag("es")).format(dateCal.time).take(1).uppercase()
            val dayOfMonth = SimpleDateFormat("d", Locale.getDefault()).format(dateCal.time)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 4.dp)
            ) {
                Text(
                    text = dayName,
                    color = if (isToday) Color(0xFF1DB954) else Color.Gray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            when {
                                count >= 5 -> Color(0xFF1DB954)
                                count >= 3 -> Color(0xFF1DB954).copy(alpha = 0.7f)
                                count > 0 -> Color(0xFF1DB954).copy(alpha = 0.4f)
                                else -> Color(0xFF252525)
                            }
                        )
                        .border(
                            width = if (isToday) 2.dp else 0.dp,
                            color = if (isToday) Color.White else Color.Transparent,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (count > 0) count.toString() else dayOfMonth,
                        color = if (count > 0) Color.Black else (if (isToday) Color.White else Color.Gray),
                        fontSize = 14.sp,
                        fontWeight = if (isToday || count > 0) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

data class MoodFormData(
    val mood: String,
    val energy: Float,
    val danceability: Float,
    val wantNewMusic: Boolean?,
    val environment: String
)

@OptIn(ExperimentalLayoutApi::class)
@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun MoodFormScreenPreview() {
    MaterialTheme {
        MoodFormScreen()
    }
}
