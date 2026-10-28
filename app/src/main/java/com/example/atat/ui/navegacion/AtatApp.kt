package com.example.atat.ui.navegacion

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.atat.data.model.Galpon
import com.example.atat.nativo.Notificador
import com.example.atat.nativo.UbicacionDispositivo
import com.example.atat.ui.screen.AlertasScreen
import com.example.atat.ui.screen.ConfigurarUmbralesScreen
import com.example.atat.ui.screen.DetalleGalponScreen
import com.example.atat.ui.screen.HistorialScreen
import com.example.atat.ui.screen.InicioScreen
import com.example.atat.ui.screen.RegistrarIncidenciaScreen
import com.example.atat.ui.viewmodel.MainViewModel
import com.example.atat.ui.viewmodel.RegistroViewModel
import com.example.atat.ui.viewmodel.UmbralesViewModel

object Rutas {
    const val INICIO = "inicio"
    const val ALERTAS = "alertas"
    const val HISTORIAL = "historial"
    const val GALPON = "galpon/{galponId}"
    const val REGISTRAR = "registrar"
    const val UMBRALES = "umbrales/{galponId}"

    fun galpon(id: String): String {
        return "galpon/" + id
    }

    fun umbrales(id: String): String {
        return "umbrales/" + id
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AtatApp() {
    val navController = rememberNavController()
    val contexto = LocalContext.current
    val vm: MainViewModel = viewModel()

    val galpones by vm.galpones.collectAsStateWithLifecycle()
    val mediciones by vm.mediciones.collectAsStateWithLifecycle()
    val alertas by vm.alertas.collectAsStateWithLifecycle()
    val eventos by vm.eventos.collectAsStateWithLifecycle()
    val resumen by vm.resumen.collectAsStateWithLifecycle()
    val perfil by vm.perfil.collectAsStateWithLifecycle()
    val cargando by vm.cargando.collectAsStateWithLifecycle()
    val mensaje by vm.mensaje.collectAsStateWithLifecycle()
    val hayConexion by vm.hayConexion.collectAsStateWithLifecycle()
    val ultimaActualizacion by vm.ultimaActualizacion.collectAsStateWithLifecycle()
    val temperaturaDispositivo by vm.temperaturaDispositivo.collectAsStateWithLifecycle()
    val posicion by vm.posicion.collectAsStateWithLifecycle()

    val entrada by navController.currentBackStackEntryAsState()
    val ruta = entrada?.destination?.route
    val enPrincipal = ruta == Rutas.INICIO || ruta == Rutas.ALERTAS || ruta == Rutas.HISTORIAL
    val idGalpon = entrada?.arguments?.getString("galponId")
    var nombreGalpon = ""
    for (galpon in galpones) {
        if (galpon.id == idGalpon) {
            nombreGalpon = galpon.nombre
        }
    }
    val titulo = when (ruta) {
        Rutas.INICIO -> "ATAT"
        Rutas.ALERTAS -> "Alertas"
        Rutas.HISTORIAL -> "Historial"
        Rutas.GALPON -> nombreGalpon
        Rutas.REGISTRAR -> "Registrar accion"
        Rutas.UMBRALES -> "Umbrales de alerta"
        else -> "ATAT"
    }

    val snackbarHost = remember { SnackbarHostState() }
    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) {
            snackbarHost.showSnackbar(mensaje)
            vm.limpiarMensaje()
        }
    }

