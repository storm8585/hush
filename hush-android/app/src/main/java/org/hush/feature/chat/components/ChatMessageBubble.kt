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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
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
import org.hush.core.designsystem.theme.IncomingBubbleShape
import org.hush.core.designsystem.theme.OutgoingBubbleShape
import org.hush.feature.chat.ChatMessageUiModel
import kotlin.math.max
import kotlin.math.roundToInt

@Composable
fun ChatMessageBubble(
    message: ChatMessageUiModel,
    modifier: Modifier = Modifier,
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

    val bubbleShape = if (isOutgoing) {
        OutgoingBubbleShape
    } else {
        IncomingBubbleShape
    }

    val alignment = if (isOutgoing) Alignment.CenterEnd else Alignment.CenterStart

    val textMeasurer = rememberTextMeasurer()
    val messageTextStyle = MaterialTheme.typography.bodyLarge.copy(
        lineHeight = 20.sp
    )

    val coroutineScope = rememberCoroutineScope()
    val offsetX = remember { Animatable(0f) }
    val hapticFeedback = LocalHapticFeedback.current
    var hasTriggeredHaptic by remember { mutableStateOf(false) }

    val thresholdPx = with(LocalDensity.current) { 48.dp.toPx() }
    val maxDragPx = with(LocalDensity.current) { 68.dp.toPx() }

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
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp)
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
                .graphicsLayer {
                    translationX = offsetX.value
                }
                .widthIn(max = 290.dp)
                .clip(bubbleShape)
                .background(containerColor)
                .padding(start = 14.dp, end = 12.dp, top = 8.dp, bottom = 6.dp)
        ) {
            Column {
                if (message.replyToText != null) {
                    val quoteBarColor = if (isOutgoing) {
                        MaterialTheme.colorScheme.onPrimary
                    } else {
                        MaterialTheme.colorScheme.primary
                    }

                    val quoteContainerColor = if (isOutgoing) {
                        MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.15f)
                    } else {
                        MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.7f)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(quoteContainerColor)
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(28.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(quoteBarColor)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = message.replyToAuthor ?: "Reply",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                ),
                                color = quoteBarColor,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = message.replyToText,
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

                Layout(
                    content = {
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
                    val infoPlaceable = measurables[1].measure(
                        Constraints(
                            minWidth = 0,
                            maxWidth = constraints.maxWidth,
                            minHeight = 0,
                            maxHeight = constraints.maxHeight
                        )
                    )
                    val infoWidth = infoPlaceable.width
                    val infoHeight = infoPlaceable.height

                    val textPlaceable = measurables[0].measure(
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
                    val fitsOnSameLine = (lastLineWidth + spacing + infoWidth) <= constraints.maxWidth

                    val totalWidth: Int
                    val totalHeight: Int
                    val infoX: Int
                    val infoY: Int

                    if (fitsOnSameLine) {
                        totalWidth = max(maxLineWidth.roundToInt(), (lastLineWidth + spacing + infoWidth).roundToInt())
                        if (lineCount == 1) {
                            val bottomDrop = 1.dp.roundToPx()
                            totalHeight = max(textPlaceable.height, infoHeight) + bottomDrop
                            infoX = totalWidth - infoWidth
                            infoY = totalHeight - infoHeight
                        } else {
                            totalHeight = textPlaceable.height
                            infoX = totalWidth - infoWidth
                            infoY = totalHeight - infoHeight
                        }
                    } else {
                        totalWidth = max(maxLineWidth.roundToInt(), infoWidth)
                        val tightGap = 2.dp.roundToPx()
                        totalHeight = textPlaceable.height + infoHeight - tightGap
                        infoX = totalWidth - infoWidth
                        infoY = textPlaceable.height - tightGap
                    }

                    layout(totalWidth, totalHeight) {
                        textPlaceable.placeRelative(0, 0)
                        infoPlaceable.placeRelative(infoX, infoY)
                    }
                }
            }
        }
    }
}
