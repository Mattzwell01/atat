package com.example.atat.ui.screen

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.atat.data.model.Galpon
import com.example.atat.data.model.Medicion
import com.example.atat.ui.componentes.GraficoMediciones
import com.example.atat.util.formatear
import com.example.atat.util.Fechas

@Composable
fun HistorialScreen(
    mediciones: List<Medicion>,
    galpones: List<Galpon>
) {
    var galponSeleccionado by rememberSaveable { mutableStateOf("todos") }
    val filtradas = if (galponSeleccionado == "todos") {
        mediciones
    } else {
        mediciones.filter { medicion -> medicion.galponId == galponSeleccionado }
    }
    val ordenadas = filtradas.sortedByDescending { medicion -> medicion.fechaMedicion }
    val grafico = ordenadas.take(20).reversed()
    var limite = 33.0
    for (galpon in galpones) {
        if (galpon.id == galponSeleccionado) {
            limite = galpon.umbralCritico
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = galponSeleccionado == "todos",
                    onClick = { galponSeleccionado = "todos" },
                    label = { Text("Todos") }
                )
                for (galpon in galpones) {
                    FilterChip(
                        selected = galponSeleccionado == galpon.id,
                        onClick = { galponSeleccionado = galpon.id },
                        label = { Text(galpon.nombre) }
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Historial de mediciones",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Ultimas " + grafico.size + " mediciones (linea roja = limite critico)",
                        style = MaterialTheme.typography.bodySmall
                    )
                    GraficoMediciones(mediciones = grafico, limiteCritico = limite)
                }
            }
        }

        if (ordenadas.isEmpty()) {
            item {
                Text(
                    text = "Todavia no hay mediciones en el historial",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(ordenadas.take(40), key = { it.id }) { medicion ->
            FilaMedicion(medicion = medicion, galpones = galpones)
        }
    }
}

@Composable
fun FilaMedicion(medicion: Medicion, galpones: List<Galpon>) {
    var nombre = "Galpon"
    for (galpon in galpones) {
        if (galpon.id == medicion.galponId) {
            nombre = galpon.nombre
        }
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = nombre, style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = " " + Fechas.mostrar(medicion.fechaMedicion),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Icon(
                imageVector = Icons.Outlined.Thermostat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = " " + formatear(medicion.temperatura) + " C")
            Spacer(modifier = Modifier.size(12.dp))
            Icon(
                imageVector = Icons.Outlined.WaterDrop,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Text(text = " " + formatear(medicion.humedad) + " %")
        }
    }
}
