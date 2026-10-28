package com.example.atat.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Fechas {

    fun ahoraIso(): String {
        val formato = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
        return formato.format(Date())
    }

    fun mostrar(iso: String): String {
        if (iso.isEmpty()) {
            return "sin registro"
        }
        val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val fecha = try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(iso)
        } catch (error: Exception) {
            null
        }
        if (fecha == null) {
            return iso
        }
        return formato.format(fecha)
    }
}
