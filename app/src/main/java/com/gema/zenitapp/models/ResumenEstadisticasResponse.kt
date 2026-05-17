package com.gema.zenitapp.models

import com.google.gson.annotations.SerializedName
// O si usas Kotlinx Serialization: import kotlinx.serialization.Serializable

// Si usas Gson (lo habitual con Retrofit), se estructura así:
data class ResumenEstadisticasResponse(
    @SerializedName("saldoTotal") val saldoTotal: Double,
    @SerializedName("ingresosMes") val ingresosMes: Double,
    @SerializedName("gastosMes") val gastosMes: Double,
    @SerializedName("porcentajeVariacion") val porcentajeVariacion: Double // Para el "+5.2% vs mes anterior"
)