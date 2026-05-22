package com.gema.zenitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import com.gema.zenitapp.ui.theme.verdeFondo
import com.gema.zenitapp.ui.theme.verdeOscuro

@Composable
fun CargaScreen(onNavigateToLogin: () -> Unit) {
    LaunchedEffect(key1 = true) {
        delay(2000L)
        onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(verdeFondo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ZENIT",
            color = verdeOscuro,
            fontSize = 80.sp,
            fontWeight = FontWeight.W900,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 8.sp
        )
    }
}