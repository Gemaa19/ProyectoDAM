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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.ZenitInputField
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import com.gema.zenitapp.ui.theme.verdeTitulos
import com.gema.zenitapp.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigateToSignUp: () -> Unit, onLoginSuccess: () -> Unit, authViewModel: AuthViewModel = viewModel()) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(bottomEnd = 100.dp)
                )
                .padding(top = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "ZENIT",
                    color = MaterialTheme.colorScheme.primary,
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
            style = TextStyle(
                fontSize = 32.sp,
                color = verdeTitulos,
                fontWeight = FontWeight.W500,
                fontFamily = FontFamily.SansSerif,
                letterSpacing = 2.sp
            )
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

            Spacer(modifier = Modifier.height(25.dp))

            val mensajeDeError = when {
                errorLocal.isNotEmpty() -> errorLocal
                authViewModel.errorMessage.isNotEmpty() -> authViewModel.errorMessage
                else -> ""
            }

            if (mensajeDeError.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSystemInDarkTheme())
                            Color(0xFF2C1B1A) else Color(0xFFFCE8E6)
                    ),
                    border = BorderStroke(1.dp, if (isSystemInDarkTheme()) Color(0xFF632422) else Color(0xFFF5B7B1).copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alerta de acceso",
                            tint = if (isSystemInDarkTheme()) Color(0xFFFFB4AB) else Color(0xFFA12620),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = mensajeDeError,
                            color = if (isSystemInDarkTheme()) Color(0xFFFFDAD6) else Color(0xFF5C1916),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 18.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                Spacer(modifier = Modifier.height(15.dp))
            }

            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        errorLocal = "Por favor, rellena todos los campos"
                        return@Button
                    }
                    errorLocal = ""
                    authViewModel.loginUsuario(
                        context = context,
                        correo = email.trim(),
                        clave = password.trim(),
                        onResult = { exito ->
                            if (exito) {
                                Log.d("API_SUCCESS", "Login correcto a través de AuthViewModel")
                                Toast.makeText(context, "¡Bienvenido a ZenitApp!", Toast.LENGTH_SHORT).show()
                                onLoginSuccess()
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
                    containerColor = MaterialTheme.colorScheme.onBackground,
                    contentColor = Color.White
                )
            ) {
                if (authViewModel.isLoading) {
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
                    color = MaterialTheme.colorScheme.scrim
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
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CONTROLA LO QUE GASTAS, DOMINA LO QUE AHORRAS",
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            )
        }
    }
}

@Preview(showBackground = true, name = "Login - Modo Claro")
@Composable
fun LoginScreenLightPreview() {
    ZenitAppTheme(darkTheme = false) {
        LoginScreen(
            onNavigateToSignUp = {},
            onLoginSuccess = {},
            authViewModel = viewModel()
        )
    }
}

@Preview(showBackground = true, name = "Login - Modo Oscuro")
@Composable
fun LoginScreenDarkPreview() {
    ZenitAppTheme(darkTheme = true) {
        LoginScreen(
            onNavigateToSignUp = {},
            onLoginSuccess = {},
            authViewModel = viewModel()
        )
    }
}