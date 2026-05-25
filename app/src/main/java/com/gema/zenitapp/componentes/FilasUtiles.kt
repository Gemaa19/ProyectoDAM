package com.gema.zenitapp.componentes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenit.models.TransaccionResponse
import com.gema.zenitapp.ui.theme.verdeTitulos
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun FilaMovimiento(
    transaccion: TransaccionResponse,
    onEditarClick: (Long) -> Unit,
    onEliminarClick: (Long) -> Unit
) {
    val esIngreso = transaccion.tipo == "INGRESO"

    val iconoCategoria = when (transaccion.categoriaId) {
        1L -> Icons.Default.Home
        2L -> Icons.Default.ElectricBolt
        3L -> Icons.Default.DirectionsCar
        4L -> Icons.Default.Restaurant
        else -> Icons.Default.CreditCard
    }

    val textoCategoria = when (transaccion.categoriaId) {
        1L -> "Hogar"
        2L -> "Servicios"
        3L -> "Transporte"
        4L -> "Comida"
        else -> "General"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp)
            .padding(horizontal = 20.dp, vertical = 3.dp),
        shape = RoundedCornerShape(25.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxHeight()
                    .width(60.dp)
                    .background(color = MaterialTheme.colorScheme.background, shape = RoundedCornerShape(30.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (esIngreso) Icons.Default.Payments else iconoCategoria,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(25.dp)
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = transaccion.descripcion ?: "Movimiento general",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = textoCategoria,
                    color = Color(0xFF9EA1A7),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
            Text(
                text = "${if (esIngreso) "+" else "-"}${String.format("%.2f", transaccion.monto)}€",
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                fontSize = 16.sp,
                color = if (esIngreso) Color(0xFF029B09) else Color(0xFFB2130F),
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))
            Row(
                modifier = Modifier.padding(end = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onEditarClick(transaccion.id) },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Edit, "Modificar", tint = Color.DarkGray, modifier = Modifier.size(18.dp))
                }

                IconButton(
                    onClick = { onEliminarClick(transaccion.id) },
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Delete, "Eliminar", tint = Color(0xFFB2130F), modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun FilaFechaDinamica(fechaSql: String) {
    var textoIzquierda = "MOVIMIENTO"

    try {
        val fechaTransaccion = LocalDate.parse(fechaSql)
        val formateadorMes = DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", Locale("es"))
        val hoy = LocalDate.now()

        textoIzquierda = when (fechaTransaccion) {
            hoy -> "HOY"
            hoy.minusDays(1) -> "AYER"
            else -> fechaTransaccion.format(formateadorMes).uppercase()
        }

    } catch (e: Exception) {
        textoIzquierda = "MOVIMIENTO"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = textoIzquierda,
            fontWeight = FontWeight.W200,
            fontSize = 13.sp,
            color = verdeTitulos
        )
    }
}