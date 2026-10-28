package com.example.atat.nativo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.withTimeoutOrNull

class SensorTemperatura(private val contexto: Context) {

    private val gestor: SensorManager =
        contexto.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    fun disponible(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return false
        }
        return gestor.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null
    }

    suspend fun leer(): Double? {
        if (!disponible()) {
            return null
        }
        val sensor = gestor.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        if (sensor == null) {
            return null
        }
        val espera = CompletableDeferred<Double>()
        val escuchador = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                espera.complete(event.values[0].toDouble())
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
            }
        }
        gestor.registerListener(escuchador, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        try {
            return withTimeoutOrNull(2500) { espera.await() }
        } finally {
            gestor.unregisterListener(escuchador)
        }
    }
}
