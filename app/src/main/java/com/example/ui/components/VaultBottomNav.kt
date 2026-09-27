package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Key
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldMuted
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary
import com.example.ui.viewmodel.VaultTab

@Composable
fun VaultBottomNav(
    selectedTab: VaultTab,
    onTabSelected: (VaultTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        VaultSurface.copy(alpha = 0.95f),
                        VaultSurfaceElevated
                    )
                )
            )
            .border(
                width = 1.dp,
                color = VaultBorder,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 600.dp)
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                label = "Home",
                selected = selectedTab == VaultTab.DASHBOARD,
                activeIcon = Icons.Filled.Home,
                inactiveIcon = Icons.Outlined.Home,
                onClick = { onTabSelected(VaultTab.DASHBOARD) },
                testTag = "nav_item_home"
            )

            NavItem(
                label = "Vault",
                selected = selectedTab == VaultTab.VAULT,
                activeIcon = Icons.Filled.Key,
                inactiveIcon = Icons.Outlined.Key,
                onClick = { onTabSelected(VaultTab.VAULT) },
                testTag = "nav_item_vault"
            )

            NavItem(
                label = "Activity",
                selected = selectedTab == VaultTab.ACTIVITY,
                activeIcon = Icons.Filled.History,
                inactiveIcon = Icons.Outlined.History,
                onClick = { onTabSelected(VaultTab.ACTIVITY) },
                testTag = "nav_item_activity"
            )

            NavItem(
                label = "Settings",
                selected = selectedTab == VaultTab.SETTINGS,
                activeIcon = Icons.Filled.Settings,
                inactiveIcon = Icons.Outlined.Settings,
                onClick = { onTabSelected(VaultTab.SETTINGS) },
                testTag = "nav_item_settings"
            )
        }
    }
}

@Composable
private fun NavItem(
    label: String,
    selected: Boolean,
    activeIcon: ImageVector,
    inactiveIcon: ImageVector,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val iconTint by animateColorAsState(
        targetValue = if (selected) VaultEmeraldBright else VaultTextMuted,
        label = "navIconTint"
    )
    val textTint by animateColorAsState(
        targetValue = if (selected) VaultTextPrimary else VaultTextMuted,
        label = "navTextTint"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .height(30.dp)
                .widthIn(min = 44.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    if (selected) VaultSurfaceHighlight else androidx.compose.ui.graphics.Color.Transparent
                )
                .border(
                    width = if (selected) 0.8.dp else 0.dp,
                    color = if (selected) VaultBorderHighlight else androidx.compose.ui.graphics.Color.Transparent,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (selected) activeIcon else inactiveIcon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            color = textTint,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            letterSpacing = 0.2.sp
        )
    }
}
