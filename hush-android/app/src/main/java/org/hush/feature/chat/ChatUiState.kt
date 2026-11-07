package org.hush.feature.chat

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
    val replyToAuthor: String? = null
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
        name = "Elena Rostova",
        avatarInitials = "ER",
        isOnline = true,
        statusSubtitle = "online"
    ),
    val messages: List<ChatMessageUiModel> = emptyList(),
    val inputText: String = "",
    val isRecipientTyping: Boolean = false,
    val replyingToMessage: ChatMessageUiModel? = null
) {
    val isSendActionEnabled: Boolean
        get() = inputText.isNotBlank()
}
