package com.gema.zenitapp.models

import com.google.gson.annotations.SerializedName
data class ResumenEstadisticasResponse(
    @SerializedName("saldoTotal") val saldoTotal: Double,
    @SerializedName("ingresosMes") val ingresosMes: Double,
    @SerializedName("gastosMes") val gastosMes: Double,
    @SerializedName("porcentajeVariacion") val porcentajeVariacion: Double
)