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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeClaro
import com.gema.zenitapp.componentes.IconoSeleccionableCategoria
import java.time.LocalDate
import java.util.Calendar

// 💡 LAS DOS LÍNEAS CORRECTAS PARA QUE EL LAZYROW COMPILE:
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items // ⬅️ ESTA ES LA QUE TE FALTA

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NuevoMovimientoScreen(
    movimientoId: Long? = null,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    var esGasto by remember { mutableStateOf(true) }
    var esFijo by remember { mutableStateOf(true) }
    var importe by remember { mutableStateOf("") }
    var nombreGasto by remember { mutableStateOf("") }
    var recordatorio by remember { mutableStateOf(false) }
    var fechaSeleccionada by remember { mutableStateOf(LocalDate.now().toString()) }
    var categoriaSeleccionadaId by remember { mutableStateOf<Long?>(null) }

    // 💡 GESTOR DE PERMISOS NATIVO PARA COMPOSE
    val launcherPermiso = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission()
    ) { esAceptado ->
        if (!esAceptado) {
            // Si el usuario lo rechaza, apagamos el switch para que la interfaz sea coherente
            recordatorio = false
            Toast.makeText(context, "Necesitas activar las notificaciones en los ajustes del móvil", Toast.LENGTH_LONG).show()
        }
    }

    // 💡 SOLUCIÓN: Sincronizamos las categorías de AWS al abrir la pantalla por si entra directo
    LaunchedEffect(Unit) {
        authViewModel.obtenerCategoriasBBDD(context)
    }

    // 💡 CORRECCIÓN: Buscamos en listaMovimientos (el histórico completo de AWS) para la edición
    LaunchedEffect(movimientoId) {
        if (movimientoId != null) {
            val movAEditar = authViewModel.listaMovimientos.find { it.id == movimientoId }
            if (movAEditar != null) {
                nombreGasto = movAEditar.descripcion ?: ""
                importe = movAEditar.monto.toString()
                esGasto = movAEditar.tipo == "GASTO"
                fechaSeleccionada = movAEditar.fecha
                categoriaSeleccionadaId = movAEditar.categoriaId
            }
        }
    }

    // ... El resto del archivo se queda exactamente igual a tu bloque ...

    // Configuración del DatePickerDialog Nativo
    val calendarioLogico = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, anyo, mes, dia ->
            val mesFormateado = String.format("%02d", mes + 1)
            val diaFormateado = String.format("%02d", dia)
            fechaSeleccionada = "$anyo-$mesFormateado-$diaFormateado"
        },
        calendarioLogico.get(Calendar.YEAR),
        calendarioLogico.get(Calendar.MONTH),
        calendarioLogico.get(Calendar.DAY_OF_MONTH)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // 💡 CABECERA DINÁMICA: Cambia el título según si el ID existe o no
        CabeceraSimple(
            titulo = if (movimientoId == null) "Nuevo movimiento" else "Editar movimiento",
            onBack = onBack
        )

        Column(modifier = Modifier.padding(20.dp)) {

            // SELECTOR GASTO / INGRESO
            SelectorDoble(
                opcion1 = "Gasto",
                opcion2 = "Ingreso",
                seleccionado1 = esGasto,
                onSeleccion = { esGasto = it },
                icono1 = Icons.Default.ArrowDownward,
                icono2 = Icons.Default.ArrowUpward
            )

            Spacer(Modifier.height(16.dp))

            // SELECTOR FIJO / VARIABLE
            SelectorDoble(
                opcion1 = "Fijo",
                opcion2 = "Variable",
                seleccionado1 = esFijo,
                onSeleccion = { esFijo = it }
            )

            Spacer(Modifier.height(15.dp))

            // IMPORTE EDITABLE REFORZADO
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
                        .background(Color(0xFFF9F9F9), RoundedCornerShape(15.dp)),
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = verdeOscuro,
                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f),
                        focusedContainerColor = Color(0xFFF9F9F9),
                        unfocusedContainerColor = Color(0xFFF9F9F9)
                    )
                )
            }

            Spacer(Modifier.height(15.dp))

            // NOMBRE DEL GASTO
            Text("Nombre del gasto o ingreso", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                placeholder = { Text("ej. Alquiler, internet, Nómina") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
            )

            Spacer(Modifier.height(15.dp))

            // SECCIÓN SELECCIÓN DE FECHA
            Text("Fecha del movimiento", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = fechaSeleccionada,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(20.dp),
                leadingIcon = {
                    IconButton(onClick = { datePickerDialog.show() }) {
                        Icon(Icons.Default.CalendarToday, null, tint = verdeOscuro)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = verdeOscuro
                )
            )

            Spacer(Modifier.height(24.dp))

            // CATEGORÍAS SELECCIONABLES
            // CATEGORÍAS SELECCIONABLES (Sintonizadas dinámicamente)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Categorías", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(12.dp))

            // 💡 SOLUCIÓN: Despliegue infinito horizontal basado en tu AWS
            // 💡 SOLUCIÓN: Ajustamos los parámetros exactos de tu componente base
            // 💡 SOLUCIÓN: Despliegue dinámico adaptado a tu componente exacto
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

                    // 💡 CORRECCIÓN DE PARÁMETROS NOMBRADOS:
                    IconoSeleccionableCategoria(
                        nombre = cat.nombre,
                        icono = vectorIcono,
                        id = cat.id,                 // ⬅️ Cambiado 'categoriaId' por 'id'
                        idSeleccionado = categoriaSeleccionadaId, // ⬅️ Cambiado 'seleccionadaId' por 'idSeleccionado'
                        onSelect = { idClasificada -> // ⬅️ Cambiado 'onSeleccion' por 'onSelect'
                            categoriaSeleccionadaId = idClasificada
                        }
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            // RECORDATORIO (Modificado)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsNone, null, tint = verdeOscuro, modifier = Modifier.size(28.dp))
                    Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
                        Text("Recordatorio", fontWeight = FontWeight.Bold)
                        Text("Avisar 2 días antes", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = recordatorio,
                        onCheckedChange = { activo ->
                            if (activo && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                // Lanzamos la ventana emergente oficial de Android
                                launcherPermiso.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                            recordatorio = activo
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = verdeClaro)
                    )
                }
            }

            Spacer(Modifier.height(30.dp))

            // BOTÓN GUARDAR DINÁMICO
            Button(
                onClick = {
                    val importeLimpio = importe.replace(",", ".").trim()
                    val montoDouble = importeLimpio.toDoubleOrNull() ?: 0.0
                    val tipoMovimiento = if (esGasto) "GASTO" else "INGRESO"

                    if (nombreGasto.isBlank()) {
                        Toast.makeText(context, "Por favor, introduce una descripción", Toast.LENGTH_SHORT).show()
                    } else if (montoDouble <= 0.0) {
                        Toast.makeText(context, "Por favor, introduce un importe válido", Toast.LENGTH_SHORT).show()
                    } else if (categoriaSeleccionadaId == null) {
                        Toast.makeText(context, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show()
                    } else {
                        if (movimientoId == null) {
                            // Modo CREAR
                            authViewModel.guardarMovimientoenBBDD(
                                context = context,
                                monto = montoDouble,
                                descripcion = nombreGasto,
                                tipo = tipoMovimiento,
                                fechaElegida = fechaSeleccionada,
                                categoriaId = categoriaSeleccionadaId!!,
                                onSuccess = {
                                    // 💡 SI EL RECORDATORIO ESTÁ SELECCIONADO, PROGRAMAMOS LA ALERTA
                                    if (recordatorio) {
                                        authViewModel.registrarAlertaNotificacion(
                                            context = context,
                                            titulo = "Aviso de Gasto Fijo",
                                            mensaje = "En 2 días se pasará el cargo de: $nombreGasto ($importeLimpio €)",
                                            fechaMovimiento = fechaSeleccionada
                                        )
                                    }

                                    authViewModel.obtenerMovimientosBBDD(context)
                                    onBack()
                                }
                            )
                        } else {
                            // Modo EDITAR
                            authViewModel.editarMovimientoEnBBDD(
                                context = context,
                                id = movimientoId,
                                monto = montoDouble,
                                descripcion = nombreGasto,
                                tipo = tipoMovimiento,
                                fechaElegida = fechaSeleccionada,
                                categoriaId = categoriaSeleccionadaId!!,
                                onSuccess = {
                                    // También puedes replicar la lógica aquí si se edita un recordatorio
                                    authViewModel.obtenerMovimientosBBDD(context)
                                    onBack()
                                }
                            )
                        }
                    }
                },
                enabled = !authViewModel.isLoading,
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = verdeOscuro)
            ) {
                if (authViewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    // Texto del botón adaptivo
                    Text(
                        text = if (movimientoId == null) "Guardar movimiento" else "Actualizar movimiento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// MANTENEMOS COMPONENTES DE DISEÑO BASE FIJOS
@Composable
fun CabeceraSimple(titulo: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(verdeClaro).padding(16.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color(0xFF0D5140))
        }
        Text(titulo, fontWeight = FontWeight.ExtraBold, fontSize = 22.sp, color = Color(0xFF0D5140), modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun SelectorDoble(opcion1: String, opcion2: String, seleccionado1: Boolean, onSeleccion: (Boolean) -> Unit, icono1: ImageVector? = null, icono2: ImageVector? = null) {
    Card(shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Row(Modifier.fillMaxWidth().padding(4.dp)) {
            Box(Modifier.weight(1f).height(45.dp).background(if (seleccionado1) verdeClaro else Color.Transparent, RoundedCornerShape(12.dp)).clickable { onSeleccion(true) }, contentAlignment = Alignment.Center) {
                Row {
                    if (icono1 != null) Icon(icono1, null, tint = if (seleccionado1) Color(0xFF0D5140) else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(opcion1, fontWeight = FontWeight.Bold, color = if (seleccionado1) Color(0xFF0D5140) else Color.Gray)
                }
            }
            Box(Modifier.weight(1f).height(45.dp).background(if (!seleccionado1) verdeClaro else Color.Transparent, RoundedCornerShape(12.dp)).clickable { onSeleccion(false) }, contentAlignment = Alignment.Center) {
                Row {
                    if (icono2 != null) Icon(icono2, null, tint = if (!seleccionado1) Color(0xFF0D5140) else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(opcion2, fontWeight = FontWeight.Bold, color = if (!seleccionado1) Color(0xFF0D5140) else Color.Gray)
                }
            }
        }
    }
}