package com.gema.zenitapp

import android.widget.Toast
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.ui.theme.colorBotonGeneral
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeTitulos
import com.gema.zenitapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ObjetivosScreen(
    onMenuClick: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToAnalisis: () -> Unit,
    onNavigateToNuevoObjetivo: (id: String?, tipo: String) -> Unit,
    pestañaInicial: String = "Presupuestos",
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var filtroSeleccionado by remember { mutableStateOf(pestañaInicial) }

    LaunchedEffect(pestañaInicial) {
        filtroSeleccionado = pestañaInicial
    }

    val presupuestosReales = authViewModel.listaPresupuestos
    val metasReales = authViewModel.listaMetas

    LaunchedEffect(Unit) {
        authViewModel.obtenerObjetivosBBDD(context)
    }

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
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CabeceraPrincipal(titulo = "Objetivos", tamañoLetra = 35, onMenuClick = onMenuClick)

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FiltroObjetivoChip("Presupuestos", seleccionado = (filtroSeleccionado == "Presupuestos")) {
                    filtroSeleccionado = "Presupuestos"
                }
                Spacer(Modifier.width(12.dp))
                FiltroObjetivoChip("Metas", seleccionado = (filtroSeleccionado == "Metas")) {
                    filtroSeleccionado = "Metas"
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(bottom = 12.dp)
            ) {
                if (authViewModel.isLoading) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = verdeOscuro)
                        }
                    }
                } else {
                    if (filtroSeleccionado == "Presupuestos") {
                        val presupuestosMensuales = presupuestosReales.filter { it.mes != 13 }
                        val presupuestosAnuales = presupuestosReales.filter { it.mes == 13 }

                        if (presupuestosReales.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("No hay presupuestos configurados", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        } else {
                            if (presupuestosMensuales.isNotEmpty()) {
                                item {
                                    Text("Mensual", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = verdeOscuro, modifier = Modifier.padding(start = 22.dp, top = 10.dp, bottom = 6.dp))
                                }
                                items(presupuestosMensuales) { presupuesto ->
                                    val catId = presupuesto.categoriaId
                                    val totalGastadoEnEstaCategoria = authViewModel.listaMovimientos
                                        .filter { it.tipo == "GASTO" && it.categoriaId == catId }
                                        .sumOf { it.monto }

                                    val porcentajeProgreso = if (presupuesto.montoLimite > 0) (totalGastadoEnEstaCategoria / presupuesto.montoLimite).toFloat() else 0f

                                    ItemObjetivoRealEstilizado(
                                        nombre = presupuesto.nombreCategoria ?: "Categoría",
                                        cantidadTexto = "${String.format("%.2f", totalGastadoEnEstaCategoria)}€ / ${String.format("%.2f", presupuesto.montoLimite)}€",
                                        progreso = porcentajeProgreso.coerceIn(0f, 1f),
                                        esProgresoCero = totalGastadoEnEstaCategoria == 0.0,
                                        icono = when (catId) {
                                            1L -> Icons.Default.Home
                                            2L -> Icons.Default.ElectricBolt
                                            3L -> Icons.Default.DirectionsCar
                                            4L -> Icons.Default.Restaurant
                                            else -> Icons.Default.CreditCard
                                        },
                                        colorBarra = if (porcentajeProgreso >= 0.9f) Color(0xFFB2130F) else verdeOscuro,
                                        onEditarClick = { onNavigateToNuevoObjetivo(presupuesto.id.toString(), "PRESUPUESTO") },
                                        onEliminarClick = {
                                            authViewModel.eliminarPresupuestoBBDD(context, presupuesto.id) {
                                                Toast.makeText(context, "Presupuesto eliminado", Toast.LENGTH_SHORT).show()
                                                authViewModel.obtenerObjetivosBBDD(context)
                                            }
                                        }
                                    )
                                }
                            }

                            if (presupuestosAnuales.isNotEmpty()) {
                                item {
                                    Text("Anual", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = verdeOscuro, modifier = Modifier.padding(start = 22.dp, top = 16.dp, bottom = 6.dp))
                                }
                                items(presupuestosAnuales) { presupuesto ->
                                    val catId = presupuesto.categoriaId
                                    val totalGastadoEnEstaCategoria = authViewModel.listaMovimientos
                                        .filter { it.tipo == "GASTO" && it.categoriaId == catId }
                                        .sumOf { it.monto }

                                    val porcentajeProgreso = if (presupuesto.montoLimite > 0) (totalGastadoEnEstaCategoria / presupuesto.montoLimite).toFloat() else 0f

                                    ItemObjetivoRealEstilizado(
                                        nombre = presupuesto.nombreCategoria ?: "Categoría",
                                        cantidadTexto = "${String.format("%.2f", totalGastadoEnEstaCategoria)}€ / ${String.format("%.2f", presupuesto.montoLimite)}€",
                                        progreso = porcentajeProgreso.coerceIn(0f, 1f),
                                        esProgresoCero = totalGastadoEnEstaCategoria == 0.0,
                                        icono = when (catId) {
                                            1L -> Icons.Default.Home
                                            2L -> Icons.Default.ElectricBolt
                                            3L -> Icons.Default.DirectionsCar
                                            4L -> Icons.Default.Restaurant
                                            else -> Icons.Default.CreditCard
                                        },
                                        colorBarra = if (porcentajeProgreso >= 0.9f) Color(0xFFB2130F) else verdeOscuro,
                                        onEditarClick = { onNavigateToNuevoObjetivo(presupuesto.id.toString(), "PRESUPUESTO") },
                                        onEliminarClick = {
                                            authViewModel.eliminarPresupuestoBBDD(context, presupuesto.id) {
                                                Toast.makeText(context, "Presupuesto eliminado", Toast.LENGTH_SHORT).show()
                                                authViewModel.obtenerObjetivosBBDD(context)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        val metasMensuales = metasReales.filter { it.fechaLimite != null && !it.fechaLimite.contains("-12-31") }
                        val metasAnuales = metasReales.filter { it.fechaLimite != null && it.fechaLimite.contains("-12-31") }

                        if (metasReales.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("No hay metas configuradas", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        } else {
                            if (metasMensuales.isNotEmpty()) {
                                item {
                                    Text("Mensual", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = verdeOscuro, modifier = Modifier.padding(start = 22.dp, top = 10.dp, bottom = 6.dp))
                                }
                                items(metasMensuales) { meta ->
                                    ItemObjetivoRealEstilizado(
                                        nombre = meta.nombre ?: "Meta sin nombre",
                                        cantidadTexto = "${String.format("%.2f", meta.ahorrado)}€ / ${String.format("%.2f", meta.objetivo)}€",
                                        progreso = (meta.progreso / 100.0).toFloat().coerceIn(0f, 1f),
                                        esProgresoCero = meta.ahorrado == 0.0,
                                        icono = Icons.Default.TrackChanges,
                                        colorBarra = verdeOscuro,
                                        onEditarClick = { onNavigateToNuevoObjetivo(meta.id.toString(), "META") },
                                        onEliminarClick = {
                                            authViewModel.eliminarMetaBBDD(context, meta.id) {
                                                Toast.makeText(context, "Meta de ahorro eliminada", Toast.LENGTH_SHORT).show()
                                                authViewModel.obtenerObjetivosBBDD(context)
                                            }
                                        }
                                    )
                                }
                            }

                            if (metasAnuales.isNotEmpty()) {
                                item {
                                    Text("Anual", fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = verdeOscuro, modifier = Modifier.padding(start = 22.dp, top = 16.dp, bottom = 6.dp))
                                }
                                items(metasAnuales) { meta ->
                                    ItemObjetivoRealEstilizado(
                                        nombre = meta.nombre ?: "Meta sin nombre",
                                        cantidadTexto = "${String.format("%.2f", meta.ahorrado)}€ / ${String.format("%.2f", meta.objetivo)}€",
                                        progreso = (meta.progreso / 100.0).toFloat().coerceIn(0f, 1f),
                                        esProgresoCero = meta.ahorrado == 0.0,
                                        icono = Icons.Default.TrackChanges,
                                        colorBarra = verdeOscuro,
                                        onEditarClick = { onNavigateToNuevoObjetivo(meta.id.toString(), "META") },
                                        onEliminarClick = {
                                            authViewModel.eliminarMetaBBDD(context, meta.id) {
                                                Toast.makeText(context, "Meta de ahorro eliminada", Toast.LENGTH_SHORT).show()
                                                authViewModel.obtenerObjetivosBBDD(context)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 32.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { onNavigateToNuevoObjetivo(null, "CLEAN") },
                    colors = ButtonDefaults.buttonColors(containerColor = colorBotonGeneral),
                    border = BorderStroke(width = 4.dp, color = verdeTitulos),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(50.dp).fillMaxWidth(0.8f)
                ) {
                    Icon(Icons.Default.AddCircleOutline, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Añadir objetivo", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ItemObjetivoRealEstilizado(
    nombre: String,
    cantidadTexto: String,
    progreso: Float,
    esProgresoCero: Boolean,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorBarra: Color,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(6.dp)
                    .fillMaxHeight()
                    .width(62.dp)
                    .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 14.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1
                )

                Text(
                    text = cantidadTexto,
                    color = Color(0xFF9EA1A7),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1
                )

                LinearProgressIndicator(
                    progress = { if (esProgresoCero) 0f else progreso },
                    modifier = Modifier.fillMaxWidth().height(6.dp),
                    color = if (esProgresoCero) Color(0xFFF0F2F5) else colorBarra,
                    trackColor = Color(0xFFF0F2F5),
                    strokeCap = StrokeCap.Round
                )
            }

            Row(
                modifier = Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onEditarClick, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Edit, null, tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onEliminarClick, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Default.Delete, null, tint = Color(0xFFB2130F), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
@Composable
fun FiltroObjetivoChip(texto: String, seleccionado: Boolean, onClick: () -> Unit) {
    Surface(
        color = if (seleccionado) Color(0xFF0D5140) else Color.White,
        shape = RoundedCornerShape(20.dp),
        border = if (!seleccionado) BorderStroke(1.dp, Color.LightGray) else null,
        modifier = Modifier
            .height(36.dp)
            .clickable { onClick() }
    ) {
        Box(Modifier.padding(horizontal = 24.dp), contentAlignment = Alignment.Center) {
            Text(
                text = texto,
                color = if (seleccionado) Color.White else Color.Gray,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
        }
    }
}