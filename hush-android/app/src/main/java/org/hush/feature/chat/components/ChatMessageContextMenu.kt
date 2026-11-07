package org.hush.feature.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hush.feature.chat.ChatMessageUiModel

@Composable
fun ChatMessageContextMenu(
    message: ChatMessageUiModel?,
    onDismiss: () -> Unit,
    onReply: (ChatMessageUiModel) -> Unit,
    onCopy: (ChatMessageUiModel) -> Unit,
    onEdit: (ChatMessageUiModel) -> Unit,
    onDelete: (ChatMessageUiModel) -> Unit,
    modifier: Modifier = Modifier
) {
    if (message == null) return

    val interactionSource = remember { MutableInteractionSource() }
    val visibleState = remember {
        MutableTransitionState(false).apply {
            targetState = true
        }
    }

    LaunchedEffect(visibleState.currentState, visibleState.targetState) {
        if (!visibleState.targetState && !visibleState.currentState) {
            onDismiss()
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.36f))
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                visibleState.targetState = false
            }
    ) {
        AnimatedVisibility(
            visibleState = visibleState,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                initialScale = 0.65f
            ) + fadeIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                )
            ),
            exit = scaleOut(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMedium
                ),
                targetScale = 0.8f
            ) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(18.dp),
                        ambientColor = Color.Black.copy(alpha = 0.15f),
                        spotColor = Color.Black.copy(alpha = 0.25f)
                    )
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {}
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ContextMenuItem(
                        icon = Icons.AutoMirrored.Filled.Reply,
                        title = "Reply",
                        tint = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onReply(message)
                            onDismiss()
                        }
                    )

                    HorizontalDivider(color = Color(0xFFF2F2F4), thickness = 0.8.dp)

                    ContextMenuItem(
                        icon = Icons.Default.ContentCopy,
                        title = "Copy",
                        tint = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onCopy(message)
                            onDismiss()
                        }
                    )

                    if (message.isOutgoing) {
                        HorizontalDivider(color = Color(0xFFF2F2F4), thickness = 0.8.dp)

                        ContextMenuItem(
                            icon = Icons.Default.Edit,
                            title = "Edit",
                            tint = MaterialTheme.colorScheme.onSurface,
                            onClick = {
                                onEdit(message)
                                onDismiss()
                            }
                        )
                    }

                    HorizontalDivider(color = Color(0xFFF2F2F4), thickness = 0.8.dp)

                    ContextMenuItem(
                        icon = Icons.Default.DeleteOutline,
                        title = "Delete",
                        tint = MaterialTheme.colorScheme.onSurface,
                        onClick = {
                            onDelete(message)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextMenuItem(
    icon: ImageVector,
    title: String,
    tint: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            color = tint
        )

        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = tint,
            modifier = Modifier.size(19.dp)
        )
    }
}
