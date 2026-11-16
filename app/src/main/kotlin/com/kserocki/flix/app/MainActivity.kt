package com.kserocki.flix.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kserocki.flix.app.navigation.FlixNavHost
import com.kserocki.flix.core.designsystem.theme.FlixTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlixTheme {
                FlixNavHost()
            }
        }
    }
}
