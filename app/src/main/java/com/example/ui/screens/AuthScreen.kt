package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorder
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldGlow
import com.example.ui.theme.VaultRed
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultSurfaceHighlight
import com.example.ui.theme.VaultTextMuted
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary

@Composable
fun AuthScreen(
    isLoading: Boolean,
    errorMessage: String?,
    supabaseUrl: String,
    verificationPendingEmail: String? = null,
    onLogin: (email: String, pass: String) -> Unit,
    onSignUp: (email: String, pass: String) -> Unit,
    onResetPassword: (email: String) -> Unit,
    onDismissVerificationPending: () -> Unit = {},
    onConfigureSupabase: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var localValidation by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = VaultBackground,
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 480.dp)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 20.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Emblem & Brand Title
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(VaultEmeraldGlow, VaultSurfaceElevated, VaultBackground)
                            )
                        )
                        .border(1.5.dp, VaultEmerald.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(VaultSurfaceHighlight)
                            .border(1.dp, VaultBorderHighlight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = "Bank Vault Emblem",
                            tint = VaultEmeraldBright,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "BANK VAULT",
                    color = VaultTextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Encrypted Supabase Security Enclave",
                    color = VaultEmeraldBright,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Main Auth Card Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(VaultSurfaceElevated)
                        .border(1.2.dp, VaultBorderHighlight, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (verificationPendingEmail != null) {
                            // Dedicated Verification Required State
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_verification_pending_container"),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(CircleShape)
                                        .background(VaultEmerald.copy(alpha = 0.15f))
                                        .border(1.2.dp, VaultEmeraldBright, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Email,
                                        contentDescription = "Verification Email",
                                        tint = VaultEmeraldBright,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "Verify Your Email",
                                    color = VaultTextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = "Your account was registered in Supabase. Email confirmation is required by your project before entering the vault enclave.",
                                    color = VaultTextSecondary,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 17.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(VaultSurfaceHighlight)
                                        .border(1.dp, VaultBorder, RoundedCornerShape(10.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = verificationPendingEmail,
                                        color = VaultEmeraldBright,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = "1. Open your inbox and click the verification link.\n2. Tap the button below to initialize your dashboard:",
                                    color = VaultTextMuted,
                                    fontSize = 11.sp,
                                    lineHeight = 16.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(18.dp))

                                Button(
                                    onClick = {
                                        onLogin(verificationPendingEmail, password)
                                    },
                                    enabled = !isLoading,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp)
                                        .testTag("auth_verify_and_login_button"),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = VaultEmerald,
                                        contentColor = VaultBackground
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            color = VaultBackground,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text("I've Confirmed — Open Dashboard", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    TextButton(
                                        onClick = {
                                            onSignUp(verificationPendingEmail, password)
                                        },
                                        enabled = !isLoading
                                    ) {
                                        Text("Resend Email", color = VaultEmeraldBright, fontSize = 12.sp)
                                    }

                                    TextButton(
                                        onClick = {
                                            onDismissVerificationPending()
                                            isSignUpMode = false
                                        }
                                    ) {
                                        Text("Back to Sign In", color = VaultTextSecondary, fontSize = 12.sp)
                                    }
                                }
                            }
                        } else {
                        // Sign In / Sign Up Mode Switcher Tabs
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(VaultSurface)
                                .border(1.dp, VaultBorder, RoundedCornerShape(12.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Surface(
                                onClick = {
                                    isSignUpMode = false
                                    localValidation = null
                                    focusManager.clearFocus()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (!isSignUpMode) VaultSurfaceHighlight else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .testTag("tab_sign_in")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Sign In",
                                        color = if (!isSignUpMode) VaultEmeraldBright else VaultTextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }

                            Surface(
                                onClick = {
                                    isSignUpMode = true
                                    localValidation = null
                                    focusManager.clearFocus()
                                },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSignUpMode) VaultSurfaceHighlight else Color.Transparent,
                                modifier = Modifier
                                    .weight(1f)
                                    .defaultMinSize(minHeight = 44.dp)
                                    .testTag("tab_sign_up")
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(vertical = 10.dp)
                                ) {
                                    Text(
                                        text = "Create Account",
                                        color = if (isSignUpMode) VaultEmeraldBright else VaultTextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Active Error Banner
                        val activeError = localValidation ?: errorMessage
                        AnimatedVisibility(
                            visible = activeError != null,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            if (activeError != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(VaultRed.copy(alpha = 0.15f))
                                        .border(1.dp, VaultRed.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = activeError,
                                        color = VaultRed,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                        }

                        // Account Email Field
                        Text(
                            text = "ACCOUNT EMAIL",
                            color = VaultTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = {
                                email = it
                                localValidation = null
                            },
                            enabled = !isLoading,
                            placeholder = {
                                Text("keyholder@secure.vault", color = VaultTextMuted, fontSize = 13.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Email,
                                    contentDescription = "Email",
                                    tint = VaultTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = FocusDirection.Down.let { ImeAction.Next }
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_email_input"),
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

                        // Master Password Field
                        Text(
                            text = "MASTER PASSWORD",
                            color = VaultTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = password,
                            onValueChange = {
                                password = it
                                localValidation = null
                            },
                            enabled = !isLoading,
                            placeholder = {
                                Text("••••••••••••", color = VaultTextMuted, fontSize = 13.sp)
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Lock,
                                    contentDescription = "Password",
                                    tint = VaultTextSecondary,
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            trailingIcon = {
                                IconButton(
                                    onClick = { isPasswordVisible = !isPasswordVisible },
                                    modifier = Modifier.size(48.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPasswordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                                        tint = VaultTextSecondary
                                    )
                                }
                            },
                            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (isSignUpMode) ImeAction.Next else ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    focusManager.clearFocus()
                                    if (email.isNotBlank() && password.isNotBlank()) {
                                        onLogin(email.trim(), password)
                                    }
                                }
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_password_input"),
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

                        // Confirm Password (Sign Up Mode)
                        if (isSignUpMode) {
                            Spacer(modifier = Modifier.height(14.dp))

                            Text(
                                text = "CONFIRM MASTER PASSWORD",
                                color = VaultTextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            OutlinedTextField(
                                value = confirmPassword,
                                onValueChange = {
                                    confirmPassword = it
                                    localValidation = null
                                },
                                enabled = !isLoading,
                                placeholder = {
                                    Text("••••••••••••", color = VaultTextMuted, fontSize = 13.sp)
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Filled.Lock,
                                        contentDescription = "Confirm password",
                                        tint = VaultTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        if (email.isNotBlank() && password.isNotBlank()) {
                                            if (password == confirmPassword) {
                                                onSignUp(email.trim(), password)
                                            } else {
                                                localValidation = "Passwords do not match."
                                            }
                                        }
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("auth_confirm_password_input"),
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
                        } else {
                            // Forgot Password Link with generous touch target
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(
                                    onClick = {
                                        forgotPasswordEmail = email
                                        showForgotPasswordDialog = true
                                    },
                                    modifier = Modifier.testTag("auth_forgot_password_button")
                                ) {
                                    Text(
                                        text = "Forgot password?",
                                        color = VaultEmeraldBright,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Primary Submit Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                if (email.isBlank()) {
                                    localValidation = "Please enter your email."
                                    return@Button
                                }
                                if (password.isBlank()) {
                                    localValidation = "Please enter your master password."
                                    return@Button
                                }
                                if (isSignUpMode) {
                                    if (password.length < 6) {
                                        localValidation = "Password must be at least 6 characters."
                                        return@Button
                                    }
                                    if (password != confirmPassword) {
                                        localValidation = "Passwords do not match."
                                        return@Button
                                    }
                                    onSignUp(email.trim(), password)
                                } else {
                                    onLogin(email.trim(), password)
                                }
                            },
                            enabled = !isLoading,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("auth_submit_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultEmerald,
                                contentColor = VaultBackground,
                                disabledContainerColor = VaultSurfaceHighlight,
                                disabledContentColor = VaultTextMuted
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = VaultBackground,
                                    strokeWidth = 2.5.dp,
                                    modifier = Modifier.size(22.dp)
                                )
                            } else {
                                Text(
                                    text = if (isSignUpMode) "Create Enclave Account" else "Authorize Vault Access",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }
                    }
                }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Supabase Connection Status Pill & Setup Button
                Surface(
                    onClick = onConfigureSupabase,
                    shape = RoundedCornerShape(30.dp),
                    color = VaultSurfaceElevated,
                    modifier = Modifier
                        .border(1.dp, VaultBorder, RoundedCornerShape(30.dp))
                        .defaultMinSize(minHeight = 44.dp)
                        .testTag("auth_supabase_config_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(VaultEmeraldBright, CircleShape)
                        )
                        Text(
                            text = "Supabase: ${supabaseUrl.replace("https://", "").take(22)}...",
                            color = VaultTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Icon(
                            imageVector = Icons.Filled.Settings,
                            contentDescription = "Configure connection",
                            tint = VaultEmeraldBright,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }

    // Forgot Password Dialog using Compose Dialog
    if (showForgotPasswordDialog) {
        Dialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VaultBackground.copy(alpha = 0.85f))
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = 420.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(VaultSurfaceElevated)
                        .border(1.2.dp, VaultBorderHighlight, RoundedCornerShape(18.dp))
                        .padding(22.dp)
                ) {
                    Text(
                        text = "Reset Master Password",
                        color = VaultTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Enter your registered email. Supabase Auth will dispatch a cryptographic recovery link to reset your credentials.",
                        color = VaultTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        onValueChange = { forgotPasswordEmail = it },
                        placeholder = {
                            Text("keyholder@secure.vault", color = VaultTextMuted, fontSize = 13.sp)
                        },
                        modifier = Modifier.fillMaxWidth(),
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

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = { showForgotPasswordDialog = false },
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                        ) {
                            Text("Cancel", color = VaultTextSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (forgotPasswordEmail.isNotBlank()) {
                                    onResetPassword(forgotPasswordEmail.trim())
                                    showForgotPasswordDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = VaultEmerald,
                                contentColor = VaultBackground
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.defaultMinSize(minHeight = 44.dp)
                        ) {
                            Text("Send Recovery Email", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
