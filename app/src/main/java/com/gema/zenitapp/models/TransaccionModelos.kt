package com.gema.zenit.models

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudTransaccion(
    val monto: Double,
    val descripcion: String?,
    val tipo: String,
    val fecha: String,
    val categoriaId: Long?
)

@Serializable
data class TransaccionResponse(
    val id: Long,
    val monto: Double,
    val descripcion: String?,
    val tipo: String,
    val fecha: String,
    val categoriaId: Long?
)