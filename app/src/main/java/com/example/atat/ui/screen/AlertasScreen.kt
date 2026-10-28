package com.example.atat.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.atat.data.model.Alerta
import com.example.atat.util.formatear
import com.example.atat.ui.theme.NaranjaAdvertencia
import com.example.atat.ui.theme.RojoCritico
import com.example.atat.util.Fechas

@Composable
fun AlertasScreen(
    alertas: List<Alerta>,
    onResolver: (String) -> Unit,
    onMarcarVista: (String) -> Unit,
    onGalpon: (String) -> Unit
) {
    var filtro by rememberSaveable { mutableStateOf("Activas") }
    val opciones = listOf("Activas", "Resueltas", "Todas")
    val lista = when (filtro) {
        "Activas" -> alertas.filter { alerta -> alerta.activa }
        "Resueltas" -> alertas.filter { alerta -> !alerta.activa }
        else -> alertas
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                for (opcion in opciones) {
                    FilterChip(
                        selected = filtro == opcion,
                        onClick = { filtro = opcion },
                        label = { Text(opcion) }
                    )
                }
            }
        }

        if (lista.isEmpty()) {
            item {
                Text(
                    text = "No hay alertas para mostrar en este filtro",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        items(lista, key = { it.id }) { alerta ->
            TarjetaAlerta(
                alerta = alerta,
                onResolver = { onResolver(alerta.id) },
                onMarcarVista = { onMarcarVista(alerta.id) },
                onGalpon = { onGalpon(alerta.galponId) }
            )
        }
    }
}

@Composable
fun TarjetaAlerta(
    alerta: Alerta,
    onResolver: () -> Unit,
    onMarcarVista: () -> Unit,
    onGalpon: () -> Unit
) {
    val color = if (alerta.activa) RojoCritico else NaranjaAdvertencia
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (alerta.activa) Icons.Outlined.Warning else Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    tint = color
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = alerta.galponNombre,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = alerta.tipo + " - " + Fechas.mostrar(alerta.fechaAlerta),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Text(text = alerta.mensaje, style = MaterialTheme.typography.bodyMedium)

            AnimatedVisibility(visible = alerta.activa && !alerta.vista) {
                Text(
                    text = "Alerta sin revisar por el supervisor",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onGalpon() }) {
                    Text(text = "Ver galpon")
                }
            }

            if (alerta.activa) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!alerta.vista) {
                        OutlinedButton(
                            onClick = { onMarcarVista() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(text = "  Marcar vista")
                        }
                    }
                    OutlinedButton(
                        onClick = { onResolver() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "Resolver")
                    }
                }
            }
        }
    }
}
