package org.hush.feature.chat

import androidx.compose.ui.geometry.Rect

enum class MessageDeliveryStatus {
    Sending,
    Sent,
    Delivered,
    Read
}

data class ChatMessageUiModel(
    val id: String,
    val text: String,
    val timestamp: String,
    val isOutgoing: Boolean,
    val status: MessageDeliveryStatus = MessageDeliveryStatus.Read,
    val replyToText: String? = null,
    val replyToAuthor: String? = null,
    val isEdited: Boolean = false
)

data class RecipientUiModel(
    val id: String,
    val name: String,
    val avatarInitials: String,
    val isOnline: Boolean,
    val statusSubtitle: String
)

data class ChatUiState(
    val recipient: RecipientUiModel = RecipientUiModel(
        id = "user_01",
        name = "thunder",
        avatarInitials = "T",
        isOnline = true,
        statusSubtitle = "online"
    ),
    val messages: List<ChatMessageUiModel> = emptyList(),
    val inputText: String = "",
    val isRecipientTyping: Boolean = false,
    val replyingToMessage: ChatMessageUiModel? = null,
    val editingMessage: ChatMessageUiModel? = null,
    val selectedMessageForMenu: ChatMessageUiModel? = null,
    val selectedMessageBounds: Rect? = null,
    val toastMessage: String? = null
) {
    val isSendActionEnabled: Boolean
        get() = inputText.isNotBlank()
    val isEditing: Boolean
        get() = editingMessage != null
}
