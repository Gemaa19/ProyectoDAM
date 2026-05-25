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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.componentes.IconoSeleccionableCategoria
import java.time.LocalDate
import java.util.Calendar
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview
import com.gema.zenitapp.componentes.CabeceraFormulario
import com.gema.zenitapp.componentes.SelectorDobleOpciones
import com.gema.zenitapp.componentes.recordarPermisoNotificaciones
import com.gema.zenitapp.ui.theme.ZenitAppTheme

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

    var fechaSeleccionadaVariable by remember { mutableStateOf(LocalDate.now().toString()) }
    var diaFijoSeleccionado by remember { mutableStateOf(1) }
    var expandirMenuDias by remember { mutableStateOf(false) }

    var categoriaSeleccionadaId by remember { mutableStateOf<Long?>(null) }

    val solicitarPermiso = recordarPermisoNotificaciones { esAceptado -> recordatorio = esAceptado }

    LaunchedEffect(Unit) {
        authViewModel.obtenerCategoriasBBDD(context)
    }

    LaunchedEffect(movimientoId) {
        if (movimientoId != null) {
            val movAEditar = authViewModel.listaMovimientos.find { it.id == movimientoId }
            if (movAEditar != null) {
                nombreGasto = movAEditar.descripcion ?: ""
                importe = movAEditar.monto.toString()
                esGasto = movAEditar.tipo == "GASTO"
                categoriaSeleccionadaId = movAEditar.categoriaId

                try {
                    val fechaParsed = LocalDate.parse(movAEditar.fecha)
                    fechaSeleccionadaVariable = movAEditar.fecha
                    diaFijoSeleccionado = fechaParsed.dayOfMonth
                } catch (e: Exception) {
                    Log.e("ZenitApp", "Error al mapear fecha de edición")
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
            fechaSeleccionadaVariable = "$anyo-$mesFormateado-$diaFormateado"
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
        CabeceraFormulario(
            titulo = if (movimientoId == null) "Nuevo movimiento" else "Editar movimiento",
            onBack = onBack
        )

        Column(modifier = Modifier.padding(20.dp)) {

            SelectorDobleOpciones("Gasto", "Ingreso", esGasto,
                { esGasto = it },
                icono1 = Icons.Default.ArrowDownward,
                icono2 = Icons.Default.ArrowUpward)

            SelectorDobleOpciones(
                opcion1 = "Fijo",
                opcion2 = "Variable",
                estaSeleccionadaOpcion1 = esFijo,
                onSeleccionCambiada = {
                    esFijo = it
                    if (!it) { recordatorio = false }
                }
            )

            Spacer(Modifier.height(15.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Importe", color = Color.Gray, fontSize = 14.sp)
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = importe,
                    onValueChange = { importe = it },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("0.00", fontSize = 24.sp,
                        color = Color.LightGray, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center) },
                    suffix = { Text("€", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0D5140)) },
                    textStyle = LocalTextStyle.current.copy(fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D5140), textAlign = TextAlign.Center),
                    modifier = Modifier.width(220.dp).background(Color(0xFFF9F9F9), RoundedCornerShape(15.dp)),
                    shape = RoundedCornerShape(15.dp),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeOscuro, unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f))
                )
            }

            Spacer(Modifier.height(15.dp))

            Text("Nombre del gasto o ingreso",
                fontWeight = FontWeight.Bold,
                color = verdeOscuro,
                modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                placeholder = { Text("ej. Alquiler, internet, Nómina", color = MaterialTheme.colorScheme.scrim) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.scrim,
                    unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                    focusedBorderColor = MaterialTheme.colorScheme.scrim,
                    unfocusedBorderColor = MaterialTheme.colorScheme.scrim
                )
            )

            Spacer(Modifier.height(15.dp))

            if (esFijo) {
                Text("Día del mes de cobro/ingreso",
                    fontWeight = FontWeight.Bold,
                    color = verdeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp))
                ExposedDropdownMenuBox(
                    expanded = expandirMenuDias,
                    onExpandedChange = { expandirMenuDias = !expandirMenuDias }
                ) {
                    OutlinedTextField(
                        value = "Cada día $diaFijoSeleccionado del mes",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandirMenuDias) },
                        leadingIcon = { Icon(Icons.Default.Timelapse, null, tint = MaterialTheme.colorScheme.scrim) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.scrim,
                            unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                            focusedBorderColor = MaterialTheme.colorScheme.scrim,
                            unfocusedBorderColor = MaterialTheme.colorScheme.scrim
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expandirMenuDias,
                        onDismissRequest = { expandirMenuDias = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        (1..31).forEach { dia ->
                            DropdownMenuItem(
                                text = { Text("Día $dia") },
                                onClick = {
                                    diaFijoSeleccionado = dia
                                    expandirMenuDias = false
                                }
                            )
                        }
                    }
                }
            } else {
                Text("Fecha del movimiento",
                    fontWeight = FontWeight.Bold,
                    color = verdeOscuro,
                    modifier = Modifier.padding(bottom = 8.dp))
                OutlinedTextField(
                    value = fechaSeleccionadaVariable,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth().clickable { datePickerDialog.show() },
                    shape = RoundedCornerShape(20.dp),
                    leadingIcon = {
                        IconButton(onClick = { datePickerDialog.show() }) {
                            Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.scrim)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.scrim,
                        unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                        focusedBorderColor = MaterialTheme.colorScheme.scrim,
                        unfocusedBorderColor = MaterialTheme.colorScheme.scrim
                    )
                )
            }

            Spacer(Modifier.height(24.dp))

            Text("Categorías", fontWeight = FontWeight.Bold, color = verdeOscuro)
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

            Spacer(Modifier.height(20.dp))

            if (esFijo) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp),
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsNone, null, tint = verdeOscuro, modifier = Modifier.size(28.dp))
                        Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
                            Text("Recordatorio de pago",
                                fontWeight = FontWeight.Bold,
                                color = verdeOscuro)
                            Text("La alerta saltará 2 días antes automáticamente", fontSize = 12.sp, color = Color.Gray)
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
            } else {
                Spacer(Modifier.height(15.dp))
            }

            Button(
                onClick = {
                    val importeLimpio = importe.replace(",", ".").trim()
                    val montoDouble = importeLimpio.toDoubleOrNull() ?: 0.0
                    val tipoMovimiento = if (esGasto) "GASTO" else "INGRESO"

                    val fechaAEnviar = if (esFijo) {
                        val anioActual = LocalDate.now().year
                        val mesActual = String.format("%02d", LocalDate.now().monthValue)
                        val diaFormateado = String.format("%02d", diaFijoSeleccionado)
                        "$anioActual-$mesActual-$diaFormateado"
                    } else {
                        fechaSeleccionadaVariable
                    }

                    if (nombreGasto.isBlank()) {
                        Toast.makeText(context, "Por favor, introduce una descripción", Toast.LENGTH_SHORT).show()
                    } else if (montoDouble <= 0.0) {
                        Toast.makeText(context, "Por favor, introduce un importe válido", Toast.LENGTH_SHORT).show()
                    } else if (categoriaSeleccionadaId == null) {
                        Toast.makeText(context, "Por favor, selecciona una categoría", Toast.LENGTH_SHORT).show()
                    } else {
                        if (movimientoId == null) {
                            authViewModel.guardarMovimientoenBBDD(
                                context = context,
                                monto = montoDouble,
                                descripcion = nombreGasto,
                                tipo = tipoMovimiento,
                                fechaElegida = fechaAEnviar,
                                categoriaId = categoriaSeleccionadaId!!,
                                onSuccess = {
                                    if (recordatorio) {
                                        try {
                                            authViewModel.registrarAlertaNotificacion(
                                                context = context,
                                                titulo = if (esGasto) "Aviso de Gasto Fijo" else "Aviso de Ingreso Fijo",
                                                mensaje = "En 2 días se pasará: $nombreGasto ($importeLimpio €)",
                                                fechaMovimiento = fechaAEnviar
                                            )
                                        } catch (e: Exception) {
                                            Log.e("ZenitApp", "Error al registrar notificación: ${e.message}")
                                        }
                                    }
                                    authViewModel.obtenerMovimientosBBDD(context)
                                    onBack()
                                }
                            )
                        } else {
                            authViewModel.editarMovimientoEnBBDD(
                                context = context,
                                id = movimientoId,
                                monto = montoDouble,
                                descripcion = nombreGasto,
                                tipo = tipoMovimiento,
                                fechaElegida = fechaAEnviar,
                                categoriaId = categoriaSeleccionadaId!!,
                                onSuccess = {
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
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (authViewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
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


@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Nuevo Movimiento - Modo Claro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Nuevo Movimiento - Modo Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NuevoMovimientoScreenP() {
    ZenitAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NuevoMovimientoScreen(
                movimientoId = null,
                onBack = {},
                authViewModel = viewModel()
            )
        }
    }
}