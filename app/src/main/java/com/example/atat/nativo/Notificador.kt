package com.example.atat.nativo

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.atat.MainActivity
import com.example.atat.R
import com.example.atat.data.model.Alerta

class Notificador(private val contexto: Context) {

    private val canal = "alertas_criticas"

    fun crearCanal() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return
        }
        val gestor = contexto.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val canalNuevo = NotificationChannel(
            canal,
            "Alertas criticas de galpones",
            NotificationManager.IMPORTANCE_HIGH
        )
        canalNuevo.description = "Avisos cuando un galpon supera el limite critico de temperatura"
        gestor.createNotificationChannel(canalNuevo)
    }

    fun permisoConcedido(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return true
        }
        val permiso = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.POST_NOTIFICATIONS
        )
        return permiso == PackageManager.PERMISSION_GRANTED
    }

    fun avisar(alerta: Alerta) {
        if (!permisoConcedido()) {
            return
        }
        val intencion = Intent(contexto, MainActivity::class.java)
        val pendiente = PendingIntent.getActivity(
            contexto,
            0,
            intencion,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notificacion = NotificationCompat.Builder(contexto, canal)
            .setSmallIcon(R.drawable.ic_notificacion)
            .setContentTitle("Alerta critica - " + alerta.galponNombre)
            .setContentText(alerta.mensaje)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendiente)
            .build()
        NotificationManagerCompat.from(contexto).notify(alerta.id.hashCode(), notificacion)
    }
}
