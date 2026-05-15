package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    onMenuClick: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToPrevision: () -> Unit
) {
    val movimientosHoy = listOf(
        Movimiento("Starbucks", "Café y snacks", "-5.50€", false, Icons.Default.Restaurant, ZenitGreen),
        Movimiento("Nómina", "Salario Mensual", "+2000€", true, Icons.Default.Payments, ZenitGreen)
    )
    val movimientosAyer = listOf(
        Movimiento("Netflix", "Entretenimiento", "-15.99€", false, Icons.Default.Tv, ZenitGreen),
        Movimiento("Mercadona", "Comida", "-85.20€", false, Icons.Default.ShoppingCart, ZenitGreen)
    )

    Scaffold(
        bottomBar = {
            // Reutilizamos la barra que ya configuramos con los iconos en círculos
            BarraNavegacionInferior(
                pantallaActual = "Movimientos", // Aquí se iluminará el icono de los tickets
                onInicioClick = onNavigateToInicio,
                onMovimientosClick = { /* No hará nada */ },
                onAnalisisClick = { /* TODO */ },
                onObjetivosClick = onNavigateToPrevision
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            CabeceraPrincipal(
                titulo = "Movimientos",
                onMenuClick = onMenuClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TARJETAS SUPERIORES
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val mod = Modifier.weight(1f)
                        TarjetaMovimientoResumen(mod, "Ingresos", "+3200€", Icons.Default.ArrowUpward, ZenitGreen)
                        TarjetaMovimientoResumen(mod, "Gastos", "-1450€", Icons.Default.ArrowDownward, Color.Red)
                    }
                }

                // FILTROS CHIPS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center, // <--- CAMBIADO A CENTER
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FiltroChip("Todos", seleccionado = true)
                        Spacer(Modifier.width(8.dp)) // Espacio manual entre chips
                        FiltroChip("Ingresos", seleccionado = false)
                        Spacer(Modifier.width(8.dp))
                        FiltroChip("Gastos", seleccionado = false)
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // LISTADO AGRUPADO POR FECHA
                item { FilaFecha("Hoy", "16 Abril") }
                items(movimientosHoy) { mov -> ItemGasto(mov) }

                item {
                    Spacer(Modifier.height(16.dp))
                    FilaFecha("Ayer", "15 Abril")
                }
                items(movimientosAyer) { mov -> ItemGasto(mov) }

                // BOTÓN PUNTEADO "NUEVO MOVIMIENTO"
                item {
                    BotonNuevoMovimiento()
                    Spacer(Modifier.height(30.dp))
                }
            }
        }
    }
}
@Composable
fun TarjetaMovimientoResumen(modifier: Modifier, titulo: String, cantidad: String, icono: androidx.compose.ui.graphics.vector.ImageVector, colorIcono: Color) {
    Card(
        modifier = modifier.height(85.dp), // <--- REDUCIDO DE 100.dp A 85.dp
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(20.dp)) // Icono un pelín más pequeño
            Text(titulo, color = Color.Gray, fontSize = 11.sp) // Texto un pelín más pequeño
            Text(cantidad, fontWeight = FontWeight.Bold, fontSize = 17.sp, color = colorIcono)
        }
    }
}

@Composable
fun FiltroChip(texto: String, seleccionado: Boolean) {
    Surface(
        color = if (seleccionado) Color(0xFF0D5140) else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = if (!seleccionado) BorderStroke(1.dp, Color.LightGray) else null,
        modifier = Modifier.height(36.dp)
    ) {
        Box(Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                texto,
                color = if (seleccionado) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun BotonNuevoMovimiento() {
    // Botón con borde punteado (Dash)
    OutlinedButton(
        onClick = { /* Navegar a añadir */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 16.dp)
            .height(55.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, ZenitGreen),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
    ) {
        Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(8.dp))
        Text("Nuevo movimiento", fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

data class Movimiento(
    val nombre: String,
    val categoria: String,
    val cantidad: String,
    val esIngreso: Boolean,
    val icono: ImageVector,
    val color: Color
)