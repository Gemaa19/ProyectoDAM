package com.gema.zenitapp

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.tooling.preview.Preview
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
import android.content.res.Configuration
import com.gema.zenitapp.ui.theme.ZenitAppTheme

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
    var anioSeleccionado by remember { mutableStateOf(hoy.year.toString()) }

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
        authViewModel.obtenerCategoriasBBDD(context)
    }

    val movimientosReales = authViewModel.listaMovimientos
    val mapaMeses = listOf("Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre")
    val numeroMesSeleccionado = mapaMeses.indexOf(mesSeleccionado) + 1
    val movimientosDelMes = movimientosReales.filter { transaccion ->
        try {
            val fechaTransaccion = LocalDate.parse(transaccion.fecha)

            fechaTransaccion.monthValue == numeroMesSeleccionado &&
                    fechaTransaccion.year.toString() == anioSeleccionado
        } catch (e: Exception) {
            false
        }
    }

    val gastosFiltrados = movimientosDelMes.filter { it.tipo == "GASTO" }
    val totalGastosMes = gastosFiltrados.sumOf { it.monto }

    val datosGraficoUnificados = remember(gastosFiltrados, authViewModel.listaCategorias) {
        authViewModel.listaCategorias.map { cat ->
            val totalGastoCategoria = gastosFiltrados.filter { it.categoriaId == cat.id }.sumOf { it.monto }
            val pct = if (totalGastosMes > 0) (totalGastoCategoria / totalGastosMes).toFloat() else 0f

            val colorAsignado = when (cat.icono?.lowercase()) {
                "hogar"      -> Color(0xFF0D5140)
                "servicios"  -> Color(0xFFFFB300)
                "transporte" -> Color(0xFF03A9F4)
                "comida"     -> Color(0xFFFF5722)
                "gym"        -> Color(0xFF9C27B0)
                "salud"      -> Color(0xFFE91E63)
                "ocio"       -> Color(0xFF009688)
                else         -> Color(0xFF90A4AE)
            }

            DatosPorcentajeCategoria(
                nombre = cat.nombre,
                porcentaje = pct,
                color = colorAsignado
            )
        }
    }
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CajaEtiquetaAnalisis {
                        Text(
                            text = "Gastos del mes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

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

                if (totalGastosMes > 0) {
                    GraficoDonutPrincipal(datos = datosGraficoUnificados)
                    Spacer(Modifier.height(24.dp))
                    LeyendaAnalisis(datos = datosGraficoUnificados)
                } else {
                    Box(modifier = Modifier.height(220.dp), contentAlignment = Alignment.Center) {
                        Text("No hay gastos en $mesSeleccionado de $anioSeleccionado", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                Spacer(Modifier.height(30.dp))

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
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

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

                GraficoBarrasAnualHorizontal(
                    movimientos = authViewModel.listaMovimientos,
                    anioFiltrado = anioSeleccionado.toInt()
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
data class DatosPorcentajeCategoria(
    val nombre: String,
    val porcentaje: Float,
    val color: Color
)

@Composable
fun GraficoDonutPrincipal(datos: List<DatosPorcentajeCategoria>) {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val grosorLinea = 32.dp.toPx()
            var anguloInicio = -90f

            datos.forEach { item ->
                val anguloArco = item.porcentaje * 360f
                if (anguloArco > 0) {
                    drawArc(
                        color = item.color,
                        startAngle = anguloInicio,
                        sweepAngle = anguloArco,
                        useCenter = false,
                        style = Stroke(grosorLinea)
                    )
                    anguloInicio += anguloArco
                }
            }
        }
    }
}

@Composable
fun LeyendaAnalisis(datos: List<DatosPorcentajeCategoria>) {
    val categoriasActivas = datos.filter { it.porcentaje > 0f }

    val mitad = (categoriasActivas.size + 1) / 2
    val columna1 = categoriasActivas.take(mitad)
    val columna2 = categoriasActivas.drop(mitad)

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            columna1.forEach { item ->
                ItemLeyenda(item.color, item.nombre, "${(item.porcentaje * 100).toInt()}%")
            }
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            columna2.forEach { item ->
                ItemLeyenda(item.color, item.nombre, "${(item.porcentaje * 100).toInt()}%")
            }
        }
    }
}
@Composable
fun ItemLeyenda(color: Color, texto: String, porcentaje: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(16.dp), shape = RoundedCornerShape(4.dp), color = color) {}
        Spacer(Modifier.width(8.dp))
        Text("$texto ($porcentaje)", fontSize = 13.sp, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Medium)
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
        Text(seleccionado, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary)

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            meses.forEach { mes ->
                DropdownMenuItem(text = { Text(mes) }, onClick = { onMesCambio(mes); expanded = false })
            }
        }
    }
}

