package com.gema.zenitapp

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import com.gema.zenitapp.ui.theme.verdeClaro
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.verdeGrisaceo
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeTitulos

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamburguesaScreen(
    authViewModel: AuthViewModel,
    esModoOscuroActivo: Boolean,
    onModoOscuroCambiado: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (authViewModel.usuarioLogueado == null) {
            authViewModel.cargarSesionLocal(context)
        }
        authViewModel.obtenerCategoriasBBDD(context)

        authViewModel.obtenerMovimientosBBDD(context)
    }


    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarDialogoCategorias by remember { mutableStateOf(false) }
    var mostrarDialogoNotificaciones by remember { mutableStateOf(false) }
    var mostrarDialogoAyuda by remember { mutableStateOf(false) }
    var mostrarDialogoAjustes by remember { mutableStateOf(false) }

    var mostrarDialogoEditarCategoria by remember { mutableStateOf(false) }
    var categoriaAEditarId by remember { mutableStateOf<Long?>(null) }
    var nombreCategoriaAEditar by remember { mutableStateOf("") }

    var debaHacerScrollAlFinal by remember { mutableStateOf(false) }

    var nuevaCategoriaNombre by remember { mutableStateOf("") }

    val nombreUsuario = authViewModel.usuarioLogueado?.nombre ?: "Usuario Zenit"
    val correoUsuario = authViewModel.usuarioLogueado?.email ?: "usuario@zenit.com"

    val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)

    val iniciales = remember(nombreUsuario) {
        val partes = nombreUsuario.trim().split("\\s+".toRegex())
        if (partes.isEmpty() || partes[0].isEmpty()) "Z"
        else if (partes.size >= 2) "${partes[0].first().uppercase()}${partes[1].first().uppercase()}"
        else "${partes[0].first().uppercase()}${partes[0].first().uppercase()}"
    }

    val menuOptions = listOf(
        MenuOption(icon = Icons.Default.GridView, text = "Categorías"),
        MenuOption(icon = Icons.Default.Notifications, text = "Notificaciones"),
        MenuOption(icon = Icons.Default.Settings, text = "Ajustes"),
        MenuOption(icon = Icons.Default.Help, text = "Ayuda"),
        MenuOption(icon = Icons.Default.ExitToApp, text = "Cerrar sesión")
    )

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Volver", tint = verdeIconos) }
                    IconButton(onClick = { mostrarDialogoEditar = true }) { Icon(Icons.Default.Edit, "Editar perfil", tint = verdeIconos) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(verdeGrisaceo, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iniciales, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, letterSpacing = 2.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = nombreUsuario, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Text(text = correoUsuario, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        LazyColumn(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(menuOptions) { option ->
                MenuOptionRow(option = option, onClick = {
                    when (option.text) {
                        "Categorías" -> {
                            debaHacerScrollAlFinal = false
                            mostrarDialogoCategorias = true
                        }
                        "Notificaciones" -> mostrarDialogoNotificaciones = true
                        "Ajustes" -> mostrarDialogoAjustes = true
                        "Ayuda" -> mostrarDialogoAyuda = true
                        "Cerrar sesión" -> {
                            val p = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                            p.edit().remove("token_jwt").apply()
                            authViewModel.logout()
                            onLogoutSuccess()
                        }
                    }
                })
            }
        }
    }

    if (mostrarDialogoCategorias) {
        val scrollCategoriasState = rememberScrollState()

        LaunchedEffect(authViewModel.listaCategorias.size) {
            if (debaHacerScrollAlFinal && authViewModel.listaCategorias.isNotEmpty()) {
                scrollCategoriasState.animateScrollTo(scrollCategoriasState.maxValue)
                debaHacerScrollAlFinal = false
            }
        }

        AlertDialog(
            containerColor = verdeClaro,
            onDismissRequest = { mostrarDialogoCategorias = false },
            title = { Text("Categorías", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoCategorias = false }) { Text("Cerrar", color = verdeIconos) }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Lista de tus categorías", color = Color.Gray, fontSize = 18.sp, fontWeight = FontWeight.Bold)

                    Spacer(Modifier.height(4.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .verticalScroll(scrollCategoriasState),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        authViewModel.listaCategorias.forEach { cat ->
                            val vectorIcono = when (cat.icono) {
                                "hogar" -> Icons.Default.Home
                                "servicios" -> Icons.Default.ElectricBolt
                                "transporte" -> Icons.Default.DirectionsCar
                                "comida" -> Icons.Default.Restaurant
                                "gym" -> Icons.Default.FitnessCenter
                                "salud" -> Icons.Default.LocalHospital
                                "ocio" -> Icons.Default.ConfirmationNumber
                                else -> Icons.Default.Stars
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color.White, RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = vectorIcono,
                                    contentDescription = null,
                                    tint = verdeOscuro,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Text(
                                    text = cat.nombre,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.DarkGray,
                                    fontSize = 15.sp
                                )

                                Spacer(Modifier.weight(1f))

                                if (cat.usuarioId != null) {
                                    IconButton(
                                        onClick = {
                                            categoriaAEditarId = cat.id
                                            nombreCategoriaAEditar = cat.nombre
                                            mostrarDialogoEditarCategoria = true
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Editar nombre de categoría",
                                            tint = Color.DarkGray,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            authViewModel.eliminarCategoriaBBDD(context, cat.id) {
                                                authViewModel.obtenerCategoriasBBDD(context)
                                            }
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Eliminar categoría",
                                            tint = MaterialTheme.colorScheme.tertiary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nuevaCategoriaNombre,
                            onValueChange = { nuevaCategoriaNombre = it },
                            placeholder = { Text("Nueva categoría...", fontSize = 13.sp, color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.scrim,
                                unfocusedTextColor = MaterialTheme.colorScheme.scrim,
                                focusedBorderColor = MaterialTheme.colorScheme.scrim,
                                unfocusedBorderColor = MaterialTheme.colorScheme.scrim
                            )
                        )
                        Button(
                            onClick = {
                                if (nuevaCategoriaNombre.isNotBlank()) {
                                    debaHacerScrollAlFinal = true
                                    authViewModel.crearCategoriaEnBBDD(context, nuevaCategoriaNombre.trim(), "default_card") {
                                        nuevaCategoriaNombre = ""
                                        authViewModel.obtenerCategoriasBBDD(context)
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = verdeIconos),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.Save, null, tint = Color.White)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoEditarCategoria && categoriaAEditarId != null) {
        AlertDialog(
            containerColor = verdeClaro,
            onDismissRequest = { mostrarDialogoEditarCategoria = false },
            title = { Text("Editar nombre", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (nombreCategoriaAEditar.isNotBlank()) {
                            val catId = categoriaAEditarId!!
                            authViewModel.editarCategoriaEnBBDD(context, catId, nombreCategoriaAEditar.trim(), "default_card") {
                                Toast.makeText(context, "Nombre modificado con éxito", Toast.LENGTH_SHORT).show()
                                authViewModel.obtenerCategoriasBBDD(context)
                                mostrarDialogoEditarCategoria = false
                            }
                        }
                    }
                ) {
                    Text("Actualizar", color = verdeIconos, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditarCategoria = false }) { Text("Cancelar", color = Color.Gray) }
            },
            text = {
                OutlinedTextField(
                    value = nombreCategoriaAEditar,
                    onValueChange = { nombreCategoriaAEditar = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeIconos, focusedLabelColor = verdeIconos),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    if (mostrarDialogoNotificaciones) {
        val listaMetasFiltrada = authViewModel.listaMetas.map { meta ->
            AlertaComun(
                id = meta.id,
                nombre = meta.nombre ?: "Meta de Ahorro",
                montoTexto = "${meta.objetivo} €",
                fechaTexto = meta.fechaLimite ?: "",
                tipoGrupo = "META",
                esMeta = true
            )
        }

        val listaGastosFiltrada = authViewModel.listaMovimientos.filter { it.tipo == "GASTO" }.map { mov ->
            AlertaComun(
                id = mov.id,
                nombre = mov.descripcion ?: "Gasto Fijo",
                montoTexto = "${mov.monto} €",
                fechaTexto = mov.fecha ?: "",
                tipoGrupo = "GASTO",
                esMeta = false
            )
        }

        val listaIngresosFiltrada = authViewModel.listaMovimientos.filter { it.tipo == "INGRESO" }.map { mov ->
            AlertaComun(
                id = mov.id,
                nombre = mov.descripcion ?: "Ingreso Fijo",
                montoTexto = "${mov.monto} €",
                fechaTexto = mov.fecha ?: "",
                tipoGrupo = "INGRESO",
                esMeta = false
            )
        }

        AlertDialog(
            containerColor = verdeClaro,
            onDismissRequest = { mostrarDialogoNotificaciones = false },
            title = { Text("Notificaciones", fontWeight = FontWeight.Bold, color = verdeOscuro) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoNotificaciones = false }) {
                    Text("Entendido", color = verdeTitulos, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    if (listaMetasFiltrada.isEmpty() && listaGastosFiltrada.isEmpty() && listaIngresosFiltrada.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.NotificationsNone, null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("No tienes alertas configuradas.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().heightIn(max = 350.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            item {
                                Text("Metas", fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp, color = verdeTitulos)
                            }
                            if (listaMetasFiltrada.isEmpty()) {
                                item { Text("No hay metas vigentes", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)) }
                            } else {
                                items(listaMetasFiltrada) { alerta ->
                                    FilaAlertaNotificacion(alerta, colorMonto = MaterialTheme.colorScheme.primary, prefijo = "  ")
                                }
                            }

                            item {
                                Text("Gastos", fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp, color = verdeTitulos)
                            }
                            if (listaGastosFiltrada.isEmpty()) {
                                item { Text("No hay alertas de gastos", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)) }
                            } else {
                                items(listaGastosFiltrada) { alerta ->
                                    FilaAlertaNotificacion(alerta, colorMonto = Color(0xFFB2130F), prefijo = " — ")
                                }
                            }

                            item {
                                Text("Ingresos", fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp, color = verdeTitulos)
                            }
                            if (listaIngresosFiltrada.isEmpty()) {
                                item { Text("No hay alertas de ingresos", fontSize = 12.sp,
                                    color = Color.Gray, modifier = Modifier.padding(start = 8.dp, bottom = 4.dp)) }
                            } else {
                                items(listaIngresosFiltrada) { alerta ->
                                    FilaAlertaNotificacion(alerta, colorMonto = verdeIconos, prefijo = " + ")
                                }
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
    if (mostrarDialogoEditar) {
        var nombreTemporal by remember { mutableStateOf(nombreUsuario) }

        AlertDialog(
            containerColor = verdeClaro,
            onDismissRequest = { mostrarDialogoEditar = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(onClick = {
                    if (nombreTemporal.isNotBlank() && nombreTemporal != nombreUsuario) {
                        authViewModel.actualizarNombreUsuarioBBDD(context, nombreTemporal.trim()) {
                            Toast.makeText(context, "Nombre actualizado con éxito", Toast.LENGTH_SHORT).show()
                        }
                    }
                    mostrarDialogoEditar = false
                }) {
                    Text("Guardar", color = verdeIconos, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditar = false }) { Text("Cancelar", color = Color.Gray) }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "Modifica tu nombre de acceso público en la aplicación. El correo electrónico no se puede alterar por motivos de seguridad.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )

                    OutlinedTextField(
                        value = nombreTemporal,
                        onValueChange = { nombreTemporal = it },
                        label = { Text("Nombre de usuario") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeIconos, focusedLabelColor = verdeIconos, cursorColor = verdeIconos),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoAjustes) {
        AlertDialog(
            containerColor = verdeClaro,
            onDismissRequest = { mostrarDialogoAjustes = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAjustes = false }) { Text("Aceptar", color = verdeIconos) }
            },
            title = { Text("Ajustes", fontWeight = FontWeight.Bold, color = verdeIconos) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activar Modo Oscuro", fontSize = 16.sp, color = verdeOscuro)

                    Switch(
                        checked = esModoOscuroActivo,
                        onCheckedChange = { nuevoValor ->
                            onModoOscuroCambiado(nuevoValor)

                            val p = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                            p.edit().putBoolean("modo_oscuro_activo", nuevoValor).apply()
                        },
                        colors = SwitchDefaults.colors(checkedThumbColor = verdeIconos)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoAyuda) {
        AlertDialog(
            containerColor = verdeClaro,
            onDismissRequest = { mostrarDialogoAyuda = false },
            confirmButton = { TextButton(onClick = { mostrarDialogoAyuda = false }) { Text("Entendido", color = verdeIconos) } },
            title = { Text("Soporte Zenit", fontWeight = FontWeight.Bold, color = verdeOscuro) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("¿Qué es Zenit?", fontWeight = FontWeight.SemiBold, color = verdeTitulos)
                    Text("ZenitApp es tu gestor financiero inteligente diseñado para tomar el control absoluto de tus ahorros diarios. Te ayuda a planificar tus presupuestos mensuales y a registrar tus gastos en tiempo real de forma segura.", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("¿Qué es el Dinero Libre Real?", fontWeight = FontWeight.SemiBold, color = verdeTitulos)
                    Text("Representa el dinero líquido y real que te queda disponible para gastar libremente en el mes, tras restar de tus ingresos todos tus presupuestos fijos configurados y las metas de ahorro activas.", fontSize = 14.sp, color = Color.Gray)
                    Text("¿Qué puedes hacer en ZenitApp?", fontWeight = FontWeight.Bold, color = verdeTitulos)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    ) {
                        Text("• Registrar, editar y eliminar tus gastos e ingresos diarios.", fontSize = 14.sp, color = Color.Gray)
                        Text("• Fijar límites de gasto mensuales mediante Presupuestos por categoría.", fontSize = 14.sp, color = Color.Gray)
                        Text("• Crear Metas de Ahorro personalizadas con seguimiento de capital acumulado.", fontSize = 14.sp, color = Color.Gray)
                        Text("• Añadir tus propias Categorías personalizadas y eliminar las que ya no uses.", fontSize = 14.sp, color = Color.Gray)
                        Text("• Recibir alertas automáticas en el móvil 2 días antes de tus fechas límite.", fontSize = 14.sp, color = Color.Gray)
                        Text("• Mantener tu sesión segura y actualizar tu nombre de perfil cuando quieras.", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
@Composable
fun MenuOptionRow(option: MenuOption, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = verdeIconos) {
            Box(contentAlignment = Alignment.Center) {
                Icon(imageVector = option.icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(30.dp))
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(text = option.text, fontSize = 20.sp, color = verdeIconos, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun FilaAlertaNotificacion(alerta: AlertaComun, colorMonto: Color, prefijo: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Color.White,
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = prefijo,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            color = colorMonto,
            modifier = Modifier.padding(end = 4.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = alerta.nombre,
                fontWeight = FontWeight.Bold,
                color = verdeTitulos,
                fontSize = 14.sp
            )
            Text(
                text = alerta.fechaTexto,
                color = Color.Gray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        Text(
            text = alerta.montoTexto,
            fontWeight = FontWeight.ExtraBold,
            color = colorMonto,
            fontSize = 15.sp
        )
    }
}

data class MenuOption(val icon: ImageVector, val text: String)

data class AlertaComun(
    val id: Long,
    val nombre: String,
    val montoTexto: String,
    val fechaTexto: String,
    val tipoGrupo: String,
    val esMeta: Boolean
)

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Hamburguesa - Modo Claro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
fun HamburguesaClaroPreview() {
    val authViewModelMock = AuthViewModel()
    ZenitAppTheme(darkTheme = false) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HamburguesaScreen(
                authViewModel = authViewModelMock,
                esModoOscuroActivo = false,
                onModoOscuroCambiado = {},
                onBackClick = {},
                onLogoutSuccess = {}
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(
    name = "Hamburguesa - Modo Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun HamburguesaOscuroPreview() {
    val authViewModelMock = AuthViewModel()
    ZenitAppTheme(darkTheme = true) {
        Surface(color = MaterialTheme.colorScheme.background) {
            HamburguesaScreen(
                authViewModel = authViewModelMock,
                esModoOscuroActivo = true,
                onModoOscuroCambiado = {},
                onBackClick = {},
                onLogoutSuccess = {}
            )
        }
    }
}
