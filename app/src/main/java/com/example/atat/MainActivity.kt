package com.example.atat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.atat.nativo.Notificador
import com.example.atat.ui.navegacion.AtatApp
import com.example.atat.ui.theme.AtatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Notificador(applicationContext).crearCanal()
        setContent {
            AtatTheme {
                AtatApp()
            }
        }
    }
}
