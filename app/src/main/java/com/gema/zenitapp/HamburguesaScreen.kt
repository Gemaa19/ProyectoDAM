package com.gema.zenitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

// Definimos los colores específicos para esta pantalla si no están ya en tu tema
// Basado en el verde agua brillante del panel superior
val ZenitBrightAqua = Color(0xFFA1FCED)
// El verde oscuro para texto e iconos
val ZenitDarkGreenText = Color(0xFF006D5B)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HamburguesaScreen(
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onMenuOptionClick: (String) -> Unit
) {
    // Lista de opciones de menú basadas en tu diseño
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
            .background(ZenitLightGreen.copy(alpha = 0.3f)) // Fondo general tenue
    ) {
        // --- SECCIÓN SUPERIOR DE PERFIL (Fondo brillante) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = ZenitBrightAqua, // Verde agua brillante
                    shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp) // Esquinas inferiores redondeadas
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Barra superior: Icono atrás y Lápiz
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = ZenitDarkGreenText
                        )
                    }
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil",
                            tint = ZenitDarkGreenText
                        )
                    }
                }

                // Contenido central: Icono grande, Nombre y Correo
                Spacer(modifier = Modifier.height(16.dp))
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = ZenitDarkGreenText,
                    modifier = Modifier.size(100.dp) // Tamaño grande para el icono de perfil
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Gema González",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = ZenitDarkGreenText
                )
                Text(
                    text = "gemaaa1911@gmail.com",
                    fontSize = 16.sp,
                    color = ZenitDarkGreenText
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // --- SECCIÓN INFERIOR DE MENÚ ---
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espacio entre opciones
        ) {
            items(menuOptions) { option ->
                MenuOptionRow(
                    option = option,
                    onClick = { onMenuOptionClick(option.text) }
                )
            }
        }
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
            .clickable(onClick = onClick) // Fila entera clicable
            .padding(vertical = 12.dp), // Espaciado vertical interno
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icono circular relleno de verde oscuro con icono blanco/claro dentro
        Surface(
            modifier = Modifier.size(56.dp),
            shape = CircleShape,
            color = ZenitDarkGreenText // Color de fondo del círculo (verde oscuro)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = null,
                    tint = Color.White, // Color del icono dentro del círculo (verde claro)
                    modifier = Modifier.size(30.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(20.dp)) // Espacio entre icono y texto

        // Texto de la opción en verde oscuro
        Text(
            text = option.text,
            fontSize = 20.sp,
            color = ZenitDarkGreenText,
            fontWeight = FontWeight.Normal
        )
    }
}

// Modelo de datos simple para las opciones de menú
data class MenuOption(
    val icon: ImageVector,
    val text: String
)

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    ZenitAppTheme {
        HamburguesaScreen(
            onBackClick = {},
            onEditClick = {},
            onMenuOptionClick = {}
        )
    }
}