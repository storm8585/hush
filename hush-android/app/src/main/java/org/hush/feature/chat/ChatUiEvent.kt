package org.hush.feature.chat

sealed interface ChatUiEvent {
    data class InputTextChanged(val text: String) : ChatUiEvent
    data object SendMessage : ChatUiEvent
    data object AttachmentClicked : ChatUiEvent
    data object VoiceRecordClicked : ChatUiEvent
    data object CallClicked : ChatUiEvent
    data object VideoCallClicked : ChatUiEvent
    data object MoreOptionsClicked : ChatUiEvent
    data object BackClicked : ChatUiEvent
}
