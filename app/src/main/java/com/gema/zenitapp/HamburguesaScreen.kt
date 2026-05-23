package com.gema.zenitapp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.verdeFondo
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeOscuro

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamburguesaScreen(
    authViewModel: AuthViewModel,
    onBackClick: () -> Unit,
    onLogoutSuccess: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (authViewModel.usuarioLogueado == null) {
            authViewModel.cargarSesionLocal(context)
        }
        authViewModel.obtenerCategoriasBBDD(context)
    }

    val coloresPastel = listOf(
        Color(0xFFAEFCEB), Color(0xFFFCE4EC), Color(0xFFFFF9C4), Color(0xFFE3F2FD)
    )

    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var mostrarDialogoCategorias by remember { mutableStateOf(false) }
    var mostrarDialogoNotificaciones by remember { mutableStateOf(false) }
    var mostrarDialogoAyuda by remember { mutableStateOf(false) }
    var mostrarDialogoAjustes by remember { mutableStateOf(false) }

    var nuevaCategoriaNombre by remember { mutableStateOf("") }
    var esModoOscuroActivo by remember { mutableStateOf(false) }

    val nombreUsuario = authViewModel.usuarioLogueado?.nombre ?: "Usuario Zenit"
    val correoUsuario = authViewModel.usuarioLogueado?.email ?: "usuario@zenit.com"

    val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
    val colorGuardadoInt = prefs.getInt("user_avatar_color", coloresPastel[0].value.toLong().toInt())
    var colorAvatarSeleccionado by remember { mutableStateOf(Color(colorGuardadoInt.toLong())) }

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

    Column(modifier = Modifier.fillMaxSize().background(Color(0x57AEFCEB))) {
        // --- SECCIÓN SUPERIOR DE PERFIL ---
        Box(
            modifier = Modifier.fillMaxWidth().background(color = verdeFondo, shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)).padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBack, "Volver", tint = verdeIconos) }
                    IconButton(onClick = { mostrarDialogoEditar = true }) { Icon(Icons.Default.Edit, "Editar perfil", tint = verdeIconos) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Box(modifier = Modifier.size(100.dp).background(color = colorAvatarSeleccionado, shape = CircleShape), contentAlignment = Alignment.Center) {
                    Text(text = iniciales, fontSize = 36.sp, fontWeight = FontWeight.Bold, color = verdeIconos, letterSpacing = 2.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = nombreUsuario, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = verdeOscuro)
                Text(text = correoUsuario, fontSize = 16.sp, color = verdeOscuro)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- SECCIÓN INFERIOR DE MENÚ ---
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(menuOptions) { option ->
                MenuOptionRow(option = option, onClick = {
                    when (option.text) {
                        "Categorías" -> mostrarDialogoCategorias = true
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

    // ==========================================
    // DIÁLOGO DE CATEGORÍAS (CORREGIDO)
    // ==========================================
    if (mostrarDialogoCategorias) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCategorias = false },
            title = { Text("Categorías de Zenit", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoCategorias = false }) { Text("Cerrar", color = verdeIconos) }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Gestión de tus carpetas de gastos activos:", fontSize = 14.sp, color = Color.Gray)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = nuevaCategoriaNombre,
                            onValueChange = { nuevaCategoriaNombre = it },
                            placeholder = { Text("Nueva categoría...", fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeIconos)
                        )
                        Button(
                            onClick = {
                                if (nuevaCategoriaNombre.isNotBlank()) {
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
                            Icon(Icons.Default.Add, null, tint = Color.White)
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // 💡 LA CORRECCIÓN: Cambiado LazyColumn por un Column con scroll convencional
                    // Esto evita el crash por anidamiento ilegal de listas en Jetpack Compose
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        authViewModel.listaCategorias.forEach { cat ->
                            val vectorIcono = when (cat.id) {
                                1L -> Icons.Default.Home
                                2L -> Icons.Default.ElectricBolt
                                3L -> Icons.Default.DirectionsCar
                                4L -> Icons.Default.Restaurant
                                else -> Icons.Default.CreditCard
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF9F9F9), RoundedCornerShape(10.dp))
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(vectorIcono, null, tint = verdeOscuro, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(12.dp))
                                Text(cat.nombre, fontWeight = FontWeight.Medium, color = Color.DarkGray, fontSize = 15.sp)
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // El resto de tus diálogos (Notificaciones, Edición, Ajustes, Ayuda) permanecen igual...
    if (mostrarDialogoNotificaciones) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoNotificaciones = false },
            title = { Text("Centro de Avisos", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoNotificaciones = false }) { Text("Entendido", color = verdeIconos) }
            },
            text = {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.NotificationsNone, null, tint = Color.LightGray, modifier = Modifier.size(60.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(text = "No tienes alertas pendientes.\nTe avisaremos 2 días antes de tus gastos fijos configurados.", fontSize = 14.sp, color = Color.Gray, textAlign = TextAlign.Center)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoEditar) {
        var nombreTemporal by remember { mutableStateOf(nombreUsuario) }
        var colorTemporal by remember { mutableStateOf(colorAvatarSeleccionado) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoEditar = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(onClick = {
                    if (nombreTemporal.isNotBlank()) {
                        authViewModel.usuarioLogueado?.let { sesionActual ->
                            sesionActual.nombre = nombreTemporal
                            val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("user_name", nombreTemporal).apply()
                        }
                    }
                    colorAvatarSeleccionado = colorTemporal
                    val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putInt("user_avatar_color", colorTemporal.value.toLong().toInt()).apply()
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
                    OutlinedTextField(
                        value = nombreTemporal,
                        onValueChange = { nombreTemporal = it },
                        label = { Text("Nombre de usuario") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = verdeIconos, focusedLabelColor = verdeIconos, cursorColor = verdeIconos),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Color del avatar:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                            coloresPastel.forEach { color ->
                                val estaSeleccionado = (color == colorTemporal)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .border(width = if (estaSeleccionado) 3.dp else 1.dp, color = if (estaSeleccionado) verdeIconos else Color.LightGray, shape = CircleShape)
                                        .background(color = color, shape = CircleShape)
                                        .clickable { colorTemporal = color }
                                )
                            }
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoAjustes) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAjustes = false },
            confirmButton = { TextButton(onClick = { mostrarDialogoAjustes = false }) { Text("Aceptar", color = verdeIconos) } },
            title = { Text("Ajustes de Interfaz", fontWeight = FontWeight.Bold, color = verdeIconos) },
            text = {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Activar Modo Oscuro", fontSize = 16.sp)
                    Switch(checked = esModoOscuroActivo, onCheckedChange = { esModoOscuroActivo = it }, colors = SwitchDefaults.colors(checkedThumbColor = verdeIconos))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    if (mostrarDialogoAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAyuda = false },
            confirmButton = { TextButton(onClick = { mostrarDialogoAyuda = false }) { Text("Entendido", color = verdeIconos) } },
            title = { Text("Soporte ZenitApp", fontWeight = FontWeight.Bold, color = verdeIconos) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("💬 ¿Qué es el Dinero Libre Real?", fontWeight = FontWeight.SemiBold)
                    Text("Es el saldo neto que te queda disponible tras restar tus presupuestos fijados y las metas de ahorro activas.", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("📧 ¿Tienes problemas técnicos?", fontWeight = FontWeight.SemiBold)
                    Text("Escríbenos a soporte@zenitapp.com y resolveremos cualquier fallo de sincronización con tu base de datos de AWS.", fontSize = 14.sp, color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun MenuOptionRow(option: MenuOption, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 12.dp),
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

data class MenuOption(val icon: ImageVector, val text: String)