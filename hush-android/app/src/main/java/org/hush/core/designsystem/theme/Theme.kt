package org.hush.core.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun HushTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = HushLightColorScheme,
        typography = HushTypography,
        shapes = HushShapes,
        content = content
    )
}
