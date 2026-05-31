package com.example.frontend.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.ui.home.SymphonixBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String = "Usuario",
    email: String = "usuario@gmail.com",
    favoriteGenres: List<String> = listOf("Pop", "Rock", "Reggaetón", "Heavy Metal"),
    onBack: () -> Unit = {},
    onHomeClick: () -> Unit = {},
    onMoodFormClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onAboutUsClick: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val bgColor = Color(0xFF121212)
    val accentColor = Color(0xFF1DB954)

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
                    selectedIndex = 3,
                    onTabSelected = { index ->
                        when (index) {
                            0 -> onHomeClick()
                            1 -> onMoodFormClick()
                            2 -> onFavoritesClick()
                            3 -> { /* Ya estamos aquí */ }
                        }
                    },
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
        ) {
            // Cabecera Inmersiva
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            ) {
                // Fondo con degradado
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.7f)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF5A107C), Color(0xFF121212))
                            )
                        )
                )
                
                // Botón Atrás
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(16.dp).align(Alignment.TopStart)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Atrás", tint = Color.White)
                }

                // Info de Usuario Centrada
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(4.dp, bgColor, CircleShape)
                            .background(Color.DarkGray)
                    ) {
                        Icon(
                            Icons.Default.Person, 
                            null, 
                            Modifier.size(60.dp).align(Alignment.Center), 
                            tint = Color.White.copy(alpha = 0.5f)
                        )
                    }
                    
                    Spacer(Modifier.height(12.dp))
                    
                    Text(
                        text = username,
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = email,
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                
                // Botón Editar Perfil (Estilizado)
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF252525)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Editar Perfil", color = Color.White, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(24.dp))

                ProfileSectionModern(title = "Géneros Favoritos") {
                    GenresGridModern(favoriteGenres)
                }

                Spacer(Modifier.height(24.dp))

                ProfileSectionModern(title = "Soporte") {
                    ProfileItemModern(
                        Icons.Outlined.Info, 
                        "Sobre nosotros",
                        onClick = onAboutUsClick
                    )
                    ProfileItemModern(
                        Icons.AutoMirrored.Outlined.Logout, 
                        "Cerrar Sesión", 
                        tint = Color(0xFFFF5252),
                        onClick = onLogout
                    )
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileSectionModern(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        content()
    }
}

@Composable
fun ProfileItemModern(
    icon: ImageVector,
    title: String,
    value: String? = null,
    tint: Color = Color.White,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = tint.copy(alpha = 0.7f), modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, color = tint, fontSize = 16.sp, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray.copy(alpha = 0.5f), modifier = Modifier.size(20.dp))
    }
}

@Composable
fun GenresGridModern(genres: List<String>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            Surface(
                color = Color(0xFF252525),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
            ) {
                Text(
                    text = genre,
                    color = Color.LightGray,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ProfileScreenModernPreview() {
    ProfileScreen()
}
