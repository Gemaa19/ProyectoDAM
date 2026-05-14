package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(onNavigateToPrevision: () -> Unit, onNavigateToMovimientos: () -> Unit, onMenuClick: () -> Unit) {

    // Datos de prueba adaptados a tu clase original
    val movimientosHoy = listOf(
        Movimiento("Starbucks", "Café y snacks", "-5.50€", false, Icons.Default.Restaurant, ZenitGreen),
        Movimiento("Nómina", "Salario Mensual", "+2000€", true, Icons.Default.Payments, ZenitGreen)
    )

    val movimientosAyer = listOf(
        Movimiento("Netflix", "Entretenimiento", "-15.99€", false, Icons.Default.Tv, ZenitGreen)
    )

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                onMetasClick = onNavigateToPrevision,
                onMovimientosClick = onNavigateToMovimientos
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White) // Fondo general blanco
        ) {
            CabeceraPrincipal(onMenuClick = onMenuClick)

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    SeccionSaldo()
                }

                item {
                    TarjetasResumidas()
                }

                item {
                    Text(
                        text = "Gastos del mes",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                    )
                }

                // Sección HOY
                item {
                    FilaFecha("Hoy", "16 Abril")
                }
                items(movimientosHoy) { mov ->
                    ItemGasto(mov)
                }

                // Sección AYER
                item {
                    Spacer(Modifier.height(8.dp))
                    FilaFecha("Ayer", "15 Abril")
                }
                items(movimientosAyer) { mov ->
                    ItemGasto(mov)
                }

                item { Spacer(Modifier.height(30.dp)) }
            }
        }
    }
}
@Composable
fun CabeceraPrincipal(onMenuClick: () -> Unit) {
    Box( // <--- CAMBIADO DE Row A Box
        modifier = Modifier.fillMaxWidth().background(ZenitLightGreen).padding(16.dp)
    ) {
        // Icono hamburguesa clicable
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menú lateral",
            modifier = Modifier
                .size(30.dp)
                .align(Alignment.CenterStart) // <--- Pegado a la izquierda
                .clickable { onMenuClick() }
        )
        Text(
            text = "ZENIT",
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            letterSpacing = 4.sp,
            color = Color(0xFF0D5140),
            modifier = Modifier.align(Alignment.Center) // <--- Perfectamente centrado
        )
    }
}
@Composable
fun SeccionSaldo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DINERO LIBRE REAL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("8512,46€", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D5140))
        Surface(
            color = ZenitLightGreen,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "+5.2% vs mes anterior",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color(0xFF0D5140),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TarjetasResumidas() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val modifier = Modifier.weight(1f)
        TarjetaResumenPequeña(modifier, "Ingresos", "+1500€", Icons.Default.Payments, Color(0xFF4285F4))
        TarjetaResumenPequeña(modifier, "Gastos", "-800€", Icons.Default.CreditCard, Color(0xFFE91E63))
        TarjetaResumenPequeña(modifier, "Saldo mensual", "700€", Icons.Default.AccountBalance, Color(0xFF9C27B0))
    }
}

@Composable
fun TarjetaResumenPequeña(modifier: Modifier, title: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, color = Color.Gray, fontSize = 11.sp, maxLines = 2, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(amount, fontWeight = FontWeight.Bold, color = ZenitGreen, fontSize = 14.sp)
        }
    }
}

@Composable
fun FilaFecha(dia: String, fecha: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(dia, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(fecha, color = Color.Gray, fontSize = 14.sp)
    }
}
@Composable
fun ItemGasto(movimiento: Movimiento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .height(70.dp),
        shape = RoundedCornerShape(35.dp), // Forma de píldora
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bloque de color izquierdo con el icono
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(70.dp)
                    .background(movimiento.color), // Cambiado a .color
                contentAlignment = Alignment.Center
            ) {
                Icon(movimiento.icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }

            // Textos centrales
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(movimiento.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black) // Cambiado a .nombre
                Text(movimiento.categoria, color = Color.Gray, fontSize = 13.sp) // Cambiado a .categoria
            }

            // Cantidad
            Text(
                text = movimiento.cantidad,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                // Usamos tu variable esIngreso para cambiar el color del dinero
                color = if (movimiento.esIngreso) ZenitGreen else Color.Black,
                modifier = Modifier.padding(end = 20.dp)
            )
        }
    }
}
@Composable
fun BarraNavegacionInferior(onMetasClick: () -> Unit, onMovimientosClick: () -> Unit) {
    // Variable para controlar visualmente qué pestaña está activa
    var pestañaSeleccionada by remember { mutableStateOf(0) }

    NavigationBar(
        containerColor = ZenitLightGreen, // Fondo verde claro como en la imagen
        tonalElevation = 0.dp,
        modifier = Modifier.height(90.dp) // Un poco más alto para dar respiro
    ) {
        val items = listOf(
            Triple("Inicio", Icons.Default.Home, {}),
            Triple("Movimientos", Icons.Default.Receipt, onMovimientosClick),
            Triple("Análisis", Icons.Default.PieChart, {}),
            Triple("Objetivos", Icons.Default.Savings, onMetasClick)
        )

        items.forEachIndexed { index, item ->
            val seleccionado = pestañaSeleccionada == index

            NavigationBarItem(
                icon = {
                    Box(
                        modifier = Modifier
                            .padding(top = 15.dp) // Espacio extra por arriba de los iconos
                            .size(48.dp)
                            .background(
                                color = if (seleccionado) Color.White else ZenitGreen, // COLORES AL REVÉS
                                shape = androidx.compose.foundation.shape.CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ){
                        Icon(
                            imageVector = item.second,
                            contentDescription = item.first,
                            tint = if (seleccionado) ZenitGreen else Color.White // ICONO AL REVÉS
                        )
                    }
                },
                label = { Text(item.first, color = Color.Gray) },
                selected = seleccionado,
                onClick = {
                    pestañaSeleccionada = index
                    item.third.invoke() // Ejecuta la navegación
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color.Transparent // ELIMINA EL FONDO GRIS DE MATERIAL 3
                )
            )
        }
    }
}