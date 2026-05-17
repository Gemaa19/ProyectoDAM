package com.gema.zenit.models

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudPresupuesto(
    val montoLimite: Double,
    val categoriaId: Long,
    val mes: Int,
    val anio: Int
)

@Serializable
data class RespuestaPresupuesto(
    val id: Long,
    val montoLimite: Double,
    val categoriaId: Long,
    val nombreCategoria: String,
    val mes: Int,
    val anio: Int
)