package org.hush.feature.chat.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
                    strokeWidth = 1.dp,
                    color = Color(0xFF4283BD),
                    modifier = Modifier.size(10.dp)
                )
            }
            MessageDeliveryStatus.Sent -> {
                CustomCheckBadge()
            }
            MessageDeliveryStatus.Delivered, MessageDeliveryStatus.Read -> {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CustomCheckBadge()
                    Spacer(modifier = Modifier.width((-2.5).dp))
                    CustomCheckBadge()
                }
            }
        }
    }
}

@Composable
private fun CustomCheckBadge(
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(10.dp)
            .border(
                width = 1.dp,
                color = Color(0xFF4283BD),
                shape = CircleShape
            )
            .clip(CircleShape)
            .background(Color.White)
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF4283BD),
            modifier = Modifier.size(6.5.dp)
        )
    }
}
