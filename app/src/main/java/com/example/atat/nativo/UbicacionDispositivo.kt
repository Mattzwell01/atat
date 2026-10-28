package com.example.atat.nativo

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

data class Posicion(
    val latitud: Double,
    val longitud: Double
)

fun distanciaEntre(actual: Posicion, objetivo: Posicion): Double {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        actual.latitud,
        actual.longitud,
        objetivo.latitud,
        objetivo.longitud,
        resultado
    )
    return resultado[0] / 1000.0
}

class UbicacionDispositivo(private val contexto: Context) {

    private val gestor: LocationManager =
        contexto.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    fun permisoConcedido(): Boolean {
        val permiso = ContextCompat.checkSelfPermission(
            contexto,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return permiso == PackageManager.PERMISSION_GRANTED
    }

    fun obtenerPosicion(): Posicion? {
        if (!permisoConcedido()) {
            return null
        }
        var mejor: Location? = null
        val proveedores = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        for (proveedor in proveedores) {
            try {
                val ubicacion = gestor.getLastKnownLocation(proveedor)
                if (ubicacion != null) {
                    if (mejor == null || ubicacion.time > mejor.time) {
                        mejor = ubicacion
                    }
                }
            } catch (error: Exception) {
            }
        }
        if (mejor == null) {
            return null
        }
        return Posicion(mejor.latitude, mejor.longitude)
    }

    fun distanciaKm(objetivo: Posicion): Double {
        val actual = obtenerPosicion() ?: return 0.0
        val resultado = FloatArray(1)
        Location.distanceBetween(
            actual.latitud,
            actual.longitud,
            objetivo.latitud,
            objetivo.longitud,
            resultado
        )
        return resultado[0] / 1000.0
    }
}
