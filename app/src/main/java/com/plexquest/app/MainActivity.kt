package com.plexquest.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.plexquest.app.ui.PlexQuestNavHost
import com.plexquest.app.ui.theme.PlexQuestTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlexQuestTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    PlexQuestNavHost()
                }
            }
        }
    }
}
