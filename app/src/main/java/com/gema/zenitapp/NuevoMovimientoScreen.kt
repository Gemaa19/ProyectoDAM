package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@Composable
fun NuevoMovimientoScreen(onBack: () -> Unit) {
    var esGasto by remember { mutableStateOf(true) }
    var esFijo by remember { mutableStateOf(true) }
    var importe by remember { mutableStateOf("0.00") }
    var nombreGasto by remember { mutableStateOf("") }
    var esMensual by remember { mutableStateOf(true) }
    var recordatorio by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // CABECERA
        CabeceraSimple("Nuevo movimiento", onBack)

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

            // IMPORTE GIGANTE
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("Importe", color = Color.Gray, fontSize = 14.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = importe + "€",
                        fontSize = 35.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0D5140)
                    )
                }
            }

            Spacer(Modifier.height(15.dp))

            // NOMBRE DEL GASTO
            Text("Nombre del gasto", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
            OutlinedTextField(
                value = nombreGasto,
                onValueChange = { nombreGasto = it },
                placeholder = { Text("ej. Alquiler, internet, Netflix") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                leadingIcon = { Icon(Icons.Default.Edit, null, tint = Color.Gray) },
                colors = OutlinedTextFieldDefaults.colors(unfocusedBorderColor = Color.LightGray)
            )

            Spacer(Modifier.height(15.dp))

            // FRECUENCIA (MENSUAL / ANUAL)
            Text("Frecuencia", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp), // <--- AJUSTA TAMBIÉN LA FILA AQUÍ
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CajaFrecuencia(Modifier.weight(1f), "Mensual", Icons.Default.CalendarMonth, esMensual) { esMensual = true }
                CajaFrecuencia(Modifier.weight(1f), "Anual", Icons.Default.CalendarToday, !esMensual) { esMensual = false }
            }

            Spacer(Modifier.height(24.dp))

            // CATEGORÍAS
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Categorías", fontWeight = FontWeight.Bold)
                Text("Ver todo", color = ZenitGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconoCategoria("Hogar", Icons.Default.Home, ZenitGreen)
                IconoCategoria("Servicios", Icons.Default.ElectricBolt, ZenitGreen)
                IconoCategoria("Transporte", Icons.Default.DirectionsCar, ZenitGreen)
                IconoCategoria("Comida", Icons.Default.Restaurant, ZenitGreen)
            }

            Spacer(Modifier.height(15.dp))

            // RECORDATORIO
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp),
                border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
            ) {
                Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsNone, null, tint = ZenitGreen, modifier = Modifier.size(28.dp))
                    Column(Modifier.padding(horizontal = 12.dp).weight(1f)) {
                        Text("Recordatorio", fontWeight = FontWeight.Bold)
                        Text("Avisar 2 días antes", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(checked = recordatorio, onCheckedChange = { recordatorio = it }, colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ZenitLightGreen))
                }
            }

            Spacer(Modifier.height(30.dp))

            // BOTÓN GUARDAR
            Button(
                onClick = { /* Guardar */ },
                modifier = Modifier.fillMaxWidth().height(60.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A680))
            ) {
                Icon(Icons.Default.Save, null)
                Spacer(Modifier.width(8.dp))
                Text("Guardar Gasto", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CabeceraSimple(titulo: String, onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().background(ZenitLightGreen).padding(16.dp)) {
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
            Box(Modifier.weight(1f).height(45.dp).background(if (seleccionado1) ZenitLightGreen else Color.Transparent, RoundedCornerShape(12.dp)).clickable { onSeleccion(true) }, contentAlignment = Alignment.Center) {
                Row {
                    if (icono1 != null) Icon(icono1, null, tint = if (seleccionado1) Color(0xFF0D5140) else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(opcion1, fontWeight = FontWeight.Bold, color = if (seleccionado1) Color(0xFF0D5140) else Color.Gray)
                }
            }
            Box(Modifier.weight(1f).height(45.dp).background(if (!seleccionado1) ZenitLightGreen else Color.Transparent, RoundedCornerShape(12.dp)).clickable { onSeleccion(false) }, contentAlignment = Alignment.Center) {
                Row {
                    if (icono2 != null) Icon(icono2, null, tint = if (!seleccionado1) Color(0xFF0D5140) else Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(opcion2, fontWeight = FontWeight.Bold, color = if (!seleccionado1) Color(0xFF0D5140) else Color.Gray)
                }
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
            .height(65.dp) // <--- REDUCIDO DE 80.dp A 65.dp
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) ZenitLightGreen else Color.White
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (seleccionado) ZenitLightGreen else Color.LightGray.copy(alpha = 0.5f)
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

@Composable
fun IconoCategoria(nombre: String, icono: ImageVector, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(shape = CircleShape, color = color, modifier = Modifier.size(55.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icono, null, tint = Color.White) }
        }
        Text(nombre, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
    }
}