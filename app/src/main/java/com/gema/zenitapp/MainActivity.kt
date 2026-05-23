package com.gema.zenitapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.gema.zenitapp.ui.theme.ZenitAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
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
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    authViewModel.cargarSesionLocal(context)
                }

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
                            authViewModel = authViewModel,
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
                            authViewModel = authViewModel,
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToObjetivos = { navController.navigate("objetivos") },
                            onNavigateToNuevoMovimiento = { movimientoId ->
                                if (movimientoId != null) {
                                    // Si lleva ID, viajamos con el argumento a la pantalla de edición
                                    navController.navigate("nuevo_movimiento?movimientoId=$movimientoId")
                                } else {
                                    // Si es null, navegamos a la pantalla limpia para crear
                                    navController.navigate("nuevo_movimiento")
                                }
                            }
                        )
                    }

                    composable("hamburguesa") {
                        HamburguesaScreen(
                            authViewModel = authViewModel,
                            onBackClick = { navController.popBackStack() },
                            // 💡 SOLUCIÓN: Quitamos 'onEditClick' porque ahora el lápiz abre el diálogo interno
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

                    // 💡 CORRECCIÓN CRUCIAL EN TU MAINACTIVITY.KT
                    // En tu MainActivity.kt actualiza estas dos declaraciones en el NavHost:

                    composable("objetivos") {
                        ObjetivosScreen(
                            authViewModel = authViewModel,
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            // 💡 MAPEAMOS LOS DESTINOS DEPENDIENDO DE SI LLEVAN ID O NO
                            onNavigateToNuevoObjetivo = { id, tipo ->
                                if (id != null) {
                                    navController.navigate("nuevo_objetivo?id=$id&tipo=$tipo")
                                } else {
                                    navController.navigate("nuevo_objetivo")
                                }
                            }
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

                    composable(
                        route = "nuevo_objetivo?id={id}&tipo={tipo}",
                        arguments = listOf(
                            navArgument("id") { type = NavType.StringType; nullable = true; defaultValue = null },
                            navArgument("tipo") { type = NavType.StringType; nullable = true; defaultValue = null }
                        )
                    ) { backStackEntry ->
                        val idStr = backStackEntry.arguments?.getString("id")
                        val tipoStr = backStackEntry.arguments?.getString("tipo")

                        NuevoObjetivoScreen(
                            objetivoId = idStr?.toLongOrNull(),
                            tipoObjetivo = tipoStr,
                            authViewModel = authViewModel,
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



/*@Preview(showBackground = true, showSystemUi = true)
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
}*/

/*@Preview(showBackground = true, showSystemUi = true)
@Composable
fun NuevoObjetivoScreenPreview() { // He añadido "Preview" al nombre para que no choque con la original
    ZenitAppTheme {
        NuevoObjetivoScreen(onBack = {})
    }
}*/

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