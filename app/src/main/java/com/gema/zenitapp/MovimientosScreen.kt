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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenit.models.TransaccionResponse
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen
import com.gema.zenitapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientosScreen(
    onMenuClick: () -> Unit,
    onNavigateToNuevoMovimiento: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToAnalisis: () -> Unit,
    onNavigateToObjetivos: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    // Observamos las transacciones que ya traen la fecha de AWS
    val movimientosReales = authViewModel.listaMovimientos

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
    }

    val totalIngresos = movimientosReales.filter { it.tipo == "INGRESO" }.sumOf { it.monto }
    val totalGastos = movimientosReales.filter { it.tipo == "GASTO" }.sumOf { it.monto }

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                pantallaActual = "Movimientos",
                onInicioClick = onNavigateToInicio,
                onMovimientosClick = {},
                onAnalisisClick = onNavigateToAnalisis,
                onObjetivosClick = onNavigateToObjetivos
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
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                // TARJETAS SUPERIORES DINÁMICAS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val mod = Modifier.weight(1f)
                        TarjetaMovimientoResumen(mod, "Ingresos", "+${String.format("%.2f", totalIngresos)}€", Icons.Default.ArrowUpward, ZenitGreen)
                        TarjetaMovimientoResumen(mod, "Gastos", "-${String.format("%.2f", totalGastos)}€", Icons.Default.ArrowDownward, Color.Red)
                    }
                }

                // FILTROS CHIPS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FiltroChip("Todos", seleccionado = true)
                        Spacer(Modifier.width(8.dp))
                        FiltroChip("Ingresos", seleccionado = false)
                        Spacer(Modifier.width(8.dp))
                        FiltroChip("Gastos", seleccionado = false)
                    }
                    Spacer(Modifier.height(20.dp))
                }

                // LISTADO REAL CON CONTROL DE CARGA
                if (authViewModel.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = ZenitGreen)
                        }
                    }
                } else if (movimientosReales.isEmpty()) {
                    item {
                        Text(
                            text = "No hay movimientos registrados",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    items(movimientosReales) { transaccion: TransaccionResponse ->
                        ItemGastoReal(transaccion = transaccion)
                    }
                }

                // BOTÓN "NUEVO MOVIMIENTO"
                item {
                    Spacer(Modifier.height(16.dp))
                    BotonNuevoMovimiento(onClick = onNavigateToNuevoMovimiento)
                }
            }
        }
    }
}

@Composable
fun ItemGastoReal(transaccion: TransaccionResponse) {
    val esIngreso = transaccion.tipo == "INGRESO"
    val iconoDinamico = when (transaccion.categoriaId) {
        1L -> Icons.Default.Home              // Hogar
        2L -> Icons.Default.ElectricBolt      // Servicios
        3L -> Icons.Default.DirectionsCar     // Transporte
        4L -> Icons.Default.Restaurant        // Comida
        else -> Icons.Default.ShoppingCart   // Por defecto
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (esIngreso) ZenitLightGreen else Color(0xFFFFEBEE),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (esIngreso) Icons.Default.Payments else iconoDinamico, // <-- Icono corregido
                            contentDescription = null,
                            tint = if (esIngreso) ZenitGreen else Color.Red,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(Modifier.width(12.dp))

                Column {
                    Text(
                        text = transaccion.descripcion ?: "Movimiento general",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )

                    // INCLUSIÓN DE LA FECHA: Pintamos la fecha de la BBDD debajo del nombre
                    Text(
                        text = transaccion.fecha,
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Importe
            Text(
                text = "${if (esIngreso) "+" else "-"}${String.format("%.2f", transaccion.monto)}€",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                color = if (esIngreso) ZenitGreen else Color.Black
            )
        }
    }
}

@Composable
fun TarjetaMovimientoResumen(modifier: Modifier, titulo: String, cantidad: String, icono: androidx.compose.ui.graphics.vector.ImageVector, colorIcono: Color) {
    Card(
        modifier = modifier.height(85.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icono, null, tint = colorIcono, modifier = Modifier.size(20.dp))
            Text(titulo, color = Color.Gray, fontSize = 11.sp)
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
fun BotonNuevoMovimiento(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
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