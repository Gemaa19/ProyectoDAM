package com.gema.zenitapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(onNavigateToPrevision: () -> Unit, onNavigateToMovimientos: () -> Unit) {
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState()
    var mostrarSheet by rememberSaveable() { mutableStateOf(false) }
    val categorias = listOf(
        CategoriaPresupuesto(
            "Comida & bebida",
            "Quedan 200€",
            "200€",
            "400€",
            0.5f,
            Icons.Default.Restaurant,
            Color(0xFF00BFA5)
        ),
        CategoriaPresupuesto(
            "Transporte",
            "¡Presupuesto bajo!",
            "90€",
            "100€",
            0.9f,
            Icons.Default.DirectionsCar,
            Color(0xFF00897B)
        ),
        CategoriaPresupuesto(
            "Ropa",
            "Sin gastos",
            "0€",
            "150€",
            0.0f,
            Icons.Default.Checkroom,
            Color(0xFF26A69A)
        ),
        CategoriaPresupuesto(
            "Ocio",
            "Disfrutando",
            "50€",
            "200€",
            0.25f,
            Icons.Default.ConfirmationNumber,
            Color(0xFF4DB6AC)
        ),
        CategoriaPresupuesto(
            "Hogar",
            "Todo al día",
            "300€",
            "300€",
            1.0f,
            Icons.Default.Home,
            Color(0xFF00796B)
        ),
        CategoriaPresupuesto(
            "Salud",
            "Seguro médico",
            "60€",
            "60€",
            1.0f,
            Icons.Default.MedicalServices,
            Color(0xFF80CBC4)
        ),
        CategoriaPresupuesto(
            "Gimnasio",
            "Cuota mensual",
            "45€",
            "45€",
            1.0f,
            Icons.Default.FitnessCenter,
            Color(0xFF4DB6AC)
        ),
        CategoriaPresupuesto(
            "Suscripciones",
            "Netflix y Spotify",
            "25€",
            "30€",
            0.8f,
            Icons.Default.Subscriptions,
            Color(0xFF26A69A)
        )
    )

    Scaffold(
        bottomBar = {
            BarraNavegacionInferior(
                onMetasClick = onNavigateToPrevision,
                onMovimientosClick = onNavigateToMovimientos
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarSheet = true }, // Al pulsar, abre la pantalla de arriba
                containerColor = ZenitGreen,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Categoría")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ZenitLightGreen.copy(alpha = 0.3f))
        ) {
            CabeceraPrincipal()
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    SeccionSaldo()
                }

                item {
                    TarjetasResumidas()
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Previsiones Mensuales",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text("Ver todo", color = Color.Gray, fontSize = 14.sp)
                    }
                }

                items(categorias) { cat ->
                    SeccionPrevisiones(cat)
                }

                item { Spacer(Modifier.height(80.dp)) }
            }
        }
        if (mostrarSheet) {
            ModalBottomSheet(
                onDismissRequest = { mostrarSheet = false },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                ContenidoAnadirCategoria(onClose = { mostrarSheet = false })
            }
        }
    }
}
@Composable
fun CabeceraPrincipal() {
    Row(
        modifier = Modifier.fillMaxWidth().background(ZenitLightGreen).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Menu, contentDescription = null, modifier = Modifier.size(30.dp))
        Text("ZENIT", fontWeight = FontWeight.Bold, fontSize = 28.sp, letterSpacing = 4.sp)
        Row {
            Icon(Icons.Default.NotificationsNone, contentDescription = null, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(30.dp))
        }
    }
}

@Composable
fun SeccionSaldo() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("DINERO LIBRE REAL", color = Color.Gray, fontSize = 14.sp)
        Text("1250.00€", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF004D40))
        Surface(
            color = ZenitLightGreen,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("+5.2% vs mes anterior", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF00796B), fontSize = 12.sp)
        }
    }
}

@Composable
fun TarjetasResumidas() {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val modifier = Modifier.weight(1f)
        TarjetaResumenPequeña(modifier, "Ingresos", "+1500€", Icons.Default.Payments, Color.Blue)
        TarjetaResumenPequeña(modifier, "Gastos", "-800€", Icons.Default.CreditCard, Color.Red)
        TarjetaResumenPequeña(modifier, "Saldo", "700€", Icons.Default.AccountBalance, Color.Magenta)
    }
}

