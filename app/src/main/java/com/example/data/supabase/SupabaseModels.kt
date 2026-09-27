package com.example.data.supabase

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SupabaseUser(
    val id: String,
    val email: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    val role: String? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseAuthResponse(
    @Json(name = "access_token") val accessToken: String? = null,
    @Json(name = "refresh_token") val refreshToken: String? = null,
    @Json(name = "token_type") val tokenType: String? = null,
    @Json(name = "expires_in") val expiresIn: Long? = null,
    val user: SupabaseUser? = null
)

@JsonClass(generateAdapter = true)
data class SupabaseItem(
    val id: Long? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    val category: String,
    val title: String,
    val content: String,
    @Json(name = "user_id") val userId: String
)

sealed interface AuthState {
    data object Loading : AuthState
    data object Unauthenticated : AuthState
    data class Authenticated(val user: SupabaseUser, val isAdmin: Boolean) : AuthState
}
