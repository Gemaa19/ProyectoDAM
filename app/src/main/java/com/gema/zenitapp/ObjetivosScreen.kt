package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.ui.theme.ZenitGreen

@Composable
fun ObjetivosScreen(
    onMenuClick: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToAnalisis: () -> Unit,
    onNavigateToNuevoObjetivo: () -> Unit // <-- Este parámetro es el que activa la ruta
) {
    val presupuestos = listOf(
        CategoriaPresupuesto("Comida & bebida", "120€ / 200€", 0.6f, Icons.Default.Restaurant, ZenitGreen, Color.Red),
        CategoriaPresupuesto("Transporte", "65€ / 150€", 0.4f, Icons.Default.DirectionsBus, ZenitGreen, Color.Red),
        CategoriaPresupuesto("Entretenimiento", "10€ / 50€", 0.2f, Icons.Default.Movie, ZenitGreen, Color.Red),
        CategoriaPresupuesto("Hogar", "630€ / 700€", 0.9f, Icons.Default.Home, ZenitGreen, Color.Red)
    )

    val metas = listOf(
        CategoriaPresupuesto("Viaje Japón", "200€ / 900€", 0.22f, Icons.Default.Flight, ZenitGreen, Color(0xFF4CAF50))
    )

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                pantallaActual = "Objetivos",
                onInicioClick = onNavigateToInicio,
                onMovimientosClick = onNavigateToMovimientos,
                onAnalisisClick = onNavigateToAnalisis,
                onObjetivosClick = {}
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            CabeceraPrincipal(titulo = "Objetivos", onMenuClick = onMenuClick)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // SECCIÓN PRESUPUESTOS
                item {
                    Text(
                        "Presupuestos",
                        modifier = Modifier.padding(start = 25.dp, top = 20.dp, bottom = 10.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                items(presupuestos) { item -> ItemObjetivo(item) }

                // SECCIÓN METAS
                item {
                    Text(
                        "Metas",
                        modifier = Modifier.padding(start = 25.dp, top = 25.dp, bottom = 10.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                items(metas) { item -> ItemObjetivo(item) }

                // BOTÓN NUEVO OBJETIVO ENLAZADO
                item {
                    BotonPunteado(
                        texto = "Nuevo objetivo",
                        onClick = onNavigateToNuevoObjetivo // <-- Le pasamos la navegación aquí
                    )
                }
            }
        }
    }
}

@Composable
fun ItemObjetivo(item: CategoriaPresupuesto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(85.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().width(65.dp).background(item.colorIcono),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icono, null, tint = Color.White)
            }

            Column(Modifier.padding(horizontal = 16.dp).weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(item.cantidades, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                    LinearProgressIndicator(
                        progress = { item.progreso },
                        modifier = Modifier.weight(1f).height(8.dp),
                        color = item.colorBarra,
                        trackColor = Color.Black,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(Modifier.width(10.dp))
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.LightGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BotonPunteado(texto: String, onClick: () -> Unit) { // <-- Añadido el parámetro onClick
    OutlinedButton(
        onClick = onClick, // <-- Corregido: Sin llaves lambda internas para que responda directo
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 35.dp, vertical = 20.dp)
            .height(55.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, ZenitGreen),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
    ) {
        Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Text(texto, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

data class CategoriaPresupuesto(
    val nombre: String,
    val cantidades: String,
    val progreso: Float,
    val icono: androidx.compose.ui.graphics.vector.ImageVector,
    val colorIcono: Color,
    val colorBarra: Color
)