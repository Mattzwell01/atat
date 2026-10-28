package com.example.atat.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.atat.data.MonitoreoRepository
import com.example.atat.validacion.ResultadoCampo
import com.example.atat.validacion.Validadores
import kotlinx.coroutines.launch

class RegistroViewModel(application: Application) : AndroidViewModel(application) {

    private val repositorio = MonitoreoRepository.obtener(application)

    val galpones = repositorio.galpones

    var galponId by mutableStateOf("")
        private set
    var tipo by mutableStateOf("Incidente")
        private set
    var temperatura by mutableStateOf("")
        private set
    var humedad by mutableStateOf("")
        private set
    var responsable by mutableStateOf("")
        private set
    var observacion by mutableStateOf("")
        private set

    private var tocados by mutableStateOf(setOf<String>())
    var enviado by mutableStateOf(false)
        private set

    val tipos = listOf("Incidente", "Mantencion", "Control de clima", "Revision", "Otro")

    fun elegirGalpon(id: String) {
        galponId = id
        tocar("galpon")
    }

    fun cambiarTipo(valor: String) {
        tipo = valor
        tocar("tipo")
    }

    fun cambiarTemperatura(texto: String) {
        temperatura = texto
        tocar("temperatura")
    }

    fun cambiarHumedad(texto: String) {
        humedad = texto
        tocar("humedad")
    }

    fun cambiarResponsable(texto: String) {
        responsable = texto
        tocar("responsable")
    }

    fun cambiarObservacion(texto: String) {
        observacion = texto
        tocar("observacion")
    }

    fun tocar(campo: String) {
        tocados = tocados + campo
    }

    fun mostrarError(campo: String): Boolean {
        return tocados.contains(campo)
    }

    fun errorGalpon(): ResultadoCampo {
        if (galponId.isEmpty()) {
            return ResultadoCampo(false, "Selecciona un galpon")
        }
        return ResultadoCampo(true, "")
    }

    fun errorTemperatura(): ResultadoCampo {
        return Validadores.temperatura(temperatura)
    }

    fun errorHumedad(): ResultadoCampo {
        return Validadores.humedad(humedad)
    }

    fun errorResponsable(): ResultadoCampo {
        return Validadores.responsable(responsable)
    }

    fun errorObservacion(): ResultadoCampo {
        return Validadores.observacion(observacion)
    }

    fun todoValido(): Boolean {
        return errorGalpon().esValido &&
            errorTemperatura().esValido &&
            errorHumedad().esValido &&
            errorResponsable().esValido &&
            errorObservacion().esValido
    }

    fun registrar(onListo: () -> Unit) {
        tocados = setOf("galpon", "tipo", "temperatura", "humedad", "responsable", "observacion")
        if (!todoValido()) {
            return
        }
        val valorTemperatura = temperatura.replace(",", ".").toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            repositorio.registrarEvento(
                galponId = galponId,
                tipo = tipo,
                descripcion = observacion.trim(),
                responsable = responsable.trim(),
                temperatura = valorTemperatura
            )
            enviado = true
            onListo()
        }
    }

    fun limpiar() {
        galponId = ""
        tipo = "Incidente"
        temperatura = ""
        humedad = ""
        responsable = ""
        observacion = ""
        tocados = setOf()
        enviado = false
    }
}
