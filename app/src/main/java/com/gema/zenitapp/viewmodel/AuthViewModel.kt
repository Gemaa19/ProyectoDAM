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
import com.gema.zenit.models.ActualizarNombreRequest
import com.gema.zenitapp.MiNotificacionReceiver
import java.time.LocalDate
import java.time.ZoneId

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

    var usuarioLogueado by mutableStateOf<UsuarioSesion?>(null)
        private set

    var transaccionesReales by mutableStateOf<List<TransaccionResponse>>(listOf())
        private set

    val listaMovimientos = mutableStateListOf<TransaccionResponse>()

    var listaPresupuestos by mutableStateOf<List<RespuestaPresupuesto>>(listOf())
        private set

    var listaMetas by mutableStateOf<List<RespuestaMeta>>(listOf())
        private set

    var listaCategorias by mutableStateOf<List<com.gema.zenit.models.CategoriaResponse>>(listOf())
        private set
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
    fun eliminarCategoriaBBDD(context: Context, idCategoria: Long, onEliminadoOk: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val respuesta = RetrofitClient.instancia.eliminarCategoria(headerToken, idCategoria)

                    withContext(Dispatchers.Main) {
                        if (respuesta.isSuccessful) {
                            Toast.makeText(context, "Categoría eliminada con éxito", Toast.LENGTH_SHORT).show()
                            onEliminadoOk()
                        } else if (respuesta.code() == 409 || respuesta.code() == 500) {
                            Toast.makeText(
                                context,
                                "No puedes eliminar esta categoría porque contiene movimientos u objetivos asociados.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(context, "Error al intentar borrar la categoría", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("ZENIT_DEBUG", "Fallo de red: ${e.message}")
                    Toast.makeText(context, "Error de conexión con el servidor de AWS", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun editarCategoriaEnBBDD(context: Context, idCategoria: Long, nuevoNombre: String, nuevoIcono: String, onEdicionOk: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val solicitud = com.gema.zenit.models.SolicitudCategoria(nombre = nuevoNombre, icono = nuevoIcono)
                    val respuesta = RetrofitClient.instancia.editarCategoria(headerToken, idCategoria, solicitud)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            onEdicionOk()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error de red al actualizar nombre de categoría: ${e.message}")
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

    fun actualizarNombreUsuarioBBDD(context: Context, nuevoNombre: String, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val request = ActualizarNombreRequest(nuevoNombre)

                    val respuesta = RetrofitClient.instancia.actualizarUsername(headerToken, request)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            usuarioLogueado?.let {
                                usuarioLogueado = it.copy(nombre = nuevoNombre)
                            }

                            prefs.edit().putString("user_name", nuevoNombre).apply()

                            onSuccess()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error al actualizar perfil: ${e.message}")
            }
        }
    }

    fun cargarSesionLocal(context: Context) {
        val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token_jwt", null)
        val name = prefs.getString("user_name", null)
        val email = prefs.getString("user_email", null)
        val id = prefs.getLong("user_id", -1L)
        val rol = prefs.getString("user_rol", "USER")

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
    fun obtenerMovimientosBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
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
                        withContext(Dispatchers.Main) {
                            try {
                                onSuccess()
                            } catch (e: Exception) {
                                Log.e("ZENIT_DEBUG", "Error dentro del callback onSuccess de la UI: ${e.message}")
                            }
                        }
                    } else {
                        val codigoError = respuesta.code()
                        val cuerpoError = respuesta.errorBody()?.string() ?: "Error desconocido"
                        Log.e("ZENIT_DEBUG", "Error de AWS ($codigoError): $cuerpoError")

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error en el servidor: $codigoError", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "EXCEPCIÓN de red en AWS: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error de red: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }
    fun eliminarMovimientoBBDD(context: Context, idTransaccion: Long, onSuccess: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
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

    fun guardarMetaEnBBDD(
        context: Context,
        name: String,
        objetivo: Double,
        ahorrado: Double,
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

                    val nuevaMeta = SolicitudMeta(
                        nombre = name,
                        objetivo = objetivo,
                        ahorrado = ahorrado,
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

    fun obtenerObjetivosBBDD(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
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
                val token = prefs.getString("token_jwt", null)

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
            try {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

                val fechaAlerta = LocalDate.parse(fechaMovimiento).minusDays(2)

                val milisegundosAlerta = fechaAlerta.atTime(9, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

                if (milisegundosAlerta <= System.currentTimeMillis()) {
                    Log.d("ZENIT_DEBUG", "Aviso: La notificación calculada ya ha pasado en el tiempo, no se agenda.")
                    return
                }

                val intent = Intent(context, MiNotificacionReceiver::class.java).apply {
                    putExtra("ALERTA_TITULO", titulo)
                    putExtra("ALERTA_MENSAJE", mensaje)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    fechaMovimiento.hashCode(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    milisegundosAlerta,
                    pendingIntent
                )
                Log.d("ZENIT_DEBUG", "Alarma ZenitApp programada con éxito para el milisegundo: $milisegundosAlerta")

            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error de conversión temporal en registrarAlertaNotificacion: ${e.message}")
            }
        }
    }

    fun editarMovimientoEnBBDD(
        context: Context,
        id: Long,
        monto: Double,
        descripcion: String,
        tipo: String,
        fechaElegida: String,
        categoriaId: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val sharedPreferences = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = sharedPreferences.getString("token_jwt", null)

                if (token == null) {
                    Toast.makeText(context, "Error: Sesión expirada", Toast.LENGTH_SHORT).show()
                    isLoading = false
                    return@launch
                }

                val movimientoEditado = SolicitudTransaccion(
                    monto = monto,
                    descripcion = descripcion,
                    tipo = tipo,
                    fecha = fechaElegida,
                    categoriaId = categoriaId
                )

                val respuesta = RetrofitClient.instancia.editarTransaccion(
                    token = "Bearer $token",
                    id = id,
                    transaccion = movimientoEditado
                )

                if (respuesta.isSuccessful) {
                    Log.d("ZENIT_DEBUG", "¡Movimiento actualizado con éxito en AWS!")
                    onSuccess()
                } else {
                    Log.e("ZENIT_DEBUG", "Fallo en el servidor: ${respuesta.code()} - ${respuesta.errorBody()?.string()}")
                    Toast.makeText(context, "No se pudo actualizar el movimiento", Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error de red al editar: ${e.localizedMessage}")
                Toast.makeText(context, "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
            } finally {
                isLoading = false
            }
        }
    }
    fun editarMetaEnBBDD(
        context: Context,
        id: Long,
        name: String,
        objetivo: Double,
        ahorrado: Double,
        fechaLimite: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val datosModificados = SolicitudMeta(name, objetivo, ahorrado, fechaLimite)
                    val respuesta = RetrofitClient.instancia.editarMeta("Bearer $token", id, datosModificados)
                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                }
            } catch (e: Exception) { Log.e("ZENIT_DEBUG", "Error al editar meta: ${e.message}") }
        }
    }

    fun editarPresupuestoEnBBDD(
        context: Context,
        id: Long,
        montoLimite: Double,
        categoriaId: Long,
        mes: Int,
        anio: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val datosModificados = SolicitudPresupuesto(montoLimite, categoriaId, mes, anio)
                    val respuesta = RetrofitClient.instancia.editarPresupuesto("Bearer $token", id, datosModificados)
                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                }
            } catch (e: Exception) { Log.e("ZENIT_DEBUG", "Error al editar presupuesto: ${e.message}") }
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
                    val respuesta = RetrofitClient.instancia.borrarPresupuesto(headerToken, idPresupuesto)

                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) {
                            obtenerObjetivosBBDD(context)
                            onSuccess()
                        }
                    } else {
                        Log.e("AuthViewModel", "El servidor Ktor rechazó el borrado: ${respuesta.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error de red al borrar presupuesto: ${e.message}")
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

    fun guardarRecordatorioFuturoBBDD(
        context: Context,
        monto: Double,
        descripcion: String,
        tipo: String,
        fechaElegida: String,
        categoriaId: Long,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) { isLoading = true }
            try {
                val prefs = context.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
                val token = prefs.getString("token_jwt", null)

                if (token != null) {
                    val headerToken = "Bearer $token"
                    val nuevoRecordatorio = SolicitudTransaccion(
                        monto = monto,
                        descripcion = descripcion,
                        tipo = tipo,
                        fecha = fechaElegida,
                        categoriaId = categoriaId
                    )

                    val respuesta = RetrofitClient.instancia.guardarRecordatorioFuturo(headerToken, nuevoRecordatorio)
                    if (respuesta.isSuccessful) {
                        withContext(Dispatchers.Main) { onSuccess() }
                    }
                }
            } catch (e: Exception) {
                Log.e("ZENIT_DEBUG", "Error al guardar transacción futura en AWS: ${e.message}")
            } finally {
                withContext(Dispatchers.Main) { isLoading = false }
            }
        }
    }
}