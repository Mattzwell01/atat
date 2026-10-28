package com.example.atat.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.atat.data.model.Galpon
import com.example.atat.data.model.Perfil
import com.example.atat.data.model.Resumen
import com.example.atat.ui.componentes.TarjetaGalpon
import com.example.atat.ui.componentes.TarjetaResumen
import com.example.atat.util.Fechas

@Composable
fun InicioScreen(
    galpones: List<Galpon>,
    resumen: Resumen,
    perfil: Perfil,
    cargando: Boolean,
    hayConexion: Boolean,
    ultimaActualizacion: String,
    onPerfil: (Perfil) -> Unit,
    onGalpon: (String) -> Unit,
    onActualizar: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!hayConexion) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.CloudOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = "Sin conexion a internet",
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Se muestran los ultimos datos guardados en el dispositivo",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }

        item { TarjetaResumen(resumen) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = perfil == Perfil.OPERARIO,
                    onClick = { onPerfil(Perfil.OPERARIO) },
                    label = { Text("Operario") }
                )
                FilterChip(
                    selected = perfil == Perfil.SUPERVISOR,
                    onClick = { onPerfil(Perfil.SUPERVISOR) },
                    label = { Text("Supervisor") }
                )
                FilterChip(
                    selected = perfil == Perfil.JEFATURA,
                    onClick = { onPerfil(Perfil.JEFATURA) },
                    label = { Text("Jefatura") }
                )
            }
        }

        if (perfil == Perfil.JEFATURA) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Vista de jefatura",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Resumen de la granja para la toma de decisiones. " +
                                "Se detectan " + resumen.criticos + " galpones en estado critico y " +
                                resumen.alertasActivas + " alertas activas.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        } else {
            if (galpones.isEmpty() && !cargando) {
                item {
                    Text(
                        text = "No hay galpones cargados en el dispositivo",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            items(galpones, key = { it.id }) { galpon ->
                TarjetaGalpon(galpon = galpon, onClick = { onGalpon(galpon.id) })
            }
        }

        if (cargando) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp))
                    Text(
                        text = " Consultando el sistema de monitoreo...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ultima actualizacion: " + Fechas.mostrar(ultimaActualizacion),
                    style = MaterialTheme.typography.labelSmall
                )
                TextButton(onClick = { onActualizar() }) {
                    Icon(
                        imageVector = Icons.Outlined.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = "  Actualizar")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
