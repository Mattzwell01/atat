package com.example.atat.nativo

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class Vibrador(private val contexto: Context) {

    @Suppress("DEPRECATION")
    fun vibrar() {
        val vibrador = obtenerVibrador()
        if (!vibrador.hasVibrator()) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrador.vibrate(VibrationEffect.createOneShot(400, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrador.vibrate(400)
        }
    }

    @Suppress("DEPRECATION")
    private fun obtenerVibrador(): Vibrator {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val gestor = contexto.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            return gestor.defaultVibrator
        }
        return contexto.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }
}
