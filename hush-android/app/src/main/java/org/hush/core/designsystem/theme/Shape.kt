package org.hush.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val ShapeCornerNone = CornerSize(0.dp)
val ShapeCornerExtraSmall = CornerSize(4.dp)
val ShapeCornerSmall = CornerSize(8.dp)
val ShapeCornerMedium = CornerSize(12.dp)
val ShapeCornerLarge = CornerSize(16.dp)
val ShapeCornerLargeIncreased = CornerSize(20.dp)
val ShapeCornerExtraLarge = CornerSize(28.dp)
val ShapeCornerExtraLargeIncreased = CornerSize(32.dp)
val ShapeCornerExtraExtraLarge = CornerSize(48.dp)

val HushShapes = Shapes(
    extraSmall = RoundedCornerShape(ShapeCornerExtraSmall),
    small = RoundedCornerShape(ShapeCornerSmall),
    medium = RoundedCornerShape(ShapeCornerMedium),
    large = RoundedCornerShape(ShapeCornerLarge),
    extraLarge = RoundedCornerShape(ShapeCornerExtraLarge)
)

val OutgoingBubbleShape = RoundedCornerShape(
    topStart = ShapeCornerLargeIncreased,
    topEnd = ShapeCornerLargeIncreased,
    bottomStart = ShapeCornerLargeIncreased,
    bottomEnd = ShapeCornerExtraSmall
)

val IncomingBubbleShape = RoundedCornerShape(
    topStart = ShapeCornerLargeIncreased,
    topEnd = ShapeCornerLargeIncreased,
    bottomStart = ShapeCornerExtraSmall,
    bottomEnd = ShapeCornerLargeIncreased
)

val InputContainerShape = RoundedCornerShape(ShapeCornerExtraLargeIncreased)
val DateChipShape = RoundedCornerShape(ShapeCornerMedium)
val PillShape = CircleShape
