package com.example.atat.util

import java.util.Locale

fun formatear(valor: Double): String {
    return String.format(Locale.getDefault(), "%.1f", valor)
}
