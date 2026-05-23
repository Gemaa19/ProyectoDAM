package com.gema.zenitapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.rosa
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalisisScreen(
    onMenuClick: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToObjetivos: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val hoy = LocalDate.now()

    var mesSeleccionado by remember {
        mutableStateOf(hoy.month.getDisplayName(TextStyle.FULL, Locale("es")).replaceFirstChar { it.uppercase() })
    }

    // NUEVO: Estado para controlar el año seleccionado
    var anioSeleccionado by remember { mutableStateOf(hoy.year.toString()) }

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
    }

    // 💡 SOLUCIÓN: Cambiamos .transaccionesReales (que solo tiene 10) por .listaMovimientos (el histórico completo)
    val movimientosReales = authViewModel.listaMovimientos

    // El resto de tus filtros cruzados se queda exactamente igual...
    val movimientosDelMes = movimientosReales.filter { transaccion ->
        try {
            val fechaTransaccion = LocalDate.parse(transaccion.fecha)
            val nombreMesTransaccion = fechaTransaccion.month.getDisplayName(TextStyle.FULL, Locale("es"))
            nombreMesTransaccion.equals(mesSeleccionado, ignoreCase = true) &&
                    fechaTransaccion.year.toString() == anioSeleccionado
        } catch (e: Exception) {
            false
        }
    }

    val gastosFiltrados = movimientosDelMes.filter { it.tipo == "GASTO" }
    val totalGastosMes = gastosFiltrados.sumOf { it.monto }

    val gastoHogar = gastosFiltrados.filter { it.categoriaId == 1L }.sumOf { it.monto }
    // 💡 CORRECCIÓN ORTOGRÁFICA:
    val gastoServicios = gastosFiltrados.filter { it.categoriaId == 2L }.sumOf { it.monto }

    val gastoTransporte = gastosFiltrados.filter { it.categoriaId == 3L }.sumOf { it.monto }
    val gastoComida = gastosFiltrados.filter { it.categoriaId == 4L }.sumOf { it.monto }

    val pctHogar = if (totalGastosMes > 0) (gastoHogar / totalGastosMes).toFloat() else 0f
    val pctServicios = if (totalGastosMes > 0) (gastoServicios / totalGastosMes).toFloat() else 0f
    val pctTransporte = if (totalGastosMes > 0) (gastoTransporte / totalGastosMes).toFloat() else 0f
    val pctComida = if (totalGastosMes > 0) (gastoComida / totalGastosMes).toFloat() else 0f
    val pctOtros = (1f - (pctHogar + pctServicios + pctTransporte + pctComida)).coerceAtLeast(0f)

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                pantallaActual = "Análisis",
                onInicioClick = onNavigateToInicio,
                onMovimientosClick = onNavigateToMovimientos,
                onAnalisisClick = {},
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
                titulo = "Análisis",
                tamañoLetra = 35,
                onMenuClick = onMenuClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // FILA DE SELECTORES (Mes y Año lado a lado)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CajaEtiquetaAnalisis(modifier = Modifier.weight(1f)) {
                        SelectorMesSimple(mesSeleccionado) { mesSeleccionado = it }
                    }
                    CajaEtiquetaAnalisis(modifier = Modifier.weight(0.7f)) {
                        SelectorAnioSimple(anioSeleccionado) { anioSeleccionado = it }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // GRÁFICO DE DONUT
                if (totalGastosMes > 0) {
                    GraficoDonutPrincipal(pctHogar, pctServicios, pctTransporte, pctComida, pctOtros)
                    Spacer(Modifier.height(24.dp))
                    LeyendaAnalisis(pctHogar, pctServicios, pctTransporte, pctComida, pctOtros)
                } else {
                    Box(modifier = Modifier.height(220.dp), contentAlignment = Alignment.Center) {
                        Text("No hay gastos en $mesSeleccionado de $anioSeleccionado", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(30.dp))

                // SECCIÓN HISTÓRICO ANUAL CON MINI LEYENDA INCORPORADA AL LADO
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CajaEtiquetaAnalisis {
                        Text(
                            text = "Resumen anual",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0D5140)
                        )
                    }

                    // Mini Leyenda de Barras
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(verdeIconos, RoundedCornerShape(2.dp)))
                            Spacer(Modifier.width(4.dp))
                            Text("Ingresos", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(rosa, RoundedCornerShape(2.dp)))
                            Spacer(Modifier.width(4.dp))
                            Text("Gastos", fontSize = 11.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Gráfico de barras horizontal pasándole el año actual de filtrado
                GraficoBarrasAnualHorizontal(movimientosReales, anioSeleccionado.toInt())

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun GraficoDonutPrincipal(pHogar: Float, pServicios: Float, pTransporte: Float, pComida: Float, pOtros: Float) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val grosorLinea = 32.dp.toPx()
            var anguloInicio = -90f

            val sHogar = pHogar * 360f
            if (sHogar > 0) drawArc(Color(0xFF0D5140), anguloInicio, sHogar, false, style = Stroke(grosorLinea))
            anguloInicio += sHogar

            val sServicios = pServicios * 360f
            if (sServicios > 0) drawArc(Color(0xFFFFB300), anguloInicio, sServicios, false, style = Stroke(grosorLinea))
            anguloInicio += sServicios

            val sTransporte = pTransporte * 360f
            if (sTransporte > 0) drawArc(Color(0xFF03A9F4), anguloInicio, sTransporte, false, style = Stroke(grosorLinea))
            anguloInicio += sTransporte

            val sComida = pComida * 360f
            if (sComida > 0) drawArc(Color(0xFFFF5722), anguloInicio, sComida, false, style = Stroke(grosorLinea))
            anguloInicio += sComida

            val sOtros = pOtros * 360f
            if (sOtros > 0) drawArc(Color(0xFF90A4AE), anguloInicio, sOtros, false, style = Stroke(grosorLinea))
        }
    }
}

