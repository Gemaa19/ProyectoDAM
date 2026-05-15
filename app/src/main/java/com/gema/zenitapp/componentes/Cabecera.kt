package com.gema.zenitapp.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@Composable
fun CabeceraPrincipal(
    titulo: String, // <--- 1. AÑADIMOS ESTE PARÁMETRO
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(ZenitLightGreen)
            .padding(16.dp)
    ) {
        // Icono hamburguesa
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menú lateral",
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterStart)
                .clickable { onMenuClick() }
        )

        // Texto dinámico
        Text(
            text = titulo, // <--- 2. USAMOS LA VARIABLE AQUÍ
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp, // Bajamos un pelín el tamaño para que quepan títulos largos
            letterSpacing = 2.sp,
            color = Color(0xFF0D5140),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}