    val permisoNotificaciones = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (!concedido) {
            vm.mostrarMensaje("Sin permiso de notificaciones no habra avisos de alerta critica")
        }
    }

    val permisoUbicacion = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            vm.leerUbicacion()
        } else {
            vm.mostrarMensaje("Se necesita el permiso de ubicacion para calcular la distancia")
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificador = Notificador(contexto.applicationContext)
            if (!notificador.permisoConcedido()) {
                permisoNotificaciones.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = titulo, fontWeight = FontWeight.Bold)
                        Text(
                            text = "Alerta temprana de altas temperaturas",
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    if (!enPrincipal) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver"
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { vm.actualizar() }) {
                        Icon(
                            imageVector = Icons.Outlined.Refresh,
                            contentDescription = "Actualizar"
                        )
                    }
                }
            )
        },
        bottomBar = {
            if (enPrincipal) {
                NavigationBar {
                    NavigationBarItem(
                        selected = ruta == Rutas.INICIO,
                        onClick = { navController.navigate(Rutas.INICIO) { launchSingleTop = true } },
                        icon = { Icon(Icons.Outlined.Home, contentDescription = null) },
                        label = { Text("Galpones") }
                    )
                    NavigationBarItem(
                        selected = ruta == Rutas.ALERTAS,
                        onClick = { navController.navigate(Rutas.ALERTAS) { launchSingleTop = true } },
                        icon = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
                        label = { Text("Alertas") }
                    )
                    NavigationBarItem(
                        selected = ruta == Rutas.HISTORIAL,
                        onClick = { navController.navigate(Rutas.HISTORIAL) { launchSingleTop = true } },
                        icon = { Icon(Icons.Outlined.History, contentDescription = null) },
                        label = { Text("Historial") }
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHost) }
    ) { relleno ->
        NavHost(
            navController = navController,
            startDestination = Rutas.INICIO,
            modifier = Modifier.padding(relleno)
        ) {
            composable(Rutas.INICIO) {
                InicioScreen(
                    galpones = galpones,
                    resumen = resumen,
                    perfil = perfil,
                    cargando = cargando,
                    hayConexion = hayConexion,
                    ultimaActualizacion = ultimaActualizacion,
                    onPerfil = { vm.cambiarPerfil(it) },
                    onGalpon = { navController.navigate(Rutas.galpon(it)) },
                    onActualizar = { vm.actualizar() }
                )
            }
            composable(Rutas.ALERTAS) {
                AlertasScreen(
                    alertas = alertas,
                    onResolver = { vm.resolverAlerta(it) },
                    onMarcarVista = { vm.marcarAlertaVista(it) },
                    onGalpon = { navController.navigate(Rutas.galpon(it)) }
                )
            }
            composable(Rutas.HISTORIAL) {
                HistorialScreen(mediciones = mediciones, galpones = galpones)
            }
            composable(
                route = Rutas.GALPON,
                arguments = listOf(navArgument("galponId") { type = NavType.StringType })
            ) { pantalla ->
                val galponId = pantalla.arguments?.getString("galponId").orEmpty()
                var galpon: Galpon? = null
                for (item in galpones) {
                    if (item.id == galponId) {
                        galpon = item
                    }
                }
                DetalleGalponScreen(
                    galpon = galpon,
                    mediciones = mediciones,
                    alertas = alertas,
                    eventos = eventos,
                    temperaturaDispositivo = temperaturaDispositivo,
                    sensorDisponible = vm.sensorDisponible(),
                    posicion = posicion,
                    hayConexion = hayConexion,
                    onRegistrar = { navController.navigate(Rutas.REGISTRAR) },
                    onUmbrales = { navController.navigate(Rutas.umbrales(it)) },
                    onLeerSensor = { vm.leerSensor() },
                    onLeerUbicacion = {
                        val ubicacion = UbicacionDispositivo(contexto.applicationContext)
                        if (ubicacion.permisoConcedido()) {
                            vm.leerUbicacion()
                        } else {
                            permisoUbicacion.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                    }
                )
            }
            composable(Rutas.REGISTRAR) {
                val registroVm: RegistroViewModel = viewModel()
                RegistrarIncidenciaScreen(
                    vm = registroVm,
                    onVolver = { navController.popBackStack() },
                    onGuardado = {
                        vm.mostrarMensaje("Accion registrada en el dispositivo")
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = Rutas.UMBRALES,
                arguments = listOf(navArgument("galponId") { type = NavType.StringType })
            ) { pantalla ->
                val galponId = pantalla.arguments?.getString("galponId").orEmpty()
                var galpon: Galpon? = null
                for (item in galpones) {
                    if (item.id == galponId) {
                        galpon = item
                    }
                }
                val umbralesVm: UmbralesViewModel = viewModel()
                ConfigurarUmbralesScreen(
                    vm = umbralesVm,
                    galpon = galpon,
                    onVolver = { navController.popBackStack() },
                    onGuardado = {
                        vm.mostrarMensaje("Umbrales actualizados")
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
