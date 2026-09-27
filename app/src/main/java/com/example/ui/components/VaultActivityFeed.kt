package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultActivity
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultDivider
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultGold
import com.example.ui.theme.VaultRed
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VaultActivityItem(
    activity: VaultActivity,
    modifier: Modifier = Modifier
) {
    val icon: ImageVector = when {
        activity.action.contains("added", ignoreCase = true) -> Icons.Filled.Add
        activity.action.contains("updated", ignoreCase = true) -> Icons.Filled.Edit
        activity.action.contains("locked", ignoreCase = true) -> Icons.Filled.Lock
        activity.action.contains("unlocked", ignoreCase = true) -> Icons.Filled.LockOpen
        activity.action.contains("purged", ignoreCase = true) || activity.action.contains("wiped", ignoreCase = true) -> Icons.Filled.DeleteOutline
        activity.action.contains("audit", ignoreCase = true) || activity.action.contains("security", ignoreCase = true) -> Icons.Filled.Security
        else -> Icons.Filled.Shield
    }

    val statusColor = when (activity.status) {
        "SECURE" -> VaultEmeraldBright
        "VERIFIED" -> VaultEmerald
        "WARNING" -> VaultRed
        else -> VaultGold
    }

    val timeFormatted = formatTimestamp(activity.timestamp)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(VaultSurfaceElevated)
            .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Activity Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(VaultSurface)
                    .border(0.8.dp, VaultBorder, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = activity.action,
                    tint = statusColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = activity.action,
                        color = VaultTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        color = VaultTextMuted,
                        fontSize = 12.sp
                    )
                    Text(
                        text = activity.category,
                        color = VaultTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = activity.details,
                    color = VaultTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = timeFormatted,
                    color = VaultTextMuted,
                    fontSize = 10.sp
                )
            }

            // Subtle Status Indicator Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(0.7.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 3.dp)
            ) {
                Text(
                    text = activity.status,
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
            }
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    val minutes = diff / (1000 * 60)
    val hours = diff / (1000 * 60 * 60)
    val days = diff / (1000 * 60 * 60 * 24)

    return when {
        minutes < 2 -> "Just now"
        minutes < 60 -> "$minutes min ago"
        hours < 24 -> "Today, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))}"
        days == 1L -> "Yesterday, ${SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(millis))}"
        else -> SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(millis))
    }
}
