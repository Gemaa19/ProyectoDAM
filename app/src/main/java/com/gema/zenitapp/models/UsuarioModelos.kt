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
    val username: String
)