package org.hush.feature.chat

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.hush.feature.chat.components.ChatDateChip
import org.hush.feature.chat.components.ChatInputBar
import org.hush.feature.chat.components.ChatMessageBubble
import org.hush.feature.chat.components.ChatMessageContextMenu
import org.hush.feature.chat.components.ChatToast
import org.hush.feature.chat.components.ChatTopBar
import org.hush.feature.chat.components.ChatTypingIndicator
import org.hush.feature.chat.components.MessageBubblePosition

@Composable
fun ChatRoute(
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ChatScreen(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        modifier = modifier
    )
}

@Composable
fun ChatScreen(
    uiState: ChatUiState,
    onEvent: (ChatUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val clipboardManager = LocalClipboardManager.current

    val isMenuOpen = uiState.selectedMessageForMenu != null
    val contentBlur by animateDpAsState(
        targetValue = if (isMenuOpen) 14.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "content_blur"
    )

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navBarHeight = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { _ ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(contentBlur)
            ) {
                LazyColumn(
                    state = listState,
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = statusBarHeight + 74.dp,
                        bottom = navBarHeight + 76.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {
                    item(
                        key = "date_chip_today",
                        contentType = "header_date_chip"
                    ) {
                        ChatDateChip(dateText = "Today")
                    }

                    itemsIndexed(
                        items = uiState.messages,
                        key = { _, message -> message.id },
                        contentType = { _, _ -> "chat_message_item" }
                    ) { index, message ->
                        val prevMessage = if (index > 0) uiState.messages[index - 1] else null
                        val nextMessage = if (index < uiState.messages.size - 1) uiState.messages[index + 1] else null

                        val isPrevSame = prevMessage != null && prevMessage.isOutgoing == message.isOutgoing
                        val isNextSame = nextMessage != null && nextMessage.isOutgoing == message.isOutgoing

                        val position = when {
                            !isPrevSame && !isNextSame -> MessageBubblePosition.Single
                            !isPrevSame && isNextSame -> MessageBubblePosition.First
                            isPrevSame && isNextSame -> MessageBubblePosition.Middle
                            else -> MessageBubblePosition.Last
                        }

                        val extraTopPadding = if (prevMessage != null && !isPrevSame) {
                            10.dp
                        } else {
                            0.dp
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = extraTopPadding)
                        ) {
                            ChatMessageBubble(
                                message = message,
                                position = position,
                                onSwipeToReply = { onEvent(ChatUiEvent.ReplyToMessage(it)) },
                                onLongClick = { msg, bounds -> onEvent(ChatUiEvent.MessageLongClicked(msg, bounds)) }
                            )
                        }
                    }

                    if (uiState.isRecipientTyping) {
                        item(
                            key = "typing_indicator",
                            contentType = "status_typing_indicator"
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                            ) {
                                ChatTypingIndicator()
                            }
                        }
                    }
                }

                ChatTopBar(
                    recipient = uiState.recipient,
                    onBackClick = { onEvent(ChatUiEvent.BackClicked) },
                    onCallClick = { onEvent(ChatUiEvent.CallClicked) },
                    onMoreOptionsClick = { onEvent(ChatUiEvent.MoreOptionsClicked) },
                    modifier = Modifier.align(Alignment.TopCenter)
                )

                ChatInputBar(
                    inputText = uiState.inputText,
                    onInputTextChanged = { onEvent(ChatUiEvent.InputTextChanged(it)) },
                    onSendClick = { onEvent(ChatUiEvent.SendMessage) },
                    onAttachmentClick = { onEvent(ChatUiEvent.AttachmentClicked) },
                    onVoiceRecordClick = { onEvent(ChatUiEvent.VoiceRecordClicked) },
                    replyingToMessage = uiState.replyingToMessage,
                    onCancelReply = { onEvent(ChatUiEvent.CancelReply) },
                    editingMessage = uiState.editingMessage,
                    onCancelEdit = { onEvent(ChatUiEvent.CancelEdit) },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }

            ChatToast(
                message = uiState.toastMessage,
                onDismiss = { onEvent(ChatUiEvent.DismissToast) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = navBarHeight + 76.dp)
            )

            uiState.selectedMessageForMenu?.let { selectedMessage ->
                ChatMessageContextMenu(
                    message = selectedMessage,
                    bounds = uiState.selectedMessageBounds,
                    onDismiss = { onEvent(ChatUiEvent.DismissMenu) },
                    onReply = { onEvent(ChatUiEvent.ReplyToMessage(it)) },
                    onCopy = {
                        clipboardManager.setText(AnnotatedString(it.text))
                        onEvent(ChatUiEvent.CopyMessage(it))
                    },
                    onEdit = { onEvent(ChatUiEvent.StartEditMessage(it)) },
                    onDelete = { onEvent(ChatUiEvent.DeleteMessage(it)) }
                )
            }
        }
    }
}
