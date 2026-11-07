package org.hush.feature.chat.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.AttachFile
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    val inputCapsuleShape = RoundedCornerShape(25.dp)

    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = maxOf(bottomInset, 8.dp), start = 12.dp, end = 12.dp)
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
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, Color(0xFF8E8E93), RoundedCornerShape(16.dp))
                            .background(Color.White)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(36.dp)
                                .background(Color(0xFF0088FF))
                        )

                        Spacer(modifier = Modifier.width(10.dp))

                        if (isEditMode) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit message",
                                tint = Color(0xFF0088FF),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (isEditMode) "Edit Message" else if (bannerTargetMessage.isOutgoing) "You" else "thunder",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp,
                                color = Color(0xFF0088FF),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = bannerTargetMessage.text,
                                fontSize = 12.sp,
                                color = Color(0xFF8E8E93),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        IconButton(
                            onClick = if (isEditMode) onCancelEdit else onCancelReply,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFF8E8E93),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .clip(inputCapsuleShape)
                    .border(1.dp, Color(0xFF8E8E93), inputCapsuleShape)
                    .background(Color.White)
                    .padding(start = 4.dp, end = 4.dp)
            ) {
                IconButton(
                    onClick = onAttachmentClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AttachFile,
                        contentDescription = "Attach file",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 6.dp)
                ) {
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Message",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF8E8E93)
                        )
                    }
                    BasicTextField(
                        value = inputText,
                        onValueChange = onInputTextChanged,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color(0xFF000000)
                        ),
                        cursorBrush = SolidColor(Color(0xFF0088FF)),
                        maxLines = 4,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                IconButton(
                    onClick = {},
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.EmojiEmotions,
                        contentDescription = "Sticker",
                        tint = Color(0xFF8E8E93),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF0088FF))
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
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(42.dp)
                        ) {
                            if (isEditMode) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Confirm Edit",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.Send,
                                    contentDescription = "Send Message",
                                    tint = Color.White,
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
                                    tint = Color.White,
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
