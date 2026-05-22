package com.gema.zenitapp.api

import com.gema.zenit.models.CategoriaResponse
import com.gema.zenit.models.LoginUsuario
import com.gema.zenit.models.RegistroUsuarios
import com.gema.zenit.models.RespuestaAutenticacion
import com.gema.zenit.models.RespuestaMeta
import com.gema.zenit.models.RespuestaPresupuesto
import com.gema.zenit.models.SolicitudCategoria
import com.gema.zenit.models.SolicitudMeta
import com.gema.zenit.models.SolicitudPresupuesto
import com.gema.zenit.models.SolicitudTransaccion
import com.gema.zenit.models.TransaccionResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ZenitApiService {

    // ==========================================
    // 1. AUTENTICACIÓN (Rutas públicas sin Token)
    // ==========================================

    @POST("/auth/register")
    suspend fun registrar(
        @Body datos: RegistroUsuarios
    ): Response<ResponseBody>

    @POST("/auth/login")
    suspend fun login(
        @Body datos: LoginUsuario
    ): Response<RespuestaAutenticacion>


    // ==========================================
    // 2. TRANSACCIONES / MOVIMIENTOS (Rutas protegidas)
    // ==========================================

    @GET("/transacciones")
    suspend fun obtenerTransacciones(
        @Header("Authorization") token: String
    ): Response<List<TransaccionResponse>>

    @POST("/transacciones")
    suspend fun guardarTransaccion(
        @Header("Authorization") token: String,
        @Body transaccion: SolicitudTransaccion
    ): Response<Any>

    @PUT("/transacciones/{id}")
    suspend fun editarTransaccion(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body transaccion: SolicitudTransaccion
    ): Response<Any>

    // CORRECCIÓN: Nombre cambiado de borrarTransaccion a eliminarTransaccion
    // para que coincida exactamente con lo que busca tu AuthViewModel
    @DELETE("/transacciones/{id}")
    suspend fun eliminarTransaccion(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Any>


    // ==========================================
    // 3. CATEGORÍAS (Rutas protegidas)
    // ==========================================

    @GET("/categorias")
    suspend fun obtenerCategorias(
        @Header("Authorization") token: String
    ): Response<List<CategoriaResponse>>

    @POST("/categorias")
    suspend fun guardarCategoria(
        @Header("Authorization") token: String,
        @Body categoria: SolicitudCategoria
    ): Response<Any>

    @PUT("/categorias/{id}")
    suspend fun editarCategoria(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body categoria: SolicitudCategoria
    ): Response<Any>

    @DELETE("/categorias/{id}")
    suspend fun borrarCategoria(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Any>


    // ==========================================
    // 4. METAS / OBJETIVOS / PRESUPUESTOS (Rutas unificadas de AWS)
    // ==========================================

    @GET("/metas")
    suspend fun obtenerMetas(
        @Header("Authorization") token: String
    ): Response<List<RespuestaMeta>>

    @POST("/metas")
    suspend fun guardarMeta(
        @Header("Authorization") token: String,
        @Body meta: SolicitudMeta
    ): Response<Any>

    @PUT("/metas/{id}")
    suspend fun editarMeta(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body meta: SolicitudMeta
    ): Response<Any>

    @DELETE("/metas/{id}")
    suspend fun borrarMeta(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Any>


    // ==========================================
    // 5. PRESUPUESTOS INDEPENDIENTES (Opcionales)
    // ==========================================
    // Nota: Déjalos solo si tu Ktor en AWS los gestiona en tablas y rutas
    // distintas a las de la sección 4 (/metas).

    @GET("/presupuestos")
    suspend fun obtenerPresupuestos(
        @Header("Authorization") token: String
    ): Response<List<RespuestaPresupuesto>>
    // <-- Cambiado para recibir tu modelo real de salida

    @POST("/presupuestos")
    suspend fun guardarPresupuesto(
        @Header("Authorization") token: String,
        @Body presupuesto: SolicitudPresupuesto
    ): Response<Any>
}

