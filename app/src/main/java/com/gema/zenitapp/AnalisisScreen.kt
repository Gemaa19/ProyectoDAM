package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.CabeceraPrincipal

@Composable
fun AnalisisScreen(
    onMenuClick: () -> Unit,
    onNavigateToInicio: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToPrevision: () -> Unit
) {
    var mesSeleccionado by remember { mutableStateOf("Marzo") }

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                pantallaActual = "Análisis",
                onInicioClick = onNavigateToInicio,
                onMovimientosClick = onNavigateToMovimientos,
                onAnalisisClick = { /* Ya estamos aquí */ },
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
                titulo = "Análisis",
                onMenuClick = onMenuClick
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SELECTOR DE MES
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    CajaEtiquetaAnalisis {
                        SelectorMesSimple(mesSeleccionado) { mesSeleccionado = it }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // GRÁFICO DE DONUT
                GraficoDonutPrincipal()

                Spacer(Modifier.height(24.dp))

                // LEYENDA (Categorías)
                LeyendaAnalisis()

                Spacer(Modifier.height(30.dp))

                // RESUMEN ANUAL (Gráfico de barras)
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                    CajaEtiquetaAnalisis {
                        Text(
                            text = "Resumen anual",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF0D5140)
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                GraficoBarrasAnual()

                Spacer(Modifier.height(20.dp))
            }
        }
    }
}

@Composable
fun GraficoDonutPrincipal() {
    Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
        Canvas(modifier = Modifier.size(180.dp)) {
            // Dibujamos las porciones (esto es representativo)
            drawArc(Color(0xFFFFD54F), startAngle = -90f, sweepAngle = 144f, useCenter = false, style = Stroke(40.dp.toPx()))
            drawArc(Color(0xFFE57373), startAngle = 54f, sweepAngle = 90f, useCenter = false, style = Stroke(40.dp.toPx()))
            drawArc(Color(0xFFFF8A65), startAngle = 144f, sweepAngle = 36f, useCenter = false, style = Stroke(40.dp.toPx()))
            drawArc(Color(0xFF006064), startAngle = 180f, sweepAngle = 54f, useCenter = false, style = Stroke(40.dp.toPx()))
            drawArc(Color(0xFF26A69A), startAngle = 234f, sweepAngle = 36f, useCenter = false, style = Stroke(40.dp.toPx()))
        }
    }
}

@Composable
fun LeyendaAnalisis() {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ItemLeyenda(Color(0xFFFFD54F), "Comida", "40%")
            ItemLeyenda(Color(0xFFE57373), "Salud", "25%")
            ItemLeyenda(Color(0xFFFF8A65), "Hogar", "10%")
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ItemLeyenda(Color(0xFF006064), "Estudios", "15%")
            ItemLeyenda(Color(0xFF26A69A), "Compras", "10%")
        }
    }
}

@Composable
fun ItemLeyenda(color: Color, texto: String, porcentaje: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(Modifier.size(24.dp), shape = RoundedCornerShape(6.dp), color = color) {}
        Spacer(Modifier.width(8.dp))
        Text("$texto - $porcentaje", fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun SelectorMesSimple(seleccionado: String, onMesCambio: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val meses = listOf("Enero", "Febrero", "Marzo", "Abril")

    Row(
        modifier = Modifier.clickable { expanded = true },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(seleccionado, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140))
        Icon(Icons.Default.ArrowDropDown, null, tint = Color(0xFF0D5140))

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            meses.forEach { mes ->
                DropdownMenuItem(
                    text = { Text(mes) },
                    onClick = { onMesCambio(mes); expanded = false }
                )
            }
        }
    }
}

@Composable
fun GraficoBarrasAnual() {
    val datos = listOf(0.4f, 0.8f, 0.6f, 0.9f)
    val meses = listOf("Ene", "Feb", "Mar", "Abr")

    Row(
        Modifier.fillMaxWidth().height(150.dp).padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        datos.forEachIndexed { i, valor ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Barra azul
                    Box(Modifier.width(18.dp).fillMaxHeight(valor).background(Color(0xFF4F81BD)))
                    // Barra roja
                    Box(Modifier.width(18.dp).fillMaxHeight(valor * 0.8f).background(Color(0xFFC0504D)))
                }
                Text(meses[i], fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
fun CajaEtiquetaAnalisis(contenido: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.padding(vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f)),
        shadowElevation = 2.dp
    ) {
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            contenido()
        }
    }
}