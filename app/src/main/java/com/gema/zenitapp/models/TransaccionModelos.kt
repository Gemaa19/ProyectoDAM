package com.gema.zenitapp.models

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudTransaccion(
    val monto: Double,
    val descripcion: String?,
    val tipo: String, // "INGRESO" o "GASTO"
    val categoriaId: Long?
)

@Serializable
data class TransaccionResponse(
    val id: Long,
    val monto: Double,
    val descripcion: String?,
    val tipo: String,
    val fecha: String, // La mandamos como String para facilitar la lectura en Android
    val categoriaId: Long?
)