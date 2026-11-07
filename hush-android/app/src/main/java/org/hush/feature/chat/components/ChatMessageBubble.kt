package org.hush.feature.chat.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
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
    val large = 20.dp
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
    onSwipeToReply: (ChatMessageUiModel) -> Unit = {}
) {
    val isOutgoing = message.isOutgoing

    val containerColor = if (isOutgoing) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainerHigh
    }

    val contentColor = if (isOutgoing) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    val timestampColor = if (isOutgoing) {
        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.72f)
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    val bubbleShape = getBubbleShape(isOutgoing, position)

    val alignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart

    val textMeasurer = rememberTextMeasurer()
    val messageTextStyle = MaterialTheme.typography.bodyLarge.copy(
        lineHeight = 20.sp
    )

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val hapticFeedback = LocalHapticFeedback.current
    var hasTriggeredHaptic by remember { mutableStateOf(false) }
    var bubbleWidth by remember { mutableIntStateOf(0) }

    val thresholdPx = with(LocalDensity.current) { 48.dp.toPx() }
    val maxDragPx = with(LocalDensity.current) { 68.dp.toPx() }

    val hasQuote = message.replyToText != null

    Box(
        contentAlignment = alignment,
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
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
                                )
                            )
                        }
                    },
                    onDragCancel = {
                        coroutineScope.launch {
                            offsetX.animateTo(
                                targetValue = 0f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMediumLow
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
    ) {
        val dragProgress = (-offsetX.value / thresholdPx).coerceIn(0f, 1f)
        if (dragProgress > 0f) {
            val iconModifier = if (isOutgoing) {
                Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
            } else {
                Modifier
                    .align(Alignment.CenterStart)
                    .offset {
                        val iconX = maxOf(0, bubbleWidth - 42.dp.roundToPx() - (offsetX.value * 0.25f).roundToInt())
                        IntOffset(x = iconX, y = 0)
                    }
            }

            Box(
                contentAlignment = Alignment.Center,
                modifier = iconModifier
                    .graphicsLayer {
                        val scale = 0.3f + dragProgress * 0.7f
                        scaleX = scale
                        scaleY = scale
                        alpha = dragProgress
                        rotationZ = (1f - dragProgress) * 45f
                    }
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Reply,
                    contentDescription = "Reply",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .onSizeChanged { bubbleWidth = it.width }
                .graphicsLayer {
                    translationX = offsetX.value
                }
                .widthIn(max = 290.dp)
                .clip(bubbleShape)
                .background(containerColor)
                .padding(start = 10.dp, end = 10.dp, top = 7.dp, bottom = 6.dp)
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
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                lineHeight = 11.sp,
                                fontWeight = FontWeight.Normal
                            ),
                            color = timestampColor,
                            maxLines = 1,
                            softWrap = false
                        )

                        if (isOutgoing) {
                            Spacer(modifier = Modifier.width(3.dp))
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
    }
}
