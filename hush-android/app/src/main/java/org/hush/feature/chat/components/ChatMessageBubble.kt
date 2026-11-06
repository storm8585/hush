package org.hush.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hush.core.designsystem.theme.IncomingBubbleShape
import org.hush.core.designsystem.theme.OutgoingBubbleShape
import org.hush.feature.chat.ChatMessageUiModel

@Composable
fun ChatMessageBubble(
    message: ChatMessageUiModel,
    modifier: Modifier = Modifier
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

    Box(
        contentAlignment = alignment,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 290.dp)
                .clip(bubbleShape)
                .background(containerColor)
        ) {
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 10.dp, top = 5.dp, bottom = 2.dp)
            ) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 20.sp
                    ),
                    color = contentColor
                )

                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.End)
                        .offset(y = (-3).dp)
                ) {
                    Text(
                        text = message.timestamp,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 11.sp,
                            lineHeight = 11.sp
                        ),
                        color = timestampColor
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
        }
    }
}
