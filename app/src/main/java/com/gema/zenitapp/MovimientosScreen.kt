package com.gema.zenitapp

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.componentes.Movimiento // Importamos el modelo visual unificado
import com.gema.zenitapp.ui.theme.colorBotonGeneral
import com.gema.zenitapp.ui.theme.rosa
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeTitulos
import com.gema.zenitapp.viewmodel.AuthViewModel


@RequiresApi(Build.VERSION_CODES.O)
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
    var filtroSeleccionado by remember { mutableStateOf("Todos") }
    val movimientosReales = authViewModel.listaMovimientos

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
    }

    val totalIngresos = movimientosReales.filter { it.tipo == "INGRESO" }.sumOf { it.monto }
    val totalGastos = movimientosReales.filter { it.tipo == "GASTO" }.sumOf { it.monto }

    val movimientosFiltrados = when (filtroSeleccionado) {
        "Ingresos" -> movimientosReales.filter { it.tipo == "INGRESO" }
        "Gastos" -> movimientosReales.filter { it.tipo == "GASTO" }
        else -> movimientosReales
    }

    val transaccionesAgrupadas = movimientosFiltrados.groupBy { it.fecha }

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
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally // Centra el botón estático horizontalmente
        ) {
            CabeceraPrincipal(
                titulo = "Movimientos",
                tamañoLetra = 30,
                onMenuClick = onMenuClick
            )

            // Contenedor del listado: Ocupa el espacio disponible de forma elástica
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // CORRECCIÓN: Deja libre el fondo para que el botón no se mueva
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                // TARJETAS SUPERIORES DINÁMICAS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val mod = Modifier.weight(1f)
                        TarjetaMovimientoResumen(mod, "Ingresos", "+${String.format("%.2f", totalIngresos)}€", Icons.Default.ArrowUpward, verdeIconos)
                        TarjetaMovimientoResumen(mod, "Gastos", "-${String.format("%.2f", totalGastos)}€", Icons.Default.ArrowDownward, rosa)
                    }
                }

                // FILTROS CHIPS
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FiltroChipButton("Todos", seleccionado = (filtroSeleccionado == "Todos")) { filtroSeleccionado = "Todos" }
                        Spacer(Modifier.width(8.dp))
                        FiltroChipButton("Ingresos", seleccionado = (filtroSeleccionado == "Ingresos")) { filtroSeleccionado = "Ingresos" }
                        Spacer(Modifier.width(8.dp))
                        FiltroChipButton("Gastos", seleccionado = (filtroSeleccionado == "Gastos")) { filtroSeleccionado = "Gastos" }
                    }
                    Spacer(Modifier.height(14.dp))
                }

                // CONTROL DE CARGA ASÍNCRONO
                if (authViewModel.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = verdeOscuro)
                        }
                    }
                } else if (movimientosFiltrados.isEmpty()) {
                    item {
                        Text(
                            text = "No hay registros que coincidan con el filtro",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(24.dp)
                        )
                    }
                } else {
                    transaccionesAgrupadas.forEach { (fechaStr, listaDeEseDia) ->
                        item { FilaFechaDinamica(fechaSql = fechaStr) }

                        items(listaDeEseDia) { transaccion ->
                            val esIngreso = transaccion.tipo == "INGRESO"

                            val iconoCategoria = when (transaccion.categoriaId) {
                                1L -> Icons.Default.Home
                                2L -> Icons.Default.ElectricBolt
                                3L -> Icons.Default.DirectionsCar
                                4L -> Icons.Default.Restaurant
                                else -> Icons.Default.CreditCard
                            }

                            val movVisual = Movimiento(
                                nombre = transaccion.descripcion ?: "Movimiento general",
                                categoria = when (transaccion.categoriaId) {
                                    1L -> "Hogar"
                                    2L -> "Servicios"
                                    3L -> "Transporte"
                                    4L -> "Comida"
                                    else -> "General"
                                },
                                cantidad = "${if (esIngreso) "+" else "-"}${String.format("%.2f", transaccion.monto)}€",
                                esIngreso = esIngreso,
                                icono = if (esIngreso) Icons.Default.Payments else iconoCategoria,
                                color = Color(0xFF90A4AE)
                            )

                            ItemGasto(
                                movimiento = movVisual,
                                onEditarClick = { /* Próxima funcionalidad */ },
                                onEliminarClick = { /* Próxima funcionalidad */ }
                            )
                        }
                    }
                }
            } // El LazyColumn termina aquí y ya NO contiene al botón

            // CORRECCIÓN CRÍTICA: El botón se renderiza fuera de la lista.
            // Siempre se mantendrá visible pegado abajo independientemente de los registros.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp), // Margen de separación limpio arriba del menú inferior
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onNavigateToNuevoMovimiento,
                    colors = ButtonDefaults.buttonColors(containerColor = colorBotonGeneral),
                    border = BorderStroke(width = 4.dp, color = verdeTitulos),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth(0.8f)
                ) {
                    Icon(
                        imageVector = Icons.Default.AddCircleOutline,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Añadir movimiento",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// NUEVO COMPONENTE: Convertido en botón interactivo con lambda clickable
@Composable
fun FiltroChipButton(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (seleccionado) Color(0xFF0D5140) else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = if (!seleccionado) BorderStroke(1.dp, Color.LightGray) else null,
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() } // Detecta la selección y recarga la pantalla
    ) {
        Box(Modifier.padding(horizontal = 20.dp), contentAlignment = Alignment.Center) {
            Text(
                text = texto,
                color = if (seleccionado) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
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