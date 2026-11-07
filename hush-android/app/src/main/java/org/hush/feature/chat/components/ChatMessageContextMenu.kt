package org.hush.feature.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hush.feature.chat.ChatMessageUiModel
import kotlin.math.roundToInt

@Composable
fun ChatMessageContextMenu(
    message: ChatMessageUiModel?,
    bounds: Rect?,
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

    val spotlightScale by animateFloatAsState(
        targetValue = if (visibleState.targetState) 1.035f else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "spotlight_scale"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                visibleState.targetState = false
            }
    ) {
        val density = LocalDensity.current
        val screenWidthPx = constraints.maxWidth.toFloat()
        val screenHeightPx = constraints.maxHeight.toFloat()

        val menuWidth = 224.dp
        val menuHeight = if (message.isOutgoing) 206.dp else 158.dp
        val menuWidthPx = with(density) { menuWidth.toPx() }
        val menuHeightPx = with(density) { menuHeight.toPx() }

        val spacing = with(density) { 10.dp.toPx() }
        val edgePadding = with(density) { 14.dp.toPx() }
        val topSafeArea = with(density) { 88.dp.toPx() }
        val bottomSafeArea = with(density) { 96.dp.toPx() }

        val targetBounds = bounds ?: Rect(
            left = (screenWidthPx - menuWidthPx) / 2,
            top = (screenHeightPx - menuHeightPx) / 2,
            right = (screenWidthPx + menuWidthPx) / 2,
            bottom = (screenHeightPx + menuHeightPx) / 2
        )

        val spaceBelow = screenHeightPx - bottomSafeArea - targetBounds.bottom
        val spaceAbove = targetBounds.top - topSafeArea

        val isBelow = spaceBelow >= menuHeightPx + spacing || spaceBelow >= spaceAbove

        val menuY = if (isBelow) {
            targetBounds.bottom + spacing
        } else {
            targetBounds.top - spacing - menuHeightPx
        }.coerceIn(topSafeArea, screenHeightPx - bottomSafeArea - menuHeightPx)

        val menuX = if (message.isOutgoing) {
            targetBounds.right - menuWidthPx
        } else {
            targetBounds.left
        }.coerceIn(edgePadding, screenWidthPx - edgePadding - menuWidthPx)

        val transformOrigin = TransformOrigin(
            pivotFractionX = if (message.isOutgoing) 0.88f else 0.12f,
            pivotFractionY = if (isBelow) 0f else 1.0f
        )

        AnimatedVisibility(
            visibleState = visibleState,
            enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)),
            exit = fadeOut(spring(stiffness = Spring.StiffnessMedium))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.28f))
            )
        }

        if (targetBounds.width > 0 && targetBounds.height > 0) {
            AnimatedVisibility(
                visibleState = visibleState,
                enter = fadeIn(spring(stiffness = Spring.StiffnessMedium)),
                exit = fadeOut(spring(stiffness = Spring.StiffnessMedium))
            ) {
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = targetBounds.left.roundToInt(),
                                y = targetBounds.top.roundToInt()
                            )
                        }
                        .width(with(density) { targetBounds.width.toDp() })
                        .height(with(density) { targetBounds.height.toDp() })
                        .graphicsLayer {
                            scaleX = spotlightScale
                            scaleY = spotlightScale
                        }
                        .shadow(
                            elevation = 16.dp,
                            shape = getBubbleShape(message.isOutgoing, MessageBubblePosition.Single),
                            ambientColor = Color.Black.copy(alpha = 0.22f),
                            spotColor = if (message.isOutgoing) MaterialTheme.colorScheme.primary.copy(alpha = 0.45f) else Color.Black.copy(alpha = 0.28f)
                        )
                ) {
                    ChatMessageBubble(
                        message = message,
                        position = MessageBubblePosition.Single
                    )
                }
            }
        }

        AnimatedVisibility(
            visibleState = visibleState,
            enter = scaleIn(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMediumLow
                ),
                initialScale = 0.55f,
                transformOrigin = transformOrigin
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
                targetScale = 0.75f,
                transformOrigin = transformOrigin
            ) + fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            x = menuX.roundToInt(),
                            y = menuY.roundToInt()
                        )
                    }
                    .width(menuWidth)
                    .shadow(
                        elevation = 18.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color.Black.copy(alpha = 0.12f),
                        spotColor = Color(0xFF00384D).copy(alpha = 0.20f)
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFE0F7FA).copy(alpha = 0.85f),
                                Color(0xFFB2EBF2).copy(alpha = 0.35f),
                                Color(0xFFFFFFFF).copy(alpha = 0.15f)
                            )
                        ),
                        shape = RoundedCornerShape(22.dp)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFFFAFDFE).copy(alpha = 0.95f),
                                Color(0xFFF1F9FB).copy(alpha = 0.93f)
                            )
                        )
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {}
                    .padding(vertical = 6.dp, horizontal = 6.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    SeaGlassMenuItem(
                        icon = Icons.AutoMirrored.Filled.Reply,
                        title = "Reply",
                        iconBackground = Color(0xFFEFF3FE),
                        iconTint = Color(0xFF4C68D7),
                        onClick = {
                            onReply(message)
                            onDismiss()
                        }
                    )

                    HorizontalDivider(
                        color = Color(0xFFE2F1F5).copy(alpha = 0.7f),
                        thickness = 0.7.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    SeaGlassMenuItem(
                        icon = Icons.Default.ContentCopy,
                        title = "Copy",
                        iconBackground = Color(0xFFE8F8F5),
                        iconTint = Color(0xFF1E8E75),
                        onClick = {
                            onCopy(message)
                            onDismiss()
                        }
                    )

                    if (message.isOutgoing) {
                        HorizontalDivider(
                            color = Color(0xFFE2F1F5).copy(alpha = 0.7f),
                            thickness = 0.7.dp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        SeaGlassMenuItem(
                            icon = Icons.Default.Edit,
                            title = "Edit",
                            iconBackground = Color(0xFFFEF7E6),
                            iconTint = Color(0xFFC78400),
                            onClick = {
                                onEdit(message)
                                onDismiss()
                            }
                        )
                    }

                    HorizontalDivider(
                        color = Color(0xFFE2F1F5).copy(alpha = 0.7f),
                        thickness = 0.7.dp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )

                    SeaGlassMenuItem(
                        icon = Icons.Default.DeleteOutline,
                        title = "Delete",
                        iconBackground = Color(0xFFF4F5F7),
                        iconTint = Color(0xFF455A64),
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
private fun SeaGlassMenuItem(
    icon: ImageVector,
    title: String,
    iconBackground: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(30.dp)
                .clip(RoundedCornerShape(9.dp))
                .background(iconBackground)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(17.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            ),
            color = Color(0xFF1E2832)
        )
    }
}
