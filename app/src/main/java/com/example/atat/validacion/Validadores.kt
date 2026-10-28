package com.example.atat.validacion

data class ResultadoCampo(
    val esValido: Boolean,
    val mensaje: String
)

object Validadores {

    fun temperatura(texto: String): ResultadoCampo {
        if (texto.isEmpty()) {
            return ResultadoCampo(false, "Ingresa la temperatura medida")
        }
        val valor = texto.replace(",", ".").toDoubleOrNull()
        if (valor == null) {
            return ResultadoCampo(false, "La temperatura debe ser un numero")
        }
        if (valor < 0.0 || valor > 50.0) {
            return ResultadoCampo(false, "La temperatura debe estar entre 0 y 50 grados")
        }
        return ResultadoCampo(true, "")
    }

    fun humedad(texto: String): ResultadoCampo {
        if (texto.isEmpty()) {
            return ResultadoCampo(false, "Ingresa la humedad medida")
        }
        val valor = texto.replace(",", ".").toDoubleOrNull()
        if (valor == null) {
            return ResultadoCampo(false, "La humedad debe ser un numero")
        }
        if (valor < 0.0 || valor > 100.0) {
            return ResultadoCampo(false, "La humedad debe estar entre 0 y 100 por ciento")
        }
        return ResultadoCampo(true, "")
    }

    fun responsable(texto: String): ResultadoCampo {
        if (texto.isEmpty()) {
            return ResultadoCampo(false, "Ingresa el nombre del responsable")
        }
        if (texto.trim().length < 3) {
            return ResultadoCampo(false, "El responsable debe tener al menos 3 caracteres")
        }
        return ResultadoCampo(true, "")
    }

    fun observacion(texto: String): ResultadoCampo {
        if (texto.isEmpty()) {
            return ResultadoCampo(false, "Ingresa la observacion")
        }
        if (texto.trim().length < 10) {
            return ResultadoCampo(false, "La observacion debe tener al menos 10 caracteres")
        }
        return ResultadoCampo(true, "")
    }

    fun umbralAdvertencia(texto: String): ResultadoCampo {
        if (texto.isEmpty()) {
            return ResultadoCampo(false, "Ingresa el limite de advertencia")
        }
        val valor = texto.replace(",", ".").toDoubleOrNull()
        if (valor == null) {
            return ResultadoCampo(false, "El limite debe ser un numero")
        }
        if (valor < 0.0 || valor > 45.0) {
            return ResultadoCampo(false, "El limite debe estar entre 0 y 45 grados")
        }
        return ResultadoCampo(true, "")
    }

    fun umbralCritico(texto: String): ResultadoCampo {
        if (texto.isEmpty()) {
            return ResultadoCampo(false, "Ingresa el limite critico")
        }
        val valor = texto.replace(",", ".").toDoubleOrNull()
        if (valor == null) {
            return ResultadoCampo(false, "El limite debe ser un numero")
        }
        if (valor < 0.0 || valor > 50.0) {
            return ResultadoCampo(false, "El limite debe estar entre 0 y 50 grados")
        }
        return ResultadoCampo(true, "")
    }

    fun limitesCoherentes(advertencia: Double, critico: Double): Boolean {
        return critico > advertencia
    }
}
