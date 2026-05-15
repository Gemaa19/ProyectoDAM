package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@Composable
fun NuevoObjetivoScreen(onBack: () -> Unit) {
    var esPresupuesto by remember { mutableStateOf(true) }
    var importe by remember { mutableStateOf("0.00") }
    var nombreObjetivo by remember { mutableStateOf("") }
    var esMensual by remember { mutableStateOf(true) }
    var recordatorio by remember { mutableStateOf(true) }

    Scaffold(
        topBar = { CabeceraSimple("Establecer objetivo", onBack) },
        bottomBar = {
            // Botón fijo abajo como pediste
            Button(
                onClick = { /* Lógica de guardado */ },
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

            // SELECTOR PRESUPUESTO / META
            SelectorDoble(
                opcion1 = "Presupuesto",
                opcion2 = "Meta",
                seleccionado1 = esPresupuesto,
                onSeleccion = { esPresupuesto = it }
            )

            // IMPORTE
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
            ) {
                Text("Importe", color = Color.Gray, fontSize = 14.sp)
                Text(
                    text = "${importe}€",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF0D5140)
                )
            }

            // NOMBRE DEL OBJETIVO
            Text("Nombre del objetivo", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = nombreObjetivo,
                onValueChange = { nombreObjetivo = it },
                placeholder = { Text("ej. Alquiler, internet, Netflix", fontSize = 14.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp), // Tu altura personalizada
                shape = RoundedCornerShape(20.dp),
                leadingIcon = {
                    Icon(
                        Icons.Default.Edit,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = ZenitGreen // Añadido para consistencia
                ),
                singleLine = true // Esto ya ayuda a reducir el padding vertical
            )

            Spacer(Modifier.height(20.dp))

            // FRECUENCIA
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

            // CATEGORÍAS
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Categorías", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Ver todo", color = Color(0xFF00A680), fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                IconoCategoria("Hogar", Icons.Default.Home, Color(0xFF00A680))
                IconoCategoria("Servicios", Icons.Default.ElectricBolt, Color(0xFF00A680))
                IconoCategoria("Transporte", Icons.Default.DirectionsCar, Color(0xFF00A680))
                IconoCategoria("Comida", Icons.Default.Restaurant, Color(0xFF00A680))
            }

            Spacer(Modifier.height(25.dp))

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
                        Text("Avisar al 80% del límite", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = recordatorio,
                        onCheckedChange = { recordatorio = it },
                        modifier = Modifier.scale(0.8f),
                        colors = SwitchDefaults.colors(checkedTrackColor = ZenitLightGreen)
                    )
                }
            }

            Spacer(Modifier.height(20.dp)) // Espacio final para que el scroll no choque con el botón
        }
    }
}