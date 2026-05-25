package com.gema.zenitapp.componentes

import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CabeceraFormulario(
    titulo: String,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(16.dp)) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = MaterialTheme.colorScheme.primary)
        }
        Text(
            text = titulo,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 22.sp,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun SelectorDobleOpciones(
    opcion1: String,
    opcion2: String,
    estaSeleccionadaOpcion1: Boolean,
    onSeleccionCambiada: (Boolean) -> Unit,
    icono1: ImageVector? = null,
    icono2: ImageVector? = null
) {
    Card(shape = RoundedCornerShape(15.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))) {
        Row(Modifier.fillMaxWidth().padding(4.dp)) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
                    .background(if (estaSeleccionadaOpcion1) MaterialTheme.colorScheme.background else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { onSeleccionCambiada(true) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icono1 != null) Icon(icono1, null, tint = if (estaSeleccionadaOpcion1) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(opcion1, fontWeight = FontWeight.Bold, color = if (estaSeleccionadaOpcion1) MaterialTheme.colorScheme.primary else Color.Gray)
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(45.dp)
                    .background(if (!estaSeleccionadaOpcion1) MaterialTheme.colorScheme.background else Color.Transparent, RoundedCornerShape(12.dp))
                    .clickable { onSeleccionCambiada(false) },
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (icono2 != null) Icon(icono2, null, tint = if (!estaSeleccionadaOpcion1) MaterialTheme.colorScheme.primary else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(opcion2, fontWeight = FontWeight.Bold, color = if (!estaSeleccionadaOpcion1) MaterialTheme.colorScheme.primary else Color.Gray)
                }
            }
        }
    }
}

@Composable
fun recordarPermisoNotificaciones(
    onPermisoResultado: (Boolean) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { esAceptado ->
        onPermisoResultado(esAceptado)
        if (!esAceptado) {
            Toast.makeText(context, "Necesitas activar las notificaciones en los ajustes del móvil", Toast.LENGTH_LONG).show()
        }
    }

    return {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        } else {
            onPermisoResultado(true)
        }
    }
}