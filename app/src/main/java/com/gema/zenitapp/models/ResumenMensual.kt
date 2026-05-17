package com.gema.zenit.models

import kotlinx.serialization.Serializable

@Serializable
data class ResumenMensual(
    val totalIngresos: Double,
    val totalGastos: Double,
    val balance: Double,
    val mes: String
)