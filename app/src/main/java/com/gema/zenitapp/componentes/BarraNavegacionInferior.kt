package com.gema.zenitapp.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeFondo

@Composable
fun BarraNavegacionInferior(
    pantallaActual: String,
    onInicioClick: () -> Unit,
    onMovimientosClick: () -> Unit,
    onAnalisisClick: () -> Unit,
    onObjetivosClick: () -> Unit,
) {
    NavigationBar(
        containerColor = verdeFondo,
        tonalElevation = 0.dp,
        modifier = Modifier.height(100.dp)
    ) {
        val items = listOf(
            Triple("Inicio", Icons.Default.Home, onInicioClick),
            Triple("Movimientos", Icons.Default.Receipt, onMovimientosClick),
            Triple("Análisis", Icons.Default.PieChart, onAnalisisClick),
            Triple("Objetivos", Icons.Default.Savings, onObjetivosClick)
        )

        items.forEach { item ->
            // 3. SELECCIÓN AUTOMÁTICA: Ya no necesitamos "pestañaSeleccionada" manual
            val seleccionado = pantallaActual == item.first

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .padding(top = 25.dp)
                            .size(50.dp)
                            .background(
                                color = if (seleccionado) Color.White else verdeIconos,
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = item.second,
                            contentDescription = item.first,
                            tint = if (seleccionado) verdeIconos else Color.White
                        )
                    }
                },
                label = { Text(item.first, color = Color.Gray) },
                selected = seleccionado,
                onClick = {
                    if (!seleccionado) {
                        item.third.invoke()
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent
                )
            )
        }
    }
}