package com.gema.zenitapp.models

import kotlinx.serialization.Serializable

@Serializable
data class ResumenMensual(
    val totalIngresos: Double,
    val totalGastos: Double,
    val balance: Double,
    val mes: String
)