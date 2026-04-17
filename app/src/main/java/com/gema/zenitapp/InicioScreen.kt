package com.gema.zenitapp

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@Composable
fun InicioScreen() {
    val categorias = listOf(
        CategoriaPresupuesto("Comida & bebida", "Quedan 200€", "200€", "400€", 0.5f, Icons.Default.Restaurant, Color(0xFF00BFA5)),
        CategoriaPresupuesto("Transporte", "¡Presupuesto bajo!", "90€", "100€", 0.9f, Icons.Default.DirectionsCar, Color(0xFF00897B)),
        CategoriaPresupuesto("Ropa", "Sin gastos", "0€", "150€", 0.0f, Icons.Default.Checkroom, Color(0xFF26A69A)),
        CategoriaPresupuesto("Ocio", "Disfrutando", "50€", "200€", 0.25f, Icons.Default.ConfirmationNumber, Color(0xFF4DB6AC)),
        CategoriaPresupuesto("Hogar", "Todo al día", "300€", "300€", 1.0f, Icons.Default.Home, Color(0xFF00796B)),
        CategoriaPresupuesto("Salud", "Seguro médico", "60€", "60€", 1.0f, Icons.Default.MedicalServices, Color(0xFF80CBC4)),
        CategoriaPresupuesto("Gimnasio", "Cuota mensual", "45€", "45€", 1.0f, Icons.Default.FitnessCenter, Color(0xFF4DB6AC)),
        CategoriaPresupuesto("Suscripciones", "Netflix y Spotify", "25€", "30€", 0.8f, Icons.Default.Subscriptions, Color(0xFF26A69A))
    )

    Scaffold(
        bottomBar = { BarraNavegacionInferior() }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZenitLightGreen.copy(alpha = 0.3f))
                .padding(paddingValues)
        ) {
            item {
                CabeceraPrincipal()
            }

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
                    Text("Previsiones Mensuales", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Ver todo", color = Color.Gray, fontSize = 14.sp)
                }
            }

            items(categorias) { cat ->
                SeccionPrevisiones(cat)
            }

            item {
                AddCategoryButton()
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
fun BarraNavegacionInferior() {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, null) }, label = { Text("Inicio") }, selected = true, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.ListAlt, null) }, label = { Text("Movimientos") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.PieChart, null) }, label = { Text("Reportes") }, selected = false, onClick = {})
        NavigationBarItem(icon = { Icon(Icons.Default.Savings, null) }, label = { Text("Metas") }, selected = false, onClick = {})
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