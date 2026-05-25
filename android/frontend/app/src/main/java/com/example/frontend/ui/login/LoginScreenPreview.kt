package com.example.frontend.ui.login

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, widthDp = 360, heightDp = 780, name = "Login Screen - Default")
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        Surface {
            LoginScreen()
        }
    }
}