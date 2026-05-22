package com.gema.zenitapp

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
import com.gema.zenitapp.ui.theme.verdeFondo

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    onMenuClick: () -> Unit,
    onNavigateToMovimientos: () -> Unit,
    onNavigateToAnalisis: () -> Unit,
    onNavigateToObjetivos: () -> Unit,
    // 💡 CAMBIO AQUÍ: Ahora el callback acepta el ID del movimiento a editar (si es null, es que es uno nuevo)
    onNavigateToNuevoMovimiento: (Long?) -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        authViewModel.obtenerMovimientosBBDD(context)
    }

    val movimientosReales = authViewModel.transaccionesReales
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
            // ==========================================
            // BLOQUE FIJO 1: Nombre de la aplicación
            // ==========================================
            CabeceraPrincipal(
                titulo = "ZENIT",
                tamañoLetra = 35,
                onMenuClick = onMenuClick
            )

            // Si no hay datos, mostramos el estado vacío ocupando el resto de la pantalla
            if (sinDatos) {
                // 💡 CAMBIO: Le pasamos null porque es un movimiento nuevo de paquete
                EstadoVacioInicio(onAgregarClick = { onNavigateToNuevoMovimiento(null) })
            } else {
                // ==========================================
                // BLOQUE FIJO 2: Indicadores y tarjetas (No se mueven)
                // ==========================================
                SeccionSaldo(saldoReal = saldoMensual)

                TarjetasResumidas(
                    ingresos = totalIngresos,
                    gastos = totalGastos,
                    saldo = saldoMensual
                )
                HorizontalDivider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, start = 24.dp, end = 24.dp), // Alineado con los márgenes de tus tarjetas
                    thickness = 1.dp,
                    color = Color(0xFFE5E7EB) // Un gris clarito y limpio (estilo Tailwind/Pastel)
                )
                // ==========================================
                // BLOQUE CON SCROLL: Solo los últimos movimientos
                // ==========================================
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f), // Toma el espacio restante de la pantalla de forma dinámica
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
                            val esIngreso = transaccion.tipo == "INGRESO"

                            val iconoCategoria = when (transaccion.categoriaId) {
                                1L -> Icons.Default.Home
                                2L -> Icons.Default.ElectricBolt
                                3L -> Icons.Default.DirectionsCar
                                4L -> Icons.Default.Restaurant
                                else -> Icons.Default.CreditCard
                            }

                            val movVisual = Movimiento(
                                nombre = transaccion.descripcion ?: "Movimiento general",
                                categoria = when (transaccion.categoriaId) {
                                    1L -> "Hogar"
                                    2L -> "Servicios"
                                    3L -> "Transporte"
                                    4L -> "Comida"
                                    else -> "General"
                                },
                                cantidad = "${if (esIngreso) "+" else "-"}${String.format("%.2f", transaccion.monto)}€",
                                esIngreso = esIngreso,
                                icono = if (esIngreso) Icons.Default.Payments else iconoCategoria,
                                color = Color(0xFF90A4AE)
                            )

                            ItemGasto(
                                movimiento = movVisual,
                                onEditarClick = {
                                    // 💡 SOLUCIÓN: Usamos el callback de la pantalla pasándole el ID real
                                    onNavigateToNuevoMovimiento(transaccion.id)
                                },
                                onEliminarClick = {
                                    authViewModel.eliminarMovimientoBBDD(context, transaccion.id) {
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
            color = Color.DarkGray
        )
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
            color = Color.DarkGray,
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
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(24.dp))
            Spacer(Modifier.height(4.dp))
            Text(title, color = Color.Gray, fontSize = 11.sp, maxLines = 1, textAlign = TextAlign.Center)
            Spacer(Modifier.height(4.dp))
            Text(amount, fontWeight = FontWeight.Bold, color = verdeTitulos, fontSize = 13.sp)
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ItemGasto(
    movimiento: Movimiento,
    onEditarClick: () -> Unit,   // Callback para abrir la pantalla de edición
    onEliminarClick: () -> Unit  // Callback para borrar el registro en AWS
) {
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
            // Contenedor del icono (Extremo izquierdo)
            Box(
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxHeight()
                    .width(60.dp)
                    .background(
                        color = verdeFondo,
                        shape = RoundedCornerShape(30.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = movimiento.icono,
                    contentDescription = null,
                    tint = verdeOscuro,
                    modifier = Modifier.size(25.dp)
                )
            }

            // Textos centrales (Descripción y Categoría)
            Column(
                modifier = Modifier
                    .weight(1f) // Se expande para ocupar todo el espacio central
                    .padding(start = 16.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = movimiento.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = movimiento.categoria,
                    color = Color(0xFF9EA1A7),
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }

            // Cantidad de dinero (Empujada hacia la izquierda de las acciones)
            Text(
                text = movimiento.cantidad,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (movimiento.esIngreso) Color(0xFF029B09) else Color(0xFFB2130F),
                modifier = Modifier.padding(horizontal = 4.dp) // Ajuste fino de separación
            )

            // Este Spacer actúa como un muelle invisible que empuja todo lo que viene detrás
            // al extremo derecho de la tarjeta
            Spacer(modifier = Modifier.width(8.dp))

            // Bloque de botones (Pegados al extremo derecho)
            Row(
                modifier = Modifier.padding(end = 12.dp), // Margen exterior derecho de la cápsula
                horizontalArrangement = Arrangement.spacedBy(2.dp), // Reducido para que estén bien pegados entre sí
                verticalAlignment = Alignment.CenterVertically
            ) {
                // BOTÓN DE MODIFICAR (Lápiz gris)
                IconButton(
                    onClick = onEditarClick,
                    modifier = Modifier.size(30.dp) // Ajustado a 30dp para mejorar la respuesta táctil sin separarlos
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Modificar movimiento",
                        tint = Color.DarkGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // BOTÓN DE ELIMINAR (Papelera roja)
                IconButton(
                    onClick = onEliminarClick,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar movimiento",
                        tint = Color(0xFFB2130F),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}




@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioScreenPreview() {
    ZenitAppTheme {
        InicioScreen(
            onMenuClick = {},
            onNavigateToMovimientos = {},
            onNavigateToAnalisis = {},
            onNavigateToObjetivos = {},
            onNavigateToNuevoMovimiento = {} // <--- Cambiado con el nombre nuevo
        )
    }
}