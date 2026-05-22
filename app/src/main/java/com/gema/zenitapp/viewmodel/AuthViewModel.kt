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
import com.gema.zenit.models.RespuestaMeta
import com.gema.zenit.models.RespuestaPresupuesto
import com.gema.zenit.models.SolicitudMeta
import com.gema.zenit.models.SolicitudTransaccion
import com.gema.zenit.models.TransaccionResponse

// Modelo local auxiliar para guardar la sesión activa en el Frontend
data class UsuarioSesion(
    val id: Long,
    val nombre: String,
    val email: String,
    val rol: String
)

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    // AÑADIDO: Guardará la información del perfil para que la lea HamburguesaScreen
    var usuarioLogueado by mutableStateOf<UsuarioSesion?>(null)
        private set

    var transaccionesReales by mutableStateOf<List<TransaccionResponse>>(listOf())
        private set

    val listaMovimientos = mutableStateListOf<TransaccionResponse>()

    var listaPresupuestos by mutableStateOf<List<RespuestaPresupuesto>>(listOf())
        private set

    var listaMetas by mutableStateOf<List<RespuestaMeta>>(listOf())
        private set

    // REGISTRO DE USUARIOS
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
                Log.e("ZENIT_DEBUG", "Error real en el registro: ${e.localizedMessage}", e)
                withContext(Dispatchers.Main) {
                    errorMessage = "Error al procesar el registro en el servidor."
                    onResult(false)
                }
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // LÓGICA DE LOGIN (CORREGIDA: Ahora guarda los datos reales de AWS)
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
                    val body = respuesta.body()!!
                    val tokenRecibido = body.token

                    val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("auth_token", tokenRecibido).apply()

                    withContext(Dispatchers.Main) {
                        // Mapeamos los datos reales que ya vienen en la RespuestaAutenticacion
                        usuarioLogueado = UsuarioSesion(
                            id = body.id,
                            nombre = body.username,
                            email = body.email,
                            rol = body.rol ?: "USER"
                        )
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

    // TRAER MOVIMIENTOS DESDE AWS ESTOCOLMO
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
                            val todosLosMovimientos = respuesta.body()!!.sortedByDescending { it.fecha }
                            listaMovimientos.clear()
                            listaMovimientos.addAll(todosLosMovimientos)
                            transaccionesReales = todosLosMovimientos.take(10)
                        }
                    } else {
                        Log.e("AuthViewModel", "Error en la respuesta del servidor: ${respuesta.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción al conectar con la API de AWS: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // GUARDAR MOVIMIENTO
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
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "EXCEPCIÓN de red al conectar con AWS: ${e.message}", e)
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // ELIMINAR MOVIMIENTO
    fun eliminarMovimientoBBDD(context: Context, idTransaccion: Long, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val respuesta = RetrofitClient.instancia.eliminarTransaccion(headerToken, idTransaccion)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            obtenerMovimientosBBDD(context)
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al borrar transacción en AWS: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // GUARDAR METAS DE AHORRO
    fun guardarMetaEnBBDD(
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

    // TRAER OBJETIVOS (METAS Y PRESUPUESTOS POR SEPARADO)
    fun obtenerObjetivosBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val resPresupuestos = RetrofitClient.instancia.obtenerPresupuestos(headerToken)
                    val resMetas = RetrofitClient.instancia.obtenerMetas(headerToken)

                    withContext(Dispatchers.Main) {
                        if (resPresupuestos.isSuccessful && resPresupuestos.body() != null) {
                            listaPresupuestos = resPresupuestos.body()!!
                        }
                        if (resMetas.isSuccessful && resMetas.body() != null) {
                            listaMetas = resMetas.body()!!
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error de conexión en objetivos: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // AÑADIDO: Resetea el estado local del ViewModel al cerrar sesión
    fun logout() {
        usuarioLogueado = null
        listaMovimientos.clear()
        transaccionesReales = listOf()
        listaPresupuestos = listOf()
        listaMetas = listOf()
    }
    fun guardarPresupuestoEnBBDD(
        context: Context,
        montoLimite: Double,
        categoriaId: Long,
        mes: Int,
        anio: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("auth_token", null)

                if (token != null) {
                    val headerToken = "Bearer $token"

                    // Construimos el modelo exacto que espera tu interfaz ZenitApiService
                    val nuevoPresupuesto = com.gema.zenit.models.SolicitudPresupuesto(
                        montoLimite = montoLimite,
                        categoriaId = categoriaId,
                        mes = mes,
                        anio = anio
                    )

                    val respuesta = RetrofitClient.instancia.guardarPresupuesto(headerToken, nuevoPresupuesto)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    } else {
                        Log.e("AuthViewModel", "Error al guardar presupuesto: ${respuesta.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción de red en presupuestos: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }
}