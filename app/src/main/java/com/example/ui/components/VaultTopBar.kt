package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldGlow
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun VaultTopBar(
    userEmail: String? = null,
    isSyncing: Boolean = false,
    onSyncClick: (() -> Unit)? = null,
    onLockClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "statusPulse"
    )

    val spinTransition = rememberInfiniteTransition(label = "spin")
    val spinAngle by spinTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing)
        ),
        label = "syncSpin"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VaultSurface,
                        VaultBackground
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Logo & Vault Status
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Polished Metallic Vault Shield Emblem
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(VaultSurfaceHighlight, VaultSurfaceElevated)
                            )
                        )
                        .border(1.dp, VaultBorderHighlight, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Shield,
                        contentDescription = "Bank Vault Emblem",
                        tint = VaultEmeraldBright,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "BANK VAULT",
                            color = VaultTextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.8.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Pulsing security dot
                        Box(contentAlignment = Alignment.Center) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .scale(pulseScale)
                                    .background(VaultEmeraldGlow, CircleShape)
                            )
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(VaultEmeraldBright, CircleShape)
                            )
                        }

                        Text(
                            text = "Vault Protected",
                            color = VaultEmeraldBright,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.4.sp
                        )

                        Text(
                            text = if (!userEmail.isNullOrBlank()) "•  ${userEmail.take(16)}" else "•  Supabase",
                            color = VaultTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            // Quick Actions: Sync, Lock Vault & Profile
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Sync button
                if (onSyncClick != null) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VaultSurfaceElevated)
                            .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                            .clickable(onClick = onSyncClick)
                            .testTag("sync_vault_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CloudSync,
                            contentDescription = "Sync with Supabase",
                            tint = if (isSyncing) VaultEmeraldBright else VaultTextSecondary,
                            modifier = Modifier
                                .size(18.dp)
                                .then(if (isSyncing) Modifier.rotate(spinAngle) else Modifier)
                        )
                    }
                }

                // Lock Vault button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VaultSurfaceElevated)
                        .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                        .clickable(onClick = onLockClick)
                        .testTag("lock_vault_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Lock Vault",
                        tint = VaultEmerald,
                        modifier = Modifier.size(17.dp)
                    )
                }

                // Profile button
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(VaultSurfaceElevated)
                        .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                        .clickable(onClick = onProfileClick)
                        .testTag("profile_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Person,
                        contentDescription = "Security Profile",
                        tint = VaultTextSecondary,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }
    }
}
