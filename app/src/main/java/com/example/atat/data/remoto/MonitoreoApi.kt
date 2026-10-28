package com.example.atat.data.remoto

import com.example.atat.data.model.Galpon
import com.example.atat.util.Fechas
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

class MonitoreoApi {

    private class Catalogo(
        val id: String,
        val nombre: String,
        val granja: String,
        val ubicacion: String,
        val latitud: Double,
        val longitud: Double,
        val temperaturaBase: Double,
        val tendencia: Double,
        val humedadBase: Double
    )

    private val catalogos = listOf(
        Catalogo("g1", "Galpon A - Pollos broiler", "Granja Melipilla Norte", "Sector poniente", -33.3620, -71.2140, 27.0, -0.2, 58.0),
        Catalogo("g2", "Galpon B - Pavos", "Granja Melipilla Norte", "Sector oriente", -33.3635, -71.2105, 26.5, -0.1, 55.0),
        Catalogo("g3", "Galpon C - Reproductoras", "Granja El Cobre", "Sector poniente", -33.3580, -71.2010, 29.0, 0.9, 61.0),
        Catalogo("g4", "Galpon D - Crias", "Granja El Cobre", "Sector oriente", -33.3565, -71.1980, 25.5, -0.2, 52.0),
        Catalogo("g5", "Galpon E - Engorde", "Granja San Pedro", "Planta sur", -33.3700, -71.2200, 28.5, 0.4, 64.0),
        Catalogo("g6", "Galpon F - Almacen climatizado", "Granja San Pedro", "Planta norte", -33.3685, -71.2165, 22.0, -0.1, 48.0)
    )

    private val aleatorio = Random(20260831)
    private val ultimaLectura = mutableMapOf<String, Double>()

    suspend fun consultarGalpones(): List<Galpon> {
        delay(1500)
        val galpones = mutableListOf<Galpon>()
        for (catalogo in catalogos) {
            val anterior = ultimaLectura[catalogo.id]
            var temperatura = catalogo.temperaturaBase
            if (anterior != null) {
                temperatura = anterior + catalogo.tendencia + aleatorio.nextDouble(-0.4, 0.4)
            } else {
                temperatura = catalogo.temperaturaBase + aleatorio.nextDouble(-0.8, 0.8)
            }
            temperatura = max(18.0, minOf(temperatura, 39.0))
            ultimaLectura[catalogo.id] = temperatura
            val humedad = catalogo.humedadBase + aleatorio.nextDouble(-3.0, 3.0)
            galpones.add(
                Galpon(
                    id = catalogo.id,
                    nombre = catalogo.nombre,
                    granja = catalogo.granja,
                    ubicacion = catalogo.ubicacion,
                    latitud = catalogo.latitud,
                    longitud = catalogo.longitud,
                    temperatura = temperatura,
                    humedad = humedad,
                    umbralAdvertencia = 30.0,
                    umbralCritico = 33.0,
                    fechaMedicion = Fechas.ahoraIso()
                )
            )
        }
        return galpones
    }
}
