package com.example.data.supabase

import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

class SupabaseClient(private val config: SupabaseConfig) {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    private val authAdapter = moshi.adapter(SupabaseAuthResponse::class.java)
    private val userAdapter = moshi.adapter(SupabaseUser::class.java)
    private val itemsListAdapter = moshi.adapter<List<SupabaseItem>>(
        Types.newParameterizedType(List::class.java, SupabaseItem::class.java)
    )
    private val itemAdapter = moshi.adapter(SupabaseItem::class.java)

    // ==========================================
    // AUTHENTICATION APIs
    // ==========================================

    suspend fun signUp(email: String, password: String): Result<SupabaseAuthResponse> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/auth/v1/signup"
        val payload = mapOf("email" to email, "password" to password)
        val body = moshi.adapter(Map::class.java).toJson(payload).toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        executeRequest(request) { json ->
            try {
                val authRes = authAdapter.fromJson(json)
                if (authRes != null && (authRes.accessToken != null || authRes.user != null)) {
                    return@executeRequest authRes
                }
            } catch (_: Exception) {}

            try {
                val directUser = userAdapter.fromJson(json)
                if (directUser != null && directUser.id.isNotBlank()) {
                    return@executeRequest SupabaseAuthResponse(
                        accessToken = null,
                        refreshToken = null,
                        user = directUser
                    )
                }
            } catch (_: Exception) {}

            authAdapter.fromJson(json) ?: throw IOException("Empty auth response")
        }
    }

    suspend fun signInWithPassword(email: String, password: String): Result<SupabaseAuthResponse> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/auth/v1/token?grant_type=password"
        val payload = mapOf("email" to email, "password" to password)
        val body = moshi.adapter(Map::class.java).toJson(payload).toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        executeRequest(request) { json ->
            authAdapter.fromJson(json) ?: throw IOException("Empty auth response")
        }
    }

    suspend fun resetPassword(email: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/auth/v1/recover"
        val payload = mapOf("email" to email)
        val body = moshi.adapter(Map::class.java).toJson(payload).toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        executeRequest(request) { true }
    }

    suspend fun signOut(accessToken: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/auth/v1/logout"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .post("{}".toRequestBody(jsonMediaType))
            .build()

        executeRequest(request) { true }
    }

    suspend fun getCurrentUser(accessToken: String): Result<SupabaseUser> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/auth/v1/user"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        executeRequest(request) { json ->
            userAdapter.fromJson(json) ?: throw IOException("Failed to parse user profile")
        }
    }

    // ==========================================
    // DATABASE APIs ("user_items" table)
    // ==========================================

    suspend fun getUserItems(accessToken: String): Result<List<SupabaseItem>> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/rest/v1/user_items?select=*&order=created_at.desc"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        executeRequest(request) { json ->
            itemsListAdapter.fromJson(json) ?: emptyList()
        }
    }

    suspend fun getAllItemsForAdmin(accessToken: String): Result<List<SupabaseItem>> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/rest/v1/user_items?select=*&order=created_at.desc"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .get()
            .build()

        executeRequest(request) { json ->
            itemsListAdapter.fromJson(json) ?: emptyList()
        }
    }

    suspend fun insertItem(
        accessToken: String,
        userId: String,
        category: String,
        title: String,
        content: String
    ): Result<SupabaseItem> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/rest/v1/user_items"
        val payload = mapOf(
            "user_id" to userId,
            "category" to category,
            "title" to title,
            "content" to content
        )
        val body = moshi.adapter(Map::class.java).toJson(payload).toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Prefer", "return=representation")
            .post(body)
            .build()

        executeRequest(request) { json ->
            val list = itemsListAdapter.fromJson(json)
            list?.firstOrNull() ?: itemAdapter.fromJson(json) ?: throw IOException("Failed to parse created item")
        }
    }

    suspend fun updateItem(
        accessToken: String,
        id: Long,
        category: String,
        title: String,
        content: String
    ): Result<SupabaseItem?> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/rest/v1/user_items?id=eq.$id"
        val payload = mapOf(
            "category" to category,
            "title" to title,
            "content" to content
        )
        val body = moshi.adapter(Map::class.java).toJson(payload).toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .addHeader("Prefer", "return=representation")
            .patch(body)
            .build()

        executeRequest(request) { json ->
            val list = itemsListAdapter.fromJson(json)
            list?.firstOrNull()
        }
    }

    suspend fun deleteItem(accessToken: String, id: Long): Result<Boolean> = withContext(Dispatchers.IO) {
        val url = "${config.supabaseUrl}/rest/v1/user_items?id=eq.$id"
        val request = Request.Builder()
            .url(url)
            .addHeader("apikey", config.supabaseAnonKey)
            .addHeader("Authorization", "Bearer $accessToken")
            .delete()
            .build()

        executeRequest(request) { true }
    }

    // ==========================================
    // Request Helper
    // ==========================================

    private inline fun <T> executeRequest(request: Request, parser: (String) -> T): Result<T> {
        return try {
            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMsg = extractErrorMessage(response.code, responseBody)
                Result.failure(IOException(errorMsg))
            } else {
                Result.success(parser(responseBody))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun extractErrorMessage(statusCode: Int, body: String): String {
        return try {
            val map = moshi.adapter(Map::class.java).fromJson(body)
            val msg = map?.get("message") as? String
                ?: map?.get("error_description") as? String
                ?: map?.get("msg") as? String
                ?: map?.get("error") as? String
            msg ?: "Supabase HTTP error $statusCode"
        } catch (_: Exception) {
            "Supabase HTTP error $statusCode: ${body.take(120)}"
        }
    }
}
