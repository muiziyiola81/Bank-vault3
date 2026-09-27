package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldGlow
import com.example.ui.theme.VaultGold
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun QuickActionsBar(
    onAddRecordClick: () -> Unit,
    onOpenVaultClick: () -> Unit,
    onRecentActivityClick: () -> Unit,
    isFavoritesActive: Boolean,
    onToggleFavoritesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Primary Action: Add Record (highlighted with emerald metallic border & soft glow)
            QuickActionButton(
                label = "Add Record",
                subtitle = "New Secret",
                icon = Icons.Filled.Add,
                iconTint = VaultEmeraldBright,
                isPrimary = true,
                onClick = onAddRecordClick,
                testTag = "quick_action_add_record",
                modifier = Modifier.weight(1.3f)
            )

            // Open Vault Compartments
            QuickActionButton(
                label = "Open Vault",
                subtitle = "Browse All",
                icon = Icons.Filled.Key,
                iconTint = VaultEmerald,
                isPrimary = false,
                onClick = onOpenVaultClick,
                testTag = "quick_action_open_vault",
                modifier = Modifier.weight(1.1f)
            )

            // Activity Log
            QuickActionButton(
                label = "Audit Log",
                subtitle = "Events",
                icon = Icons.Filled.History,
                iconTint = VaultTextSecondary,
                isPrimary = false,
                onClick = onRecentActivityClick,
                testTag = "quick_action_recent_activity",
                modifier = Modifier.weight(1.0f)
            )

            // Starred / Favorites toggle
            QuickActionButton(
                label = if (isFavoritesActive) "Starred" else "VIP Keys",
                subtitle = if (isFavoritesActive) "Filtered" else "Show Only",
                icon = if (isFavoritesActive) Icons.Filled.Star else Icons.Outlined.StarBorder,
                iconTint = if (isFavoritesActive) VaultGold else VaultTextSecondary,
                isPrimary = false,
                onClick = onToggleFavoritesClick,
                testTag = "quick_action_favorites",
                modifier = Modifier.weight(1.0f)
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color,
    isPrimary: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val backgroundBrush = if (isPrimary) {
        Brush.verticalGradient(
            colors = listOf(
                VaultSurfaceHighlight,
                VaultSurfaceElevated
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                VaultSurfaceElevated,
                VaultSurface
            )
        )
    }

    val borderColor = if (isPrimary) VaultBorderHighlight else VaultBorder

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(backgroundBrush)
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp, horizontal = 10.dp)
            .testTag(testTag)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isPrimary) VaultEmerald.copy(alpha = 0.15f) else VaultSurface
                    )
                    .border(
                        0.8.dp,
                        if (isPrimary) VaultEmerald.copy(alpha = 0.35f) else VaultBorder,
                        RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconTint,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                color = VaultTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Text(
                text = subtitle,
                color = VaultTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Normal,
                maxLines = 1
            )
        }
    }
}
