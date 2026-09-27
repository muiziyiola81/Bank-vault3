package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.supabase.SupabaseConfig
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultRed
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun SupabaseConnectionDialog(
    config: SupabaseConfig,
    onDismiss: () -> Unit,
    onSave: (url: String, anonKey: String, adminId: String) -> Unit,
    onReset: () -> Unit
) {
    var urlInput by remember { mutableStateOf(config.supabaseUrl) }
    var anonKeyInput by remember { mutableStateOf(config.supabaseAnonKey) }
    var adminIdInput by remember { mutableStateOf(config.adminUserId) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VaultBackground.copy(alpha = 0.94f))
                .imePadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 560.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(VaultSurfaceElevated)
                    .border(1.2.dp, VaultBorderHighlight, RoundedCornerShape(20.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(22.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(VaultEmerald.copy(alpha = 0.15f))
                                .border(1.dp, VaultEmerald.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Security,
                                contentDescription = null,
                                tint = VaultEmeraldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "Supabase Connection",
                                color = VaultTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Existing project URL, anon key & admin UID",
                                color = VaultTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = VaultTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (validationError != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultRed.copy(alpha = 0.15f))
                            .border(1.dp, VaultRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(text = validationError ?: "", color = VaultRed, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Project URL
                Text(
                    text = "SUPABASE PROJECT URL",
                    color = VaultTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = {
                        urlInput = it
                        validationError = null
                    },
                    placeholder = { Text("https://your-project.supabase.co", color = VaultTextMuted, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("supabase_url_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultEmerald,
                        unfocusedBorderColor = VaultBorder,
                        focusedContainerColor = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        cursorColor = VaultEmeraldBright,
                        focusedTextColor = VaultTextPrimary,
                        unfocusedTextColor = VaultTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Anon Key
                Text(
                    text = "PUBLISHABLE / ANON KEY",
                    color = VaultTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = anonKeyInput,
                    onValueChange = {
                        anonKeyInput = it
                        validationError = null
                    },
                    placeholder = { Text("eyJhbGciOi...", color = VaultTextMuted, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("supabase_anon_key_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultEmerald,
                        unfocusedBorderColor = VaultBorder,
                        focusedContainerColor = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        cursorColor = VaultEmeraldBright,
                        focusedTextColor = VaultTextPrimary,
                        unfocusedTextColor = VaultTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Admin Auth User ID
                Text(
                    text = "AUTHORIZED ADMINISTRATOR USER ID",
                    color = VaultTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = adminIdInput,
                    onValueChange = { adminIdInput = it },
                    placeholder = { Text("UUID e.g. 550e8400-e29b-41d4-a716-446655440000", color = VaultTextMuted, fontSize = 13.sp) },
                    modifier = Modifier.fillMaxWidth().testTag("supabase_admin_id_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultEmerald,
                        unfocusedBorderColor = VaultBorder,
                        focusedContainerColor = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        cursorColor = VaultEmeraldBright,
                        focusedTextColor = VaultTextPrimary,
                        unfocusedTextColor = VaultTextPrimary
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Security Note
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VaultSurfaceHighlight)
                        .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Info,
                                contentDescription = null,
                                tint = VaultEmeraldBright,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Client Security Guarantee",
                                color = VaultEmeraldBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Never input your service-role key or database password. Only the public anon key is used. Row Level Security on table user_items enforces strict tenant privacy.",
                            color = VaultTextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onReset) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(imageVector = Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(13.dp))
                            Text("Reset Defaults", color = VaultTextSecondary, fontSize = 12.sp)
                        }
                    }

                    Button(
                        onClick = {
                            if (urlInput.isBlank()) {
                                validationError = "Please specify a Supabase project URL."
                                return@Button
                            }
                            if (anonKeyInput.isBlank()) {
                                validationError = "Please specify the publishable anon key."
                                return@Button
                            }
                            onSave(urlInput.trim(), anonKeyInput.trim(), adminIdInput.trim())
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = VaultEmerald,
                            contentColor = VaultBackground
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("save_supabase_connection_button")
                    ) {
                        Text("Connect Project", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
