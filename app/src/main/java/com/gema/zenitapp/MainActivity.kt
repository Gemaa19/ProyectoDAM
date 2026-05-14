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
                NavHost(navController = navController, startDestination = "splash") {

                    // 2. AÑADIMOS LA PANTALLA DE CARGA
                    composable("splash") {
                        CargaScreen(onNavigateToLogin = {
                            navController.navigate("login") {
                                // Esto borra el Splash de la historia para que el botón "Atrás" no vuelva a él
                                popUpTo("splash") { inclusive = true }
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

                    composable("prevision") {
                        PrevisionScreen(onBack = { navController.popBackStack() })
                    }

                    composable("movimientos") {
                        MovimientosScreen(onBack = { navController.popBackStack() })
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

@Preview(showBackground = true, showSystemUi = true)
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
fun PrevisionScreen() {
    ZenitAppTheme {
        PrevisionScreen(onBack = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MovimientosScreen() {
    ZenitAppTheme {
        MovimientosScreen(onBack = {})
    }
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SplashScreenPreview() {
    ZenitAppTheme {
        CargaScreen(onNavigateToLogin = {})
    }
}