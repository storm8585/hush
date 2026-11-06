package org.hush.feature.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        ChatUiState(
            messages = listOf(
                ChatMessageUiModel(
                    id = "m1",
                    text = "Hey there! Have you reviewed the new Material 3 Expressive guidelines?",
                    timestamp = "10:42",
                    isOutgoing = false,
                    status = MessageDeliveryStatus.Read
                ),
                ChatMessageUiModel(
                    id = "m2",
                    text = "Yes, absolutely! The new spring motion and tonal container elevations are fantastic.",
                    timestamp = "10:44",
                    isOutgoing = true,
                    status = MessageDeliveryStatus.Read
                ),
                ChatMessageUiModel(
                    id = "m3",
                    text = "The 8dp spacing system makes responsive multi-window layouts so much cleaner.",
                    timestamp = "10:45",
                    isOutgoing = true,
                    status = MessageDeliveryStatus.Read
                ),
                ChatMessageUiModel(
                    id = "m4",
                    text = "Agreed! Are we applying the violet-purple theme palette across our chat screens?",
                    timestamp = "10:48",
                    isOutgoing = false,
                    status = MessageDeliveryStatus.Read
                ),
                ChatMessageUiModel(
                    id = "m5",
                    text = "Working on it right now. Pure light theme, pristine token pairing, and zero clutter.",
                    timestamp = "10:50",
                    isOutgoing = true,
                    status = MessageDeliveryStatus.Delivered
                )
            )
        )
    )
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.InputTextChanged -> {
                _uiState.update { it.copy(inputText = event.text) }
            }
            is ChatUiEvent.SendMessage -> {
                sendMessage()
            }
            is ChatUiEvent.AttachmentClicked -> Unit
            is ChatUiEvent.VoiceRecordClicked -> Unit
            is ChatUiEvent.CallClicked -> Unit
            is ChatUiEvent.VideoCallClicked -> Unit
            is ChatUiEvent.MoreOptionsClicked -> Unit
            is ChatUiEvent.BackClicked -> Unit
        }
    }

    private fun sendMessage() {
        val currentText = _uiState.value.inputText.trim()
        if (currentText.isEmpty()) return

        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        val newMessage = ChatMessageUiModel(
            id = UUID.randomUUID().toString(),
            text = currentText,
            timestamp = currentTime,
            isOutgoing = true,
            status = MessageDeliveryStatus.Sent
        )

        _uiState.update { state ->
            state.copy(
                messages = state.messages + newMessage,
                inputText = ""
            )
        }

        viewModelScope.launch {
            delay(1200)
            _uiState.update { state ->
                val updated = state.messages.map { msg ->
                    if (msg.id == newMessage.id) msg.copy(status = MessageDeliveryStatus.Delivered) else msg
                }
                state.copy(messages = updated)
            }
        }
    }
}