@Composable
fun LeyendaAnalisis(pHogar: Float, pServicios: Float, pTransporte: Float, pComida: Float, pOtros: Float) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ItemLeyenda(Color(0xFF0D5140), "Hogar", "${(pHogar * 100).toInt()}%")
            ItemLeyenda(Color(0xFFFFB300), "Servicios", "${(pServicios * 100).toInt()}%")
            ItemLeyenda(Color(0xFF03A9F4), "Transporte", "${(pTransporte * 100).toInt()}%")
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ItemLeyenda(Color(0xFFFF5722), "Comida", "${(pComida * 100).toInt()}%")
            ItemLeyenda(Color(0xFF90A4AE), "Otros", "${(pOtros * 100).toInt()}%")
        }
    }
}

@Composable
fun ItemLeyenda(color: Color, texto: String, porcentaje: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(16.dp), shape = RoundedCornerShape(4.dp), color = color) {}
        Spacer(Modifier.width(8.dp))
        Text("$texto ($porcentaje)", fontSize = 13.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SelectorMesSimple(seleccionado: String, onMesCambio: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val meses = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")

    Row(
        modifier = Modifier.clickable { expanded = true }.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(seleccionado, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140))
        Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF0D5140))

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            meses.forEach { mes ->
                DropdownMenuItem(text = { Text(mes) }, onClick = { onMesCambio(mes); expanded = false })
            }
        }
    }
}

// NUEVO COMPONENTE: Selector de Año Dinámico
@Composable
fun SelectorAnioSimple(seleccionado: String, onAnioCambio: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val anios = listOf("2024", "2025", "2026", "2027")

    Row(
        modifier = Modifier.clickable { expanded = true }.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(seleccionado, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140))
        Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF0D5140))

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            anios.forEach { anio ->
                DropdownMenuItem(text = { Text(anio) }, onClick = { onAnioCambio(anio); expanded = false })
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun GraficoBarrasAnualHorizontal(movimientos: List<com.gema.zenit.models.TransaccionResponse>, anioFiltrado: Int) {
    val listaMeses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    val hoy = LocalDate.now()
    val mesActualIndex = hoy.monthValue - 1

    val anchoColumna = (LocalConfiguration.current.screenWidthDp.dp - 40.dp) / 4
    val alturaMaximaGrafico = 110.dp // Fijamos la altura del contenedor de barras

    LazyRow(
        modifier = Modifier.fillMaxWidth().height(150.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        itemsIndexed(listaMeses) { index, nombreMes ->
            val datosMes = movimientos.filter {
                try {
                    val f = LocalDate.parse(it.fecha)
                    f.monthValue == (index + 1) && f.year == anioFiltrado
                } catch (e: Exception) { false }
            }

            val ingresosMes = datosMes.filter { it.tipo == "INGRESO" }.sumOf { it.monto }
            val gastosMes = datosMes.filter { it.tipo == "GASTO" }.sumOf { it.monto }

            val esMesVacioOFuturo = (index > mesActualIndex && anioFiltrado == hoy.year) || (anioFiltrado > hoy.year) && datosMes.isEmpty()

            Column(
                modifier = Modifier.width(anchoColumna),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                // Contenedor con altura fija para evitar solapamientos del weight
                Box(
                    modifier = Modifier.height(alturaMaximaGrafico).fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (esMesVacioOFuturo) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Canvas(modifier = Modifier.width(16.dp).height(20.dp)) {
                                drawRect(
                                    color = Color.LightGray,
                                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                )
                            }
                            Canvas(modifier = Modifier.width(16.dp).height(10.dp)) {
                                drawRect(
                                    color = Color.LightGray,
                                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                )
                            }
                        }
                    } else {
                        val maxMontoPosible = 3000.0
                        // Convertimos la proporción directamente a Dp fijos del contenedor
                        val alturaIngresoDp = (alturaMaximaGrafico * (ingresosMes / maxMontoPosible).toFloat()).coerceIn(6.dp, alturaMaximaGrafico)
                        val alturaGastoDp = (alturaMaximaGrafico * (gastosMes / maxMontoPosible).toFloat()).coerceIn(6.dp, alturaMaximaGrafico)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            // CORRECCIÓN: Usamos Modifier.height() explícito en lugar de fillMaxHeight() dentro del Row
                            Box(Modifier.width(16.dp).height(alturaIngresoDp).background(verdeIconos, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                            Box(Modifier.width(16.dp).height(alturaGastoDp).background(rosa, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                        }
                    }
                }

                Text(
                    text = nombreMes,
                    fontSize = 12.sp,
                    fontWeight = if (index == mesActualIndex && anioFiltrado == hoy.year) FontWeight.Bold else FontWeight.Medium,
                    color = if (esMesVacioOFuturo) Color.LightGray else Color.DarkGray,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }
    }
}

@Composable
fun CajaEtiquetaAnalisis(modifier: Modifier = Modifier, contenido: @Composable () -> Unit) {
    Surface(
        modifier = modifier.padding(vertical = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.4f)),
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            contenido()
        }
    }
}