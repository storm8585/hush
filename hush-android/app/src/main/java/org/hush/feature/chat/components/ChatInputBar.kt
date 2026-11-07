package org.hush.feature.chat.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.outlined.EmojiEmotions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hush.core.designsystem.theme.InputContainerShape
import org.hush.feature.chat.ChatMessageUiModel

@Composable
fun ChatInputBar(
    inputText: String,
    onInputTextChanged: (String) -> Unit,
    onSendClick: () -> Unit,
    onAttachmentClick: () -> Unit,
    onVoiceRecordClick: () -> Unit,
    modifier: Modifier = Modifier,
    replyingToMessage: ChatMessageUiModel? = null,
    onCancelReply: () -> Unit = {},
    editingMessage: ChatMessageUiModel? = null,
    onCancelEdit: () -> Unit = {}
) {
    val isEditMode = editingMessage != null
    val isSendEnabled = inputText.isNotBlank()
    val transition = updateTransition(targetState = isSendEnabled && !isEditMode, label = "send_mic_morph")

    val buttonContainerColor by animateColorAsState(
        targetValue = if (isSendEnabled) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_container_color"
    )

    val buttonContentColor by animateColorAsState(
        targetValue = if (isSendEnabled) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onPrimaryContainer
        },
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_content_color"
    )

    val sendScale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "send_scale"
    ) { target ->
        if (target) 1.0f else 0.1f
    }

    val sendRotation by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "send_rotation"
    ) { target ->
        if (target) 0f else -45f
    }

    val sendAlpha by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "send_alpha"
    ) { target ->
        if (target) 1.0f else 0.0f
    }

    val micScale by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "mic_scale"
    ) { target ->
        if (target) 0.1f else 1.0f
    }

    val micRotation by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            )
        },
        label = "mic_rotation"
    ) { target ->
        if (target) 45f else 0f
    }

    val micAlpha by transition.animateFloat(
        transitionSpec = {
            spring(
                dampingRatio = Spring.DampingRatioNoBouncy,
                stiffness = Spring.StiffnessMedium
            )
        },
        label = "mic_alpha"
    ) { target ->
        if (target) 0.0f else 1.0f
    }

    val navBarPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val imePadding = WindowInsets.ime.asPaddingValues().calculateBottomPadding()
    val bottomInset = maxOf(navBarPadding, imePadding)

    val replyBannerShape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color.Transparent,
                        0.25f to MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                        0.65f to MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                        1.0f to MaterialTheme.colorScheme.surface
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = bottomInset)
        ) {
            val bannerVisible = replyingToMessage != null || isEditMode
            val bannerTargetMessage = editingMessage ?: replyingToMessage

            AnimatedVisibility(
                visible = bannerVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (bannerTargetMessage != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .shadow(
                                elevation = 3.dp,
                                shape = replyBannerShape,
                                ambientColor = Color.Black.copy(alpha = 0.04f),
                                spotColor = Color.Black.copy(alpha = 0.06f)
                            )
                            .border(
                                width = 1.dp,
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.White,
                                        Color(0xFFE4E4EB)
                                    )
                                ),
                                shape = replyBannerShape
                            )
                            .clip(replyBannerShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.White,
                                        Color(0xFFF9F9FB)
                                    )
                                )
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .width(4.dp)
                                .height(44.dp)
                                .background(MaterialTheme.colorScheme.primary)
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        if (isEditMode) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit message",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isEditMode) "Edit Message" else if (bannerTargetMessage.isOutgoing) "You" else "Elena Rostova",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = bannerTargetMessage.text,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                ),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = if (isEditMode) onCancelEdit else onCancelReply,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = if (isEditMode) "Cancel edit" else "Cancel reply",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(44.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.04f),
                            spotColor = Color.Black.copy(alpha = 0.06f)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color(0xFFE4E4EB)
                                )
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color(0xFFF9F9FB)
                                )
                            )
                        )
                ) {
                    IconButton(
                        onClick = onAttachmentClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Attach file",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 3.dp,
                            shape = InputContainerShape,
                            ambientColor = Color.Black.copy(alpha = 0.04f),
                            spotColor = Color.Black.copy(alpha = 0.06f)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color(0xFFE4E4EB)
                                )
                            ),
                            shape = InputContainerShape
                        )
                        .clip(InputContainerShape)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.White,
                                    Color(0xFFF9F9FB)
                                )
                            )
                        )
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier.weight(1f)
                        ) {
                            if (inputText.isEmpty()) {
                                Text(
                                    text = "Message...",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                            BasicTextField(
                                value = inputText,
                                onValueChange = onInputTextChanged,
                                textStyle = MaterialTheme.typography.bodyLarge.copy(
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                maxLines = 4,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        Icon(
                            imageVector = Icons.Outlined.EmojiEmotions,
                            contentDescription = "Emojis",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(48.dp)
                        .shadow(
                            elevation = if (isSendEnabled || isEditMode) 5.dp else 2.dp,
                            shape = CircleShape,
                            ambientColor = Color.Black.copy(alpha = 0.06f),
                            spotColor = if (isSendEnabled || isEditMode) MaterialTheme.colorScheme.primary.copy(alpha = 0.40f) else Color.Black.copy(alpha = 0.06f)
                        )
                        .border(
                            width = 1.dp,
                            brush = Brush.verticalGradient(
                                if (isSendEnabled || isEditMode) {
                                    listOf(
                                        Color.White.copy(alpha = 0.50f),
                                        Color.White.copy(alpha = 0.05f)
                                    )
                                } else {
                                    listOf(
                                        Color.White.copy(alpha = 0.85f),
                                        Color(0xFFE2D6F8)
                                    )
                                }
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .background(
                            if (isSendEnabled || isEditMode) {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFF7E60DF),
                                        Color(0xFF5E3FB5)
                                    )
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(
                                        Color(0xFFF3EDFD),
                                        Color(0xFFE4D4FC)
                                    )
                                )
                            }
                        )
                ) {
                    IconButton(
                        onClick = {
                            if (isEditMode) {
                                if (inputText.isNotBlank()) onSendClick()
                            } else {
                                if (isSendEnabled) onSendClick() else onVoiceRecordClick()
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Transparent,
                            contentColor = buttonContentColor
                        ),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(48.dp)
                        ) {
                            if (isEditMode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirm Edit",
                                    tint = buttonContentColor,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Message",
                                    tint = buttonContentColor,
                                    modifier = Modifier
                                        .size(20.dp)
                                        .graphicsLayer {
                                            scaleX = sendScale
                                            scaleY = sendScale
                                            rotationZ = sendRotation
                                            alpha = sendAlpha
                                        }
                                )

                                Icon(
                                    imageVector = Icons.Default.Mic,
                                    contentDescription = "Record Voice Message",
                                    tint = buttonContentColor,
                                    modifier = Modifier
                                        .size(22.dp)
                                        .graphicsLayer {
                                            scaleX = micScale
                                            scaleY = micScale
                                            rotationZ = micRotation
                                            alpha = micAlpha
                                        }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
