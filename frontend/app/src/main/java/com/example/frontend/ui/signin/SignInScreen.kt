package com.example.frontend.ui.signin

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.frontend.R
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun SignInScreen(
    onSignIn: (name: String, email: String, password: String) -> Unit = { _, _, _ -> },
    onLoginClick: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val bgColor = Color(0xFF0D1612)
    val greenColor = Color(0xFF1DB954)
    val purpleColor = Color(0xFF913AA1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Radial Gradients (Efecto Refinado: Verde a la izquierda, Púrpura a la derecha)
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
                .padding(horizontal = 24.dp, vertical = 32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(0.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_symphonix_logo),
                    contentDescription = "Symphonix Logo",
                    modifier = Modifier
                        .widthIn(max = 205.dp)
                        .fillMaxWidth(0.6f)
                        .aspectRatio(205f / 92f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            Column(
                modifier = Modifier
                    .shadow(elevation = 4.dp, spotColor = Color(0x40000000), ambientColor = Color(0x40000000))
                    .border(width = 2.dp, color = Color(0xFF383838), shape = RoundedCornerShape(size = 24.dp))
                    .widthIn(max = 320.dp)
                    .fillMaxWidth()
                    .background(color = Color(0xFF201F1F).copy(alpha = 0.5f), shape = RoundedCornerShape(size = 24.dp))
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Crea tu cuenta",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 32.sp
                )

                Spacer(Modifier.height(8.dp))

                // Campo Nombre
                Text(text = "Nombre completo", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                SignInTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Ingresa tu nombre",
                    isPassword = false
                )

                // Campo Email
                Text(text = "Email", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                SignInTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Ingresa tu correo electrónico",
                    isPassword = false
                )

                // Campo Contraseña
                Text(text = "Contraseña", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                SignInTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Crea una contraseña",
                    isPassword = true
                )

                // Campo Confirmar Contraseña
                Text(text = "Confirmar Contraseña", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                SignInTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repite tu contraseña",
                    isPassword = true
                )

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = { 
                        if (password == confirmPassword) {
                            onSignIn(name.trim(), email.trim(), password)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A107C))
                ) {
                    Text("Registrarse", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                TextButton(
                    onClick = onLoginClick,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("¿Ya tienes cuenta? ", color = Color(0xFFCCCCCC), fontSize = 14.sp)
                        Text("Inicia sesión", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SignInTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        placeholder = { Text(placeholder, color = Color.White.copy(alpha = 0.8f)) },
        singleLine = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF383838),
            unfocusedBorderColor = Color(0xFF383838),
            cursorColor = Color.White
        )
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenPreview() {
    MaterialTheme {
        SignInScreen()
    }
}
