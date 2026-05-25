package com.gema.zenitapp.api

import com.gema.zenit.models.*
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface ZenitApiService {
    @POST("/auth/register")
    suspend fun registrar(
        @Body datos: RegistroUsuarios
    ): Response<ResponseBody>

    @POST("/auth/login")
    suspend fun login(
        @Body datos: LoginUsuario
    ): Response<RespuestaAutenticacion>

    @PUT("/usuarios/actualizar-nombre")
    suspend fun actualizarUsername(
        @Header("Authorization") token: String,
        @Body nuevoNombre: ActualizarNombreRequest
    ): Response<Any>

    @GET("/transacciones")
    suspend fun obtenerTransacciones(
        @Header("Authorization") token: String
    ): Response<List<TransaccionResponse>>

    @POST("/transacciones")
    suspend fun guardarTransaccion(
        @Header("Authorization") token: String,
        @Body transaccion: SolicitudTransaccion
    ): Response<Any>

    @PUT("transacciones/{id}")
    suspend fun editarTransaccion(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body transaccion: SolicitudTransaccion
    ): Response<Void>

    @DELETE("/transacciones/{id}")
    suspend fun eliminarTransaccion(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Any>


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

    // 💡 SOLUCIÓN: Cambiado 'borrarCategoria' por 'eliminarCategoria'
    @DELETE("/categorias/{id}")
    suspend fun eliminarCategoria(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Any>

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

    @GET("/presupuestos")
    suspend fun obtenerPresupuestos(
        @Header("Authorization") token: String
    ): Response<List<RespuestaPresupuesto>>

    @POST("/presupuestos")
    suspend fun guardarPresupuesto(
        @Header("Authorization") token: String,
        @Body presupuesto: SolicitudPresupuesto
    ): Response<Any>

    @PUT("/presupuestos/{id}")
    suspend fun editarPresupuesto(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body presupuesto: SolicitudPresupuesto
    ): Response<Any>

    // 💡 EXTRA: Faltaba el DELETE de presupuestos mapeado hacia Ktor
    @DELETE("/presupuestos/{id}")
    suspend fun borrarPresupuesto(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Any>

    @POST("/transacciones/recordatorios")
    suspend fun guardarRecordatorioFuturo(
        @Header("Authorization") token: String,
        @Body recordatorio: SolicitudTransaccion
    ): Response<Any>

    @POST("/transacciones/procesar-pendientes")
    suspend fun procesarRecordatoriosDelDia(
        @Header("Authorization") token: String
    ): Response<Any>
}