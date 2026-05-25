package com.gema.zenitapp

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.componentes.ZenitInputField
import com.gema.zenitapp.viewmodel.AuthViewModel
import com.gema.zenitapp.ui.theme.ZenitAppTheme
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
    var errorLocal by remember { mutableStateOf("") }
    var mostrarDialogoExito by remember { mutableStateOf(false) }

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

                ZenitInputField(
                    value = userName,
                    onValueChange = { userName = it },
                    placeholder = "Nombre Usuario",
                    icon = Icons.Default.Person,
                    iconOnLeft = false
                )

                Spacer(modifier = Modifier.height(16.dp))

                ZenitInputField(
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Email",
                    icon = Icons.Default.Email,
                    iconOnLeft = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                ZenitInputField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Contraseña",
                    icon = Icons.Default.Lock,
                    iconOnLeft = true,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                ZenitInputField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "Repetir Contraseña",
                    icon = Icons.Default.Lock,
                    iconOnLeft = true,
                    isPassword = true
                )

                val mensajeDeError = when {
                    errorLocal.isNotEmpty() -> errorLocal
                    authViewModel.errorMessage.isNotEmpty() -> authViewModel.errorMessage
                    else -> ""
                }

                if (mensajeDeError.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFCE8E6)
                        ),
                        border = BorderStroke(1.dp, Color(0xFFF5B7B1).copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alerta de validación",
                                tint = Color(0xFFA12620),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = mensajeDeError,
                                color = Color(0xFF5C1916),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 18.sp,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(35.dp))

                Button(
                    onClick = {
                        val contraseñaRobusta = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!_.,])(?=\\S+$).{8,}$".toRegex()

                        if(userName.isBlank() || email.isBlank() || password.isBlank()) {
                            errorLocal = "Por favor, rellena todos los campos"
                        } else if (password != confirmPassword) {
                            errorLocal = "Las contraseñas no coinciden"
                        } else if (!password.matches(contraseñaRobusta)) {
                            errorLocal = "La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial"
                        } else {
                            errorLocal = ""

                            authViewModel.registrarUsuario(
                                nombre = userName.trim(),
                                correo = email.trim(),
                                clave = password.trim(),
                                onResult = { exito ->
                                    if (exito) {
                                        mostrarDialogoExito = true
                                    }
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    enabled = !authViewModel.isLoading,
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.onBackground)
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
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CONTROLA LO QUE GASTAS, DOMINA LO QUE AHORRAS",
                style = TextStyle(fontSize = 10.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.primary)
            )
        }
    }
    if (mostrarDialogoExito) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(28.dp),
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoExito = false
                        userName = ""
                        email = ""
                        password = ""
                        confirmPassword = ""
                        onRegistroSuccess()
                        onNavigateToLogin()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)
                ) {
                    Text("Continuar al Login", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Registro Exitoso",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(75.dp)
                    )
                    Spacer(Modifier.height(18.dp))
                    Text(
                        text = "¡Cuenta creada con éxito!",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Ya puedes iniciar sesión en ZenitApp y empezar a controlar tus finanzas inteligentes.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )
                }
            }
        )
    }
}
@Preview(
    name = "Modo Claro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Preview(
    name = "Modo Oscuro",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun RegistroScreenPreview() {
    ZenitAppTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RegistroScreen(
                onNavigateToLogin = {},
                onRegistroSuccess = {},
            )
        }
    }
}