@Composable
fun TarjetaResumenPequeña(modifier: Modifier, title: String, amount: String, icon: androidx.compose.ui.graphics.vector.ImageVector, iconColor: Color) {
    Card(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
            Icon(icon, null, tint = iconColor, modifier = Modifier.size(20.dp))
            Text(title, color = Color.Gray, fontSize = 12.sp)
            Text(amount, fontWeight = FontWeight.Bold, color = Color(0xFF00BFA5))
        }
    }
}

@Composable
fun SeccionPrevisiones(cat: CategoriaPresupuesto) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFFFFF)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().width(60.dp).background(cat.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(cat.icono, null, tint = Color.White)
            }
            Column(Modifier.padding(12.dp).weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(cat.nombre, fontWeight = FontWeight.Bold)
                        Text(cat.info, fontSize = 12.sp, color = Color.Gray)
                    }
                    Text("${cat.gastado}/${cat.total}", fontWeight = FontWeight.Bold)
                }
                LinearProgressIndicator(
                    progress = cat.progreso,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp).height(6.dp),
                    color = Color.Magenta,
                    trackColor = Color.LightGray
                )
            }
        }
    }
}

@Composable
fun AddCategoryButton() {
    OutlinedButton(
        onClick = { },
        modifier = Modifier.fillMaxWidth().padding(32.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(2.dp, ZenitGreen),
    ) {
        Icon(Icons.Default.AddCircleOutline, null, tint = Color.Gray)
        Spacer(Modifier.width(8.dp))
        Text("Añadir categoría", color = Color.Gray)
    }
}

@Composable
fun BarraNavegacionInferior(onMetasClick: () -> Unit, onMovimientosClick: () -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Inicio") },
            selected = true,
            onClick = {}
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.List, null) },
            label = { Text("Movimientos") },
            selected = false,
            onClick = { onMovimientosClick() }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.PieChart, null) },
            label = { Text("Reportes") },
            selected = false,
            onClick = {}
        )
        // ESTE ES EL BOTÓN DEL CERDITO
        NavigationBarItem(
            icon = { Icon(Icons.Default.Savings, null) },
            label = { Text("Metas") },
            selected = false,
            onClick = { onMetasClick() }
        )
    }
}

@Composable
fun ContenidoAnadirCategoria(onClose: () -> Unit) {
    var nombre by remember { mutableStateOf("") }
    var presupuesto by remember { mutableStateOf("") }
    var iconoSeleccionado by remember { mutableStateOf(Icons.Default.Category) }
    val opcionesIconos = listOf(
        Icons.Default.Restaurant,
        Icons.Default.DirectionsCar,
        Icons.Default.ShoppingBag,
        Icons.Default.LocalMovies,
        Icons.Default.FitnessCenter,
        Icons.Default.Home,
        Icons.Default.MedicalServices,
        Icons.Default.School,
        Icons.Default.Flight
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(), // Evita que el teclado tape todo
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Nueva Categoría", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF004D40))
        Spacer(Modifier.height(24.dp))

        Text(
            "Selecciona un icono ilustrativo",
            modifier = Modifier.fillMaxWidth(),
            fontSize = 14.sp,
            color = Color.Gray
        )

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            opcionesIconos.take(5).forEach { icono ->
                val estaSeleccionado = iconoSeleccionado == icono
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(
                            if (estaSeleccionado) ZenitGreen else Color(0xFFF1F1F5),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { iconoSeleccionado = icono },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = if (estaSeleccionado) Color.White else Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // CAMPO: NOMBRE
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre de la categoría") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true
        )

        Spacer(Modifier.height(16.dp))

        // CAMPO: PRESUPUESTO (Solo números)
        OutlinedTextField(
            value = presupuesto,
            onValueChange = { presupuesto = it },
            label = { Text("Presupuesto total mensual (€)") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number) // <--- TECLADO NUMÉRICO
        )

        Spacer(Modifier.height(32.dp))

        // BOTÓN FINAL
        Button(
            onClick = { onClose() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ZenitGreen),
            shape = RoundedCornerShape(16.dp),
            enabled = nombre.isNotEmpty() && presupuesto.isNotEmpty()
        ) {
            Icon(Icons.Default.Save, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Guardar", fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(20.dp))
    }
}

data class CategoriaPresupuesto(
    val nombre: String,
    val info: String,
    val gastado: String,
    val total: String,
    val progreso: Float, // de 0.0 a 1.0
    val icono: androidx.compose.ui.graphics.vector.ImageVector,
    val color: Color
)