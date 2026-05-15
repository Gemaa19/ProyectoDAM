package com.gema.zenitapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gema.zenitapp.ui.theme.ZenitAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenitAppTheme {
                val navController = rememberNavController()

                // 1. CAMBIO AQUÍ: startDestination ahora es "splash"
                NavHost(navController = navController, startDestination = "carga") {

                    // 2. AÑADIMOS LA PANTALLA DE CARGA
                    composable("carga") {
                        CargaScreen(onNavigateToLogin = {
                            navController.navigate("login") {
                                // Esto borra el Splash de la historia para que el botón "Atrás" no vuelva a él
                                popUpTo("carga") { inclusive = true }
                            }
                        })
                    }

                    composable("signup") {
                        SignUpScreen(onNavigateToLogin = {
                            navController.navigate("login")
                        })
                    }

                    composable("login") {
                        LoginScreen(
                            onNavigateToSignUp = { navController.navigate("signup") },
                            onLoginSuccess = {
                                // Opcional: Aquí también podrías usar popUpTo para que no vuelvan al login
                                navController.navigate("inicio")
                            }
                        )
                    }

                    composable("inicio") {
                        InicioScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToPrevision = { navController.navigate("prevision") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") }
                        )
                    }

                    composable("objetivos") {
                        ObjetivosScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") } // <--- CAMBIADO
                        )
                    }

                    composable("movimientos") {
                        MovimientosScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToPrevision = { navController.navigate("prevision") },
                            // AQUÍ ESTABA EL ERROR: Faltaba conectar la navegación a inicio
                            onNavigateToInicio = { navController.navigate("inicio") }
                        )
                    }

                    composable("analisis") {
                        AnalisisScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToPrevision = { navController.navigate("prevision") },
                            // ESTA ES LA QUE FALTABA:
                            onNavigateToMovimientos = { navController.navigate("movimientos") }
                        )
                    }

                    composable("hamburguesa") {
                        HamburguesaScreen(
                            onBackClick = { navController.popBackStack() }, // Volver atrás
                            onEditClick = { /* Lógica para editar perfil */ },
                            onMenuOptionClick = { optionName ->
                                // Manejar clics en el menú, ej. navegar a Categorías, cerrar sesión, etc.
                                when (optionName) {
                                    "Categorías" -> navController.navigate("categorias")
                                    "Cerrar sesión" -> { /* Lógica para cerrar sesión, volver al login */ }
                                    else -> { /* Otras opciones */ }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginPreview() {
    ZenitAppTheme {
        LoginScreen(onNavigateToSignUp = {}, onLoginSuccess = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SignUpPreview() {
    ZenitAppTheme {
        SignUpScreen(onNavigateToLogin = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioScreenPreview() {
    ZenitAppTheme {
        // Añadimos el onMenuClick vacío para que no dé error
        InicioScreen(
            onMenuClick = {},
            onNavigateToPrevision = {},
            onNavigateToMovimientos = {}
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HamburguesaScreenPreview() {
    ZenitAppTheme {
        // Usamos el nombre correcto de la pantalla y sus parámetros
        HamburguesaScreen(
            onBackClick = {},
            onEditClick = {},
            onMenuOptionClick = {}
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ObjetivosScreenPreview() { // <--- Nombre actualizado
    ZenitAppTheme {
        ObjetivosScreen(
            onMenuClick = {},
            onNavigateToInicio = {},
            onNavigateToMovimientos = {},
            onNavigateToAnalisis = {} // <--- Debe coincidir con la función original
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoObjetivoScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        NuevoObjetivoScreen(onBack = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovimientosScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        MovimientosScreen(
            onMenuClick = {},
            onNavigateToPrevision = {},
            onNavigateToInicio = {} // <--- Cambiado por el que faltaba
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoMovimientoScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        NuevoMovimientoScreen(onBack = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AnalisisScreenPreview() {
    ZenitAppTheme {
        AnalisisScreen(
            onMenuClick = {},
            onNavigateToInicio = {},
            onNavigateToMovimientos = {},
            onNavigateToPrevision = {}
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CargaScreenPreview() {
    ZenitAppTheme {
        CargaScreen(onNavigateToLogin = {})
    }
}