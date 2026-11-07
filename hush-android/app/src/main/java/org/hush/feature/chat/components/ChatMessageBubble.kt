package org.hush.feature.chat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import org.hush.feature.chat.ChatMessageUiModel
import kotlin.math.max
import kotlin.math.roundToInt

enum class MessageBubblePosition {
    Single,
    First,
    Middle,
    Last
}

fun getBubbleShape(
    isOutgoing: Boolean,
    position: MessageBubblePosition
): RoundedCornerShape {
    val large = 18.dp
    val small = 4.dp
    return if (isOutgoing) {
        when (position) {
            MessageBubblePosition.Single -> RoundedCornerShape(large)
            MessageBubblePosition.First -> RoundedCornerShape(
                topStart = large,
                topEnd = large,
                bottomStart = large,
                bottomEnd = small
            )
            MessageBubblePosition.Middle -> RoundedCornerShape(
                topStart = large,
                topEnd = small,
                bottomStart = large,
                bottomEnd = small
            )
            MessageBubblePosition.Last -> RoundedCornerShape(
                topStart = large,
                topEnd = small,
                bottomStart = large,
                bottomEnd = large
            )
        }
    } else {
        when (position) {
            MessageBubblePosition.Single -> RoundedCornerShape(large)
            MessageBubblePosition.First -> RoundedCornerShape(
                topStart = large,
                topEnd = large,
                bottomStart = small,
                bottomEnd = large
            )
            MessageBubblePosition.Middle -> RoundedCornerShape(
                topStart = small,
                topEnd = large,
                bottomStart = small,
                bottomEnd = large
            )
            MessageBubblePosition.Last -> RoundedCornerShape(
                topStart = small,
                topEnd = large,
                bottomStart = large,
                bottomEnd = large
            )
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageUiModel,
    modifier: Modifier = Modifier,
    position: MessageBubblePosition = MessageBubblePosition.Single,
    onSwipeToReply: (ChatMessageUiModel) -> Unit = {},
    onLongClick: (ChatMessageUiModel, Rect) -> Unit = { _, _ -> }
) {
    val isOutgoing = message.isOutgoing

    val contentColor = if (isOutgoing) Color.White else Color.Black
    val timestampColor = if (isOutgoing) Color.White else Color(0xFF8E8E93)

    val bubbleShape = getBubbleShape(isOutgoing, position)

    val bubbleBackgroundModifier = if (isOutgoing) {
        Modifier.background(
            Brush.verticalGradient(
                listOf(
                    Color(0xFF2499FF),
                    Color(0xFF4582B8)
                )
            )
        )
    } else {
        Modifier.background(Color(0xFFE5E5EA))
    }

    val alignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart

    val textMeasurer = rememberTextMeasurer()
    val messageTextStyle = MaterialTheme.typography.bodyLarge.copy(
        fontSize = 15.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp
    )

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val hapticFeedback = LocalHapticFeedback.current
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var bubbleCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val thresholdPx = with(LocalDensity.current) { 48.dp.toPx() }
    val maxDragPx = with(LocalDensity.current) { 68.dp.toPx() }

    val hasQuote = message.replyToText != null

    Layout(
        content = {
            Box(
                modifier = Modifier
                    .graphicsLayer {
                        translationX = offsetX.value
                    }
                    .onGloballyPositioned { coordinates ->
                        bubbleCoordinates = coordinates
                    }
                    .pointerInput(message.id) {
                        detectTapGestures(
                            onLongPress = {
                                hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                                val bounds = bubbleCoordinates?.boundsInRoot() ?: Rect.Zero
                                onLongClick(message, bounds)
                            }
                        )
                    }
                    .widthIn(max = 290.dp)
                    .clip(bubbleShape)
                    .then(bubbleBackgroundModifier)
                    .padding(start = 14.dp, end = 12.dp, top = 8.dp, bottom = 8.dp)
            ) {
            Layout(
                content = {
                    if (hasQuote) {
                        val quoteBarColor = if (isOutgoing) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }

                        val quoteContainerColor = if (isOutgoing) {
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.16f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 10.dp,
                                        topEnd = 10.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 0.dp
                                    )
                                )
                                .background(quoteContainerColor)
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(3.5.dp)
                                    .height(34.dp)
                                    .background(quoteBarColor)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp, top = 4.dp, bottom = 4.dp)
                            ) {
                                Text(
                                    text = message.replyToAuthor ?: "Reply",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.5.sp
                                    ),
                                    color = quoteBarColor,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = message.replyToText ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontSize = 12.sp,
                                        lineHeight = 15.sp
                                    ),
                                    color = contentColor.copy(alpha = 0.85f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    Text(
                        text = message.text,
                        style = messageTextStyle,
                        color = contentColor
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (message.isEdited) {
                            Text(
                                text = "edited",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    lineHeight = 10.sp,
                                    fontWeight = FontWeight.Normal
                                ),
                                color = timestampColor,
                                maxLines = 1,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                        }

                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 12.sp,
                                lineHeight = 12.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = timestampColor,
                            maxLines = 1,
                            softWrap = false
                        )

                        if (isOutgoing) {
                            Spacer(modifier = Modifier.width(4.dp))
                            ChatStatusIndicator(
                                status = message.status,
                                tint = timestampColor
                            )
                        }
                    }
                }
            ) { measurables, constraints ->
                val quoteMeasurable = if (hasQuote) measurables[0] else null
                val textMeasurable = if (hasQuote) measurables[1] else measurables[0]
                val infoMeasurable = if (hasQuote) measurables[2] else measurables[1]

                val infoPlaceable = infoMeasurable.measure(
                    Constraints(
                        minWidth = 0,
                        maxWidth = constraints.maxWidth,
                        minHeight = 0,
                        maxHeight = constraints.maxHeight
                    )
                )
                val infoWidth = infoPlaceable.width
                val infoHeight = infoPlaceable.height

                val textPlaceable = textMeasurable.measure(
                    Constraints(maxWidth = constraints.maxWidth)
                )

                val textLayoutResult = textMeasurer.measure(
                    text = AnnotatedString(message.text),
                    style = messageTextStyle,
                    constraints = Constraints(maxWidth = constraints.maxWidth)
                )

                val lineCount = textLayoutResult.lineCount
                val lastLineWidth = textLayoutResult.getLineRight(lineCount - 1)
                var maxLineWidth = 0f
                for (i in 0 until lineCount) {
                    val lineRight = textLayoutResult.getLineRight(i)
                    if (lineRight > maxLineWidth) {
                        maxLineWidth = lineRight
                    }
                }

                val spacing = 8.dp.roundToPx()
                val quoteSpacing = if (hasQuote) 5.dp.roundToPx() else 0

                val quotePlaceable = quoteMeasurable?.measure(
                    Constraints(
                        minWidth = 140.dp.roundToPx(),
                        maxWidth = constraints.maxWidth
                    )
                )
                val quoteWidth = quotePlaceable?.width ?: 0
                val quoteHeight = quotePlaceable?.height ?: 0

                val minContentWidth = max(quoteWidth, maxLineWidth.roundToInt())
                val textAndInfoWidth = (lastLineWidth + spacing + infoWidth).roundToInt()
                val fitsOnSameLine = (lastLineWidth + spacing + infoWidth) <= constraints.maxWidth

                val contentWidth: Int
                val totalHeight: Int
                val textY = if (hasQuote) quoteHeight + quoteSpacing else 0
                val infoX: Int
                val infoY: Int

                if (fitsOnSameLine) {
                    contentWidth = max(minContentWidth, textAndInfoWidth).coerceAtMost(constraints.maxWidth)
                    if (lineCount == 1) {
                        val bottomDrop = 1.dp.roundToPx()
                        val textRowHeight = max(textPlaceable.height, infoHeight) + bottomDrop
                        totalHeight = textY + textRowHeight
                        infoX = contentWidth - infoWidth
                        infoY = totalHeight - infoHeight
                    } else {
                        totalHeight = textY + textPlaceable.height
                        infoX = contentWidth - infoWidth
                        infoY = totalHeight - infoHeight
                    }
                } else {
                    contentWidth = max(minContentWidth, infoWidth).coerceAtMost(constraints.maxWidth)
                    val tightGap = 2.dp.roundToPx()
                    totalHeight = textY + textPlaceable.height + infoHeight - tightGap
                    infoX = contentWidth - infoWidth
                    infoY = textY + textPlaceable.height - tightGap
                }

                val finalQuotePlaceable = if (hasQuote && quotePlaceable != null && quotePlaceable.width != contentWidth) {
                    quoteMeasurable?.measure(
                        Constraints(
                            minWidth = contentWidth,
                            maxWidth = contentWidth
                        )
                    ) ?: quotePlaceable
                } else {
                    quotePlaceable
                }

                layout(contentWidth, totalHeight) {
                    finalQuotePlaceable?.placeRelative(0, 0)
                    textPlaceable.placeRelative(0, textY)
                    infoPlaceable.placeRelative(infoX, infoY)
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .graphicsLayer {
                    val p = (-offsetX.value / thresholdPx).coerceIn(0f, 1f)
                    val scale = 0.3f + p * 0.7f
                    scaleX = scale
                    scaleY = scale
                    alpha = p
                    rotationZ = (1f - p) * 45f
                    if (!isOutgoing) {
                        translationX = -offsetX.value * 0.25f
                    }
                }
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Reply,
                contentDescription = "Reply",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
        }
    },
    modifier = modifier
        .fillMaxWidth()
        .pointerInput(message.id) {
            detectHorizontalDragGestures(
                onDragStart = {
                    hasTriggeredHaptic = false
                },
                onDragEnd = {
                    if (-offsetX.value >= thresholdPx) {
                        onSwipeToReply(message)
                    }
                    coroutineScope.launch {
                        offsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                },
                onDragCancel = {
                    coroutineScope.launch {
                        offsetX.animateTo(
                            targetValue = 0f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                    }
                },
                onHorizontalDrag = { change, dragAmount ->
                    if (dragAmount < 0 || offsetX.value < 0) {
                        change.consume()
                        val newOffset = (offsetX.value + dragAmount * 0.55f).coerceIn(-maxDragPx, 0f)
                        coroutineScope.launch {
                            offsetX.snapTo(newOffset)
                        }
                        if (-newOffset >= thresholdPx && !hasTriggeredHaptic) {
                            hasTriggeredHaptic = true
                            hapticFeedback.performHapticFeedback(HapticFeedbackType.LongPress)
                        } else if (-newOffset < thresholdPx && hasTriggeredHaptic) {
                            hasTriggeredHaptic = false
                        }
                    }
                }
            )
        }
) { measurables, constraints ->
    val bubblePlaceable = measurables[0].measure(
        constraints.copy(minWidth = 0, minHeight = 0)
    )
    val iconPlaceable = measurables.getOrNull(1)?.measure(Constraints())

    val bubbleX = if (isOutgoing) {
        constraints.maxWidth - bubblePlaceable.width
    } else {
        0
    }

    layout(constraints.maxWidth, bubblePlaceable.height) {
        bubblePlaceable.placeRelative(bubbleX, 0)

        if (iconPlaceable != null) {
            val iconY = (bubblePlaceable.height - iconPlaceable.height) / 2
            val iconX = if (isOutgoing) {
                constraints.maxWidth - iconPlaceable.width - 8.dp.roundToPx()
            } else {
                maxOf(0, bubblePlaceable.width - 38.dp.roundToPx())
            }
            iconPlaceable.placeRelative(iconX, iconY)
        }
    }
}
}
