package com.example.frontend.ui.signin

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, widthDp = 360, heightDp = 780)
@Composable
fun SignInScreenFullPreview() {
    MaterialTheme {
        SignInScreen()
    }
}
