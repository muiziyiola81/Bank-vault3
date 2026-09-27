package com.example.data.supabase

import android.content.Context
import com.example.BuildConfig

class SupabaseConfig(context: Context) {
    private val prefs = context.getSharedPreferences("supabase_config_prefs", Context.MODE_PRIVATE)

    var customUrl: String?
        get() = prefs.getString("custom_supabase_url", null)
        set(value) = prefs.edit().putString("custom_supabase_url", value).apply()

    var customAnonKey: String?
        get() = prefs.getString("custom_supabase_anon_key", null)
        set(value) = prefs.edit().putString("custom_supabase_anon_key", value).apply()

    var customAdminUserId: String?
        get() = prefs.getString("custom_supabase_admin_user_id", null)
        set(value) = prefs.edit().putString("custom_supabase_admin_user_id", value).apply()

    val supabaseUrl: String
        get() {
            val custom = customUrl?.trim()
            if (!custom.isNullOrEmpty()) return custom.trimEnd('/')
            val buildVal = BuildConfig.SUPABASE_URL.trim()
            return if (buildVal.isNotEmpty()) buildVal.trimEnd('/') else "https://your-project.supabase.co"
        }

    val supabaseAnonKey: String
        get() {
            val custom = customAnonKey?.trim()
            if (!custom.isNullOrEmpty()) return custom
            val buildVal = BuildConfig.SUPABASE_ANON_KEY.trim()
            return if (buildVal.isNotEmpty()) buildVal else "your-supabase-anon-key"
        }

    val adminUserId: String
        get() {
            val custom = customAdminUserId?.trim()
            if (!custom.isNullOrEmpty()) return custom
            val buildVal = BuildConfig.SUPABASE_ADMIN_USER_ID.trim()
            return if (buildVal.isNotEmpty()) buildVal else "00000000-0000-0000-0000-000000000000"
        }

    val isConfigured: Boolean
        get() {
            val url = supabaseUrl
            val key = supabaseAnonKey
            return url.isNotEmpty() &&
                    !url.contains("your-project.supabase.co") &&
                    key.isNotEmpty() &&
                    !key.contains("your-supabase-anon-key")
        }

    fun isUserAdmin(userId: String): Boolean {
        if (userId.isBlank()) return false
        val targetAdmin = adminUserId.trim()
        if (targetAdmin.isEmpty() || targetAdmin.startsWith("00000000")) return false
        return userId.equals(targetAdmin, ignoreCase = true)
    }

    fun saveConfig(url: String, anonKey: String, adminId: String?) {
        customUrl = url.trim().trimEnd('/')
        customAnonKey = anonKey.trim()
        if (adminId != null) {
            customAdminUserId = adminId.trim()
        }
    }

    fun resetToDefaults() {
        prefs.edit().clear().apply()
    }
}
