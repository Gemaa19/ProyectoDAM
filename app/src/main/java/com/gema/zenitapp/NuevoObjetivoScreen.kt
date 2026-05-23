package com.gema.zenitapp

import android.app.DatePickerDialog
import android.os.Build
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
import java.time.LocalDate
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NuevoObjetivoScreen(
    objetivoId: Long? = null,
    tipoObjetivo: String? = null,
    onBack: () -> Unit,
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

// 💡 GESTOR DE PERMISOS NATIVO (Copia y pega esto)
    val launcherPermiso = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { esAceptado ->
        if (!esAceptado) {
            recordatorio = false
            Toast.makeText(context, "Necesitas activar las notificaciones en los ajustes del móvil", Toast.LENGTH_LONG).show()
        }
    }
    // 💡 SOLUCIÓN: Descarga las categorías actualizadas de la RDS al instanciarse la vista
    LaunchedEffect(Unit) {
        authViewModel.obtenerCategoriasBBDD(context)
    }

    LaunchedEffect(objetivoId, tipoObjetivo) {
        // ... Tu mapeo existente de presupuestos y metas se queda igual ...
    }

    // ... El resto del Scaffold se queda igual ...
    LaunchedEffect(objetivoId, tipoObjetivo) {
        if (objetivoId != null && tipoObjetivo != null) {
            if (tipoObjetivo == "PRESUPUESTO") {
                esPresupuesto = true
                authViewModel.listaPresupuestos.find { it.id == objetivoId }?.let { pres ->
                    importe = pres.montoLimite.toString()
                    categoriaSeleccionadaId = pres.categoriaId
                }
            } else if (tipoObjetivo == "META") {
                esPresupuesto = false
                authViewModel.listaMetas.find { it.id == objetivoId }?.let { meta ->
                    nombreObjetivo = meta.nombre ?: ""
                    importe = meta.objetivo.toString()
                    importeAhorrado = meta.ahorrado.toString()
                    fechaMetaSeleccionada = meta.fechaLimite ?: LocalDate.now().plusDays(1).toString()
                }
            }
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
    )

    Scaffold(
        topBar = { CabeceraSimple("Establecer objetivo", onBack) },
        bottomBar = {
            Button(
                onClick = {
                    val importeLimpio = importe.replace(",", ".").trim()
                    val montoDouble = importeLimpio.toDoubleOrNull() ?: 0.0

                    if (montoDouble <= 0.0) {
                        Toast.makeText(context, "Introduce un importe válido", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (esPresupuesto) {
                        val catId = categoriaSeleccionadaId ?: 1L
                        val hoy = LocalDate.now()

                        authViewModel.guardarPresupuestoEnBBDD(
                            context = context,
                            montoLimite = montoDouble,
                            categoriaId = catId,
                            mes = hoy.monthValue,
                            anio = hoy.year,
                            onSuccess = {
                                // 💡 CASO A: PROGRAMAR RECORDATORIO DE PRESUPUESTO
                                if (recordatorio) {
                                    val nombreCat = authViewModel.listaCategorias.find { it.id == catId }?.nombre ?: "Categoría"
                                    authViewModel.registrarAlertaNotificacion(
                                        context = context,
                                        titulo = "Control de Presupuesto: $nombreCat",
                                        mensaje = "Has establecido un límite de $importeLimpio €. Te avisaremos si te acercas al 80%.",
                                        // Usamos el último día del mes actual para fijar la alarma en el AlarmManager
                                        fechaMovimiento = hoy.withDayOfMonth(hoy.lengthOfMonth()).toString()
                                    )
                                }

                                authViewModel.obtenerObjetivosBBDD(context)
                                onBack()
                            }
                        )
                    } else {
                        if (nombreObjetivo.isBlank()) {
                            Toast.makeText(context, "Por favor, dale un nombre a la meta", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val ahorradoLimpio = importeAhorrado.replace(",", ".").trim()
                        val ahorradoDouble = ahorradoLimpio.toDoubleOrNull() ?: 0.0

                        if (ahorradoDouble < 0.0 || ahorradoDouble > montoDouble) {
                            Toast.makeText(context, "Verifica el importe ahorrado inicial", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        try {
                            val fechaElegidaParseada = LocalDate.parse(fechaMetaSeleccionada)
                            if (!fechaElegidaParseada.isAfter(LocalDate.now())) {
                                Toast.makeText(context, "La fecha límite debe ser un día en el futuro", Toast.LENGTH_LONG).show()
                                return@Button
                            }
                        } catch (e: Exception) {
                            Toast.makeText(context, "Formato de fecha incorrecto", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        authViewModel.guardarMetaEnBBDD(
                            context = context,
                            name = nombreObjetivo,
                            objetivo = montoDouble,
                            ahorrado = ahorradoDouble,
                            fechaLimite = fechaMetaSeleccionada,
                            onSuccess = {
                                // 💡 CASO B: PROGRAMAR RECORDATORIO DE META DE AHORRO
                                if (recordatorio) {
                                    authViewModel.registrarAlertaNotificacion(
                                        context = context,
                                        titulo = "Meta de Ahorro: $nombreObjetivo",
                                        mensaje = "Tu meta de $importeLimpio € vence pronto. ¡No olvides ingresar tus aportaciones!",
                                        // Usamos la fecha límite elegida por el usuario en el DatePicker
                                        fechaMovimiento = fechaMetaSeleccionada
                                    )
                                }

                                authViewModel.obtenerObjetivosBBDD(context)
                                onBack()
                            }
                        )
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

            SelectorDoble(
                opcion1 = "Presupuesto",
                opcion2 = "Meta",
                seleccionado1 = esPresupuesto,
                onSeleccion = { esPresupuesto = it }
            )

            // CONTENEDOR DE IMPORTES DINÁMICOS
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Bloque 1: Objetivo total (Común a presupuestos y metas)
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

                // 💡 Bloque 2: Dinero llevado hasta ahora (Solo visible si se marca "Meta")
                if (!esPresupuesto) {
                    Spacer(Modifier.height(16.dp))
                    Text("Dinero que llevas ahorrado ya", color = Color.Gray, fontSize = 14.sp)
                    Spacer(Modifier.height(4.dp))
                    OutlinedTextField(
                        value = importeAhorrado,
                        onValueChange = { importeAhorrado = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        placeholder = { Text("0.00", fontSize = 20.sp, color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                        suffix = { Text("€", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00A680)) },
                        textStyle = LocalTextStyle.current.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00A680), textAlign = TextAlign.Center),
                        modifier = Modifier.width(190.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(15.dp)),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF00A680), unfocusedBorderColor = Color.LightGray.copy(alpha = 0.4f))
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // NOMBRE DEL OBJETIVO
            Text("Nombre del objetivo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nombreObjetivo,
                onValueChange = { nombreObjetivo = it },
                placeholder = { Text("ej. Coche nuevo, Viaje fin de grado, Mac", fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.Gray, modifier = Modifier.size(20.dp)) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray, focusedBorderColor = verdeOscuro),
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            // SECCIÓN CONDICIONAL FRECUENCIA / FECHA LÍMITE
            if (esPresupuesto) {
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
            } else {
                Text("Fecha límite de la meta", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(8.dp))
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
                    colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray, focusedBorderColor = verdeOscuro)
                )
                Spacer(Modifier.height(20.dp))
            }

            // SECCIÓN DE CATEGORÍAS
            // SECCIÓN DE CATEGORÍAS (Mapeo reactivo infinito)
            if (esPresupuesto) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Spacer(Modifier.height(12.dp))

                // 💡 SOLUCIÓN: Evita el corte en 4 categorías
                // 💡 AJUSTE COMPLEMENTARIO: Carrusel dinámico corregido para el formulario
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
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
                            onSelect = { idClasificada ->
                                categoriaSeleccionadaId = idClasificada
                            }
                        )
                    }
                }
                Spacer(Modifier.height(25.dp))
            }

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
                        Text(
                            text = if (esPresupuesto) "Avisar al 80% del límite" else "Avisar una semana antes del cierre",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                    Switch(
                        checked = recordatorio,
                        onCheckedChange = { activo ->
                            if (activo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                // Lanza la ventana de Android para permitir notificaciones
                                launcherPermiso.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                            recordatorio = activo
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = verdeClaro)
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
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