package com.example.atat.data

import android.content.Context
import com.example.atat.data.local.AlmacenLocal
import com.example.atat.data.model.Alerta
import com.example.atat.data.model.Evento
import com.example.atat.data.model.Galpon
import com.example.atat.data.model.Medicion
import com.example.atat.data.model.EstadoGalpon
import com.example.atat.data.remoto.MonitoreoApi
import com.example.atat.nativo.Notificador
import com.example.atat.nativo.Vibrador
import com.example.atat.util.Fechas
import com.example.atat.util.formatear
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonitoreoRepository(contexto: Context) {

    companion object {
        private var instancia: MonitoreoRepository? = null

        fun obtener(contexto: Context): MonitoreoRepository {
            if (instancia == null) {
                instancia = MonitoreoRepository(contexto.applicationContext)
            }
            return instancia!!
        }
    }

    private val almacen = AlmacenLocal(contexto)
    private val api = MonitoreoApi()
    private val notificador = Notificador(contexto)
    private val vibrador = Vibrador(contexto)

    private val _galpones = MutableStateFlow<List<Galpon>>(emptyList())
    val galpones: StateFlow<List<Galpon>> = _galpones.asStateFlow()

    private val _mediciones = MutableStateFlow<List<Medicion>>(emptyList())
    val mediciones: StateFlow<List<Medicion>> = _mediciones.asStateFlow()

    private val _alertas = MutableStateFlow<List<Alerta>>(emptyList())
    val alertas: StateFlow<List<Alerta>> = _alertas.asStateFlow()

    private val _eventos = MutableStateFlow<List<Evento>>(emptyList())
    val eventos: StateFlow<List<Evento>> = _eventos.asStateFlow()

    private val _ultimaActualizacion = MutableStateFlow("")
    val ultimaActualizacion: StateFlow<String> = _ultimaActualizacion.asStateFlow()

    suspend fun iniciar() {
        val guardados = almacen.leerTodo()
        if (guardados.galpones.isNotEmpty()) {
            _galpones.value = guardados.galpones
            _mediciones.value = guardados.mediciones
            _alertas.value = guardados.alertas
            _eventos.value = guardados.eventos
            _ultimaActualizacion.value = Fechas.ahoraIso()
        }
        actualizar()
    }

    suspend fun actualizar() {
        val galponesNuevos = api.consultarGalpones()
        val medicionesNuevas = galponesNuevos.map { galpon ->
            Medicion(
                id = "m" + System.currentTimeMillis() + galpon.id,
                galponId = galpon.id,
                temperatura = galpon.temperatura,
                humedad = galpon.humedad,
                fechaMedicion = galpon.fechaMedicion
            )
        }
        val anteriores = _alertas.value
        val alertasNuevas = detectarAlertas(galponesNuevos, anteriores)
        _galpones.value = galponesNuevos
        _mediciones.value = (medicionesNuevas + _mediciones.value).take(90)
        _alertas.value = alertasNuevas
        _ultimaActualizacion.value = Fechas.ahoraIso()
        almacen.guardarGalpones(galponesNuevos)
        almacen.guardarMediciones(_mediciones.value)
        almacen.guardarAlertas(alertasNuevas)
        avisarCriticas(alertasNuevas, anteriores)
    }

    suspend fun registrarEvento(
        galponId: String,
        tipo: String,
        descripcion: String,
        responsable: String,
        temperatura: Double
    ) {
        val galpon = _galpones.value.firstOrNull { it.id == galponId }
        val evento = Evento(
            id = "e" + System.currentTimeMillis(),
            galponId = galponId,
            galponNombre = galpon?.nombre ?: "Galpon sin nombre",
            tipo = tipo,
            descripcion = descripcion,
            responsable = responsable,
            temperatura = temperatura,
            fechaRegistro = Fechas.ahoraIso()
        )
        _eventos.value = listOf(evento) + _eventos.value
        almacen.guardarEventos(_eventos.value)
    }

    suspend fun resolverAlerta(id: String) {
        val lista = _alertas.value.map { alerta ->
            if (alerta.id == id) alerta.copy(activa = false) else alerta
        }
        _alertas.value = lista
        almacen.guardarAlertas(lista)
    }

    suspend fun marcarAlertaVista(id: String) {
        val lista = _alertas.value.map { alerta ->
            if (alerta.id == id) alerta.copy(vista = true) else alerta
        }
        _alertas.value = lista
        almacen.guardarAlertas(lista)
    }

    suspend fun actualizarUmbrales(galponId: String, advertencia: Double, critico: Double) {
        val lista = _galpones.value.map { galpon ->
            if (galpon.id == galponId) {
                galpon.copy(umbralAdvertencia = advertencia, umbralCritico = critico)
            } else {
                galpon
            }
        }
        _galpones.value = lista
        almacen.guardarGalpones(lista)
    }

    private fun detectarAlertas(galpones: List<Galpon>, actuales: List<Alerta>): List<Alerta> {
        val resultado = actuales.toMutableList()
        for (galpon in galpones) {
            val estado = galpon.estado()
            if (estado == EstadoGalpon.NORMAL) {
                continue
            }
            val mensaje = construirMensaje(galpon, estado)
            val indice = resultado.indexOfFirst { alerta ->
                alerta.galponId == galpon.id && alerta.activa
            }
            if (indice == -1) {
                resultado.add(
                    Alerta(
                        id = "a" + System.currentTimeMillis() + galpon.id,
                        galponId = galpon.id,
                        galponNombre = galpon.nombre,
                        tipo = if (estado == EstadoGalpon.CRITICO) "Critico" else "Advertencia",
                        mensaje = mensaje,
                        fechaAlerta = Fechas.ahoraIso(),
                        activa = true,
                        vista = false
                    )
                )
            } else {
                val alerta = resultado[indice]
                if (estado == EstadoGalpon.CRITICO && alerta.tipo != "Critico") {
                    resultado[indice] = alerta.copy(
                        tipo = "Critico",
                        mensaje = mensaje,
                        fechaAlerta = Fechas.ahoraIso(),
                        vista = false
                    )
                }
            }
        }
        return resultado
    }

    private fun construirMensaje(galpon: Galpon, estado: EstadoGalpon): String {
        if (estado == EstadoGalpon.CRITICO) {
            return "Temperatura de " + formatear(galpon.temperatura) + " C supero el limite critico"
        }
        return "Temperatura de " + formatear(galpon.temperatura) + " C en zona de advertencia"
    }

    private fun avisarCriticas(nuevas: List<Alerta>, anteriores: List<Alerta>) {
        for (alerta in nuevas) {
            if (!alerta.activa || alerta.tipo != "Critico") {
                continue
            }
            val previa = anteriores.firstOrNull { anterior -> anterior.id == alerta.id }
            if (previa != null && previa.tipo == "Critico") {
                continue
            }
            notificador.avisar(alerta)
            vibrador.vibrar()
        }
    }
}
