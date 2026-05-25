package com.gema.zenit.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoriaResponse(
    val id: Long,
    val nombre: String,
    val icono: String?,
    val usuarioId: Long? = null
)

@Serializable
data class SolicitudCategoria(
    val nombre: String,
    val icono: String?
)