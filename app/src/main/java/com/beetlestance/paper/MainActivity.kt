package com.beetlestance.paper

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.insets.ProvideWindowInsets
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Draw behind the system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            ProvideWindowInsets {
                MaterialTheme(
                    colors = MaterialTheme.colors.copy(
                        primary = Color(android.graphics.Color.parseColor("#CED2F8")),
                        surface = Color(android.graphics.Color.parseColor("#353846")),
                        background = Color(android.graphics.Color.parseColor("#353846"))
                    )
                ) {
                    Home()
                }
            }
        }
    }
}