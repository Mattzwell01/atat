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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.atat.data.model.Galpon
import com.example.atat.ui.componentes.CampoValidado
import com.example.atat.ui.theme.NaranjaAdvertencia
import com.example.atat.ui.theme.RojoCritico
import com.example.atat.ui.viewmodel.UmbralesViewModel

@Composable
fun ConfigurarUmbralesScreen(
    vm: UmbralesViewModel,
    galpon: Galpon?,
    onVolver: () -> Unit,
    onGuardado: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = galpon?.nombre ?: "Galpon",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Define los limites que usa la app para clasificar el estado " +
                            "del galpon y generar las alertas.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                CampoValidado(
                    valor = vm.advertencia,
                    onValor = { vm.cambiarAdvertencia(it) },
                    etiqueta = "Limite de advertencia (C)",
                    icono = Icons.Outlined.Thermostat,
                    esNumero = true,
                    resultado = vm.errorAdvertencia(),
                    mostrarError = vm.mostrarError("advertencia")
                )
                CampoValidado(
                    valor = vm.critico,
                    onValor = { vm.cambiarCritico(it) },
                    etiqueta = "Limite critico (C)",
                    icono = Icons.Outlined.Thermostat,
                    esNumero = true,
                    resultado = vm.errorCritico(),
                    mostrarError = vm.mostrarError("critico")
                )
                if (vm.mostrarError("critico") && !vm.errorRelacion().esValido) {
                    Text(
                        text = vm.errorRelacion().mensaje,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Como se vera el galpon",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Normal por debajo de " + vm.advertencia + " C",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Advertencia entre " + vm.advertencia + " C y " + vm.critico + " C",
                        style = MaterialTheme.typography.bodySmall,
                        color = NaranjaAdvertencia
                    )
                    Text(
                        text = "Critico sobre " + vm.critico + " C",
                        style = MaterialTheme.typography.bodySmall,
                        color = RojoCritico
                    )
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { vm.guardar { onGuardado() } }) {
                    Text(text = "Guardar umbrales")
                }
                OutlinedButton(onClick = { onVolver() }) {
                    Text(text = "Cancelar")
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}
