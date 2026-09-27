package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val BankVaultColorScheme = darkColorScheme(
    primary = VaultEmerald,
    onPrimary = VaultBackground,
    primaryContainer = VaultEmeraldSubtle,
    onPrimaryContainer = VaultEmeraldBright,
    secondary = VaultEmeraldBright,
    onSecondary = VaultBackground,
    secondaryContainer = VaultSurfaceElevated,
    onSecondaryContainer = VaultTextPrimary,
    tertiary = VaultGold,
    onTertiary = VaultBackground,
    background = VaultBackground,
    onBackground = VaultTextPrimary,
    surface = VaultSurface,
    onSurface = VaultTextPrimary,
    surfaceVariant = VaultSurfaceElevated,
    onSurfaceVariant = VaultTextSecondary,
    outline = VaultBorder,
    outlineVariant = VaultDivider,
    error = VaultRed,
    onError = VaultBackground
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Bank Vault strictly uses the curated luxury dark security aesthetic
    MaterialTheme(
        colorScheme = BankVaultColorScheme,
        typography = Typography,
        content = content
    )
}
