package com.gema.zenitapp.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.gema.zenitapp.api.RetrofitClient

class TransaccionesProgramadasWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            val prefs = applicationContext.getSharedPreferences("zenit_prefs", Context.MODE_PRIVATE)
            val token = prefs.getString("token_jwt", null)

            if (token != null) {
                val headerToken = "Bearer $token"
                val respuesta = RetrofitClient.instancia.procesarRecordatoriosDelDia(headerToken)

                if (respuesta.isSuccessful) {
                    Log.d("ZENIT_WORKER", "Sincronización de transacciones futuras completada con éxito.")
                    Result.success()
                } else {
                    Result.retry()
                }
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("ZENIT_WORKER", "Error en la ejecución del Worker: ${e.message}")
            Result.failure()
        }
    }
}