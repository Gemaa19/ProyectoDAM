package com.gema.zenitapp.models

import kotlinx.serialization.Serializable

@Serializable
data class CategoriaResponse(
    val id: Long,
    val nombre: String,
    val icono: String? // Aquí guardaremos un nombre de icono para Android (ej: "ic_food")
)

@Serializable
data class SolicitudCategoria(
    val nombre: String,
    val icono: String?
)