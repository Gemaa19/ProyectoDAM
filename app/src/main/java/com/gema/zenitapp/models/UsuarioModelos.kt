package com.gema.zenit.models

import kotlinx.serialization.Serializable

@Serializable
data class RegistroUsuarios(
    val username: String,
    val email: String,
    val password: String
)

@Serializable
data class LoginUsuario(
    val email: String,
    val password: String
)

@Serializable
data class RespuestaAutenticacion(
    val token: String,
    val id: Long,           // <-- AÑADIDO: Clave primaria del usuario
    val username: String,
    val email: String,      // <-- AÑADIDO: Para pintarlo en el menú lateral
    val rol: String? = "USER" // <-- AÑADIDO: Para controlar si es ADMIN o USER más adelante
)