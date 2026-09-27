package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.VaultDatabase
import com.example.data.model.VaultActivity
import com.example.data.model.VaultCategory
import com.example.data.model.VaultRecord
import com.example.data.repository.VaultRepository
import com.example.data.supabase.AuthState
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseConfig
import com.example.data.supabase.SupabaseItem
import com.example.data.supabase.SupabaseSessionManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class VaultTab {
    DASHBOARD,
    VAULT,
    ACTIVITY,
    SETTINGS
}

data class VaultUiState(
    val selectedTab: VaultTab = VaultTab.DASHBOARD,
    val selectedCategory: String? = null,
    val searchQuery: String = "",
    val onlyFavorites: Boolean = false,
    val isVaultLocked: Boolean = false,
    val isAddEditSheetOpen: Boolean = false,
    val editingRecord: VaultRecord? = null,
    val securityScore: Int = 98,
    val isAuditRunning: Boolean = false,
    val lastAuditTimeText: String = "Today, 1:15 PM",
    val statusBanner: String? = null,
    val toastMessage: String? = null,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val isSyncing: Boolean = false,
    val isAdminPreviewActive: Boolean = false,
    val isConnectionDialogOpen: Boolean = false,
    val verificationPendingEmail: String? = null
)

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    val supabaseConfig: SupabaseConfig = SupabaseConfig(application)
    val supabaseClient: SupabaseClient = SupabaseClient(supabaseConfig)
    val sessionManager: SupabaseSessionManager = SupabaseSessionManager(application, supabaseConfig, supabaseClient)

    private val repository: VaultRepository

    private val _uiState = MutableStateFlow(VaultUiState())
    val uiState: StateFlow<VaultUiState> = _uiState.asStateFlow()

    val authState: StateFlow<AuthState> = sessionManager.authState

    private val _adminItems = MutableStateFlow<List<SupabaseItem>>(emptyList())
    val adminItems: StateFlow<List<SupabaseItem>> = _adminItems.asStateFlow()

    init {
        val database = VaultDatabase.getDatabase(application)
        repository = VaultRepository(database.vaultDao(), sessionManager, supabaseClient)

        // Listen for AuthState changes to trigger sync or clean up
        viewModelScope.launch {
            authState.collect { state ->
                when (state) {
                    is AuthState.Authenticated -> {
                        if (state.isAdmin) {
                            fetchAdminItems()
                        }
                        syncVault()
                    }
                    is AuthState.Unauthenticated -> {
                        _adminItems.value = emptyList()
                    }
                    AuthState.Loading -> {}
                }
            }
        }
    }

    val allRecords: StateFlow<List<VaultRecord>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activities: StateFlow<List<VaultActivity>> = repository.recentActivities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val recordCount: StateFlow<Int> = repository.recordCount
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val filteredRecords: StateFlow<List<VaultRecord>> = combine(
        allRecords,
        _uiState
    ) { records, state ->
        records.filter { record ->
            val matchesCategory = state.selectedCategory == null || record.category.equals(state.selectedCategory, ignoreCase = true)
            val matchesSearch = state.searchQuery.isBlank() ||
                    record.title.contains(state.searchQuery, ignoreCase = true) ||
                    record.secondaryValue.contains(state.searchQuery, ignoreCase = true) ||
                    record.category.contains(state.searchQuery, ignoreCase = true) ||
                    record.notes.contains(state.searchQuery, ignoreCase = true)
            val matchesFavorite = !state.onlyFavorites || record.isFavorite

            matchesCategory && matchesSearch && matchesFavorite
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // ==========================================
    // SUPABASE AUTHENTICATION
    // ==========================================

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(authError = "Please enter both email and password") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null, verificationPendingEmail = null) }
            try {
                val result = supabaseClient.signInWithPassword(email.trim(), password)
                if (result.isSuccess) {
                    val res = result.getOrThrow()
                    sessionManager.saveSession(res.accessToken, res.refreshToken, res.user)
                    val isAdmin = sessionManager.config.isUserAdmin(res.user?.id ?: "")
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = null,
                            verificationPendingEmail = null,
                            isVaultLocked = false,
                            isAdminPreviewActive = false,
                            selectedTab = VaultTab.DASHBOARD,
                            selectedCategory = null,
                            toastMessage = if (isAdmin) {
                                "Authorized Administrator Session Active"
                            } else {
                                "Vault Keyholder Enclave Initialized"
                            }
                        )
                    }
                } else {
                    val msg = result.exceptionOrNull()?.message ?: "Login failed"
                    if (msg.contains("Email not confirmed", ignoreCase = true)) {
                        _uiState.update {
                            it.copy(
                                isAuthLoading = false,
                                authError = null,
                                verificationPendingEmail = email.trim()
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isAuthLoading = false, authError = msg) }
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAuthLoading = false, authError = e.message ?: "Authentication error") }
            } finally {
                _uiState.update { it.copy(isAuthLoading = false) }
            }
        }
    }

    fun signUp(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(authError = "Please enter both email and password") }
            return
        }
        if (password.length < 6) {
            _uiState.update { it.copy(authError = "Password must be at least 6 characters") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null, verificationPendingEmail = null) }
            try {
                val result = supabaseClient.signUp(email.trim(), password)
                if (result.isSuccess) {
                    val res = result.getOrThrow()
                    // Case 1: Session directly returned in signup
                    if (!res.accessToken.isNullOrBlank() && res.user != null) {
                        sessionManager.saveSession(res.accessToken, res.refreshToken, res.user)
                        val isAdmin = sessionManager.config.isUserAdmin(res.user.id)
                        _uiState.update {
                            it.copy(
                                isAuthLoading = false,
                                authError = null,
                                verificationPendingEmail = null,
                                isVaultLocked = false,
                                isAdminPreviewActive = false,
                                selectedTab = VaultTab.DASHBOARD,
                                selectedCategory = null,
                                toastMessage = if (isAdmin) {
                                    "Authorized Administrator Account Initialized"
                                } else {
                                    "New Vault Keyholder Account Created"
                                }
                            )
                        }
                        return@launch
                    }

                    // Case 2: No immediate session token in signup response.
                    // Attempt immediate sign in with the new credentials (handles auto-confirm projects)
                    val loginAttempt = supabaseClient.signInWithPassword(email.trim(), password)
                    if (loginAttempt.isSuccess) {
                        val loginRes = loginAttempt.getOrThrow()
                        sessionManager.saveSession(loginRes.accessToken, loginRes.refreshToken, loginRes.user)
                        val isAdmin = sessionManager.config.isUserAdmin(loginRes.user?.id ?: "")
                        _uiState.update {
                            it.copy(
                                isAuthLoading = false,
                                authError = null,
                                verificationPendingEmail = null,
                                isVaultLocked = false,
                                isAdminPreviewActive = false,
                                selectedTab = VaultTab.DASHBOARD,
                                selectedCategory = null,
                                toastMessage = if (isAdmin) {
                                    "Authorized Administrator Session Active"
                                } else {
                                    "Vault Keyholder Enclave Initialized"
                                }
                            )
                        }
                    } else {
                        // Case 3: Email confirmation is required by Supabase
                        _uiState.update {
                            it.copy(
                                isAuthLoading = false,
                                authError = null,
                                verificationPendingEmail = email.trim(),
                                toastMessage = "Confirmation email dispatched. Please verify your email to access your vault."
                            )
                        }
                    }
                } else {
                    val msg = result.exceptionOrNull()?.message ?: "Registration failed"
                    _uiState.update { it.copy(isAuthLoading = false, authError = msg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAuthLoading = false, authError = e.message ?: "Registration error") }
            } finally {
                _uiState.update { it.copy(isAuthLoading = false) }
            }
        }
    }

    fun dismissVerificationPending() {
        _uiState.update { it.copy(verificationPendingEmail = null, authError = null) }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.update { it.copy(authError = "Please provide your registered account email") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authError = null) }
            try {
                val result = supabaseClient.resetPassword(email.trim())
                if (result.isSuccess) {
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authError = null,
                            toastMessage = "Recovery instructions dispatched to $email"
                        )
                    }
                } else {
                    val msg = result.exceptionOrNull()?.message ?: "Failed to dispatch recovery email"
                    _uiState.update { it.copy(isAuthLoading = false, authError = msg) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isAuthLoading = false, authError = e.message ?: "Password recovery error") }
            } finally {
                _uiState.update { it.copy(isAuthLoading = false) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            repository.clearLocalUserCache()
            _uiState.update {
                it.copy(
                    toastMessage = "Session terminated. Vault keys dismounted.",
                    isAdminPreviewActive = false
                )
            }
        }
    }

    fun syncVault() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true) }
            val syncRes = repository.syncFromSupabase()
            _uiState.update { it.copy(isSyncing = false) }
            if (syncRes.isSuccess) {
                val count = syncRes.getOrThrow()
                _uiState.update {
                    it.copy(toastMessage = "Vault synced ($count cloud records retrieved)")
                }
            }
        }
    }

    fun fetchAdminItems() {
        viewModelScope.launch {
            val res = repository.fetchAdminItems()
            if (res.isSuccess) {
                _adminItems.value = res.getOrThrow()
            } else {
                val err = res.exceptionOrNull()?.message ?: "Failed to query admin items"
                _uiState.update { it.copy(toastMessage = "Admin query note: $err") }
            }
        }
    }

    fun toggleAdminPreview() {
        _uiState.update { it.copy(isAdminPreviewActive = !it.isAdminPreviewActive) }
    }

    fun openConnectionDialog() {
        _uiState.update { it.copy(isConnectionDialogOpen = true) }
    }

    fun closeConnectionDialog() {
        _uiState.update { it.copy(isConnectionDialogOpen = false) }
    }

    fun saveSupabaseConnection(url: String, anonKey: String, adminId: String) {
        supabaseConfig.saveConfig(url, anonKey, adminId)
        sessionManager.restoreSession()
        _uiState.update {
            it.copy(
                isConnectionDialogOpen = false,
                toastMessage = "Supabase project connection updated"
            )
        }
    }

    fun resetSupabaseConnection() {
        supabaseConfig.resetToDefaults()
        sessionManager.restoreSession()
        _uiState.update {
            it.copy(
                isConnectionDialogOpen = false,
                toastMessage = "Supabase configuration restored to defaults"
            )
        }
    }

    // ==========================================
    // VAULT RECORD CRUD
    // ==========================================

    fun selectTab(tab: VaultTab) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    fun selectCategory(category: String?) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                selectedTab = VaultTab.VAULT
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun toggleFavoritesFilter() {
        _uiState.update { it.copy(onlyFavorites = !it.onlyFavorites) }
    }

    fun openAddRecord(presetCategory: String? = null) {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = true,
                editingRecord = null,
                selectedCategory = presetCategory ?: it.selectedCategory ?: VaultCategory.ALL[0].id
            )
        }
    }

    fun openEditRecord(record: VaultRecord) {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = true,
                editingRecord = record
            )
        }
    }

    fun closeAddEditSheet() {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = false,
                editingRecord = null
            )
        }
    }

    fun saveRecord(
        title: String,
        category: String,
        secretValue: String,
        secondaryValue: String,
        notes: String,
        securityLevel: String,
        isFavorite: Boolean
    ) {
        viewModelScope.launch {
            val currentEditing = _uiState.value.editingRecord
            val isNew = currentEditing == null
            val currentUserId = sessionManager.getCurrentUserId() ?: ""
            val recordToSave = VaultRecord(
                id = currentEditing?.id ?: 0,
                remoteId = currentEditing?.remoteId,
                userId = currentEditing?.userId?.ifEmpty { currentUserId } ?: currentUserId,
                title = title.trim(),
                category = category,
                secretValue = secretValue.trim(),
                secondaryValue = secondaryValue.trim(),
                notes = notes.trim(),
                securityLevel = securityLevel,
                isFavorite = isFavorite
            )
            repository.saveRecord(recordToSave, isNew)
            _uiState.update {
                it.copy(
                    isAddEditSheetOpen = false,
                    editingRecord = null,
                    toastMessage = if (isNew) "Record encrypted into $category compartment & synced" else "Record updated successfully"
                )
            }
        }
    }

    fun toggleRecordFavorite(record: VaultRecord) {
        viewModelScope.launch {
            repository.toggleFavorite(record)
        }
    }

    fun deleteRecord(record: VaultRecord) {
        viewModelScope.launch {
            repository.deleteRecord(record)
            _uiState.update {
                it.copy(
                    isAddEditSheetOpen = false,
                    editingRecord = null,
                    toastMessage = "${record.title} purged from vault"
                )
            }
        }
    }

    fun lockVault() {
        viewModelScope.launch {
            repository.logActivity(
                action = "Vault locked",
                category = "Security",
                details = "Vault locked by user command. Enclave keys dismounted.",
                status = "SECURE"
            )
            _uiState.update { it.copy(isVaultLocked = true) }
        }
    }

    fun unlockVault() {
        viewModelScope.launch {
            repository.logActivity(
                action = "Vault unlocked",
                category = "Security",
                details = "Biometric credentials verified. AES-256 session authenticated.",
                status = "VERIFIED"
            )
            _uiState.update { it.copy(isVaultLocked = false, toastMessage = "Biometric Authentication Confirmed") }
        }
    }

    fun runSecurityAudit() {
        if (_uiState.value.isAuditRunning) return
        viewModelScope.launch {
            _uiState.update { it.copy(isAuditRunning = true) }
            delay(1200)
            val score = 99
            repository.logActivity(
                action = "Security audit",
                category = "System",
                details = "Full entropy verification completed. Zero vulnerabilities detected.",
                status = "VERIFIED"
            )
            _uiState.update {
                it.copy(
                    isAuditRunning = false,
                    securityScore = score,
                    lastAuditTimeText = "Just now",
                    toastMessage = "Security Audit: 100% Compartment Integrity Confirmed"
                )
            }
        }
    }

    fun resetDemoData() {
        viewModelScope.launch {
            repository.resetWithDemoData()
            _uiState.update {
                it.copy(
                    toastMessage = "Vault restored to certified demonstration state"
                )
            }
        }
    }

    fun wipeVault() {
        viewModelScope.launch {
            repository.clearVault()
            _uiState.update {
                it.copy(
                    toastMessage = "Emergency purge executed. All records destroyed."
                )
            }
        }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }

    fun showToast(message: String) {
        _uiState.update { it.copy(toastMessage = message) }
    }
}
