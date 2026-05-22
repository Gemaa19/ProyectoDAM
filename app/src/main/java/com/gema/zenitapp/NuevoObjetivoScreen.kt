package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.IconoSeleccionableCategoria
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeClaro
import com.gema.zenitapp.viewmodel.AuthViewModel

@Composable
fun NuevoObjetivoScreen(onBack: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    var esPresupuesto by remember { mutableStateOf(true) }
    var importe by remember { mutableStateOf("0.00") }
    var nombreObjetivo by remember { mutableStateOf("") }
    var esMensual by remember { mutableStateOf(true) }
    var recordatorio by remember { mutableStateOf(true) }

    var categoriaSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    val context = LocalContext.current
    Scaffold(
        topBar = { CabeceraSimple("Establecer objetivo", onBack) },
        // BUSCA EL SCOFFOLD -> BOTTOMBAR DE TU NUEVOOBJETIVOSCREEN Y SUSTITUYE EL BOTÓN POR ESTE:
        bottomBar = {
            Button(
                onClick = {
                    val montoDouble = importe.toDoubleOrNull() ?: 0.0

                    if (montoDouble > 0.0) {
                        if (esPresupuesto) {
                            // ==========================================================
                            // CASO A: EL USUARIO QUIERE GUARDAR UN PRESUPUESTO EN AWS
                            // ==========================================================
                            val catId = categoriaSeleccionadaId ?: 1L // Si no marca ninguna, por defecto Hogar (1)

                            authViewModel.guardarPresupuestoEnBBDD(
                                context = context,
                                montoLimite = montoDouble,
                                categoriaId = catId,
                                mes = 5,    // Mes actual (Mayo) - Puedes automatizarlo con java.util.Calendar si quieres
                                anio = 2026, // Año actual
                                onSuccess = { onBack() } // Vuelve al listado automáticamente
                            )
                        } else {
                            // ==========================================================
                            // CASO B: EL USUARIO QUIERE GUARDAR UNA META DE AHORRO
                            // ==========================================================
                            if (nombreObjetivo.isNotBlank()) {
                                authViewModel.guardarMetaEnBBDD(
                                    context = context,
                                    nombre = nombreObjetivo,
                                    objetivo = montoDouble,
                                    fechaLimite = null,
                                    onSuccess = { onBack() }
                                )
                            } else {
                                // Podrías mostrar un Toast pidiendo el nombre de la meta
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(55.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A680))
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar objetivo", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(15.dp))

            // SELECTOR PRESUPUESTO / META
            SelectorDoble(
                opcion1 = "Presupuesto",
                opcion2 = "Meta",
                seleccionado1 = esPresupuesto,
                onSeleccion = { esPresupuesto = it }
            )

            // IMPORTE CONFIGURADO COMO CAMPO EDITABLE REFORZADO
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Importe", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = importe,
                    onValueChange = { importe = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0.00", fontSize = 24.sp, color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    suffix = { Text("€", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140)) },
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D5140),
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier
                        .width(220.dp)
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(15.dp)), // Fondo pastel para ampliar la zona de click
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdeOscuro,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedContainerColor = Color(0xFFF9F9F9),
                        unfocusedContainerColor = Color(0xFFF9F9F9)
                    )
                )
            }

            // NOMBRE DEL OBJETIVO
            Text("Nombre del objetivo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nombreObjetivo,
                onValueChange = { nombreObjetivo = it },
                placeholder = { Text("ej. Alquiler, internet, Netflix", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp), // Tu altura personalizada
                shape = RoundedCornerShape(20.dp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = verdeOscuro // Añadido para consistencia
                ),
                singleLine = true // Esto ya ayuda a reducir el padding vertical
            )

            Spacer(Modifier.height(20.dp))

            // FRECUENCIA
            Text("Frecuencia", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(65.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CajaFrecuencia(Modifier.weight(1f), "Mensual", Icons.Default.CalendarMonth, esMensual) { esMensual = true }
                CajaFrecuencia(Modifier.weight(1f), "Anual", Icons.Default.CalendarToday, !esMensual) { esMensual = false }
            }

            Spacer(Modifier.height(20.dp))

            // 5. SECCIÓN DE CATEGORÍAS
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Ver todo", color = Color(0xFF00A680), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconoSeleccionableCategoria("Hogar", Icons.Default.Home, 1L, categoriaSeleccionadaId) { categoriaSeleccionadaId = it }
                IconoSeleccionableCategoria("Servicios", Icons.Default.ElectricBolt, 2L, categoriaSeleccionadaId) { categoriaSeleccionadaId = it }
                IconoSeleccionableCategoria("Transporte", Icons.Default.DirectionsCar, 3L, categoriaSeleccionadaId) { categoriaSeleccionadaId = it }
                IconoSeleccionableCategoria("Comida", Icons.Default.Restaurant, 4L, categoriaSeleccionadaId) { categoriaSeleccionadaId = it }
            }

            Spacer(Modifier.height(25.dp))

            // RECORDATORIO
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Row(
                    Modifier.padding(12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.NotificationsNone, null, tint = Color(0xFF00A680), modifier = Modifier.size(28.dp))
                    Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
                        Text("Recordatorio", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Avisar al 80% del límite", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = recordatorio,
                        onCheckedChange = { recordatorio = it },
                        modifier = Modifier.scale(0.8f),
                        colors = SwitchDefaults.colors(checkedTrackColor = verdeClaro)
                    )
                }
            }

            Spacer(Modifier.height(20.dp)) // Espacio final para que el scroll no choque con el botón
        }
    }
}

@Composable
fun CajaFrecuencia(
    modifier: Modifier,
    texto: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(65.dp) // <--- REDUCIDO DE 80.dp A 65.dp
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) verdeClaro else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionado) verdeClaro else Color.LightGray.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icono,
                contentDescription = null,
                tint = if (seleccionado) Color(0xFF0D5140) else Color.Black,
                modifier = Modifier.size(20.dp) // Icono un pelín más pequeño para ajustar
            )
            Text(
                text = texto,
                fontSize = 13.sp, // Fuente ligeramente más pequeña
                fontWeight = FontWeight.Bold,
                color = if (seleccionado) Color(0xFF0D5140) else Color.Black
            )
        }
    }
}