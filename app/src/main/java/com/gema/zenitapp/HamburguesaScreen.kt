package com.gema.zenitapp

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import com.gema.zenitapp.ui.theme.verdeFondo
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeTitulos
import com.gema.zenitapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamburguesaScreen(
    authViewModel: AuthViewModel, // Inyectamos tu ViewModel real
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onCategoriasClick: () -> Unit,
    onNotificacionesClick: () -> Unit,
    onLogoutSuccess: () -> Unit // Callback para volver al Login tras borrar sesión
) {
    val context = LocalContext.current

    val coloresPastel = listOf(
        Color(0xFFAEFCEB), // Verde menta / salvia
        Color(0xFFFCE4EC), // Rosa pastel
        Color(0xFFFFF9C4), // Amarillo suave
        Color(0xFFE3F2FD)  // Azul bebé
    )
    // Estados locales para controlar los diálogos interactivos de Ajustes y Ayuda
    var mostrarDialogoAyuda by remember { mutableStateOf(false) }
    var mostrarDialogoAjustes by remember { mutableStateOf(false) }
    var mostrarDialogoEditar by remember { mutableStateOf(false) }
    var esModoOscuroActivo by remember { mutableStateOf(false) } // Control local del tema

    // DATOS REALES: Si tus variables de login están vacías, usamos unos fallbacks limpios
    val nombreUsuario = authViewModel.usuarioLogueado?.nombre ?: "Usuario Zenit"
    val correoUsuario = authViewModel.usuarioLogueado?.email ?: "usuario@zenit.com"
    var nombreEditado by remember {
        mutableStateOf(authViewModel.usuarioLogueado?.nombre ?:  "Usuario Zenit")
    }
    var colorAvatarSeleccionado by remember { mutableStateOf(coloresPastel[0]) }

    val iniciales = remember(nombreEditado) {
        val partes = nombreEditado.trim().split("\\s+".toRegex())
        if (partes.isEmpty() || partes[0].isEmpty()) {
            "Z"
        } else if (partes.size >= 2) {
            // Coge la primera letra del primer y segundo elemento
            "${partes[0].first().uppercase()}${partes[1].first().uppercase()}"
        } else {
            // Si es un solo nombre, duplica su inicial (ej. Gema -> GG)
            val inicial = partes[0].first().uppercase()
            "$inicial$inicial"
        }
    }

    val menuOptions = listOf(
        MenuOption(icon = Icons.Default.GridView, text = "Categorías"),
        MenuOption(icon = Icons.Default.Notifications, text = "Notificaciones"),
        MenuOption(icon = Icons.Default.Settings, text = "Ajustes"),
        MenuOption(icon = Icons.Default.Help, text = "Ayuda"),
        MenuOption(icon = Icons.Default.ExitToApp, text = "Cerrar sesión")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x57AEFCEB))
    ) {
        // --- SECCIÓN SUPERIOR DE PERFIL (Dinámica con datos reales) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = verdeFondo,
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, "Volver", tint = verdeIconos)
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(Icons.Default.Edit, "Editar perfil", tint = verdeIconos)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(color = colorAvatarSeleccionado, shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = iniciales,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = verdeIconos,
                        letterSpacing = 2.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                // Muestra el nombre real de tu base de datos de AWS
                Text(
                    text = nombreEditado,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = verdeOscuro
                )
                // Muestra el correo real del login
                Text(
                    text = correoUsuario,
                    fontSize = 16.sp,
                    color = verdeOscuro
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- SECCIÓN INFERIOR DE MENÚ (Con ruteo inteligente) ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(menuOptions) { option ->
                MenuOptionRow(
                    option = option,
                    onClick = {
                        when (option.text) {
                            "Categorías" -> onCategoriasClick()
                            "Notificaciones" -> onNotificacionesClick()
                            "Ajustes" -> mostrarDialogoAjustes = true
                            "Ayuda" -> mostrarDialogoAyuda = true
                            "Cerrar sesión" -> {
                                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                                prefs.edit().remove("auth_token").apply()
                                authViewModel.logout()
                                onLogoutSuccess()
                            }
                        }
                    }
                )
            }
        }
    }
