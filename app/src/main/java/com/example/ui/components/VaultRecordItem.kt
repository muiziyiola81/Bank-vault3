package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultCategory
import com.example.data.model.VaultRecord
import com.example.ui.theme.VaultAmber
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultDivider
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultGold
import com.example.ui.theme.VaultRed
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VaultRecordItem(
    record: VaultRecord,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onCopyValue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isRevealed by remember { mutableStateOf(false) }

    val category = VaultCategory.find(record.category)
    val accentColor = Color(category.accentHex)

    val categoryIcon: ImageVector = when (record.category) {
        "Financial" -> Icons.Filled.AccountBalance
        "Credentials" -> Icons.Filled.Key
        "Identities" -> Icons.Filled.Badge
        "Documents" -> Icons.Filled.Description
        "Payment Cards" -> Icons.Filled.CreditCard
        "Recovery Keys" -> Icons.Filled.Shield
        else -> Icons.Filled.Lock
    }

    val securityBadgeColor = when (record.securityLevel) {
        "TOP SECRET" -> VaultRed
        "RESTRICTED" -> VaultAmber
        else -> VaultEmerald
    }

    val dateFormatted = SimpleDateFormat("MMM d, yyyy", Locale.getDefault()).format(Date(record.updatedAt))

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(VaultSurfaceElevated)
            .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
            .testTag("record_card_${record.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Row: Category Icon, Title, Secondary Info, Star Favorite & Security Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(accentColor.copy(alpha = 0.12f))
                            .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = categoryIcon,
                            contentDescription = record.category,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = record.title,
                            color = VaultTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (record.secondaryValue.isNotBlank()) {
                            Text(
                                text = record.secondaryValue,
                                color = VaultTextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    }
                }

                // Security Tag & Favorite Star
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(securityBadgeColor.copy(alpha = 0.12f))
                            .border(0.8.dp, securityBadgeColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = record.securityLevel,
                            color = securityBadgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable(onClick = onToggleFavorite),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (record.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Favorite",
                            tint = if (record.isFavorite) VaultGold else VaultTextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Secret Value Preview Container (with Masking & Copy)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(VaultSurface)
                    .border(1.dp, VaultBorderHighlight, RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PROTECTED PAYLOAD",
                            color = VaultTextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        val displaySecret = if (isRevealed) {
                            record.secretValue
                        } else {
                            if (record.secretValue.length > 24) {
                                "•••• •••• •••• •••• ••••"
                            } else {
                                "•••• •••• •••• " + record.secretValue.takeLast(4)
                            }
                        }

                        Text(
                            text = displaySecret,
                            color = if (isRevealed) VaultEmeraldBright else VaultTextPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            maxLines = if (isRevealed) 3 else 1
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Eye Reveal Toggle
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(VaultSurfaceElevated)
                                .clickable { isRevealed = !isRevealed },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isRevealed) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (isRevealed) "Hide Secret" else "Reveal Secret",
                                tint = VaultTextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Copy Button
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(VaultEmerald.copy(alpha = 0.15f))
                                .clickable { onCopyValue(record.secretValue) }
                                .testTag("copy_secret_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ContentCopy,
                                contentDescription = "Copy Secret",
                                tint = VaultEmeraldBright,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }

            // Notes preview if present
            if (record.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = record.notes,
                    color = VaultTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Footer: Timestamp & Action Controls (Edit, Delete)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Encrypted $dateFormatted",
                    color = VaultTextMuted,
                    fontSize = 11.sp
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Edit Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultSurfaceHighlight)
                            .border(0.8.dp, VaultBorderHighlight, RoundedCornerShape(8.dp))
                            .clickable(onClick = onEditClick)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("edit_record_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit Record",
                            tint = VaultEmeraldBright,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Edit",
                            color = VaultEmeraldBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Delete Button
                    Row(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultSurface)
                            .clickable(onClick = onDeleteClick)
                            .testTag("delete_record_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Purge Record",
                            tint = VaultTextMuted,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}
