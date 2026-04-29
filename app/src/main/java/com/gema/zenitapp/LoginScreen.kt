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
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.gema.zenitapp.api.RetrofitClient
import com.gema.zenitapp.models.LoginUsuario
import android.util.Log
import com.gema.zenitapp.ui.theme.BackgroundWhite
import com.gema.zenitapp.ui.theme.InputGray
import com.gema.zenitapp.ui.theme.ZenitGreen
import com.gema.zenitapp.ui.theme.ZenitLightGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onNavigateToSignUp: () -> Unit, onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    ) {
        // PARTE SUPERIOR CURVADA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(ZenitLightGreen, shape = RoundedCornerShape(bottomEnd = 100.dp))
                .padding(top = 50.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = "ZENIT",
                    style = TextStyle(
                        fontSize = 80.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // TÍTULO "Login"
        Text(
            text = "Login",
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 40.dp),
            textAlign = TextAlign.End,
            style = TextStyle(fontSize = 32.sp, color = ZenitGreen)
        )

        // FORMULARIO
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Campo Email (Corregido para Material 3)
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputGray,
                    unfocusedContainerColor = InputGray,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                ),
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Campo Password (Corregido para Material 3)
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(30.dp),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = InputGray,
                    unfocusedContainerColor = InputGray,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    disabledBorderColor = Color.Transparent,
                ),
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                singleLine = true
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

            // BOTÓN LOGIN (Corregido para Material 3)
            Button(
                onClick = {
                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(context, "Rellena todos los campos", Toast.LENGTH_SHORT).show()
                        return@Button
                }
                    isLoading = true
                    scope.launch {
                        try {
                            // Creamos el objeto con los datos del formulario
                            val datos = LoginUsuario(email, password)

                            // Llamada al servidor a través de Retrofit
                            val respuesta = RetrofitClient.instancia.login(datos)

                            if (respuesta.isSuccessful) {
                                val auth = respuesta.body()
                                Log.d("API_SUCCESS", "Login correcto. Usuario: ${auth?.username}")

                                // TODO: Aquí guardaremos el token en SharedPreferences más adelante
                                Toast.makeText(context, "¡Bienvenido, ${auth?.username}!", Toast.LENGTH_SHORT).show()

                                // Acción para navegar a la siguiente pantalla
                                onLoginSuccess()
                            } else {
                                // Error de credenciales (401 Unauthorized, etc.)
                                Log.e("API_ERROR", "Código de error: ${respuesta.code()}")
                                Toast.makeText(context, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            // Error de red (Servidor apagado, IP incorrecta, sin internet)
                            Log.e("NETWORK_ERROR", "Mensaje: ${e.message}")
                            Toast.makeText(context, "No se pudo conectar con el servidor", Toast.LENGTH_SHORT).show()
                        } finally {
                            // Ocultamos el cargando al terminar
                            isLoading = false
                        }
                    }
                },
                // Deshabilitamos el botón mientras la petición está en curso
                enabled = !isLoading,

                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ZenitGreen,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    // Muestra un círculo de carga si está esperando al servidor
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

            TextButton(onClick = { onNavigateToSignUp() }) {
                Text(text = "¿No tienes cuenta? Sign up", color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // BARRA INFERIOR
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(ZenitLightGreen),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CONTROLA LO QUE GASTAS, DOMINA LO QUE AHORRAS",
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold)
            )
        }
    }
}