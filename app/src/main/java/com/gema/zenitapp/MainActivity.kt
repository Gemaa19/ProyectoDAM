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

                NavHost(navController = navController, startDestination = "carga") {

                    composable("carga") {
                        CargaScreen(onNavigateToLogin = {
                            navController.navigate("login") {
                                popUpTo("carga") { inclusive = true }
                            }
                        })
                    }

                    composable("login") {
                        LoginScreen(
                            onNavigateToSignUp = { navController.navigate("signup") },
                            onLoginSuccess = {
                                navController.navigate("inicio") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        )
                    }

                    composable("signup") {
                        RegistroScreen(
                            onNavigateToLogin = { navController.navigate("login") },
                            onRegistroSuccess = { // <--- Asegúrate de que aquí pone onRegistroSuccess
                                navController.navigate("inicio") {
                                    popUpTo("signup") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    composable("inicio") {
                        InicioScreen(
                            onMenuClick = { /* Lógica menú */ },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToObjetivos = { navController.navigate("objetivos") },
                            // ASEGÚRATE DE QUE SE LLAMA ASÍ:
                            onNavigateToNuevoMovimiento = { navController.navigate("nuevo_movimiento") }
                        )
                    }

                    composable("hamburguesa") {
                        HamburguesaScreen(
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { /* Lógica para editar perfil */ },
                            onMenuOptionClick = { optionName ->
                                // Manejar clics en el menú, ej. navegar a Categorías, cerrar sesión, etc.
                                when (optionName) {
                                    "Categorías" -> navController.navigate("categorias")
                                    "Cerrar sesión" -> { /* Lógica para cerrar sesión, volver al login */
                                    }

                                    else -> { /* Otras opciones */
                                    }
                                }
                            }
                        )
                    }

                    composable("movimientos") {
                        MovimientosScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToNuevoMovimiento = { navController.navigate("nuevo_movimiento") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToObjetivos = { navController.navigate("objetivos") }
                        )
                    }

                    composable("objetivos") {
                        ObjetivosScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToNuevoObjetivo = { navController.navigate("nuevo_objetivo") }
                        )
                    }

                    composable("analisis") {
                        AnalisisScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToObjetivos = { navController.navigate("objetivos") }
                        )
                    }

                    composable("nuevo_movimiento") {
                        NuevoMovimientoScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable("nuevo_objetivo") {
                        NuevoObjetivoScreen(
                            onBack = { navController.popBackStack() }
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
fun RegistroPreview() {
    ZenitAppTheme {
        RegistroScreen(
            onNavigateToLogin = {},
            onRegistroSuccess = {} // <--- Cambiado aquí también
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun InicioScreenPreview() {
    ZenitAppTheme {
        InicioScreen(
            onMenuClick = {},
            onNavigateToMovimientos = {},
            onNavigateToAnalisis = {},
            onNavigateToObjetivos = {},
            onNavigateToNuevoMovimiento = {} // <--- Cambiado con el nombre nuevo
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HamburguesaScreenPreview() {
    ZenitAppTheme {
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
            onNavigateToAnalisis = {},
            onNavigateToNuevoObjetivo = { }
        )
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoObjetivoScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        NuevoObjetivoScreen(onBack = {})
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovimientosScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        MovimientosScreen(
            onMenuClick = {},
            onNavigateToNuevoMovimiento = {},
            onNavigateToInicio = {},
            onNavigateToAnalisis = {},
            onNavigateToObjetivos = {},
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
            onNavigateToObjetivos = {}
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