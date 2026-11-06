package org.hush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.hush.core.designsystem.theme.HushTheme
import org.hush.feature.chat.ChatRoute

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HushTheme {
                ChatRoute()
            }
        }
    }
}
