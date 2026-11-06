package org.hush.feature.chat.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.hush.feature.chat.MessageDeliveryStatus

@Composable
fun ChatStatusIndicator(
    status: MessageDeliveryStatus,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        when (status) {
            MessageDeliveryStatus.Sending -> {
                CircularProgressIndicator(
                    strokeWidth = 1.5.dp,
                    color = tint.copy(alpha = 0.6f),
                    modifier = Modifier.size(10.dp)
                )
            }
            MessageDeliveryStatus.Sent -> {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Sent",
                    tint = tint.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
            MessageDeliveryStatus.Delivered -> {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Delivered",
                    tint = tint.copy(alpha = 0.7f),
                    modifier = Modifier.size(14.dp)
                )
            }
            MessageDeliveryStatus.Read -> {
                Icon(
                    imageVector = Icons.Default.DoneAll,
                    contentDescription = "Read",
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
