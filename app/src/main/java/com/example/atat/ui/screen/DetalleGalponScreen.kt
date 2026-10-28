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
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Sensors
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.atat.data.model.Alerta
import com.example.atat.data.model.Evento
import com.example.atat.data.model.Galpon
import com.example.atat.data.model.Medicion
import com.example.atat.nativo.Posicion
import com.example.atat.nativo.distanciaEntre
import com.example.atat.ui.componentes.ChipEstado
import com.example.atat.ui.componentes.GraficoMediciones
import com.example.atat.ui.componentes.IndicadorTemperatura
import com.example.atat.ui.componentes.colorDeEstado
import com.example.atat.util.formatear
import com.example.atat.util.Fechas

@Composable
fun DetalleGalponScreen(
    galpon: Galpon?,
    mediciones: List<Medicion>,
    alertas: List<Alerta>,
    eventos: List<Evento>,
    temperaturaDispositivo: Double?,
    sensorDisponible: Boolean,
    posicion: Posicion?,
    hayConexion: Boolean,
    onRegistrar: (String) -> Unit,
    onUmbrales: (String) -> Unit,
    onLeerSensor: () -> Unit,
    onLeerUbicacion: () -> Unit
) {
    if (galpon == null) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "No se encontro el galpon solicitado")
        }
        return
    }

    val color = colorDeEstado(galpon.estado())
    val medicionesGalpon = mediciones.filter { medicion -> medicion.galponId == galpon.id }
    val grafico = medicionesGalpon.take(20).reversed()
    val alertasGalpon = alertas.filter { alerta -> alerta.galponId == galpon.id }
    val eventosGalpon = eventos.filter { evento -> evento.galponId == galpon.id }
    val distancia = if (posicion != null) {
        distanciaEntre(posicion, Posicion(galpon.latitud, galpon.longitud))
    } else {
        null
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = galpon.nombre,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = galpon.granja + " - " + galpon.ubicacion,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        ChipEstado(galpon.estado())
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Thermostat,
                            contentDescription = null,
                            tint = color
                        )
                        Text(
                            text = " " + formatear(galpon.temperatura) + " C",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = color
                        )
                        Spacer(modifier = Modifier.size(20.dp))
                        Icon(
                            imageVector = Icons.Outlined.WaterDrop,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = " " + formatear(galpon.humedad) + " %",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    IndicadorTemperatura(galpon)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Schedule,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = " " + Fechas.mostrar(galpon.fechaMedicion),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Recursos del dispositivo",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    FilaRecurso(
                        icono = Icons.Outlined.Sensors,
                        titulo = "Sensor de temperatura del celular",
                        valor = when {
                            temperaturaDispositivo != null -> formatear(temperaturaDispositivo) + " C medidos"
                            sensorDisponible -> "Presente, sin lectura"
                            else -> "No disponible en este equipo"
                        },
                        onClick = { onLeerSensor() }
                    )
                    FilaRecurso(
                        icono = Icons.Outlined.LocationOn,
                        titulo = "Ubicacion del dispositivo",
                        valor = when {
                            distancia != null -> formatear(distancia) + " km del galpon"
                            posicion != null -> "Posicion obtenida"
                            else -> "Sin ubicacion, se requiere permiso"
                        },
                        onClick = { onLeerUbicacion() }
                    )
                    FilaRecurso(
                        icono = Icons.Outlined.Wifi,
                        titulo = "Conexion a internet",
                        valor = if (hayConexion) "Conectado" else "Sin conexion, datos locales"
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Historial de mediciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    GraficoMediciones(mediciones = grafico, limiteCritico = galpon.umbralCritico)
                    for (medicion in medicionesGalpon.take(5)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = Fechas.mostrar(medicion.fechaMedicion),
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                text = formatear(medicion.temperatura) + " C - " +
                                    formatear(medicion.humedad) + " %",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Alertas del galpon",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (alertasGalpon.isEmpty()) {
                        Text(
                            text = "Sin alertas registradas",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    for (alerta in alertasGalpon) {
                        Text(
                            text = Fechas.mostrar(alerta.fechaAlerta) + " - " + alerta.mensaje,
                            style = MaterialTheme.typography.bodySmall,
                            color = colorDeEstado(
                                if (alerta.activa) {
                                    com.example.atat.data.model.EstadoGalpon.CRITICO
                                } else {
                                    com.example.atat.data.model.EstadoGalpon.ADVERTENCIA
                                }
                            )
                        )
                    }
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Eventos registrados",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (eventosGalpon.isEmpty()) {
                        Text(
                            text = "El operario aun no registra acciones en este galpon",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    for (evento in eventosGalpon.take(5)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Column(modifier = Modifier.padding(start = 8.dp)) {
                                Text(
                                    text = evento.tipo + " - " + evento.responsable,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = evento.descripcion,
                                    style = MaterialTheme.typography.bodySmall
                                )
                                Text(
                                    text = Fechas.mostrar(evento.fechaRegistro),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { onRegistrar(galpon.id) }) {
                    Icon(
                        imageVector = Icons.Outlined.Description,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = "  Registrar accion")
                }
                OutlinedButton(onClick = { onUmbrales(galpon.id) }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(text = "  Umbrales")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
fun FilaRecurso(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    valor: String,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
        ) {
            Text(text = titulo, style = MaterialTheme.typography.bodyMedium)
            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        if (onClick != null) {
            OutlinedButton(onClick = { onClick() }) {
                Text(text = "Leer")
            }
        }
    }
}
