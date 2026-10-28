package com.example.atat.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.atat.data.model.Alerta
import com.example.atat.data.model.Evento
import com.example.atat.data.model.Galpon
import com.example.atat.data.model.Medicion
import kotlinx.coroutines.flow.first
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.archivoDatos: DataStore<Preferences> by preferencesDataStore(name = "atat_datos")

data class DatosGuardados(
    val galpones: List<Galpon>,
    val mediciones: List<Medicion>,
    val alertas: List<Alerta>,
    val eventos: List<Evento>
)

class AlmacenLocal(contexto: Context) {

    private val claveGalpones = stringPreferencesKey("galpones")
    private val claveMediciones = stringPreferencesKey("mediciones")
    private val claveAlertas = stringPreferencesKey("alertas")
    private val claveEventos = stringPreferencesKey("eventos")

    private val json = Json { ignoreUnknownKeys = true }
    private val datos = contexto.applicationContext.archivoDatos

    suspend fun leerTodo(): DatosGuardados {
        val preferencias = datos.data.first()
        return DatosGuardados(
            galpones = leer(preferencias[claveGalpones]),
            mediciones = leer(preferencias[claveMediciones]),
            alertas = leer(preferencias[claveAlertas]),
            eventos = leer(preferencias[claveEventos])
        )
    }

    suspend fun guardarGalpones(lista: List<Galpon>) {
        guardar(claveGalpones, json.encodeToString(lista))
    }

    suspend fun guardarMediciones(lista: List<Medicion>) {
        guardar(claveMediciones, json.encodeToString(lista))
    }

    suspend fun guardarAlertas(lista: List<Alerta>) {
        guardar(claveAlertas, json.encodeToString(lista))
    }

    suspend fun guardarEventos(lista: List<Evento>) {
        guardar(claveEventos, json.encodeToString(lista))
    }

    private suspend fun guardar(clave: Preferences.Key<String>, valor: String) {
        datos.edit { preferencias ->
            preferencias[clave] = valor
        }
    }

    private inline fun <reified T> leer(texto: String?): List<T> {
        if (texto.isNullOrEmpty()) {
            return emptyList()
        }
        return try {
            json.decodeFromString<List<T>>(texto)
        } catch (error: Exception) {
            emptyList()
        }
    }
}
