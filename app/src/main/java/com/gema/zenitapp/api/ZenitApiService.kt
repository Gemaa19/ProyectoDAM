package com.gema.zenitapp.api

import com.gema.zenitapp.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ZenitApiService {

    @POST("/auth/register")
    suspend fun registrar(
        @Body datos: RegistroUsuarios
    ): Response<RespuestaAutenticacion>

    @POST("/auth/login")
    suspend fun login(
        @Body datos: LoginUsuario
    ): Response<RespuestaAutenticacion>
}