package com.example.frontend.ui.profile

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R

import com.example.frontend.ui.home.SymphonixBottomBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String = "Usuario",
    email: String = "usuario@gmail.com",
    favoriteGenres: List<String> = listOf("Pop", "Rock", "Reggaetón", "Heavy Metal"),
    onBack: () -> Unit = {},
    onHomeClick: () -> Unit = {}
) {
    val bgColor = Color(0xFF252525)
    val purpleColor = Color(0xFF913AA1)
    val white = Color(0xFFFFFFFF)

    Scaffold(
        containerColor = bgColor,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Perfil",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF161616))
                    .navigationBarsPadding()
                    .padding(top = 12.dp, bottom = 8.dp)
            ) {
                SymphonixBottomBar(
                    selectedIndex = 3, // Perfil es el índice 3 en tu lista de iconos
                    onTabSelected = {
                        if (it == 0) onHomeClick()
                    },
                    white = white
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Profile Info Card
                ProfileHeader(username, email)

                Spacer(Modifier.height(24.dp))

                // Section: Account
                ProfileSection(title = "Account") {
                    ProfileItem(Icons.Outlined.Person, "Administrar Perfil")
                    ProfileItem(Icons.Outlined.Lock, "Seguridad y Contraseña")
                    ProfileItem(Icons.Outlined.Notifications, "Notificaciones")
                    ProfileItem(Icons.Outlined.Language, "Language", "Español")
                }

                Spacer(Modifier.height(24.dp))

                // Section: Favorite Genres
                ProfileSection(title = "Géneros Favoritos") {
                    GenresGrid(favoriteGenres)
                }

                Spacer(Modifier.height(24.dp))

                // Section: Preferences
                ProfileSection(title = "Preferencias") {
                    ProfileItem(Icons.Outlined.Info, "Sobre nosotros")
                    ProfileItem(Icons.Outlined.Brightness4, "Tema", "Oscuro")
                }

                Spacer(Modifier.height(24.dp))

                // Section: Support
                ProfileSection(title = "Soporte") {
                    ProfileItem(Icons.AutoMirrored.Outlined.HelpOutline, "Centro de Ayuda")
                    ProfileItem(Icons.AutoMirrored.Outlined.Logout, "Cerrar Sesión", tint = Color.Red)
                }

                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun ProfileHeader(name: String, email: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF2A2A2A))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.DarkGray)
            ) {
                Icon(Icons.Default.Person, null, Modifier.align(Alignment.Center), tint = Color.White)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(email, color = Color.Gray, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ProfileSection(title: String, content: @Composable () -> Unit) {
    Column {
        Text(
            title,
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF2A2A2A))
                .padding(vertical = 8.dp)
        ) {
            Column { content() }
        }
    }
}

@Composable
fun ProfileItem(
    icon: ImageVector,
    title: String,
    value: String? = null,
    tint: Color = Color.White
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF333333)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = tint, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Text(title, color = tint, fontSize = 16.sp, modifier = Modifier.weight(1f))
        if (value != null) {
            Text(value, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(end = 8.dp))
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
    }
}

@Composable
fun GenresGrid(genres: List<String>) {
    val mutedPalettes = listOf(
        listOf(Color(0xFF5D7A8C), Color(0xFF4A5D6A)),
        listOf(Color(0xFF6B8C6D), Color(0xFF536A55)),
        listOf(Color(0xFF8C73AD), Color(0xFF6D5A8C)),
        listOf(Color(0xFFAD7373), Color(0xFF8C5A5A))
    )

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        genres.chunked(2).forEach { rowGenres ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                rowGenres.forEach { genre ->
                    val palette = mutedPalettes[genres.indexOf(genre) % mutedPalettes.size]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1.8f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Brush.verticalGradient(palette))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            genre,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
                if (rowGenres.size == 1) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ProfileScreenLayoutPreview() {
    ProfileScreen()
}
