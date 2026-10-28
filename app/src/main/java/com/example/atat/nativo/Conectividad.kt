package com.example.atat.nativo

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

class Conectividad(private val contexto: Context) {

    private val gestor: ConnectivityManager =
        contexto.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun hayConexion(): Boolean {
        val red = gestor.activeNetwork ?: return false
        val capacidades = gestor.getNetworkCapabilities(red) ?: return false
        return capacidades.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
