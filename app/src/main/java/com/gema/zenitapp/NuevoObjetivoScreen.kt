package com.gema.zenitapp

import android.app.DatePickerDialog
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.foundation.lazy.items
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
import java.time.LocalDate
import java.util.Calendar

import com.gema.zenitapp.componentes.CabeceraFormulario
import com.gema.zenitapp.componentes.SelectorDobleOpciones
import com.gema.zenitapp.componentes.recordarPermisoNotificaciones

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoObjetivoScreen(
    objetivoId: String? = null,
    tipoObjetivo: String? = null,
    onBack: (String) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var esPresupuesto by remember { mutableStateOf(true) }
    var importe by remember { mutableStateOf("") }
    var importeAhorrado by remember { mutableStateOf("") }
    var nombreObjetivo by remember { mutableStateOf("") }
    var esMensual by remember { mutableStateOf(true) }

    var fechaMetaSeleccionada by remember { mutableStateOf(LocalDate.now().plusDays(1).toString()) }
    var categoriaSeleccionadaId by remember { mutableStateOf<Long?>(null) }
    var recordatorio by remember { mutableStateOf(false) }

    val solicitarPermiso = recordarPermisoNotificaciones { esAceptado -> recordatorio = esAceptado }

    LaunchedEffect(objetivoId, tipoObjetivo) {
        authViewModel.obtenerCategoriasBBDD(context)
        authViewModel.obtenerObjetivosBBDD(context)

        if (!objetivoId.isNullOrBlank() && tipoObjetivo != null) {
            val idFiltro = objetivoId.toLongOrNull()

            when (tipoObjetivo) {
                "PRESUPUESTO" -> {
                    esPresupuesto = true
                    authViewModel.listaPresupuestos.find { it.id == idFiltro }?.let { pres ->
                        importe = pres.montoLimite.toString()
                        categoriaSeleccionadaId = pres.categoriaId
                        esMensual = pres.mes != 13
                    }
                }
                "META" -> {
                    esPresupuesto = false
                    authViewModel.listaMetas.find { it.id == idFiltro }?.let { meta ->
                        nombreObjetivo = meta.nombre ?: ""
                        importe = meta.objetivo.toString()
                        importeAhorrado = meta.ahorrado.toString()
                        esMensual = !meta.fechaLimite.isNullOrBlank() && !meta.fechaLimite.contains("-12-31")
                        fechaMetaSeleccionada = meta.fechaLimite ?: LocalDate.now().plusDays(1).toString()
                    }
                }
            }
        } else {
            esPresupuesto = (tipoObjetivo != "META")
        }
    }

    val calendarioLogico = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, anyo, mes, dia ->
            val mesFormateado = String.format("%02d", mes + 1)
            val diaFormateado = String.format("%02d", dia)
            fechaMetaSeleccionada = "$anyo-$mesFormateado-$diaFormateado"
        },
        calendarioLogico.get(Calendar.YEAR),
        calendarioLogico.get(Calendar.MONTH),
        calendarioLogico.get(Calendar.DAY_OF_MONTH)
    ).apply {
        datePicker.minDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        CabeceraFormulario(
            titulo = if (objetivoId == null) "Establecer objetivo" else "Editar objetivo",
            onBack = { onBack(if (esPresupuesto) "Presupuestos" else "Metas") }
        )

        Column(modifier = Modifier.padding(20.dp)) {

            SelectorDobleOpciones(
                opcion1 = "Presupuesto",
                opcion2 = "Meta",
                estaSeleccionadaOpcion1 = esPresupuesto,
                onSeleccionCambiada = { esPresupuesto = it }
            )

            Spacer(Modifier.height(15.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = if (esPresupuesto) "Monto límite" else "Dinero total que quieres conseguir",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = importe,
                    onValueChange = { importe = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0.00", fontSize = 24.sp, color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    suffix = { Text("€", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140)) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D5140), textAlign = TextAlign.Center),
                    modifier = Modifier.width(220.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(15.dp)),
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeOscuro, unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f))
                )

                if (!esPresupuesto) {
                    Spacer(Modifier.height(16.dp))
                    Text("Dinero que llevas ahorrado ya", color = Color.Gray, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = importeAhorrado,
                        onValueChange = { importeAhorrado = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("0.00", fontSize = 20.sp, color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        suffix = { Text("€", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140)) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = verdeOscuro, textAlign = TextAlign.Center),
                        modifier = Modifier.width(190.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(15.dp)),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeOscuro, unfocusedBorderColor = Color.LightGray.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text("Nombre del objetivo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nombreObjetivo,
                onValueChange = { nombreObjetivo = it },
                placeholder = { Text("ej. Coche nuevo, Viaje fin de grado, Mac",
                    fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.scrim,
                    unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                    unfocusedBorderColor = MaterialTheme.colorScheme.scrim,
                    focusedBorderColor = MaterialTheme.colorScheme.scrim
                ),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            Text("Frecuencia o Plazo del objetivo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(65.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CajaFrecuencia(Modifier.weight(1f), "Mensual", Icons.Default.CalendarMonth, esMensual) { esMensual = true }
                CajaFrecuencia(Modifier.weight(1f), "Anual", Icons.Default.CalendarToday, !esMensual) { esMensual = false }
            }
            Spacer(Modifier.height(20.dp))

            if (!esPresupuesto) {
                Text("Fecha límite para cumplir la meta", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = fechaMetaSeleccionada,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, null, tint = verdeOscuro)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.scrim,
                        unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                        unfocusedBorderColor = MaterialTheme.colorScheme.scrim,
                        focusedBorderColor = MaterialTheme.colorScheme.scrim
                    )
                )
                Spacer(Modifier.height(20.dp))
            }

            if (esPresupuesto) {
                Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(12.dp))

                LazyRow(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(authViewModel.listaCategorias) { cat ->
                        val vectorIcono = when (cat.id) {
                            1L -> Icons.Default.Home
                            2L -> Icons.Default.ElectricBolt
                            3L -> Icons.Default.DirectionsCar
                            4L -> Icons.Default.Restaurant
                            else -> Icons.Default.CreditCard
                        }
                        IconoSeleccionableCategoria(
                            nombre = cat.nombre,
                            icono = vectorIcono,
                            id = cat.id,
                            idSeleccionado = categoriaSeleccionadaId,
                            onSelect = { idClasificada -> categoriaSeleccionadaId = idClasificada }
                        )
                    }
                }
                Spacer(Modifier.height(25.dp))
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Row(Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsNone, null, tint = verdeOscuro, modifier = Modifier.size(28.dp))
                    Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
                        Text("Recordatorio inteligente", fontWeight = FontWeight.Bold,
                            fontSize = 14.sp, color = verdeOscuro)
                        Text(
                            text = if (esPresupuesto) "Avisar al 80% del límite mensual" else "Avisar automáticamente 2 días antes del fin o al alcanzar el 80%",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = recordatorio,
                        onCheckedChange = { activo ->
                            if (activo) solicitarPermiso() else recordatorio = false
                        }
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            Button(
                onClick = {
                    val importeLimpio = importe.replace(",", ".").trim()
                    val montoDouble = importeLimpio.toDoubleOrNull() ?: 0.0

                    if (montoDouble <= 0.0) {
                        Toast.makeText(context, "Por favor, introduce un importe numérico válido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val idNumerico = objetivoId?.toLongOrNull()
                    val hoy = LocalDate.now()

                    val mesAGuardar = if (esMensual) hoy.monthValue else 13
                    val anioAGuardar = hoy.year

                    val fechaFinalMeta = if (!esPresupuesto) {
                        if (esMensual) fechaMetaSeleccionada else "${hoy.year}-12-31"
                    } else {
                        fechaMetaSeleccionada
                    }

                    if (esPresupuesto) {
                        val catId = categoriaSeleccionadaId ?: 1L
                        if (idNumerico == null) {
                            authViewModel.guardarPresupuestoEnBBDD(context, montoDouble, catId, mesAGuardar, anioAGuardar) {
                                authViewModel.obtenerObjetivosBBDD(context)
                                onBack("Presupuestos")
                            }
                        } else {
                            authViewModel.editarPresupuestoEnBBDD(context, idNumerico, montoDouble, catId, mesAGuardar, anioAGuardar) {
                                authViewModel.obtenerObjetivosBBDD(context)
                                onBack("Presupuestos")
                            }
                        }
                    } else {
                        val nombreLimpio = nombreObjetivo.trim()
                        val ahorradoLimpio = importeAhorrado.replace(",", ".").trim()
                        val ahorradoDouble = if (ahorradoLimpio.isBlank()) 0.0 else (ahorradoLimpio.toDoubleOrNull() ?: 0.0)

                        if (nombreLimpio.isBlank()) {
                            Toast.makeText(context, "El nombre del objetivo no puede estar vacío", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        if (idNumerico == null) {
                            authViewModel.guardarMetaEnBBDD(context, nombreLimpio, montoDouble, ahorradoDouble, fechaFinalMeta) {
                                if (recordatorio) {
                                    try {
                                        authViewModel.registrarAlertaNotificacion(
                                            context = context,
                                            titulo = "Meta próxima a vencer",
                                            mensaje = "¡Atención! Queda poco para cumplir tu meta: $nombreLimpio",
                                            fechaMovimiento = fechaFinalMeta
                                        )
                                    } catch (e: Exception) {
                                        Log.e("ZenitApp", "Error de alerta local")
                                    }
                                }
                                authViewModel.obtenerObjetivosBBDD(context)
                                onBack("Metas")
                            }
                        } else {
                            authViewModel.editarMetaEnBBDD(context, idNumerico, nombreLimpio, montoDouble, ahorradoDouble, fechaFinalMeta) {
                                authViewModel.obtenerObjetivosBBDD(context)
                                onBack("Metas")
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text(if (objetivoId == null) "Guardar" else "Actualizar", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
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
            .height(65.dp)
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
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = texto,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (seleccionado) Color(0xFF0D5140) else Color.Black
            )
        }
    }
}