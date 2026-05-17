package com.gema.zenitapp.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gema.zenitapp.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.mutableStateListOf
import com.gema.zenit.models.LoginUsuario
import com.gema.zenit.models.RegistroUsuarios
import com.gema.zenit.models.SolicitudMeta
import com.gema.zenit.models.SolicitudTransaccion
import com.gema.zenit.models.TransaccionResponse

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    // CORRECCIÓN CLAVE: Ambas variables cambian a TransaccionResponse para aceptar la Fecha y el ID de AWS
    var transaccionesReales by mutableStateOf<List<TransaccionResponse>>(listOf())
        private set

    val listaMovimientos = mutableStateListOf<TransaccionResponse>()

    // LÓGICA DE REGISTRO
    fun registrarUsuario(nombre: String, correo: String, clave: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading = true
                errorMessage = ""
            }
            try {
                val datos = RegistroUsuarios(username = nombre, email = correo, password = clave)
                val respuesta = RetrofitClient.instancia.registrar(datos)

                withContext(Dispatchers.Main) {
                    if (respuesta.isSuccessful) {
                        onResult(true)
                    } else {
                        errorMessage = "El usuario o el email ya están registrados."
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "No se pudo conectar con el servidor."
                    onResult(false)
                }
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // LÓGICA DE LOGIN
    fun loginUsuario(context: Context, correo: String, clave: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                isLoading = true
                errorMessage = ""
            }
            try {
                val datos = LoginUsuario(email = correo, password = clave)
                val respuesta = RetrofitClient.instancia.login(datos)

                if (respuesta.isSuccessful && respuesta.body() != null) {
                    val tokenRecibido = respuesta.body()!!.token

                    val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("auth_token", tokenRecibido).apply()

                    withContext(Dispatchers.Main) {
                        onResult(true)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        errorMessage = "Correo o contraseña incorrectos."
                        onResult(false)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    errorMessage = "Error de conexión con el servidor."
                    onResult(false)
                }
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // UNIFICADO Y CORREGIDO: Función única para bajarse los movimientos de AWS Estocolmo
    fun obtenerMovimientosBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val respuesta = RetrofitClient.instancia.obtenerTransacciones(headerToken)

                    if (respuesta.isSuccessful && respuesta.body() != null) {
                        withContext(Dispatchers.Main) {
                            // Actualizamos la lista síncrona de Compose
                            listaMovimientos.clear()
                            listaMovimientos.addAll(respuesta.body()!!)

                            // Actualizamos también la variable de estado por si la usas en la InicioScreen
                            transaccionesReales = respuesta.body()!!
                        }
                    } else {
                        Log.e("AuthViewModel", "Error en la respuesta del servidor: ${respuesta.code()}")
                    }
                } else {
                    Log.e("AuthViewModel", "No se encontró ningún token en SharedPreferences")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción al conectar con la API de AWS: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarMovimientoenBBDD(
        context: Context,
        monto: Double,
        descripcion: String,
        tipo: String,
        fechaElegida: String,
        categoriaId: Long?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            // 1. Iniciamos carga en el hilo principal
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val headerToken = "Bearer $token"

                    val nuevaTransaccion = SolicitudTransaccion(
                        monto = monto,
                        descripcion = descripcion,
                        tipo = tipo,
                        fecha = fechaElegida,
                        categoriaId = categoriaId
                    )

                    val respuesta = RetrofitClient.instancia.guardarTransaccion(headerToken, nuevaTransaccion)

                    if (respuesta.isSuccessful) {
                        // CORRECCIÓN CLAVE: Pasamos al hilo principal antes de ejecutar el éxito y la navegación
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    } else {
                        Log.e("AuthViewModel", "Error del servidor: ${respuesta.code()} - ${respuesta.errorBody()?.string()}")
                    }
                } else {
                    Log.e("AuthViewModel", "No se encontró el token de sesión.")
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error de red en AWS: ${e.message}")
            } finally {
                // Aseguramos quitar el loading en el hilo principal
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }
    fun guardarObjetivoEnBBDD(
        context: Context,
        nombre: String,
        objetivo: Double,
        fechaLimite: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val headerToken = "Bearer $token"

                    // CORREGIDO: Eliminada la duplicación de "objective ="
                    val nuevaMeta = SolicitudMeta(
                        nombre = nombre,
                        objetivo = objetivo,
                        fechaLimite = fechaLimite
                    )

                    val respuesta = RetrofitClient.instancia.guardarMeta(headerToken, nuevaMeta)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al guardar meta: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }
}