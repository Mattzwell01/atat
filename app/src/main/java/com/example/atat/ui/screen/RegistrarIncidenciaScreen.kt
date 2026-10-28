package com.example.atat.ui.screen

import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Agriculture
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.atat.ui.componentes.CampoValidado
import com.example.atat.ui.viewmodel.RegistroViewModel

@Composable
fun RegistrarIncidenciaScreen(
    vm: RegistroViewModel,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    val galpones by vm.galpones.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Galpon",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (galpon in galpones) {
                            FilterChip(
                                selected = vm.galponId == galpon.id,
                                onClick = { vm.elegirGalpon(galpon.id) },
                                label = { Text(galpon.nombre) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Agriculture,
                                        contentDescription = null,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            )
                        }
                    }
                    if (vm.mostrarError("galpon") && !vm.errorGalpon().esValido) {
                        Text(
                            text = vm.errorGalpon().mensaje,
                            color = MaterialTheme.colorScheme.error,
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
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tipo de accion",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        for (tipo in vm.tipos) {
                            FilterChip(
                                selected = vm.tipo == tipo,
                                onClick = { vm.cambiarTipo(tipo) },
                                label = { Text(tipo) }
                            )
                        }
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoValidado(
                    valor = vm.temperatura,
                    onValor = { vm.cambiarTemperatura(it) },
                    etiqueta = "Temperatura medida (C)",
                    icono = Icons.Outlined.Thermostat,
                    esNumero = true,
                    resultado = vm.errorTemperatura(),
                    mostrarError = vm.mostrarError("temperatura")
                )
                CampoValidado(
                    valor = vm.humedad,
                    onValor = { vm.cambiarHumedad(it) },
                    etiqueta = "Humedad relativa (%)",
                    icono = Icons.Outlined.WaterDrop,
                    esNumero = true,
                    resultado = vm.errorHumedad(),
                    mostrarError = vm.mostrarError("humedad")
                )
                CampoValidado(
                    valor = vm.responsable,
                    onValor = { vm.cambiarResponsable(it) },
                    etiqueta = "Responsable de la accion",
                    icono = Icons.Outlined.Person,
                    esNumero = false,
                    resultado = vm.errorResponsable(),
                    mostrarError = vm.mostrarError("responsable")
                )
                CampoValidado(
                    valor = vm.observacion,
                    onValor = { vm.cambiarObservacion(it) },
                    etiqueta = "Observacion",
                    icono = Icons.Outlined.Description,
                    esNumero = false,
                    resultado = vm.errorObservacion(),
                    mostrarError = vm.mostrarError("observacion"),
                    lineas = 3
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.registrar { onGuardado() } }) {
                    Text(text = "Registrar accion")
                }
                OutlinedButton(onClick = { vm.limpiar() }) {
                    Text(text = "Limpiar")
                }
            }
        }

        item {
            Text(
                text = "Los datos quedan guardados en el dispositivo y quedan disponibles " +
                    "para el supervisor aunque no haya conexion.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
