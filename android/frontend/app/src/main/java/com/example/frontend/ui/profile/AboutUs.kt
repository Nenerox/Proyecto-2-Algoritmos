package com.example.frontend.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import com.example.frontend.R
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(
    onBack: () -> Unit = {}
) {
    val bgColor = Color(0xFF121212)

    Scaffold(
        containerColor = bgColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera Inmersiva (Mismo estilo que Perfil)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
            ) {
                
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_symphonix_logo),
                        contentDescription = "Symphonix Logo",
                        modifier = Modifier.height(60.dp)
                    )
                    
                    Spacer(Modifier.height(8.dp))
                }
            }

            Spacer(Modifier.height(32.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                Text(
                    text = "Nuestra Misión",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(Modifier.height(12.dp))
                
                Text(
                    text = "En Symphonix, creemos que la música es el reflejo del alma. Nuestra misión es conectar tus emociones con el ritmo perfecto, utilizando algoritmos inteligentes que entienden cómo te sientes en cada momento del día.",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "Nuestra Visión",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Convertirnos en el acompañante emocional definitivo, donde cada nota musical sea una extensión de tu bienestar y cada recomendación sea un descubrimiento significativo para tu vida.",
                    color = Color.Gray,
                    fontSize = 15.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )

                Spacer(Modifier.height(32.dp))

                Text(
                    text = "El Equipo",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // Lista de miembros
                TeamMemberItem("Andrés Pineda", "Desarrollador Backend")
                TeamMemberItem("Miguel Sajquín", "Desarrollador Backend")
                TeamMemberItem("Alejandro Sagastume", "Desarrollador Frontend")
                TeamMemberItem("Jimena Vásquez", "Desarrollador Frontend")

                Spacer(Modifier.height(40.dp))
                
                Text(
                    text = "Versión 1.0.0",
                    color = Color.DarkGray,
                    fontSize = 12.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun TeamMemberItem(role: String, description: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp), // Multiplo de 8
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp) // Multiplo de 8
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(role, color = Color(0xFF1DB954), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(description, color = Color.LightGray, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun AboutUsScreenPreview() {
    AboutUsScreen()
}
