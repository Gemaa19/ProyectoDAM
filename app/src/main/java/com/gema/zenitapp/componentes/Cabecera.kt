package com.gema.zenitapp.componentes

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gema.zenitapp.ui.theme.verdeFondo
import com.gema.zenitapp.ui.theme.verdeOscuro

@Composable
fun CabeceraPrincipal(
    titulo: String,
    tamañoLetra: Int,
    onMenuClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(verdeFondo)
            .padding(16.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menú lateral",
            modifier = Modifier
                .size(40.dp)
                .align(Alignment.CenterStart)
                .clickable { onMenuClick() }
        )

        Text(
            text = titulo,
            color = verdeOscuro,
            fontSize = tamañoLetra.sp,
            fontWeight = FontWeight.W900,
            fontFamily = FontFamily.SansSerif,
            letterSpacing = 6.sp,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}