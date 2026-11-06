package org.hush.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
    val isShortMessage = message.text.length <= 32 && !message.text.contains('\n')

    Box(
        contentAlignment = alignment,
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .clip(bubbleShape)
                .background(containerColor)
        ) {
            if (isShortMessage) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = contentColor
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 1.dp)
                    ) {
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
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
            } else {
                Column(
                    modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 6.dp, bottom = 4.dp)
                ) {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge,
                        color = contentColor
                    )

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 1.dp)
                    ) {
                        Text(
                            text = message.timestamp,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
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
}
