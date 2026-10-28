package com.example.atat.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atat.data.MonitoreoRepository
import com.example.atat.data.model.EstadoGalpon
import com.example.atat.data.model.Perfil
import com.example.atat.data.model.Resumen
import com.example.atat.nativo.Conectividad
import com.example.atat.nativo.Posicion
import com.example.atat.nativo.SensorTemperatura
import com.example.atat.nativo.UbicacionDispositivo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = MonitoreoRepository.obtener(application)
    private val conectividad = Conectividad(application)
    private val sensor = SensorTemperatura(application)
    private val ubicacionDispositivo = UbicacionDispositivo(application)

    val galpones = repositorio.galpones
    val mediciones = repositorio.mediciones
    val alertas = repositorio.alertas
    val eventos = repositorio.eventos
    val ultimaActualizacion = repositorio.ultimaActualizacion

    private val _perfil = MutableStateFlow(Perfil.OPERARIO)
    val perfil = _perfil.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando = _cargando.asStateFlow()

    private val _mensaje = MutableStateFlow("")
    val mensaje = _mensaje.asStateFlow()

    private val _hayConexion = MutableStateFlow(true)
    val hayConexion = _hayConexion.asStateFlow()

    private val _temperaturaDispositivo = MutableStateFlow<Double?>(null)
    val temperaturaDispositivo = _temperaturaDispositivo.asStateFlow()

    private val _posicion = MutableStateFlow<Posicion?>(null)
    val posicion = _posicion.asStateFlow()

    val resumen: kotlinx.coroutines.flow.StateFlow<Resumen> =
        combine(galpones, alertas) { listaGalpones, listaAlertas ->
            var normales = 0
            var advertencias = 0
            var criticos = 0
            var suma = 0.0
            for (galpon in listaGalpones) {
                if (galpon.estado() == EstadoGalpon.NORMAL) {
                    normales = normales + 1
                } else if (galpon.estado() == EstadoGalpon.ADVERTENCIA) {
                    advertencias = advertencias + 1
                } else {
                    criticos = criticos + 1
                }
                suma = suma + galpon.temperatura
            }
            var promedio = 0.0
            if (listaGalpones.isNotEmpty()) {
                promedio = suma / listaGalpones.size
            }
            var activas = 0
            for (alerta in listaAlertas) {
                if (alerta.activa) {
                    activas = activas + 1
                }
            }
            Resumen(listaGalpones.size, normales, advertencias, criticos, activas, promedio)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), Resumen())

    init {
        viewModelScope.launch {
            _hayConexion.value = conectividad.hayConexion()
            repositorio.iniciar()
        }
    }

    fun actualizar() {
        viewModelScope.launch {
            _cargando.value = true
            _hayConexion.value = conectividad.hayConexion()
            if (_hayConexion.value) {
                repositorio.actualizar()
            } else {
                _mensaje.value = "Sin conexion: se muestran los datos guardados en el dispositivo"
            }
            _cargando.value = false
        }
    }

    fun cambiarPerfil(nuevo: Perfil) {
        _perfil.value = nuevo
    }

    fun resolverAlerta(id: String) {
        viewModelScope.launch {
            repositorio.resolverAlerta(id)
            _mensaje.value = "Alerta resuelta"
        }
    }

    fun marcarAlertaVista(id: String) {
        viewModelScope.launch {
            repositorio.marcarAlertaVista(id)
        }
    }

    fun mostrarMensaje(texto: String) {
        _mensaje.value = texto
    }

    fun limpiarMensaje() {
        _mensaje.value = ""
    }

    fun sensorDisponible(): Boolean {
        return sensor.disponible()
    }

    fun leerSensor() {
        viewModelScope.launch {
            _temperaturaDispositivo.value = sensor.leer()
            if (_temperaturaDispositivo.value == null) {
                _mensaje.value = "El equipo no tiene sensor de temperatura ambiente"
            }
        }
    }

    fun leerUbicacion() {
        viewModelScope.launch {
            _posicion.value = ubicacionDispositivo.obtenerPosicion()
            if (_posicion.value == null) {
                _mensaje.value = "No se pudo obtener la ubicacion del dispositivo"
            }
        }
    }
}
