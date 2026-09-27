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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.supabase.SupabaseUser
import com.example.ui.theme.VaultAmber
import com.example.ui.theme.VaultBackground
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

@Composable
fun SettingsScreen(
    currentUser: SupabaseUser?,
    isAdmin: Boolean,
    supabaseUrl: String,
    onOpenSupabaseConnection: () -> Unit,
    onLogout: () -> Unit,
    onLockVault: () -> Unit,
    onRunAudit: () -> Unit,
    onResetDemoData: () -> Unit,
    onWipeVault: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showWipeConfirmDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var biometricEnabled by remember { mutableStateOf(true) }
    var auditNotificationsEnabled by remember { mutableStateOf(true) }

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
                Text(
                    text = "Security Settings",
                    color = VaultTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Hardware enclave controls, cipher specifications & Supabase connection",
                    color = VaultTextSecondary,
                    fontSize = 12.sp
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Section: Account Profile
                item {
                    SettingsSection(title = "AUTHENTICATED KEYHOLDER") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(if (isAdmin) VaultGold.copy(alpha = 0.15f) else VaultEmerald.copy(alpha = 0.15f))
                                    .border(1.2.dp, if (isAdmin) VaultGold else VaultEmeraldBright, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = if (isAdmin) VaultGold else VaultEmeraldBright,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = currentUser?.email ?: "Keyholder Session",
                                        color = VaultTextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (isAdmin) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(VaultGold.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "ADMIN",
                                                color = VaultGold,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "UID: ${currentUser?.id ?: "Anonymous"}",
                                    color = VaultEmeraldBright,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    maxLines = 1
                                )
                            }
                        }

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.AutoMirrored.Filled.ExitToApp,
                            iconTint = VaultRed,
                            title = "Sign Out",
                            subtitle = "Terminate Supabase session and dismount enclave keys",
                            onClick = { showLogoutConfirmDialog = true },
                            testTag = "settings_sign_out_row"
                        )
                    }
                }

                // Section: Supabase Project Integration
                item {
                    SettingsSection(title = "SUPABASE PROJECT INTEGRATION") {
                        SettingsRow(
                            icon = Icons.Filled.CloudDone,
                            iconTint = VaultEmeraldBright,
                            title = "Supabase Project Connection",
                            subtitle = "Connected: ${supabaseUrl.replace("https://", "").take(28)}...",
                            onClick = onOpenSupabaseConnection,
                            trailingText = "Configure",
                            testTag = "settings_supabase_connection"
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Filled.Security,
                            iconTint = VaultEmerald,
                            title = "Row Level Security (RLS)",
                            subtitle = "Table user_items guarded by auth.uid() = user_id",
                            onClick = onOpenSupabaseConnection,
                            trailingText = "Verified",
                            testTag = "settings_rls_info"
                        )
                    }
                }

                // Section: Security Controls
                item {
                    SettingsSection(title = "SECURITY CONTROLS") {
                        SettingsRow(
                            icon = Icons.Filled.Lock,
                            iconTint = VaultEmeraldBright,
                            title = "Lock Vault Immediately",
                            subtitle = "Dismount cryptographic keys from memory",
                            onClick = onLockVault,
                            testTag = "settings_lock_now"
                        )

                        SettingsDivider()

                        SettingsSwitchRow(
                            icon = Icons.Filled.Fingerprint,
                            iconTint = VaultEmerald,
                            title = "Biometric Enclave Unlock",
                            subtitle = "Authenticate via device secure biometric sensor",
                            checked = biometricEnabled,
                            onCheckedChange = { biometricEnabled = it },
                            testTag = "settings_biometric_switch"
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Filled.Refresh,
                            iconTint = VaultEmerald,
                            title = "Cryptographic Integrity Audit",
                            subtitle = "Verify Zero-Knowledge checksums across compartments",
                            onClick = onRunAudit,
                            testTag = "settings_run_audit"
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Filled.Timer,
                            iconTint = VaultTextSecondary,
                            title = "Auto-Lock Session Timeout",
                            subtitle = "Immediate on background / 5 minutes idle",
                            onClick = {},
                            trailingText = "5 Min",
                            testTag = "settings_timeout"
                        )
                    }
                }

                // Section: Notifications & Audits
                item {
                    SettingsSection(title = "NOTIFICATIONS & AUDITING") {
                        SettingsSwitchRow(
                            icon = Icons.Filled.Notifications,
                            iconTint = VaultEmerald,
                            title = "Security Event Notifications",
                            subtitle = "Alert on record alterations and access attempts",
                            checked = auditNotificationsEnabled,
                            onCheckedChange = { auditNotificationsEnabled = it },
                            testTag = "settings_notifications_switch"
                        )
                    }
                }

                // Section: Appearance
                item {
                    SettingsSection(title = "APPEARANCE") {
                        SettingsRow(
                            icon = Icons.Filled.Palette,
                            iconTint = VaultEmeraldBright,
                            title = "Dark Luxury Aesthetic",
                            subtitle = "High-contrast obsidian green security interface",
                            onClick = {},
                            trailingText = "Active",
                            testTag = "settings_appearance"
                        )
                    }
                }

                // Section: Disaster Recovery & Data
                item {
                    SettingsSection(title = "DATA & DISASTER RECOVERY") {
                        SettingsRow(
                            icon = Icons.Filled.Refresh,
                            iconTint = VaultEmerald,
                            title = "Restore Certified Sample Vault",
                            subtitle = "Repopulate Swiss Bank, keys, & seed credentials",
                            onClick = onResetDemoData,
                            testTag = "settings_restore_sample"
                        )

                        SettingsDivider()

                        SettingsRow(
                            icon = Icons.Filled.DeleteForever,
                            iconTint = VaultRed,
                            title = "Emergency Vault Purge",
                            subtitle = "Irreversibly wipe all local encrypted records & audit logs",
                            onClick = { showWipeConfirmDialog = true },
                            testTag = "settings_wipe_vault"
                        )
                    }
                }

                // Section: About & Specifications
                item {
                    SettingsSection(title = "CRYPTOGRAPHIC SPECIFICATION") {
                        Column(modifier = Modifier.padding(16.dp)) {
                            SpecRow("Symmetric Cipher", "AES-256-GCM")
                            Spacer(modifier = Modifier.height(8.dp))
                            SpecRow("Backend Integration", "Supabase PostgREST & Auth")
                            Spacer(modifier = Modifier.height(8.dp))
                            SpecRow("Database Table", "user_items (RLS Enabled)")
                            Spacer(modifier = Modifier.height(8.dp))
                            SpecRow("Protocol", "Zero-Knowledge Hardware Enclave")
                            Spacer(modifier = Modifier.height(8.dp))
                            SpecRow("System Version", "Bank Vault v2.4 (Enterprise Edition)")
                        }
                    }
                }
            }
        }

        // Wipe Confirmation Dialog
        if (showWipeConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showWipeConfirmDialog = false },
                title = {
                    Text(
                        text = "Execute Emergency Purge?",
                        color = VaultRed,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "This action will permanently purge all local records and audit entries stored in this device vault. Remote Supabase items will remain safe in your account.",
                        color = VaultTextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showWipeConfirmDialog = false
                            onWipeVault()
                        }
                    ) {
                        Text(text = "CONFIRM PURGE", color = VaultRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showWipeConfirmDialog = false }) {
                        Text(text = "Cancel", color = VaultTextSecondary)
                    }
                },
                containerColor = VaultSurfaceElevated,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Logout Confirmation Dialog
        if (showLogoutConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutConfirmDialog = false },
                title = {
                    Text(
                        text = "Sign Out of Bank Vault?",
                        color = VaultTextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Your cryptographic session will be revoked and memory keys dismounted. You will need to sign in again to access your records.",
                        color = VaultTextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showLogoutConfirmDialog = false
                            onLogout()
                        }
                    ) {
                        Text(text = "Sign Out", color = VaultRed, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutConfirmDialog = false }) {
                        Text(text = "Cancel", color = VaultTextSecondary)
                    }
                },
                containerColor = VaultSurfaceElevated,
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column {
        Text(
            text = title,
            color = VaultTextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(VaultSurfaceElevated)
                .border(1.dp, VaultBorder, RoundedCornerShape(16.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    trailingText: String? = null,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = VaultTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = VaultTextSecondary,
                fontSize = 11.sp
            )
        }

        if (trailingText != null) {
            Text(
                text = trailingText,
                color = VaultEmeraldBright,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
        } else {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = VaultTextMuted,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconTint.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = VaultTextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = VaultTextSecondary,
                fontSize = 11.sp
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = VaultBackground,
                checkedTrackColor = VaultEmeraldBright,
                uncheckedThumbColor = VaultTextSecondary,
                uncheckedTrackColor = VaultSurface
            )
        )
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(VaultDivider)
    )
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = VaultTextSecondary, fontSize = 12.sp)
        Text(text = value, color = VaultTextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
