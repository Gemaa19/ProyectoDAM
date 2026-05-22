package com.gema.zenitapp.componentes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.verdeOscuro

@Composable
fun IconoSeleccionableCategoria(
    nombre: String,
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    id: Long,
    idSeleccionado: Long?,
    onSelect: (Long) -> Unit
) {
    val estaSeleccionado = id == idSeleccionado
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { onSelect(id) }
            .padding(4.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(15.dp),
            color = if (estaSeleccionado) verdeOscuro else Color(0xFFF5F5F5),
            modifier = Modifier.size(50.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icono, contentDescription = nombre, tint = if (estaSeleccionado) Color.White else Color.Gray)
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(nombre, fontSize = 12.sp, color = if (estaSeleccionado) verdeOscuro else Color.Gray, fontWeight = FontWeight.Medium)
    }
}