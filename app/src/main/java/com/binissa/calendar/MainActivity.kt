package com.binissa.calendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.binissa.calendar.calendar.CalendarRoute
import com.binissa.calendar.calendar.components.morph.DirectionalTextTransition
import com.binissa.calendar.calendar.components.morph.DirectionalTransitionConfig
import com.binissa.calendar.calendar.components.morph.TransitionControlPanel
import com.binissa.calendar.ui.theme.CalendarTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalendarTheme {
                var currentConfig by remember { mutableStateOf(DirectionalTransitionConfig()) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TransitionControlPanel(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        onConfigChanged = { currentConfig = it }
                    )
                }
            }
        }
    }
}
