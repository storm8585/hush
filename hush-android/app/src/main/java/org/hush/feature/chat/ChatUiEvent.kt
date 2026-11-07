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
    data class ReplyToMessage(val message: ChatMessageUiModel) : ChatUiEvent
    data object CancelReply : ChatUiEvent
    data class MessageLongClicked(val message: ChatMessageUiModel) : ChatUiEvent
    data object DismissMenu : ChatUiEvent
    data class StartEditMessage(val message: ChatMessageUiModel) : ChatUiEvent
    data object CancelEdit : ChatUiEvent
    data class CopyMessage(val message: ChatMessageUiModel) : ChatUiEvent
    data class DeleteMessage(val message: ChatMessageUiModel) : ChatUiEvent
    data object DismissToast : ChatUiEvent
}
