package com.gema.zenitapp.api

import com.gema.zenit.models.CategoriaResponse
import com.gema.zenit.models.LoginUsuario
import com.gema.zenit.models.RegistroUsuarios
import com.gema.zenit.models.RespuestaAutenticacion
import com.gema.zenit.models.RespuestaMeta
import com.gema.zenit.models.SolicitudCategoria
import com.gema.zenit.models.SolicitudMeta
import com.gema.zenit.models.SolicitudPresupuesto
import com.gema.zenit.models.SolicitudTransaccion
import com.gema.zenit.models.TransaccionResponse
import com.gema.zenitapp.models.*
import retrofit2.Response
import retrofit2.http.*

interface ZenitApiService {

    // ==========================================
    // 1. AUTENTICACIÓN (Rutas públicas sin Token)
    // ==========================================

    @POST("/auth/register")
    suspend fun registrar(
        @Body datos: RegistroUsuarios
    ): Response<RespuestaAutenticacion>

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

    @DELETE("/transacciones/{id}")
    suspend fun borrarTransaccion(
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
    // 4. METAS / OBJETIVOS (Rutas protegidas)
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
    // 5. PRESUPUESTOS (Rutas protegidas)
    // ==========================================

    @GET("/presupuestos")
    suspend fun obtenerPresupuestos(
        @Header("Authorization") token: String
    ): Response<List<SolicitudPresupuesto>> // Ajusta a tu modelo de respuesta si tienes uno específico

    @POST("/presupuestos")
    suspend fun guardarPresupuesto(
        @Header("Authorization") token: String,
        @Body presupuesto: SolicitudPresupuesto
    ): Response<Any>


    // ==========================================
    // 6. ESTADÍSTICAS / RESUMEN (Rutas protegidas)
    // ==========================================

    @GET("/stats/resumen")
    suspend fun obtenerResumenEstadisticas(
        @Header("Authorization") token: String
    ): Response<ResumenEstadisticasResponse> // Asegúrate de crear este modelo en Android para tus tarjetas de saldo
}