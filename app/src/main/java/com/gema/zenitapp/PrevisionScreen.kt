package com.gema.zenitapp

import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.AddCircle
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrevisionScreen(onBack: () -> Unit) {
    val categoriasEditables = listOf(
        CategoriaPresupuesto("Comida & bebida", "", "200€", "", 0.6f, Icons.Default.Restaurant, Color(0xFF00BFA5)),
        CategoriaPresupuesto("Transporte", "", "150€", "", 0.4f, Icons.Default.DirectionsBus, Color(0xFF00897B)),
        CategoriaPresupuesto("Ropa", "Sin límite establecido", "0€", "", 0.0f, Icons.Default.Checkroom, Color(0xFF26A69A)),
        CategoriaPresupuesto("Entretenimiento", "", "50€", "", 0.3f, Icons.Default.Movie, Color(0xFF4DB6AC)),
        CategoriaPresupuesto("Hogar", "", "700€", "", 0.8f, Icons.Default.Home, Color(0xFF00796B))
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Establecer previsión",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF004D40)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = ZenitLightGreen
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(ZenitLightGreen.copy(alpha = 0.2f))
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // SECCIÓN RESUMEN OCTUBRE
                item {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text("PRESUPUESTO MENSUAL - OCTUBRE", color = Color.Gray, fontSize = 12.sp)
                        Text("1250.00€", fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF004D40))
                        Surface(
                            color = ZenitLightGreen,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(top = 8.dp)
                        ) {
                            Text("+12% vs Septiembre", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), color = Color(0xFF00796B), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Text(
                        "Categorías",
                        modifier = Modifier.fillMaxWidth().padding(start = 25.dp, bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // LISTA DE CATEGORÍAS CON ICONO DE EDICIÓN
                items(categoriasEditables) { cat ->
                    ItemCategoriaEditable(cat)
                }
            }

            // BOTÓN GUARDAR FIJO ABAJO
            Button(
                onClick = { /* Lógica de guardado */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .height(60.dp),
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ZenitGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Añadir Previsión", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun ItemCategoriaEditable(cat: CategoriaPresupuesto) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(40.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(85.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().width(65.dp).background(cat.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(cat.icono, null, tint = Color.White)
            }
            Column(Modifier.padding(horizontal = 16.dp).weight(1f)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(cat.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        if (cat.info.isNotEmpty()) {
                            Text(cat.info, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                    Text(cat.gastado, fontWeight = FontWeight.Bold)
                }

                // Barra de progreso y botón de editar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LinearProgressIndicator(
                        progress = { cat.progreso },
                        modifier = Modifier.weight(1f).padding(top = 8.dp).height(6.dp),
                        color = Color(0xFFE91E63), // Color rosa de progreso en tu diseño
                        trackColor = Color.Black,
                        strokeCap = StrokeCap.Round
                    )
                    Spacer(Modifier.width(10.dp))

                    // Icono de lápiz o más según el estado
                    Icon(
                        imageVector = if (cat.nombre == "Ropa") Icons.Default.AddCircle else Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color.Gray,
                        modifier = Modifier.size(20.dp).padding(top = 5.dp)
                    )
                }
            }
        }
    }
}