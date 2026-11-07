package org.hush.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val PrimaryLight = Color(0xFF0088FF)
val OnPrimaryLight = Color(0xFFFFFFFF)
val PrimaryContainerLight = Color(0xFFE5E5EA)
val OnPrimaryContainerLight = Color(0xFF000000)

val SecondaryLight = Color(0xFF625B71)
val OnSecondaryLight = Color(0xFFFFFFFF)
val SecondaryContainerLight = Color(0xFFE8DEF8)
val OnSecondaryContainerLight = Color(0xFF1D192B)

val TertiaryLight = Color(0xFF7D5260)
val OnTertiaryLight = Color(0xFFFFFFFF)
val TertiaryContainerLight = Color(0xFFFFD8E4)
val OnTertiaryContainerLight = Color(0xFF31111D)

val ErrorLight = Color(0xFFB3261E)
val OnErrorLight = Color(0xFFFFFFFF)
val ErrorContainerLight = Color(0xFFF9DEDC)
val OnErrorContainerLight = Color(0xFF410E0B)

val SurfaceLight = Color(0xFFF3F3F3)
val OnSurfaceLight = Color(0xFF000000)
val OnSurfaceVariantLight = Color(0xFF8E8E93)

val SurfaceContainerLowestLight = Color(0xFFFFFFFF)
val SurfaceContainerLowLight = Color(0xFFF7F2FA)
val SurfaceContainerLight = Color(0xFFF3EDF7)
val SurfaceContainerHighLight = Color(0xFFECE6F0)
val SurfaceContainerHighestLight = Color(0xFFE6E0E9)

val SurfaceDimLight = Color(0xFFDED8E1)
val SurfaceBrightLight = Color(0xFFFFFFFF)

val OutlineLight = Color(0xFF79747E)
val OutlineVariantLight = Color(0xFFCAC4D0)

val InverseSurfaceLight = Color(0xFF322F35)
val InverseOnSurfaceLight = Color(0xFFF5EFF7)
val InversePrimaryLight = Color(0xFFD0BCFF)

val HushLightColorScheme: ColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    primaryContainer = PrimaryContainerLight,
    onPrimaryContainer = OnPrimaryContainerLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    secondaryContainer = SecondaryContainerLight,
    onSecondaryContainer = OnSecondaryContainerLight,
    tertiary = TertiaryLight,
    onTertiary = OnTertiaryLight,
    tertiaryContainer = TertiaryContainerLight,
    onTertiaryContainer = OnTertiaryContainerLight,
    error = ErrorLight,
    onError = OnErrorLight,
    errorContainer = ErrorContainerLight,
    onErrorContainer = OnErrorContainerLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    surfaceContainerLowest = SurfaceContainerLowestLight,
    surfaceContainerLow = SurfaceContainerLowLight,
    surfaceContainer = SurfaceContainerLight,
    surfaceContainerHigh = SurfaceContainerHighLight,
    surfaceContainerHighest = SurfaceContainerHighestLight,
    surfaceDim = SurfaceDimLight,
    surfaceBright = SurfaceBrightLight,
    outline = OutlineLight,
    outlineVariant = OutlineVariantLight,
    inverseSurface = InverseSurfaceLight,
    inverseOnSurface = InverseOnSurfaceLight,
    inversePrimary = InversePrimaryLight
)
