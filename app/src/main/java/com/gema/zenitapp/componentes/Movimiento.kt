package com.gema.zenitapp.componentes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Movimiento(
    val nombre: String,
    val categoria: String,
    val cantidad: String,
    val esIngreso: Boolean,
    val icono: ImageVector, // Asegúrate de que usa ImageVector de Compose
    val color: Color        // Asegúrate de que usa Color de Compose
)