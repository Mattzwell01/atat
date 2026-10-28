package com.example.atat.data.model

import kotlinx.serialization.Serializable

enum class EstadoGalpon {
    NORMAL,
    ADVERTENCIA,
    CRITICO
}

enum class Perfil {
    OPERARIO,
    SUPERVISOR,
    JEFATURA
}

@Serializable
data class Galpon(
    val id: String,
    val nombre: String,
    val granja: String,
    val ubicacion: String,
    val latitud: Double,
    val longitud: Double,
    val temperatura: Double,
    val humedad: Double,
    val umbralAdvertencia: Double,
    val umbralCritico: Double,
    val fechaMedicion: String
) {
    fun estado(): EstadoGalpon {
        if (temperatura >= umbralCritico) {
            return EstadoGalpon.CRITICO
        }
        if (temperatura >= umbralAdvertencia) {
            return EstadoGalpon.ADVERTENCIA
        }
        return EstadoGalpon.NORMAL
    }
}

@Serializable
data class Medicion(
    val id: String,
    val galponId: String,
    val temperatura: Double,
    val humedad: Double,
    val fechaMedicion: String
)

@Serializable
data class Alerta(
    val id: String,
    val galponId: String,
    val galponNombre: String,
    val tipo: String,
    val mensaje: String,
    val fechaAlerta: String,
    val activa: Boolean,
    val vista: Boolean
)

@Serializable
data class Evento(
    val id: String,
    val galponId: String,
    val galponNombre: String,
    val tipo: String,
    val descripcion: String,
    val responsable: String,
    val temperatura: Double,
    val fechaRegistro: String
)

data class Resumen(
    val totalGalpones: Int = 0,
    val normales: Int = 0,
    val advertencias: Int = 0,
    val criticos: Int = 0,
    val alertasActivas: Int = 0,
    val temperaturaPromedio: Double = 0.0
)
