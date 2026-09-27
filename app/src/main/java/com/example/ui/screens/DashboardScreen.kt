package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultActivity
import com.example.data.model.VaultCategory
import com.example.data.model.VaultRecord
import com.example.ui.components.QuickActionsBar
import com.example.ui.components.VaultActivityItem
import com.example.ui.components.VaultCategoryCard
import com.example.ui.components.VaultStatusHero
import com.example.ui.components.VaultTopBar
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun DashboardScreen(
    records: List<VaultRecord>,
    activities: List<VaultActivity>,
    recordCount: Int,
    securityScore: Int,
    lastAuditTimeText: String,
    isAuditRunning: Boolean,
    isFavoritesActive: Boolean,
    userEmail: String? = null,
    isSyncing: Boolean = false,
    onSyncClick: (() -> Unit)? = null,
    onRunAuditClick: () -> Unit,
    onAddRecordClick: () -> Unit,
    onOpenVaultClick: () -> Unit,
    onCategoryClick: (String) -> Unit,
    onRecentActivityClick: () -> Unit,
    onToggleFavoritesClick: () -> Unit,
    onLockClick: () -> Unit,
    onProfileClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(VaultBackground)
    ) {
        // Vault Header
        VaultTopBar(
            userEmail = userEmail,
            isSyncing = isSyncing,
            onSyncClick = onSyncClick,
            onLockClick = onLockClick,
            onProfileClick = onProfileClick
        )

        // Main Scrollable Area
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            contentAlignment = Alignment.TopCenter
        ) {
            val isWideScreen = maxWidth > 600.dp

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 900.dp)
                    .padding(horizontal = if (isWideScreen) 24.dp else 16.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Security Hero Panel
                item {
                    VaultStatusHero(
                        recordCount = recordCount,
                        securityScore = securityScore,
                        lastAuditTimeText = lastAuditTimeText,
                        isAuditRunning = isAuditRunning,
                        onRunAuditClick = onRunAuditClick,
                        modifier = Modifier.testTag("vault_status_hero")
                    )
                }

                // Quick Actions
                item {
                    QuickActionsBar(
                        onAddRecordClick = onAddRecordClick,
                        onOpenVaultClick = onOpenVaultClick,
                        onRecentActivityClick = onRecentActivityClick,
                        isFavoritesActive = isFavoritesActive,
                        onToggleFavoritesClick = onToggleFavoritesClick
                    )
                }

                // Main Vault Area Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Vault",
                                color = VaultTextPrimary,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Encrypted storage compartments",
                                color = VaultTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onOpenVaultClick)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "View All",
                                color = VaultEmeraldBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "View All",
                                tint = VaultEmeraldBright,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Categories Compartments Grid
                item {
                    val columns = if (isWideScreen) 3 else 2
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val chunks = VaultCategory.ALL.chunked(columns)
                        for (row in chunks) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                for (cat in row) {
                                    val catCount = records.count { it.category.equals(cat.id, ignoreCase = true) }
                                    Box(modifier = Modifier.weight(1f)) {
                                        VaultCategoryCard(
                                            category = cat,
                                            recordCount = catCount,
                                            isSelected = false,
                                            onClick = { onCategoryClick(cat.id) }
                                        )
                                    }
                                }
                                // Fill remaining slots if chunk is uneven
                                for (i in 0 until (columns - row.size)) {
                                    Spacer(modifier = Modifier.weight(1f))
                                }
                            }
                        }
                    }
                }

                // Recent Activity Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Recent Activity",
                                color = VaultTextPrimary,
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.3).sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Security audit feed & event timeline",
                                color = VaultTextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable(onClick = onRecentActivityClick)
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Full Log",
                                color = VaultEmeraldBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Full Log",
                                tint = VaultEmeraldBright,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }

                // Recent Activity Items (Latest 4)
                val topActivities = activities.take(4)
                if (topActivities.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(VaultSurfaceElevated)
                                .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
                                .padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No security events recorded yet.",
                                color = VaultTextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else {
                    items(topActivities, key = { it.id }) { act ->
                        VaultActivityItem(activity = act)
                    }
                }
            }
        }
    }
}