@Composable
fun SelectorAnioSimple(seleccionado: String, onAnioCambio: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val anios = listOf("2024", "2025", "2026", "2027")

    Row(
        modifier = Modifier.clickable { expanded = true }.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(seleccionado, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary)

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
    val context = LocalContext.current
    val listaMeses = listOf("Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic")
    val hoy = LocalDate.now()
    val mesActualIndex = hoy.monthValue - 1
    val estadoScroll = rememberLazyListState()

    val anchoColumna = (LocalConfiguration.current.screenWidthDp.dp - 40.dp) / 4
    val alturaMaximaGrafico = 110.dp

    LaunchedEffect(anioFiltrado, movimientos) {
        val hoyEnDispositivo = LocalDate.now()
        if (anioFiltrado == hoyEnDispositivo.year) {
            val anchoPantallaPx = context.resources.displayMetrics.widthPixels
            val densidad = context.resources.displayMetrics.density
            val anchoColumnaPx = (anchoColumna.value * densidad).toInt()
            val offsetDerecho = -(anchoPantallaPx - anchoColumnaPx - (40 * densidad).toInt())
            estadoScroll.scrollToItem(index = mesActualIndex, scrollOffset = offsetDerecho)
        } else {
            estadoScroll.scrollToItem(index = 0)
        }
    }

    LazyRow(
        state = estadoScroll,
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
                val esModoOscuro = isSystemInDarkTheme()
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
                                    color = if (esModoOscuro) Color(0xFF3A3A3C) else Color.LightGray,
                                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                )
                            }
                            Canvas(modifier = Modifier.width(16.dp).height(10.dp)) {
                                drawRect(
                                    color = if (esModoOscuro) Color(0xFF3A3A3C) else Color.LightGray,
                                    style = Stroke(width = 1.5.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f))
                                )
                            }
                        }
                    }else {
                        val maxMontoPosible = 3000.0
                        val alturaIngresoDp = (alturaMaximaGrafico * (ingresosMes / maxMontoPosible).toFloat()).coerceIn(6.dp, alturaMaximaGrafico)
                        val alturaGastoDp = (alturaMaximaGrafico * (gastosMes / maxMontoPosible).toFloat()).coerceIn(6.dp, alturaMaximaGrafico)

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(5.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Box(Modifier.width(16.dp).height(alturaIngresoDp).background(verdeIconos, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                            Box(Modifier.width(16.dp).height(alturaGastoDp).background(rosa, RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)))
                        }
                    }
                }

                Text(
                    text = nombreMes,
                    fontSize = 12.sp,
                    fontWeight = if (index == mesActualIndex && anioFiltrado == hoy.year) FontWeight.Bold else FontWeight.Medium,
                    color = if (esMesVacioOFuturo) Color.Gray else MaterialTheme.colorScheme.onBackground,
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
        color = MaterialTheme.colorScheme.onPrimary,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)),
        shadowElevation = 1.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)) {
            contenido()
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Análisis - Modo Claro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Análisis - Modo Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun AnalisisScreenPreview() {
    ZenitAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AnalisisScreen(
                onMenuClick = {},
                onNavigateToInicio = {},
                onNavigateToMovimientos = {},
                onNavigateToObjetivos = {},
                authViewModel = viewModel()
            )
        }
    }
}