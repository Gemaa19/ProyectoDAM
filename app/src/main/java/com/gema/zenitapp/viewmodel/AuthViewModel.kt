package com.gema.zenitapp.viewmodel

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
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
import com.gema.zenit.models.SolicitudPresupuesto
import com.gema.zenit.models.SolicitudTransaccion
import com.gema.zenit.models.TransaccionResponse
import com.gema.zenitapp.api.ZenitApiService
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import com.gema.zenitapp.MiNotificacionReceiver
import java.time.LocalDate
import java.time.ZoneId

// Modelo local auxiliar para guardar la sesión activa en el Frontend
data class UsuarioSesion(
    val id: Long,
    var nombre: String,
    val email: String,
    val rol: String
)

class AuthViewModel : ViewModel() {

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf("")
        private set

    // AÑADIDO: Guardará la información del perfil para que la lea HamburguesaScreen
    // Dentro de tu AuthViewModel.kt, revisa que la propiedad esté declarada así:
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
    // 💡 AÑADE ESTO EN TU AUTHVIEWMODEL.KT
    var listaCategorias by mutableStateOf<List<com.gema.zenit.models.CategoriaResponse>>(listOf())
        private set

    // Método para descargar todas las categorías (Globales + Propias) de AWS
    fun obtenerCategoriasBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val respuesta = RetrofitClient.instancia.obtenerCategorias("Bearer $token")
                    if (respuesta.isSuccessful && respuesta.body() != null) {
                        withContext(Dispatchers.Main) {
                            listaCategorias = respuesta.body()!!
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error al recuperar categorías: ${e.message}")
            }
        }
    }

    // Método para crear una nueva categoría desde la pestaña flotante de la Hamburguesa
    fun crearCategoriaEnBBDD(context: Context, nombreCat: String, iconoCat: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val nuevaCat = com.gema.zenit.models.SolicitudCategoria(nombre = nombreCat, icono = iconoCat)
                    val respuesta = RetrofitClient.instancia.guardarCategoria("Bearer $token", nuevaCat)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error de red al crear categoría: ${e.message}")
            }
        }
    }
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

                    // Guardamos TODO en SharedPreferences para que no se borre al reiniciar
                    val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                    prefs.edit().apply {
                        putString("token_jwt", tokenRecibido)
                        putLong("user_id", body.id)
                        putString("user_name", body.username)
                        putString("user_email", body.email)
                        putString("user_rol", body.rol ?: "USER")
                        apply()
                    }

                    withContext(Dispatchers.Main) {
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

    fun actualizarNombreEnServidor(context: Context, nuevoNombre: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    // Enviamos la petición HTTP PUT a la instancia de AWS
                    RetrofitClient.instancia.actualizarNombre(headerToken, nuevoNombre)
                    Log.d("ZENIT_DEBUG", "Nombre sincronizado con la BBDD de AWS")
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error de red al actualizar nombre: ${e.message}")
            }
        }
    }

    // 💡 AÑADE ESTO DENTRO DE TU AUTHVIEWMODEL.KT
    fun cargarSesionLocal(context: Context) {
        val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token_jwt", null)
        val name = prefs.getString("user_name", null)
        val email = prefs.getString("user_email", null)
        val id = prefs.getLong("user_id", -1L)
        val rol = prefs.getString("user_rol", "USER")

        // Si hay un token y datos guardados, restauramos el usuarioLogueado inmediatamente
        if (token != null && name != null && email != null && id != -1L) {
            usuarioLogueado = UsuarioSesion(
                id = id,
                nombre = name,
                email = email,
                rol = rol ?: "USER"
            )
            Log.d("ZENIT_DEBUG", "Sesión recuperada localmente de: $name")
        }
    }
    // TRAER MOVIMIENTOS DESDE AWS ESTOCOLMO
    fun obtenerMovimientosBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                // 💡 CORRECCIÓN: Cambiado "auth_token" por "token_jwt"
                val token = prefs.getString("token_jwt", null)

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
                // 💡 CORRECCIÓN: Cambiado "auth_token" por "token_jwt"
                val token = prefs.getString("token_jwt", null)

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
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "EXCEPCIÓN de red en AWS: ${e.message}")
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
                // 💡 CORRECCIÓN: Cambiado "auth_token" por "token_jwt"
                val token = prefs.getString("token_jwt", null)

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
                Log.e("AuthViewModel", "Error al borrar transacción: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    // GUARDAR METAS DE AHORRO
    // 💡 CORRECCIÓN EN TU AUTHVIEWMODEL.KT: Añadimos el parámetro 'ahorrado' para AWS
    fun guardarMetaEnBBDD(
        context: Context,
        name: String,
        objetivo: Double,
        ahorrado: Double, // 💡 NUEVO: Recibe la cantidad acumulada inicial desde la pantalla
        fechaLimite: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"

                    // 💡 ACTUALIZACIÓN: Tu DTO 'SolicitudMeta' ahora recibe el valor de ahorrado
                    val nuevaMeta = SolicitudMeta(
                        nombre = name,
                        objetivo = objetivo,
                        ahorrado = ahorrado, // Asegúrate de que tu data class SolicitudMeta tenga este campo
                        fechaLimite = fechaLimite
                    )

                    val respuesta = RetrofitClient.instancia.guardarMeta(headerToken, nuevaMeta)
                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) { onSuccess() }
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
    // TRAER OBJETIVOS (Metas y Presupuestos) corregido con la clave real del Token
    fun obtenerObjetivosBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                // 💡 CORRECCIÓN: Cambiado "auth_token" por "token_jwt"
                val token = prefs.getString("token_jwt", null)

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
                val token = prefs.getString("token_jwt", null) // 💡 CORRECCIÓN

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val nuevoPresupuesto = SolicitudPresupuesto(
                        montoLimite = montoLimite,
                        categoriaId = categoriaId,
                        mes = mes,
                        anio = anio
                    )

                    val respuesta = RetrofitClient.instancia.guardarPresupuesto(headerToken, nuevoPresupuesto)
                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción de red en presupuestos: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }



    fun registrarAlertaNotificacion(
        context: Context,
        titulo: String,
        mensaje: String,
        fechaMovimiento: String
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Calculamos la fecha del movimiento menos 2 días
            val fechaAlerta = LocalDate.parse(fechaMovimiento).minusDays(2)
            val milisegundosAlerta = fechaAlerta.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            // Intención que despertará un receptor de eventos personalizado (BroadcastReceiver)
            val intent = Intent(context, MiNotificacionReceiver::class.java).apply {
                putExtra("ALERTA_TITULO", titulo)
                putExtra("ALERTA_MENSAJE", mensaje)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                fechaMovimiento.hashCode(), // ID único para que no se pisen
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // Programamos de forma exacta en el reloj del sistema operativo
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                milisegundosAlerta,
                pendingIntent
            )
        }
    }

    // 💡 NUEVO: Método para actualizar un movimiento existente en AWS
    fun editarMovimientoEnBBDD(
        context: Context,
        id: Long,                  // ID del movimiento que vamos a modificar
        monto: Double,
        descripcion: String,
        tipo: String,
        fechaElegida: String,      // YYYY-MM-DD del calendario
        categoriaId: Long,
        onSuccess: () -> Unit      // Callback para volver a InicioScreen y refrescar
    ) {
        viewModelScope.launch {
            isLoading = true // Activa el CircularProgressIndicator del botón
            try {
                // 1. Recuperar el token JWT que guardaste en el Login (ej. en SharedPreferences o DataStore)
                val sharedPreferences = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token_jwt", null)

                if (token == null) {
                    Toast.makeText(context, "Error: Sesión expirada", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    return@launch
                }

                // 2. Preparar el objeto con los datos modificados (Tu DTO de la app)
                val movimientoEditado = SolicitudTransaccion( // Ajusta el nombre a tu clase modelo (ej. SolicitudMovimiento o TransaccionRequest)
                    monto = monto,
                    descripcion = descripcion,
                    tipo = tipo,
                    fecha = fechaElegida,
                    categoriaId = categoriaId
                )

                // 3. Lanzar la petición HTTP PUT al backend de AWS
                // NOTA: Ajusta "tuApiRetrofit" al nombre que tenga tu cliente de red en el ViewModel
                val respuesta = RetrofitClient.instancia.editarTransaccion(
                    token = "Bearer $token",
                    id = id,
                    transaccion = movimientoEditado
                )

                if (respuesta.isSuccessful) {
                    Log.d("ZENIT_DEBUG", "¡Movimiento actualizado con éxito en AWS!")
                    onSuccess() // Ejecuta el refresco de pantalla
                } else {
                    Log.e("ZENIT_DEBUG", "Fallo en el servidor: ${respuesta.code()} - ${respuesta.errorBody()?.string()}")
                    Toast.makeText(context, "No se pudo actualizar el movimiento", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error de red al editar: ${e.localizedMessage}")
                Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false // Apaga el cargando del botón
            }
        }
    }

    fun eliminarPresupuestoBBDD(context: Context, idPresupuesto: Long, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    // Recuerda que en tu ZenitApiService el endpoint se llama borrarMeta/borrarPresupuesto (ajusta el nombre si fuera necesario)
                    val respuesta = RetrofitClient.instancia.borrarMeta(headerToken, idPresupuesto) // Si comparten pasarela o implementa tu endpoint en ApiService

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            obtenerObjetivosBBDD(context)
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al borrar presupuesto: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }

    fun eliminarMetaBBDD(context: Context, idMeta: Long, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val respuesta = RetrofitClient.instancia.borrarMeta(headerToken, idMeta)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            obtenerObjetivosBBDD(context)
                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al borrar meta: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }
}