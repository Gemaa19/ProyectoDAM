package com.gema.zenitapp

import android.content.res.Configuration
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.CabeceraPrincipal
import com.gema.zenitapp.componentes.BarraNavegacionInferior
import com.gema.zenitapp.componentes.Movimiento
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import com.gema.zenitapp.ui.theme.colorBotonGeneral
import com.gema.zenitapp.ui.theme.rosa
import com.gema.zenitapp.ui.theme.verdeGrisaceo
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeTitulos
import com.gema.zenitapp.viewmodel.AuthViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import com.gema.zenit.models.TransaccionResponse
import com.gema.zenitapp.componentes.FilaFechaDinamica
import com.gema.zenitapp.componentes.FilaMovimiento
import com.gema.zenitapp.ui.theme.verdeFondo

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    onMenuClick: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToAnalisis: () -> Unit,
    onNavigateToObjetivos: () -> Unit,
    onNavigateToNuevoMovimiento: (Long?) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
    }

    val hoy = LocalDate.now()
    val movimientosReales = authViewModel.transaccionesReales.filter {
        try {
            val fechaMov = LocalDate.parse(it.fecha)
            !fechaMov.isAfter(hoy)
        } catch (e: Exception) {
            true
        }
    }
    val sinDatos = movimientosReales.isEmpty()
    val totalIngresos = movimientosReales.filter { it.tipo == "INGRESO" }.sumOf { it.monto }
    val totalGastos = movimientosReales.filter { it.tipo == "GASTO" }.sumOf { it.monto }
    val saldoMensual = totalIngresos - totalGastos
    val transaccionesAgrupadas = movimientosReales.groupBy { it.fecha }

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
                tamañoLetra = 35,
                onMenuClick = onMenuClick
            )

            if (sinDatos) {
                EstadoVacioInicio(onAgregarClick = { onNavigateToNuevoMovimiento(null) })
            } else {
                SeccionSaldo(saldoReal = saldoMensual)

                TarjetasResumidas(
                    ingresos = totalIngresos,
                    gastos = totalGastos,
                    saldo = saldoMensual
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 24.dp, end = 24.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.primary
                )
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Text(
                            text = "Últimos movimientos",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp,
                            color = verdeOscuro,
                            modifier = Modifier.padding(start = 24.dp, top = 24.dp, bottom = 8.dp)
                        )
                    }

                    transaccionesAgrupadas.forEach { (fechaStr, listaDeEseDia) ->
                        item {
                            FilaFechaDinamica(fechaSql = fechaStr)
                        }

                        items(listaDeEseDia) { transaccion ->
                            FilaMovimiento(
                                transaccion = transaccion,
                                onEditarClick = { id -> onNavigateToNuevoMovimiento(id) },
                                onEliminarClick = { id ->
                                    authViewModel.eliminarMovimientoBBDD(context, id) {
                                        Toast.makeText(context, "Movimiento eliminado", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }

                    item { Spacer(Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
fun EstadoVacioInicio(onAgregarClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Bienvenido a Zenit, añade tus primeros movimientos y empieza a usar la app con todas sus ventajas",
            color = verdeOscuro,
            fontSize = 18.sp,
            fontWeight = FontWeight.W300,
            textAlign = TextAlign.Center,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onAgregarClick,
            colors = ButtonDefaults.buttonColors(containerColor = colorBotonGeneral),
            border = BorderStroke(width = 4.dp, color = verdeTitulos),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.height(50.dp).fillMaxWidth(0.8f)
        ) {
            Icon(
                imageVector = Icons.Default.AddCircleOutline,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Añadir movimiento",
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SeccionSaldo(saldoReal: Double) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp, bottom = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DINERO LIBRE REAL", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Text("${String.format("%.2f", saldoReal)}€", fontSize = 40.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0D5140))
        Surface(
            color = verdeGrisaceo,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = "Balance en cuenta actualizado",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                color = Color(0xFF0D5140),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TarjetasResumidas(ingresos: Double, gastos: Double, saldo: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val modifier = Modifier.weight(1f)
        TarjetaResumenPequeña(modifier, "Ingresos", "+${String.format("%.2f", ingresos)}€", Icons.Default.Payments,verdeIconos)
        TarjetaResumenPequeña(modifier, "Gastos", "-${String.format("%.2f", gastos)}€", Icons.Default.CreditCard,rosa)
        TarjetaResumenPequeña(modifier, "Saldo mensual", "${String.format("%.2f", saldo)}€", Icons.Default.AccountBalance, Color(0xFF0D5140))
    }
}

@Composable
fun TarjetaResumenPequeña(modifier: Modifier, title: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Card(
        modifier = modifier.height(95.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, color = MaterialTheme.colorScheme.primary, fontSize = 11.sp, maxLines = 1, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(amount, fontWeight = FontWeight.Bold, color = verdeTitulos, fontSize = 13.sp)
        }
    }
}