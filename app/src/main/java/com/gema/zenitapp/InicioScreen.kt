package com.gema.zenitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.CreditCard
import com.gema.zenitapp.componentes.Movimiento

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    onMenuClick: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToAnalisis: () -> Unit,
    onNavigateToObjetivos: () -> Unit,
    onNavigateToNuevoMovimiento: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
    }

    val movimientosReales = authViewModel.transaccionesReales
    val sinDatos = movimientosReales.isEmpty()

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                pantallaActual = "Inicio",
                onInicioClick = {},
                onMovimientosClick = onNavigateToMovimientos,
                onAnalisisClick = onNavigateToAnalisis,
                onObjetivosClick = onNavigateToObjetivos
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
        ) {
            CabeceraPrincipal(
                titulo = "ZENIT",
                onMenuClick = onMenuClick
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                if (sinDatos) {
                    item {
                        BotonNuevoMovimiento(onClick = onNavigateToNuevoMovimiento)
                        Spacer(Modifier.height(30.dp))
                    }
                } else {
                    item {
                        SeccionSaldo()
                    }

                    item {
                        TarjetasResumidas()
                    }

                    item {
                        Text(
                            text = "Gastos del mes",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }

                    items(movimientosReales) { transaccion ->
                        val esIngreso = transaccion.tipo == "INGRESO"

                        val movVisual = Movimiento(
                            nombre = transaccion.descripcion ?: "Sin descripción",
                            categoria = "Categoría ${transaccion.categoriaId ?: ""}",
                            cantidad = "${if (esIngreso) "+" else "-"}${transaccion.monto}€",
                            esIngreso = esIngreso,
                            icono = if (esIngreso) Icons.Default.Payments else Icons.Default.CreditCard,
                            color = if (esIngreso) ZenitGreen else Color(0xFFE91E63)
                        )

                        ItemGasto(movimiento = movVisual)
                    }
                } // <--- ¡AQUÍ ESTABA EL FALLO! Cerramos el bloque 'else' correctamente

                item { Spacer(Modifier.height(30.dp)) }
            }
        }
    }
}

@Composable
fun EstadoVacioInicio(onAgregarClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "No hay ningún dato registrado",
            color = Color.Gray,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onAgregarClick() },
            colors = ButtonDefaults.buttonColors(containerColor = ZenitGreen),
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .height(48.dp)
                .padding(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Añadir nuevo movimiento",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SeccionSaldo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DINERO LIBRE REAL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("8512,46€", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D5140))
        Surface(
            color = ZenitLightGreen,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "+5.2% vs mes anterior",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color(0xFF0D5140),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TarjetasResumidas() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val modifier = Modifier.weight(1f)
        TarjetaResumenPequeña(modifier, "Ingresos", "+1500€", Icons.Default.Payments, Color(0xFF4285F4))
        TarjetaResumenPequeña(modifier, "Gastos", "-800€", Icons.Default.CreditCard, Color(0xFFE91E63))
        TarjetaResumenPequeña(modifier, "Saldo mensual", "700€", Icons.Default.AccountBalance, Color(0xFF9C27B0))
    }
}

@Composable
fun TarjetaResumenPequeña(modifier: Modifier, title: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, color = Color.Gray, fontSize = 11.sp, maxLines = 2, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(amount, fontWeight = FontWeight.Bold, color = ZenitGreen, fontSize = 14.sp)
        }
    }
}

@Composable
fun FilaFecha(dia: String, fecha: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(dia, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(fecha, color = Color.Gray, fontSize = 14.sp)
    }
}

@Composable
fun ItemGasto(movimiento: Movimiento) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .height(70.dp),
        shape = RoundedCornerShape(35.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(70.dp)
                    .background(movimiento.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(movimiento.icono, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(movimiento.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                Text(movimiento.categoria, color = Color.Gray, fontSize = 13.sp)
            }

            Text(
                text = movimiento.cantidad,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (movimiento.esIngreso) ZenitGreen else Color.Black,
                modifier = Modifier.padding(end = 20.dp)
            )
        }
    }
}