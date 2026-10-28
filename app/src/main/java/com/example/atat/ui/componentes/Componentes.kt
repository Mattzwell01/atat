package com.example.atat.ui.componentes

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.atat.data.model.EstadoGalpon
import com.example.atat.data.model.Galpon
import com.example.atat.data.model.Medicion
import com.example.atat.data.model.Resumen
import com.example.atat.ui.theme.NaranjaAdvertencia
import com.example.atat.ui.theme.RojoCritico
import com.example.atat.ui.theme.VerdeNormal
import com.example.atat.util.Fechas
import com.example.atat.util.formatear
import com.example.atat.validacion.ResultadoCampo

fun colorDeEstado(estado: EstadoGalpon): Color {
    if (estado == EstadoGalpon.NORMAL) {
        return VerdeNormal
    }
    if (estado == EstadoGalpon.ADVERTENCIA) {
        return NaranjaAdvertencia
    }
    return RojoCritico
}

fun textoDeEstado(estado: EstadoGalpon): String {
    if (estado == EstadoGalpon.NORMAL) {
        return "Normal"
    }
    if (estado == EstadoGalpon.ADVERTENCIA) {
        return "Advertencia"
    }
    return "Critico"
}

@Composable
fun ChipEstado(estado: EstadoGalpon) {
    val color = colorDeEstado(estado)
    Surface(
        color = color.copy(alpha = 0.15f),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Thermostat,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = textoDeEstado(estado),
                color = color,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun IndicadorTemperatura(galpon: Galpon) {
    val color = colorDeEstado(galpon.estado())
    val colorAnimado by animateColorAsState(targetValue = color, label = "colorEstado")
    val progreso = ((galpon.temperatura - 15.0) / 25.0).toFloat()
    val valorAnimado by animateFloatAsState(
        targetValue = if (progreso < 0f) 0f else if (progreso > 1f) 1f else progreso,
        label = "barra"
    )
    Column {
        LinearProgressIndicator(
            progress = { valorAnimado },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = colorAnimado,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Advertencia " + formatear(galpon.umbralAdvertencia) + " C",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = "Critico " + formatear(galpon.umbralCritico) + " C",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun TarjetaGalpon(galpon: Galpon, onClick: () -> Unit) {
    val color = colorDeEstado(galpon.estado())
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = color.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Agriculture,
                            contentDescription = null,
                            tint = color
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    Text(
                        text = galpon.nombre,
                        style = MaterialTheme.typography.titleMedium,
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
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatear(galpon.temperatura) + " C",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
                Spacer(modifier = Modifier.width(20.dp))
                Icon(
                    imageVector = Icons.Outlined.WaterDrop,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatear(galpon.humedad) + " %",
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
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Actualizado " + Fechas.mostrar(galpon.fechaMedicion),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun TarjetaResumen(resumen: Resumen) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Resumen de la granja",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                DatoResumen("Galpones", resumen.totalGalpones.toString(), MaterialTheme.colorScheme.primary)
                DatoResumen("Normales", resumen.normales.toString(), VerdeNormal)
                DatoResumen("Advertencia", resumen.advertencias.toString(), NaranjaAdvertencia)
                DatoResumen("Criticos", resumen.criticos.toString(), RojoCritico)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Alertas activas: " + resumen.alertasActivas,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Promedio " + formatear(resumen.temperaturaPromedio) + " C",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun DatoResumen(titulo: String, valor: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(text = titulo, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun CampoValidado(
    valor: String,
    onValor: (String) -> Unit,
    etiqueta: String,
    icono: ImageVector,
    esNumero: Boolean,
    resultado: ResultadoCampo,
    mostrarError: Boolean,
    lineas: Int = 1
) {
    val hayError = mostrarError && !resultado.esValido
    OutlinedTextField(
        value = valor,
        onValueChange = onValor,
        label = { Text(etiqueta) },
        leadingIcon = { Icon(imageVector = icono, contentDescription = null) },
        trailingIcon = {
            if (mostrarError) {
                Icon(
                    imageVector = if (resultado.esValido) {
                        Icons.Outlined.CheckCircle
                    } else {
                        Icons.Outlined.ErrorOutline
                    },
                    contentDescription = null,
                    tint = if (resultado.esValido) VerdeNormal else RojoCritico
                )
            }
        },
        isError = hayError,
        supportingText = if (hayError) {
            {
                Text(text = resultado.mensaje, color = RojoCritico)
            }
        } else {
            null
        },
        keyboardOptions = if (esNumero) {
            KeyboardOptions(keyboardType = KeyboardType.Decimal)
        } else {
            KeyboardOptions.Default
        },
        singleLine = lineas == 1,
        minLines = lineas,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
fun GraficoMediciones(mediciones: List<Medicion>, limiteCritico: Double) {
    if (mediciones.isEmpty()) {
        Text(
            text = "Sin mediciones registradas",
            style = MaterialTheme.typography.bodyMedium
        )
        return
    }
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
    ) {
        val ancho = size.width / mediciones.size
        val maximo = 45f
        for (indice in mediciones.indices) {
            val medicion = mediciones[indice]
            val alto = ((medicion.temperatura / maximo) * size.height).toFloat()
            val color = if (medicion.temperatura >= limiteCritico) {
                RojoCritico
            } else if (medicion.temperatura >= limiteCritico - 3.0) {
                NaranjaAdvertencia
            } else {
                VerdeNormal
            }
            drawRect(
                color = color,
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = indice * ancho + ancho * 0.15f,
                    y = size.height - alto
                ),
                size = androidx.compose.ui.geometry.Size(
                    width = ancho * 0.7f,
                    height = alto
                )
            )
        }
        val yLimite = (size.height - ((limiteCritico / maximo) * size.height)).toFloat()
        drawLine(
            color = RojoCritico,
            start = androidx.compose.ui.geometry.Offset(0f, yLimite),
            end = androidx.compose.ui.geometry.Offset(size.width, yLimite),
            strokeWidth = 3f
        )
    }
}