// ==========================================
    // DIÁLOGO DE EDICIÓN: Cambiar nombre y color de Avatar
    // ==========================================
    if (mostrarDialogoEditar) {
        var nombreTemporal by remember { mutableStateOf(nombreEditado) }
        var colorTemporal by remember { mutableStateOf(colorAvatarSeleccionado) }

        AlertDialog(
            onDismissRequest = { mostrarDialogoEditar = false },
            title = { Text("Editar Perfil", fontWeight = FontWeight.Bold, color = verdeIconos) },
            confirmButton = {
                TextButton(onClick = {
                    nombreEditado = nombreTemporal
                    colorAvatarSeleccionado = colorTemporal
                    mostrarDialogoEditar = false
                    // Aquí podrías lanzar un authViewModel.actualizarPerfil() si añades la ruta a AWS
                }) {
                    Text("Guardar", color = verdeIconos, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoEditar = false }) {
                    Text("Cancelar", color = Color.Gray)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = nombreTemporal,
                        onValueChange = { nombreTemporal = it },
                        label = { Text("Nombre de usuario") },
                        singleLine = true,
                        // CORRECCIÓN 1: Sintaxis oficial de Material 3 para colores de TextField
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = verdeIconos,
                            focusedLabelColor = verdeIconos,
                            cursorColor = verdeIconos
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Color del avatar:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            coloresPastel.forEach { color ->
                                val estaSeleccionado = (color == colorTemporal)
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        // CORRECCIÓN 2: El borde necesita su propio 'shape' y va ANTES del fondo/click
                                        .border(
                                            width = if (estaSeleccionado) 3.dp else 1.dp,
                                            color = if (estaSeleccionado) verdeIconos else Color.LightGray,
                                            shape = CircleShape
                                        )
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

    // ==========================================
    // DIÁLOGO DE AJUSTES: Interruptor de Modo Claro / Oscuro
    // ==========================================
    if (mostrarDialogoAjustes) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAjustes = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAjustes = false }) {
                    Text("Aceptar", color = verdeIconos)
                }
            },
            title = { Text("Ajustes de Interfaz", fontWeight = FontWeight.Bold, color = verdeIconos) },
            text = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Activar Modo Oscuro", fontSize = 16.sp)
                    Switch(
                        checked = esModoOscuroActivo,
                        onCheckedChange = { esModoOscuroActivo = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = verdeIconos)
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // ==========================================
    // DIÁLOGO DE AYUDA: Solución simple y elegante para tu TFG
    // ==========================================
    if (mostrarDialogoAyuda) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoAyuda = false },
            confirmButton = {
                TextButton(onClick = { mostrarDialogoAyuda = false }) {
                    Text("Entendido", color = verdeIconos)
                }
            },
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
fun MenuOptionRow(
    option: MenuOption,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = verdeIconos
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(20.dp))
        Text(
            text = option.text,
            fontSize = 20.sp,
            color = verdeIconos,
            fontWeight = FontWeight.Normal
        )
    }
}

// ==========================================
// CORRECCIÓN: Modelo de datos necesario para mapear las filas de la lista
// ==========================================
data class MenuOption(
    val icon: ImageVector,
    val text: String
)

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HamburguesaScreenPreview() {
    ZenitAppTheme {
        // Creamos una instancia básica del ViewModel para que la Preview se dibuje
        val mockAuthViewModel: com.gema.zenitapp.viewmodel.AuthViewModel = viewModel()

        HamburguesaScreen(
            authViewModel = mockAuthViewModel,
            onBackClick = {},
            onEditClick = {},
            onCategoriasClick = {},
            onNotificacionesClick = {},
            onLogoutSuccess = {}
        )
    }
}