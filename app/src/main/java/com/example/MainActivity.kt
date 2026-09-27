package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.VaultCategory
import com.example.data.supabase.AuthState
import com.example.ui.components.VaultBottomNav
import com.example.ui.screens.ActivityLogScreen
import com.example.ui.screens.AddEditRecordDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BankVaultAdminScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SupabaseConnectionDialog
import com.example.ui.screens.VaultLockScreen
import com.example.ui.screens.VaultScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.VaultBackground
import com.example.ui.theme.VaultBorderHighlight
import com.example.ui.theme.VaultEmerald
import com.example.ui.theme.VaultEmeraldBright
import com.example.ui.theme.VaultEmeraldGlow
import com.example.ui.theme.VaultGold
import com.example.ui.theme.VaultSurface
import com.example.ui.theme.VaultSurfaceElevated
import com.example.ui.theme.VaultTextPrimary
import com.example.ui.theme.VaultTextSecondary
import com.example.ui.viewmodel.VaultTab
import com.example.ui.viewmodel.VaultViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BankVaultApp()
            }
        }
    }
}

@Composable
fun BankVaultApp(viewModel: VaultViewModel = viewModel()) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val allRecords by viewModel.allRecords.collectAsStateWithLifecycle()
    val filteredRecords by viewModel.filteredRecords.collectAsStateWithLifecycle()
    val activities by viewModel.activities.collectAsStateWithLifecycle()
    val recordCount by viewModel.recordCount.collectAsStateWithLifecycle()
    val adminItems by viewModel.adminItems.collectAsStateWithLifecycle()

    val clipboardManager = LocalClipboardManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle toast messages from ViewModel
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { msg ->
            scope.launch {
                snackbarHostState.showSnackbar(msg)
            }
            viewModel.clearToast()
        }
    }

    // Top-level Auth State Routing
    when (val state = authState) {
        is AuthState.Loading -> {
            // Elegant Loading Splash
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(VaultBackground),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(VaultEmeraldGlow, VaultSurfaceElevated, VaultBackground)
                                )
                            )
                            .border(1.dp, VaultBorderHighlight, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Shield,
                            contentDescription = null,
                            tint = VaultEmeraldBright,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "BANK VAULT",
                        color = VaultTextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    CircularProgressIndicator(
                        color = VaultEmeraldBright,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Verifying Supabase cryptographic session...",
                        color = VaultTextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        is AuthState.Unauthenticated -> {
            // Supabase Login & Registration Screen
            AuthScreen(
                isLoading = uiState.isAuthLoading,
                errorMessage = uiState.authError,
                supabaseUrl = viewModel.supabaseConfig.supabaseUrl,
                verificationPendingEmail = uiState.verificationPendingEmail,
                onLogin = { email, pass -> viewModel.login(email, pass) },
                onSignUp = { email, pass -> viewModel.signUp(email, pass) },
                onResetPassword = { email -> viewModel.resetPassword(email) },
                onDismissVerificationPending = { viewModel.dismissVerificationPending() },
                onConfigureSupabase = { viewModel.openConnectionDialog() }
            )

            if (uiState.isConnectionDialogOpen) {
                SupabaseConnectionDialog(
                    config = viewModel.supabaseConfig,
                    onDismiss = { viewModel.closeConnectionDialog() },
                    onSave = { url, key, adminId -> viewModel.saveSupabaseConnection(url, key, adminId) },
                    onReset = { viewModel.resetSupabaseConnection() }
                )
            }
        }

        is AuthState.Authenticated -> {
            val user = state.user
            val isAdmin = state.isAdmin

            // If Admin and not previewing standard user mode, route to Bank Vault Admin System
            if (isAdmin && !uiState.isAdminPreviewActive) {
                BankVaultAdminScreen(
                    adminUser = user,
                    adminItems = adminItems,
                    onRefresh = { viewModel.fetchAdminItems() },
                    onSwitchToUserView = { viewModel.toggleAdminPreview() },
                    onLogout = { viewModel.logout() }
                )
            } else if (uiState.isVaultLocked) {
                VaultLockScreen(
                    onUnlock = { viewModel.unlockVault() }
                )
            } else {
                // Normal Bank Vault System
                BackHandler(enabled = uiState.selectedCategory != null || uiState.selectedTab != VaultTab.DASHBOARD || uiState.isAdminPreviewActive) {
                    if (uiState.isAdminPreviewActive && uiState.selectedTab == VaultTab.DASHBOARD && uiState.selectedCategory == null) {
                        viewModel.toggleAdminPreview()
                    } else if (uiState.selectedCategory != null) {
                        viewModel.selectCategory(null)
                    } else {
                        viewModel.selectTab(VaultTab.DASHBOARD)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = VaultBackground,
                    topBar = {
                        // Admin preview banner if admin is inspecting normal mode
                        if (isAdmin && uiState.isAdminPreviewActive) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(VaultGold)
                                    .statusBarsPadding()
                                    .clickable { viewModel.toggleAdminPreview() }
                                    .padding(vertical = 6.dp, horizontal = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AdminPanelSettings,
                                        contentDescription = null,
                                        tint = VaultBackground,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = "ADMIN USER PREVIEW MODE • Tap to return to Admin System",
                                        color = VaultBackground,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    bottomBar = {
                        VaultBottomNav(
                            selectedTab = uiState.selectedTab,
                            onTabSelected = { tab -> viewModel.selectTab(tab) }
                        )
                    },
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        AnimatedContent(
                            targetState = uiState.selectedTab,
                            transitionSpec = { fadeIn() togetherWith fadeOut() },
                            label = "tab_transition"
                        ) { targetTab ->
                            when (targetTab) {
                                VaultTab.DASHBOARD -> {
                                    DashboardScreen(
                                        records = allRecords,
                                        activities = activities,
                                        recordCount = recordCount,
                                        securityScore = uiState.securityScore,
                                        lastAuditTimeText = uiState.lastAuditTimeText,
                                        isAuditRunning = uiState.isAuditRunning,
                                        isFavoritesActive = uiState.onlyFavorites,
                                        userEmail = user.email,
                                        isSyncing = uiState.isSyncing,
                                        onSyncClick = { viewModel.syncVault() },
                                        onRunAuditClick = { viewModel.runSecurityAudit() },
                                        onAddRecordClick = { viewModel.openAddRecord() },
                                        onOpenVaultClick = { viewModel.selectTab(VaultTab.VAULT) },
                                        onCategoryClick = { catId -> viewModel.selectCategory(catId) },
                                        onRecentActivityClick = { viewModel.selectTab(VaultTab.ACTIVITY) },
                                        onToggleFavoritesClick = { viewModel.toggleFavoritesFilter() },
                                        onLockClick = { viewModel.lockVault() },
                                        onProfileClick = { viewModel.selectTab(VaultTab.SETTINGS) }
                                    )
                                }

                                VaultTab.VAULT -> {
                                    VaultScreen(
                                        records = filteredRecords,
                                        selectedCategory = uiState.selectedCategory,
                                        searchQuery = uiState.searchQuery,
                                        onlyFavorites = uiState.onlyFavorites,
                                        onCategorySelect = { catId -> viewModel.selectCategory(catId) },
                                        onSearchChange = { query -> viewModel.setSearchQuery(query) },
                                        onToggleFavorites = { viewModel.toggleFavoritesFilter() },
                                        onAddRecordClick = { viewModel.openAddRecord(uiState.selectedCategory) },
                                        onEditRecordClick = { record -> viewModel.openEditRecord(record) },
                                        onDeleteRecordClick = { record -> viewModel.deleteRecord(record) },
                                        onToggleRecordFavorite = { record -> viewModel.toggleRecordFavorite(record) },
                                        onCopyValue = { secret ->
                                            clipboardManager.setText(AnnotatedString(secret))
                                            viewModel.showToast("Copied to Secure Clipboard (Auto-purge in 30s)")
                                        }
                                    )
                                }

                                VaultTab.ACTIVITY -> {
                                    ActivityLogScreen(
                                        activities = activities
                                    )
                                }

                                VaultTab.SETTINGS -> {
                                    SettingsScreen(
                                        currentUser = user,
                                        isAdmin = isAdmin,
                                        supabaseUrl = viewModel.supabaseConfig.supabaseUrl,
                                        onOpenSupabaseConnection = { viewModel.openConnectionDialog() },
                                        onLogout = { viewModel.logout() },
                                        onLockVault = { viewModel.lockVault() },
                                        onRunAudit = { viewModel.runSecurityAudit() },
                                        onResetDemoData = { viewModel.resetDemoData() },
                                        onWipeVault = { viewModel.wipeVault() }
                                    )
                                }
                            }
                        }

                        // Add / Edit Record Dialog Sheet
                        if (uiState.isAddEditSheetOpen) {
                            AddEditRecordDialog(
                                recordToEdit = uiState.editingRecord,
                                defaultCategory = uiState.selectedCategory ?: VaultCategory.ALL[0].id,
                                onDismiss = { viewModel.closeAddEditSheet() },
                                onSave = { title, cat, secret, secondary, notes, secLevel, isFav ->
                                    viewModel.saveRecord(
                                        title = title,
                                        category = cat,
                                        secretValue = secret,
                                        secondaryValue = secondary,
                                        notes = notes,
                                        securityLevel = secLevel,
                                        isFavorite = isFav
                                    )
                                }
                            )
                        }

                        // Supabase Connection Dialog
                        if (uiState.isConnectionDialogOpen) {
                            SupabaseConnectionDialog(
                                config = viewModel.supabaseConfig,
                                onDismiss = { viewModel.closeConnectionDialog() },
                                onSave = { url, key, adminId -> viewModel.saveSupabaseConnection(url, key, adminId) },
                                onReset = { viewModel.resetSupabaseConnection() }
                            )
                        }
                    }
                }
            }
        }
    }
}
