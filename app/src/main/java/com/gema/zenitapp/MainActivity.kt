package com.gema.zenitapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gema.zenitapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ZenitAppTheme {
                val navController = rememberNavController()
                val authViewModel: AuthViewModel = viewModel()

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
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToObjetivos = { navController.navigate("objetivos") },
                            onNavigateToNuevoMovimiento = { navController.navigate("nuevo_movimiento") }
                        )
                    }

                    composable("menu_hamburguesa") {
                        HamburguesaScreen(
                            authViewModel = authViewModel,
                            onBackClick = { navController.popBackStack() },
                            onEditClick = { navController.navigate("editar_perfil") },
                            onCategoriasClick = { navController.navigate("categorias_screen") },
                            onNotificacionesClick = { navController.navigate("notificaciones_screen") },
                            onLogoutSuccess = {
                                navController.navigate("login") {
                                    popUpTo("login") { inclusive = true }
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
fun CargaScreenPreview() {
    ZenitAppTheme {
        CargaScreen(onNavigateToLogin = {})
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

/*@Preview(showBackground = true, showSystemUi = true)
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
}*/

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoMovimientoScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        //NuevoMovimientoScreen(onBack = {})
    }
}

/*
@Preview(showBackground = true, showSystemUi = true)
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
}*/