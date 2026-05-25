package com.gema.zenitapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
        configurarLanzadorTransaccionesDiarias()
        setContent {
            val context = LocalContext.current
            val prefs =
                remember { context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE) }

            var esModoOscuroActivo by remember {
                mutableStateOf(prefs.getBoolean("modo_oscuro_activo", false))
            }

            ZenitAppTheme(darkTheme = esModoOscuroActivo) {
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
                            onRegistroSuccess = {
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
                            onNavigateToObjetivos = { navController.navigate("objetivos/Presupuestos") },
                            onNavigateToNuevoMovimiento = { movimientoId ->
                                if (movimientoId != null) {
                                    navController.navigate("nuevo_movimiento?movimientoId=$movimientoId")
                                } else {
                                    navController.navigate("nuevo_movimiento")
                                }
                            }
                        )
                    }

                    composable("hamburguesa") {
                        HamburguesaScreen(
                            authViewModel = authViewModel,
                            onBackClick = { navController.popBackStack() },
                            esModoOscuroActivo = esModoOscuroActivo,
                            onModoOscuroCambiado = { nuevoValor ->
                                esModoOscuroActivo = nuevoValor
                            },
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
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToObjetivos = { navController.navigate("objetivos/Presupuestos") },
                            onNavigateToNuevoMovimiento = { movimientoId ->
                                if (movimientoId != null) {
                                    navController.navigate("nuevo_movimiento?movimientoId=$movimientoId")
                                } else {
                                    navController.navigate("nuevo_movimiento")
                                }
                            }
                        )
                    }

                    composable("objetivos/{pestaña}") { backStackEntry ->
                        val pestaña =
                            backStackEntry.arguments?.getString("pestaña") ?: "Presupuestos"

                        ObjetivosScreen(
                            pestañaInicial = pestaña,
                            authViewModel = authViewModel,
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToAnalisis = { navController.navigate("analisis") },
                            onNavigateToNuevoObjetivo = { id, tipo ->
                                if (id != null) {
                                    navController.navigate("nuevo_objetivo/$id/$tipo")
                                } else {
                                    navController.navigate("nuevo_objetivo/null/CLEAN")
                                }
                            }
                        )
                    }
                    composable("analisis") {
                        AnalisisScreen(
                            onMenuClick = { navController.navigate("hamburguesa") },
                            onNavigateToInicio = { navController.navigate("inicio") },
                            onNavigateToMovimientos = { navController.navigate("movimientos") },
                            onNavigateToObjetivos = { navController.navigate("objetivos/Presupuestos") }
                        )
                    }


                    composable(
                        route = "nuevo_movimiento?movimientoId={movimientoId}",
                        arguments = listOf(
                            navArgument("movimientoId") {
                                type = NavType.StringType
                                nullable = true
                                defaultValue = null
                            }
                        )
                    ) { backStackEntry ->
                        val movimientoIdStr = backStackEntry.arguments?.getString("movimientoId")

                        val movimientoIdLong = movimientoIdStr?.toLongOrNull()

                        NuevoMovimientoScreen(
                            movimientoId = movimientoIdLong,
                            authViewModel = authViewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }
                    composable("nuevo_objetivo/{id}/{tipo}") { backStackEntry ->
                        val id = backStackEntry.arguments?.getString("id")
                        val tipo = backStackEntry.arguments?.getString("tipo")

                        NuevoObjetivoScreen(
                            objetivoId = if (id == "null") null else id,
                            tipoObjetivo = if (tipo == "CLEAN") null else tipo,
                            authViewModel = authViewModel,
                            onBack = { pestañaDestino ->
                                navController.navigate("objetivos/$pestañaDestino") {
                                    popUpTo("objetivos/{pestaña}") { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    private fun configurarLanzadorTransaccionesDiarias() {

        val restricciones = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
            .build()

        val solicitudTrabajoDiario = androidx.work.PeriodicWorkRequestBuilder<com.gema.zenitapp.workers.TransaccionesProgramadasWorker>(
            24, java.util.concurrent.TimeUnit.HOURS
        )
            .setConstraints(restricciones)
            .build()

        androidx.work.WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "SincronizadorGastosFuturos",
            androidx.work.ExistingPeriodicWorkPolicy.KEEP,
            solicitudTrabajoDiario
        )
    }
}