package com.example.frontend.ui.login

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
fun LoginScreen(
    onLogin: (email: String, password: String) -> Unit = { _, _ -> },
    onRegister: () -> Unit = {},
    onForgotPassword: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val bgColor = Color(0xFF252525)
    val gradientColor = Color(0xFF4F1676)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Radial Gradients
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.2f),
                    radius = size.width * 0.8f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.2f),
                radius = size.width * 0.8f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.5f),
                    radius = size.width * 0.7f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.5f),
                radius = size.width * 0.7f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(gradientColor, Color.Transparent),
                    center = Offset(size.width * 0.3f, size.height * 0.8f),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width * 0.3f, size.height * 0.8f),
                radius = size.width * 0.9f
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
                    text = "Bienvenido\nde vuelta",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 32.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Email",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                SymphonixTextField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Ingresa tu correo electrónico",
                    isPassword = false
                )

                Text(
                    text = "Contraseña",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                SymphonixTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Ingresa tu contraseña",
                    isPassword = true
                )

                TextButton(
                    onClick = onForgotPassword,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "¿Olvidaste la Contraseña?",
                        color = Color(0xFFCCCCCC),
                        fontSize = 14.sp
                    )
                }

                Button(
                    onClick = { onLogin(email.trim(), password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A107C))
                ) {
                    Text("Iniciar Sesión", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                TextButton(
                    onClick = onRegister,
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Registrarse", color = Color(0xFFCCCCCC), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SymphonixTextField(
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
fun LoginScreenInternalPreview() {
    MaterialTheme {
        LoginScreen()
    }
}