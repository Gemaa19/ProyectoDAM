package com.gema.zenitapp.componentes

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Movimiento(
    val nombre: String,
    val categoria: String,
    val cantidad: String,
    val esIngreso: Boolean,
    val icono: ImageVector,
    val color: Color
)