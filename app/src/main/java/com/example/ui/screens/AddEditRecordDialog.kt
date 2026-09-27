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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.VaultCategory
import com.example.data.model.VaultRecord
import com.example.ui.theme.VaultAmber
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultDivider
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldMuted
import com.example.ui.theme.VaultEmeraldSubtle
import com.example.ui.theme.VaultGold
import com.example.ui.theme.VaultRed
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary
import java.security.SecureRandom

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordDialog(
    recordToEdit: VaultRecord?,
    defaultCategory: String,
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        category: String,
        secretValue: String,
        secondaryValue: String,
        notes: String,
        securityLevel: String,
        isFavorite: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(recordToEdit?.title ?: "") }
    var categoryId by remember { mutableStateOf(recordToEdit?.category ?: defaultCategory) }
    var secretValue by remember { mutableStateOf(recordToEdit?.secretValue ?: "") }
    var secondaryValue by remember { mutableStateOf(recordToEdit?.secondaryValue ?: "") }
    var notes by remember { mutableStateOf(recordToEdit?.notes ?: "") }
    var securityLevel by remember { mutableStateOf(recordToEdit?.securityLevel ?: "CONFIDENTIAL") }
    var isFavorite by remember { mutableStateOf(recordToEdit?.isFavorite ?: false) }
    var isSecretVisible by remember { mutableStateOf(false) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(VaultBackground.copy(alpha = 0.94f))
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(VaultSurfaceElevated)
                    .border(1.2.dp, VaultBorderHighlight, RoundedCornerShape(20.dp))
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // Dialog Header
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
                                imageVector = Icons.Filled.Lock,
                                contentDescription = null,
                                tint = VaultEmeraldBright,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = if (recordToEdit != null) "Modify Secret Record" else "Secure New Record",
                                color = VaultTextPrimary,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "AES-256 GCM Encrypted Enclave",
                                color = VaultEmeraldBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("dialog_close_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = VaultTextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Validation Error Banner if present
                if (validationError != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(VaultRed.copy(alpha = 0.15f))
                            .border(1.dp, VaultRed.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = validationError ?: "",
                            color = VaultRed,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Field: Compartment Selector
                Text(
                    text = "VAULT COMPARTMENT",
                    color = VaultTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = VaultCategory.find(categoryId).name,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .testTag("category_dropdown_trigger"),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = VaultEmerald,
                            unfocusedBorderColor = VaultBorder,
                            focusedContainerColor = VaultSurface,
                            unfocusedContainerColor = VaultSurface,
                            focusedTextColor = VaultTextPrimary,
                            unfocusedTextColor = VaultTextPrimary
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        modifier = Modifier.background(VaultSurfaceHighlight)
                    ) {
                        VaultCategory.ALL.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(text = cat.name, color = VaultTextPrimary, fontWeight = FontWeight.SemiBold)
                                        Text(text = cat.description, color = VaultTextSecondary, fontSize = 11.sp)
                                    }
                                },
                                onClick = {
                                    categoryId = cat.id
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Field: Record Title
                Text(
                    text = "RECORD TITLE",
                    color = VaultTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        validationError = null
                    },
                    placeholder = { Text("e.g. Master Swiss Account, AWS Prod Key", color = VaultTextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("record_title_input"),
                    shape = RoundedCornerShape(12.dp),
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

                // Field: Secret Value (with Generator Button!)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ENCRYPTED SECRET VALUE",
                        color = VaultTextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    // Generate Password Action
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(VaultSurfaceHighlight)
                            .border(0.8.dp, VaultBorderHighlight, RoundedCornerShape(6.dp))
                            .clickable {
                                secretValue = generateSecureSecret()
                                isSecretVisible = true
                            }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("generate_secret_button"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Generate",
                            tint = VaultEmeraldBright,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Auto-Gen Key",
                            color = VaultEmeraldBright,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = secretValue,
                    onValueChange = {
                        secretValue = it
                        validationError = null
                    },
                    placeholder = { Text("Enter private key, password, account number...", color = VaultTextMuted, fontSize = 13.sp) },
                    visualTransformation = if (isSecretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { isSecretVisible = !isSecretVisible }) {
                            Icon(
                                imageVector = if (isSecretVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (isSecretVisible) "Hide Secret" else "Show Secret",
                                tint = VaultTextSecondary
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("record_secret_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultEmerald,
                        unfocusedBorderColor = VaultBorder,
                        focusedContainerColor = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        cursorColor = VaultEmeraldBright,
                        focusedTextColor = VaultEmeraldBright,
                        unfocusedTextColor = VaultTextPrimary
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Field: Secondary Identifier / Username
                Text(
                    text = "SECONDARY IDENTIFIER (OPTIONAL)",
                    color = VaultTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = secondaryValue,
                    onValueChange = { secondaryValue = it },
                    placeholder = { Text("e.g. Account Holder, Expiration, Username, Routing", color = VaultTextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("record_secondary_input"),
                    shape = RoundedCornerShape(12.dp),
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

                // Field: Security Classification
                Text(
                    text = "SECURITY CLASSIFICATION",
                    color = VaultTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ClassificationPill(
                        label = "RESTRICTED",
                        color = VaultAmber,
                        isSelected = securityLevel == "RESTRICTED",
                        onClick = { securityLevel = "RESTRICTED" },
                        modifier = Modifier.weight(1f)
                    )
                    ClassificationPill(
                        label = "CONFIDENTIAL",
                        color = VaultEmerald,
                        isSelected = securityLevel == "CONFIDENTIAL",
                        onClick = { securityLevel = "CONFIDENTIAL" },
                        modifier = Modifier.weight(1f)
                    )
                    ClassificationPill(
                        label = "TOP SECRET",
                        color = VaultRed,
                        isSelected = securityLevel == "TOP SECRET",
                        onClick = { securityLevel = "TOP SECRET" },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Field: Notes
                Text(
                    text = "ADDITIONAL ENCRYPTED NOTES",
                    color = VaultTextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    placeholder = { Text("Discreet recovery memos, dual-authorization requirements...", color = VaultTextMuted, fontSize = 13.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .testTag("record_notes_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = VaultEmerald,
                        unfocusedBorderColor = VaultBorder,
                        focusedContainerColor = VaultSurface,
                        unfocusedContainerColor = VaultSurface,
                        cursorColor = VaultEmeraldBright,
                        focusedTextColor = VaultTextPrimary,
                        unfocusedTextColor = VaultTextPrimary
                    ),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Star Favorite Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(VaultSurface)
                        .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                        .clickable { isFavorite = !isFavorite }
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Classified Favorite (VIP)",
                            color = VaultTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Pin this record to priority compartment quick-access",
                            color = VaultTextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                        contentDescription = "VIP",
                        tint = if (isFavorite) VaultGold else VaultTextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.height(22.dp))

                // Save Button
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            validationError = "Please specify a record title."
                            return@Button
                        }
                        if (secretValue.isBlank()) {
                            validationError = "Please enter the confidential secret payload."
                            return@Button
                        }
                        onSave(
                            title.trim(),
                            categoryId,
                            secretValue.trim(),
                            secondaryValue.trim(),
                            notes.trim(),
                            securityLevel,
                            isFavorite
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("commit_record_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = VaultEmerald,
                        contentColor = VaultBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (recordToEdit != null) "Update Sealed Secret" else "Commit Record to Vault",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.4.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ClassificationPill(
    label: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) color.copy(alpha = 0.18f) else VaultSurface)
            .border(
                1.dp,
                if (isSelected) color else VaultBorder,
                RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (isSelected) color else VaultTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

private fun generateSecureSecret(): String {
    val charPool = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%^&*()-_+="
    val random = SecureRandom()
    val sb = StringBuilder(24)
    for (i in 0 until 24) {
        sb.append(charPool[random.nextInt(charPool.length)])
    }
    return sb.toString()
}
