package com.example.data.supabase

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupabaseSessionManager(
    private val context: Context,
    val config: SupabaseConfig,
    val client: SupabaseClient
) {
    private val prefs = context.getSharedPreferences("supabase_session_secure_prefs", Context.MODE_PRIVATE)
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _authState = MutableStateFlow<AuthState>(
        run {
            val token = prefs.getString("access_token", null)
            val userId = prefs.getString("user_id", null)
            val email = prefs.getString("email", null)
            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                AuthState.Unauthenticated
            } else {
                val user = SupabaseUser(id = userId, email = email ?: "")
                val isAdmin = config.isUserAdmin(userId)
                AuthState.Authenticated(user = user, isAdmin = isAdmin)
            }
        }
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        val token = prefs.getString("access_token", null)
        if (!token.isNullOrBlank()) {
            restoreSession()
        }
    }

    fun restoreSession() {
        val token = prefs.getString("access_token", null)
        val userId = prefs.getString("user_id", null)
        val email = prefs.getString("email", null)

        if (token.isNullOrBlank() || userId.isNullOrBlank()) {
            _authState.value = AuthState.Unauthenticated
            return
        }

        val user = SupabaseUser(id = userId, email = email ?: "")
        val isAdmin = config.isUserAdmin(userId)
        _authState.value = AuthState.Authenticated(user = user, isAdmin = isAdmin)

        // Validate session in background with Supabase
        scope.launch {
            val validation = client.getCurrentUser(token)
            if (validation.isSuccess) {
                val freshUser = validation.getOrThrow()
                val refreshedIsAdmin = config.isUserAdmin(freshUser.id)
                saveSession(token, prefs.getString("refresh_token", null), freshUser)
                _authState.value = AuthState.Authenticated(user = freshUser, isAdmin = refreshedIsAdmin)
            } else {
                // If token invalid/expired and cannot be refreshed, clear
                val error = validation.exceptionOrNull()
                if (error?.message?.contains("401", ignoreCase = true) == true ||
                    error?.message?.contains("JWT", ignoreCase = true) == true) {
                    clearSession()
                }
            }
        }
    }

    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }

    fun getCurrentUserId(): String? {
        return prefs.getString("user_id", null)
    }

    fun saveSession(accessToken: String?, refreshToken: String?, user: SupabaseUser?) {
        if (accessToken.isNullOrBlank() || user == null) return
        prefs.edit()
            .putString("access_token", accessToken)
            .putString("refresh_token", refreshToken ?: "")
            .putString("user_id", user.id)
            .putString("email", user.email ?: "")
            .putLong("saved_at", System.currentTimeMillis())
            .apply()

        val isAdmin = config.isUserAdmin(user.id)
        _authState.value = AuthState.Authenticated(user = user, isAdmin = isAdmin)
    }

    fun clearSession() {
        val currentToken = getAccessToken()
        if (!currentToken.isNullOrBlank()) {
            scope.launch {
                client.signOut(currentToken)
            }
        }
        prefs.edit().clear().apply()
        _authState.value = AuthState.Unauthenticated
    }
}
