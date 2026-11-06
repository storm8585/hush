package org.hush.feature.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.hush.feature.chat.components.ChatDateChip
import org.hush.feature.chat.components.ChatInputBar
import org.hush.feature.chat.components.ChatMessageBubble
import org.hush.feature.chat.components.ChatTopBar
import org.hush.feature.chat.components.ChatTypingIndicator

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

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            ChatTopBar(
                recipient = uiState.recipient,
                onBackClick = { onEvent(ChatUiEvent.BackClicked) },
                onCallClick = { onEvent(ChatUiEvent.CallClicked) },
                onVideoCallClick = { onEvent(ChatUiEvent.VideoCallClicked) },
                onMoreOptionsClick = { onEvent(ChatUiEvent.MoreOptionsClicked) }
            )
        },
        bottomBar = {
            ChatInputBar(
                inputText = uiState.inputText,
                onInputTextChanged = { onEvent(ChatUiEvent.InputTextChanged(it)) },
                onSendClick = { onEvent(ChatUiEvent.SendMessage) },
                onAttachmentClick = { onEvent(ChatUiEvent.AttachmentClicked) },
                onVoiceRecordClick = { onEvent(ChatUiEvent.VoiceRecordClicked) },
                modifier = Modifier.imePadding()
            )
        }
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = innerPadding.calculateTopPadding() + 8.dp,
                bottom = innerPadding.calculateBottomPadding() + 8.dp
            ),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
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
                val extraTopPadding = if (prevMessage != null && prevMessage.isOutgoing != message.isOutgoing) {
                    8.dp
                } else {
                    0.dp
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = extraTopPadding)
                ) {
                    ChatMessageBubble(message = message)
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
    }
}
