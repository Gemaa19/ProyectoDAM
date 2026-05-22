package com.gema.zenitapp

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.BackgroundWhite
import com.gema.zenitapp.ui.theme.colorBoton
import com.gema.zenitapp.ui.theme.verdeOscuro
import com.gema.zenitapp.ui.theme.verdeClaro
import com.gema.zenitapp.ui.theme.verdeFondo
import com.gema.zenitapp.ui.theme.verdeTitulos

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateToLogin: () -> Unit,
    onRegistroSuccess: () -> Unit,
    authViewModel: AuthViewModel = viewModel()
) {
    var userName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }
    var errorLocal by remember { mutableStateOf("") }
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
            text = "Sign up",
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
                Spacer(modifier = Modifier.height(20.dp))

                // CAMPO USUARIO
                ZenitInputField(
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = "Nombre Usuario",
                    icon = Icons.Default.Person,
                    iconOnLeft = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO EMAIL
                ZenitInputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    icon = Icons.Default.Email,
                    iconOnLeft = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO CONTRASEÑA
                ZenitInputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña",
                    icon = Icons.Default.Lock,
                    iconOnLeft = true,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // CAMPO REPETIR CONTRASEÑA
                ZenitInputField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repetir Contraseña",
                    icon = Icons.Default.Lock,
                    iconOnLeft = true,
                    isPassword = true
                )

                if (errorLocal.isNotEmpty()) {
                    Text(text = errorLocal, color = Color.Red, modifier = Modifier.padding(16.dp))
                } else if (authViewModel.errorMessage.isNotEmpty()) {
                    Text(text = authViewModel.errorMessage, color = Color.Red, modifier = Modifier.padding(16.dp))
                }

                Spacer(modifier = Modifier.height(35.dp))

                Button(
                    onClick = {
                        if (userName.isBlank() || email.isBlank() || password.isBlank()) {
                            errorLocal = "Por favor, rellena todos los campos"
                        } else if (password != confirmPassword) {
                            errorLocal = "Las contraseñas no coinciden"
                        } else {
                            errorLocal = "" // Limpiamos el error de validación local

                            // LLAMAMOS AL VIEWMODEL ACTUALIZADO
                            authViewModel.registrarUsuario(
                                nombre = userName.trim(),
                                correo = email.trim(),
                                clave = password.trim(),
                                onResult = { exito ->
                                    if (exito) {
                                        // 1. Informamos visualmente al usuario
                                        Toast.makeText(context, "Usuario registrado correctamente", Toast.LENGTH_LONG).show()

                                        // 2. Vaciamos todos los campos del formulario
                                        userName = ""
                                        email = ""
                                        password = ""
                                        confirmPassword = ""

                                        // 3. Regresamos de forma segura a la pantalla de Login
                                        onNavigateToLogin()
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    enabled = !authViewModel.isLoading,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colorBoton)
                ) {
                    if (authViewModel.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(text = "Sign up", fontSize = 18.sp, color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¿Ya tienes cuenta? ",
                        color = Color.Gray
                    )
                    TextButton(
                        onClick = { onNavigateToLogin() },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Login",
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