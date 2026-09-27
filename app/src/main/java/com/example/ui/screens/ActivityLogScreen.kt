package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VaultActivity
import com.example.ui.components.VaultActivityItem
import com.example.ui.theme.VaultAmber
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultRed
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun ActivityLogScreen(
    activities: List<VaultActivity>,
    modifier: Modifier = Modifier
) {
    var statusFilter by remember { mutableStateOf("ALL") }

    val filteredActivities = when (statusFilter) {
        "SECURE" -> activities.filter { it.status == "SECURE" }
        "VERIFIED" -> activities.filter { it.status == "VERIFIED" }
        "WARNING" -> activities.filter { it.status == "WARNING" }
        else -> activities
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(VaultBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = 900.dp)
                .align(Alignment.TopCenter)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Security Audit Feed",
                            color = VaultTextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.3).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Cryptographic audit trail & compartment access logs",
                            color = VaultTextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    // Security Shield Emblem
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(VaultSurfaceElevated)
                            .border(1.dp, VaultBorder, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.History,
                            contentDescription = "Audit Trail",
                            tint = VaultEmeraldBright,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Status Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ActivityFilterChip(
                        label = "All Events",
                        isSelected = statusFilter == "ALL",
                        onClick = { statusFilter = "ALL" }
                    )
                    ActivityFilterChip(
                        label = "Secure",
                        isSelected = statusFilter == "SECURE",
                        onClick = { statusFilter = "SECURE" }
                    )
                    ActivityFilterChip(
                        label = "Verified",
                        isSelected = statusFilter == "VERIFIED",
                        onClick = { statusFilter = "VERIFIED" }
                    )
                    ActivityFilterChip(
                        label = "Warnings",
                        isSelected = statusFilter == "WARNING",
                        onClick = { statusFilter = "WARNING" }
                    )
                }
            }

            // List of Events
            if (filteredActivities.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(VaultSurfaceElevated)
                            .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
                            .padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Security,
                            contentDescription = null,
                            tint = VaultTextMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Activities Recorded",
                            color = VaultTextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Activities will be logged when records are stored, audited, or updated.",
                            color = VaultTextSecondary,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredActivities, key = { it.id }) { activity ->
                        VaultActivityItem(activity = activity)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityFilterChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) VaultEmerald.copy(alpha = 0.15f) else VaultSurfaceElevated)
            .border(
                1.dp,
                if (isSelected) VaultEmerald else VaultBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
            .testTag("filter_activity_${label.lowercase()}")
    ) {
        Text(
            text = label,
            color = if (isSelected) VaultEmeraldBright else VaultTextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}
