package com.gema.zenitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(onBack: () -> Unit) {
    val movimientosHoy = listOf(
        Movimiento("Starbucks", "Café y snacks", "-5.50€", false, Icons.Default.Restaurant, Color(0xFF00BFA5)),
        Movimiento("Nómina", "Salario Mensual", "+2000€", true, Icons.Default.Payments, Color(0xFF00897B))
    )
    val movimientosAyer = listOf(
        Movimiento("Netflix", "Entretenimiento", "-15.99€", false, Icons.Default.Movie, Color(0xFF00BFA5)),
        Movimiento("Mercadona", "Comida", "-85.20€", false, Icons.Default.ShoppingCart, Color(0xFF00897B))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Movimientos", fontWeight = FontWeight.Bold, color = Color(0xFF004D40)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = ZenitLightGreen)
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ZenitLightGreen.copy(alpha = 0.2f)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val mod = Modifier.weight(1f)
                    TarjetaMovimientoResumen(mod, "Ingresos", "+3200€", Icons.Default.ArrowDownward, Color(0xFF00BFA5))
                    TarjetaMovimientoResumen(mod, "Gastos", "-1450€", Icons.Default.ArrowUpward, Color(0xFFE57373))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FiltroChip("Todos", seleccionado = true)
                    FiltroChip("Ingresos", seleccionado = false)
                    FiltroChip("Gastos", seleccionado = false)
                }
                Spacer(Modifier.height(20.dp))
            }

            item { CabeceraFecha("Hoy", "16 Abril") }
            items(movimientosHoy) { mov -> ItemMovimiento(mov) }

            item { CabeceraFecha("Ayer", "15 Abril") }
            items(movimientosAyer) { mov -> ItemMovimiento(mov) }

            item {
                BotonAnadirGasto()
            }
        }
    }
}

@Composable
fun TarjetaMovimientoResumen(modifier: Modifier, titulo: String, cantidad: String, icono: ImageVector, colorIcono: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icono, null, tint = colorIcono)
            Text(titulo, color = Color.Gray, fontSize = 14.sp)
            Text(cantidad, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = if(titulo == "Gastos") Color(0xFFE57373) else Color(0xFF00BFA5))
        }
    }
}

@Composable
fun FiltroChip(texto: String, seleccionado: Boolean) {
    Surface(
        color = if (seleccionado) Color(0xFF80CBC4) else Color(0xFFB2DFDB).copy(alpha = 0.5f),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(35.dp)
    ) {
        Box(Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
            Text(texto, color = if (seleccionado) Color.White else Color(0xFF00796B), fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

@Composable
fun CabeceraFecha(dia: String, fecha: String) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(dia, fontWeight = FontWeight.Bold, color = Color.Black)
        Text(fecha, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun ItemMovimiento(mov: Movimiento) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(Modifier.fillMaxWidth().height(80.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.fillMaxHeight().width(65.dp).background(mov.color), contentAlignment = Alignment.Center) {
                Icon(mov.icono, null, tint = Color.White)
            }
            Row(Modifier.padding(horizontal = 16.dp).weight(1f), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(mov.nombre, fontWeight = FontWeight.Bold)
                    Text(mov.categoria, fontSize = 12.sp, color = Color.Gray)
                }
                Text(mov.cantidad, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BotonAnadirGasto() {
    //este lleva a otra pantalla
    Button(
        onClick = { /* Lógica de guardado */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .height(60.dp),
        shape = RoundedCornerShape(15.dp),
        colors = ButtonDefaults.buttonColors(containerColor = ZenitGreen)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.width(12.dp))
        Text("Añadir Gasto / Ingreso", fontSize = 18.sp, fontWeight = FontWeight.Bold)
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