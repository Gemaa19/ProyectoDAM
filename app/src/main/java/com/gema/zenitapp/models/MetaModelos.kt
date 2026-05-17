package com.gema.zenit.models

import kotlinx.serialization.Serializable

@Serializable
data class SolicitudMeta(
    val nombre: String,
    val objetivo: Double,
    val fechaLimite: String?
)

@Serializable
data class RespuestaMeta(
    val id: Long,
    val nombre: String,
    val objetivo: Double,
    val ahorrado: Double,
    val progreso: Double, // Porcentaje (0-100)
    val completada: Boolean
)