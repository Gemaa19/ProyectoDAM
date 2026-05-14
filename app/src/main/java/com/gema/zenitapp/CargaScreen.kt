package com.gema.zenitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun CargaScreen(onNavigateToLogin: () -> Unit) {

    // Este bloque se ejecuta nada más abrir la pantalla
    LaunchedEffect(key1 = true) {
        delay(2000L) // Espera 2000 milisegundos (2 segundos)
        onNavigateToLogin() // Ejecuta la acción de ir al Login
    }

    // El diseño visual
    Box(
        modifier = Modifier
            .fillMaxSize() // Ocupa toda la pantalla del móvil
            .background(Color(0xFFA0FBDA)), // Tu color verde claro de fondo
        contentAlignment = Alignment.Center // Centra el contenido en medio
    ) {
        Text(
            text = "ZENIT",
            color = Color(0xFF0D5140), // Tu color verde oscuro para la letra
            fontSize = 48.sp, // Ajusta el tamaño para que quede como en Figma
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif // Le da ese toque elegante y clásico
        )
    }
}