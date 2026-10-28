package com.example.atat.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.atat.data.MonitoreoRepository
import com.example.atat.util.formatear
import com.example.atat.validacion.ResultadoCampo
import com.example.atat.validacion.Validadores
import kotlinx.coroutines.launch

class UmbralesViewModel(
    application: Application,
    estadoGuardado: SavedStateHandle
) : AndroidViewModel(application) {

    private val repositorio = MonitoreoRepository.obtener(application)
    private val galponId: String = estadoGuardado.get<String>("galponId") ?: ""

    var advertencia by mutableStateOf("")
        private set
    var critico by mutableStateOf("")
        private set

    private var tocados by mutableStateOf(setOf<String>())

    init {
        val galpon = repositorio.galpones.value.firstOrNull { item -> item.id == galponId }
        if (galpon != null) {
            advertencia = formatear(galpon.umbralAdvertencia)
            critico = formatear(galpon.umbralCritico)
        }
    }

    fun cambiarAdvertencia(texto: String) {
        advertencia = texto
        tocados = tocados + "advertencia"
    }

    fun cambiarCritico(texto: String) {
        critico = texto
        tocados = tocados + "critico"
    }

    fun mostrarError(campo: String): Boolean {
        return tocados.contains(campo)
    }

    fun errorAdvertencia(): ResultadoCampo {
        return Validadores.umbralAdvertencia(advertencia)
    }

    fun errorCritico(): ResultadoCampo {
        return Validadores.umbralCritico(critico)
    }

    fun errorRelacion(): ResultadoCampo {
        if (errorAdvertencia().esValido && errorCritico().esValido) {
            val valorAdvertencia = advertencia.replace(",", ".").toDoubleOrNull() ?: 0.0
            val valorCritico = critico.replace(",", ".").toDoubleOrNull() ?: 0.0
            if (!Validadores.limitesCoherentes(valorAdvertencia, valorCritico)) {
                return ResultadoCampo(false, "El limite critico debe ser mayor que el de advertencia")
            }
        }
        return ResultadoCampo(true, "")
    }

    fun todoValido(): Boolean {
        return errorAdvertencia().esValido && errorCritico().esValido && errorRelacion().esValido
    }

    fun guardar(onListo: () -> Unit) {
        tocados = setOf("advertencia", "critico")
        if (!todoValido()) {
            return
        }
        val valorAdvertencia = advertencia.replace(",", ".").toDoubleOrNull() ?: 0.0
        val valorCritico = critico.replace(",", ".").toDoubleOrNull() ?: 0.0
        viewModelScope.launch {
            repositorio.actualizarUmbrales(galponId, valorAdvertencia, valorCritico)
            onListo()
        }
    }
}
