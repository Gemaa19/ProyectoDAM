package com.gema.zenitapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.ui.theme.BackgroundWhite
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import com.gema.zenitapp.ui.theme.colorBoton
import com.gema.zenitapp.ui.theme.verdeClaro
import com.gema.zenitapp.ui.theme.verdeFondo
import com.gema.zenitapp.ui.theme.verdeIconos
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeTitulos
import com.gema.zenitapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable 
fun LoginScreen(onNavigateToSignUp: () -> Unit, onLoginSuccess: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(verdeFondo, shape = RoundedCornerShape(bottomEnd = 100.dp))
                .padding(top = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "ZENIT",
                    color = verdeOscuro,
                    fontSize = 80.sp,
                    fontWeight = FontWeight.W900,
                    fontFamily = FontFamily.SansSerif,
                    letterSpacing = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Login",
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 32.sp,
                color = verdeTitulos,
                fontWeight = FontWeight.W500,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp)
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            ZenitInputField(
                value = email,
                onValueChange = { email = it },
                placeholder = "Email",
                icon = Icons.Default.Email,
                iconOnLeft = false
            )

            Spacer(modifier = Modifier.height(15.dp))

            ZenitInputField(
                value = password,
                onValueChange = { password = it },
                placeholder = "Contraseña",
                icon = Icons.Default.Lock,
                isPassword = true,
                iconOnLeft = true
            )

            Text(
                text = "¿Has olvidado la contraseña?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.End,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    authViewModel.loginUsuario(
                        context = context,
                        correo = email.trim(),
                        clave = password.trim(),
                        onResult = { exito ->
                            if (exito) {
                                Log.d("API_SUCCESS", "Login correcto a través de AuthViewModel")
                                Toast.makeText(context, "¡Bienvenido a ZenitApp!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
                            } else {
                                Toast.makeText(context, authViewModel.errorMessage, Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                },

                enabled = !authViewModel.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorBoton,
                    contentColor = Color.White
                )
            ) {
                if (authViewModel.isLoading) { // Indicador de carga atado al ciclo del ViewModel
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(text = "Login", fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "¿No tienes cuenta? ",
                    color = Color.Gray
                )
                TextButton(
                    onClick = { onNavigateToSignUp() },
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "Sign up",
                        color = verdeTitulos,
                        fontWeight = FontWeight.W500,
                        fontSize = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .background(verdeFondo),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CONTROLA LO QUE GASTAS, DOMINA LO QUE AHORRAS",
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
            )
        }
    }
}

@Composable
fun ZenitInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconOnLeft: Boolean,
    isPassword: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp), // Un poquito más de altura para que luzca la curva
        shape = RoundedCornerShape(35.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // BLOQUE ICONO IZQUIERDA (Contraseña)
            if (iconOnLeft) {
                Box(
                    modifier = Modifier
                        .padding(4.dp) // Pequeño margen para que la curva no pegue al borde
                        .fillMaxHeight()
                        .width(70.dp)
                        .background(
                            color = colorBoton,
                            shape = RoundedCornerShape(30.dp) // CURVA INTERNA
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null,
                        tint = Color.White, modifier = Modifier.size(26.dp))
                }
            }

            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(placeholder, color = Color.Gray, fontSize = 16.sp) },
                modifier = Modifier.weight(1f),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )

            // BLOQUE ICONO DERECHA (Email)
            if (!iconOnLeft) {
                Box(
                    modifier = Modifier
                        .padding(4.dp) // Pequeño margen para que la curva no pegue al borde
                        .fillMaxHeight()
                        .width(70.dp)
                        .background(
                            color = verdeFondo,
                            shape = RoundedCornerShape(30.dp) // CURVA INTERNA
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = verdeOscuro, modifier = Modifier.size(26.dp))
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    ZenitAppTheme {
        LoginScreen(onNavigateToSignUp = {}, onLoginSuccess = {})
    }
}

