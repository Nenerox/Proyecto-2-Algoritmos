package com.example.frontend.ui.login

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
fun ForgotPasswordScreen(
    onResetPassword: (newPass: String) -> Unit = {},
    onBack: () -> Unit = {}
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val bgColor = Color(0xFF0D1612)
    val greenColor = Color(0xFF1DB954)
    val purpleColor = Color(0xFF913AA1)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // Fondos con degradados sutiles
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(greenColor.copy(alpha = 0.2f), Color.Transparent),
                    center = Offset(size.width * 0.0f, size.height * 0.5f),
                    radius = size.width * 0.9f
                ),
                center = Offset(size.width * 0.0f, size.height * 0.5f),
                radius = size.width * 0.9f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(purpleColor.copy(alpha = 0.15f), Color.Transparent),
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
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

            // Contenedor Central
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                color = Color(0xFF1A1A1A).copy(alpha = 0.6f),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Restablecer\nContraseña",
                        color = Color.White,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        lineHeight = 32.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Nueva Contraseña",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SymphonixTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            placeholder = "Ingresa tu nueva",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = passwordVisible,
                            onTogglePassword = { passwordVisible = !passwordVisible }
                        )
                    }

                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Confirmar Contraseña",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        SymphonixTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            placeholder = "Confirma tu nueva",
                            icon = Icons.Default.Lock,
                            isPassword = true,
                            passwordVisible = confirmPasswordVisible,
                            onTogglePassword = { confirmPasswordVisible = !confirmPasswordVisible }
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = { onResetPassword(newPassword) },
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
                            Text("Cambiar Contraseña", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }

                    TextButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Volver", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
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
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    isPassword: Boolean,
    passwordVisible: Boolean = false,
    onTogglePassword: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(16.dp),
        placeholder = { Text(placeholder, color = Color.Gray) },
        leadingIcon = if (icon != null) { { Icon(icon, null, tint = Color.Gray) } } else null,
        trailingIcon = {
            if (isPassword) {
                IconButton(onClick = onTogglePassword) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }
        },
        singleLine = true,
        visualTransformation = if (isPassword && !passwordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = Color(0xFF383838),
            unfocusedBorderColor = Color(0xFF383838),
            cursorColor = Color.White,
            focusedContainerColor = Color(0xFF252525).copy(alpha = 0.3f),
            unfocusedContainerColor = Color(0xFF252525).copy(alpha = 0.3f)
        )
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun ForgotPasswordScreenPreview() {
    MaterialTheme {
        ForgotPasswordScreen()
    }
}
