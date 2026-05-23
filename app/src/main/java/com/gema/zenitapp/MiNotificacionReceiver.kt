package com.gema.zenitapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class MiNotificacionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val titulo = intent.getStringExtra("ALERTA_TITULO") ?: "Aviso de ZenitApp"
        val mensaje = intent.getStringExtra("ALERTA_MENSAJE") ?: "Tienes un movimiento programado."

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "zenit_alertas_channel"

        // 1. Crear el canal de notificaciones (Requisito obligatorio desde Android 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                channelId,
                "Recordatorios de Gastos",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para las alertas de presupuestos y gastos fijos de ZenitApp"
            }
            notificationManager.createNotificationChannel(canal)
        }

        // 2. Crear la acción de abrir la app al pulsar la notificación
        // Cambia 'MainActivity' por el nombre de tu actividad principal si se llama distinto
        val intentAbrirApp = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intentAbrirApp,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 3. Construir la notificación visual con tus colores/iconos de diseño
        val notificacion = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_popup_reminder) // Puedes cambiarlo por tu icono de Zenit
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Se borra al pulsarla
            .setContentIntent(pendingIntent)
            .build()

        // 4. Lanzar la notificación al sistema con un ID único
        notificationManager.notify(System.currentTimeMillis().toInt(), notificacion)
    }
}