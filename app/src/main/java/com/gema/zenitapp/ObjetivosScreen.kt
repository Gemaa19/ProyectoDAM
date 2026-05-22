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
import com.gema.zenitapp.componentes.Movimiento
import com.gema.zenitapp.ui.theme.colorBotonGeneral
import com.gema.zenitapp.ui.theme.verdeFondo
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
    onNavigateToNuevoObjetivo: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    var filtroSeleccionado by remember { mutableStateOf("Presupuestos") }

    // SINTONIZACIÓN CON AWS: Consumimos las listas dinámicas reales del ViewModel
    val presupuestosReales = authViewModel.listaPresupuestos
    val metasReales = authViewModel.listaMetas

    LaunchedEffect(Unit) {
        authViewModel.obtenerObjetivosBBDD(context) // Tu llamada síncrona para refrescar metas/presupuestos
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

            // FILTROS SUPERIORES
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

            // LISTADO CON CONTROL DE CARGA Y SEPARACIÓN REFORZADA
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
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
                        if (presupuestosReales.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("No hay presupuestos configurados", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        } else {
                            items(presupuestosReales) { presupuesto ->
                                // 1. OBTENEMOS LOS CAMPOS DE TU MODELO REAL 'RespuestaPresupuesto'
                                val id = presupuesto.id
                                val nombre = presupuesto.nombreCategoria ?: "Categoría"
                                val limite = presupuesto.montoLimite
                                val catId = presupuesto.categoriaId

                                // 2. CÁLCULO DINÁMICO: Sumamos los gastos reales de AWS para esta categoría concreta
                                val totalGastadoEnEstaCategoria = authViewModel.listaMovimientos
                                    .filter { it.tipo == "GASTO" && it.categoriaId == catId }
                                    .sumOf { it.monto }

                                // 3. Calculamos el porcentaje para la barra de progreso elegante
                                val porcentajeProgreso = if (limite > 0) (totalGastadoEnEstaCategoria / limite).toFloat() else 0f

                                val colorBarraDinamica = when {
                                    porcentajeProgreso >= 0.9f -> Color(0xFFB2130F) // Rojo peligro si agota el cupo
                                    porcentajeProgreso >= 0.7f -> Color(0xFFFFB300) // Amarillo aviso
                                    else -> verdeOscuro
                                }

                                val iconoDinamico = when (catId) {
                                    1L -> Icons.Default.Home
                                    2L -> Icons.Default.ElectricBolt
                                    3L -> Icons.Default.DirectionsCar
                                    4L -> Icons.Default.Restaurant
                                    else -> Icons.Default.CreditCard
                                }

                                ItemObjetivoRealEstilizado(
                                    nombre = nombre,
                                    // Mostramos lo gastado real frente al límite que fijó el usuario
                                    cantidadTexto = "${String.format("%.2f", totalGastadoEnEstaCategoria)}€ / ${String.format("%.2f", limite)}€",
                                    progreso = porcentajeProgreso.coerceIn(0f, 1f),
                                    icono = iconoDinamico,
                                    colorBarra = colorBarraDinamica,
                                    onEditarClick = { /* Abrir modificar pasándole id */ },
                                    onEliminarClick = { /* Llamar a eliminar pasándole id */ }
                                )
                            }
                        }
                    } else {
                        // SECCIÓN PARA LAS METAS (Sintonizada con tu RespuestaMeta)
                        if (metasReales.isEmpty()) {
                            item {
                                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                                    Text("No hay metas configuradas", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                        } else {
                            items(metasReales) { meta ->
                                val id = meta.id
                                val nombre = meta.nombre ?: "Meta sin nombre"
                                val ahorrado = meta.ahorrado
                                val objetivo = meta.objetivo

                                // Adaptamos el progreso de la meta (0-100) a la escala de Compose (0.0-1.0)
                                val porcentajeProgreso = (meta.progreso / 100.0).toFloat()

                                ItemObjetivoRealEstilizado(
                                    nombre = nombre,
                                    cantidadTexto = "${String.format("%.2f", ahorrado)}€ / ${String.format("%.2f", objetivo)}€",
                                    progreso = porcentajeProgreso.coerceIn(0f, 1f),
                                    icono = Icons.Default.TrackChanges,
                                    colorBarra = Color(0xFF029B09), // Verde ahorro fijo
                                    onEditarClick = { /* Abrir modificar pasándole id */ },
                                    onEliminarClick = { /* Llamar a eliminar pasándole id */ }
                                )
                            }
                        }
                    }
                }
            }
            // BOTÓN FIJO EN EL PIE DE LA PANTALLA
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = onNavigateToNuevoObjetivo,
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

// NUEVO DISEÑO: Tarjeta expandida con mayor espacio de respiración (75.dp)
@Composable
fun ItemObjetivoRealEstilizado(
    nombre: String,
    cantidadTexto: String,
    progreso: Float,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    colorBarra: Color,
    onEditarClick: () -> Unit,
    onEliminarClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp) // NUEVO: Subido de 65.dp a 75.dp para darle más altura y presencia
            .padding(horizontal = 20.dp, vertical = 6.dp), // NUEVO: Más espacio libre entre tarjetas consecutivas
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(5.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Contenedor izquierdo del icono adaptado a la nueva altura
            Box(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxHeight()
                    .width(62.dp)
                    .background(color = verdeFondo, shape = RoundedCornerShape(18.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = verdeOscuro,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Bloque de textos central con mayor separación vertical interna
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 12.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp, // Letra un poco más grande
                        color = Color(0xFF1A1A1A),
                        maxLines = 1
                    )
                    Text(
                        text = cantidadTexto,
                        color = Color(0xFF9EA1A7),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        maxLines = 1
                    )
                }

                // NUEVO: Mayor espacio de separación entre la fila de textos y la barra de progreso
                Spacer(modifier = Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { progreso },
                    modifier = Modifier.fillMaxWidth().height(7.dp), // Barra ligeramente más gruesa
                    color = colorBarra,
                    trackColor = Color(0xFFF0F2F5),
                    strokeCap = StrokeCap.Round
                )
            }

            // Botones de control compactados a la derecha
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