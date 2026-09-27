package com.example.data.repository

import com.example.data.local.VaultDao
import com.example.data.local.VaultDatabase
import com.example.data.model.VaultActivity
import com.example.data.model.VaultRecord
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseItem
import com.example.data.supabase.SupabaseItemMapper
import com.example.data.supabase.SupabaseSessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class VaultRepository(
    private val dao: VaultDao,
    private val sessionManager: SupabaseSessionManager,
    private val supabaseClient: SupabaseClient
) {

    val allRecords: Flow<List<VaultRecord>> = dao.getAllRecords()
    val favoriteRecords: Flow<List<VaultRecord>> = dao.getFavoriteRecords()
    val recentActivities: Flow<List<VaultActivity>> = dao.getRecentActivities(50)
    val recordCount: Flow<Int> = dao.getRecordCount()

    fun getRecordsByCategory(category: String): Flow<List<VaultRecord>> {
        return dao.getRecordsByCategory(category)
    }

    fun searchRecords(query: String): Flow<List<VaultRecord>> {
        return dao.searchRecords(query)
    }

    suspend fun syncFromSupabase(): Result<Int> = withContext(Dispatchers.IO) {
        val token = sessionManager.getAccessToken()
        if (token.isNullOrBlank()) {
            return@withContext Result.failure(IllegalStateException("No active Supabase session"))
        }

        val result = supabaseClient.getUserItems(token)
        if (result.isSuccess) {
            val remoteItems = result.getOrThrow()
            val mappedRecords = remoteItems.map { item ->
                val record = SupabaseItemMapper.toVaultRecord(item)
                val existingLocal = item.id?.let { dao.getRecordByRemoteId(it) }
                if (existingLocal != null) {
                    record.copy(id = existingLocal.id)
                } else {
                    record
                }
            }

            // Sync into local database
            dao.clearAllRecords()
            if (mappedRecords.isNotEmpty()) {
                dao.insertRecords(mappedRecords)
            }

            logActivity(
                action = "Cloud sync",
                category = "Supabase",
                details = "Synchronized ${mappedRecords.size} items from user_items",
                status = "VERIFIED"
            )
            Result.success(mappedRecords.size)
        } else {
            val err = result.exceptionOrNull() ?: Exception("Unknown error")
            Result.failure(err)
        }
    }

    suspend fun fetchAdminItems(): Result<List<SupabaseItem>> = withContext(Dispatchers.IO) {
        val token = sessionManager.getAccessToken()
        if (token.isNullOrBlank()) {
            return@withContext Result.failure(IllegalStateException("No active admin session"))
        }
        supabaseClient.getAllItemsForAdmin(token)
    }

    suspend fun saveRecord(record: VaultRecord, isNew: Boolean): Long = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val token = sessionManager.getAccessToken()
        val currentUserId = sessionManager.getCurrentUserId()
        var updatedRecord = record.copy(updatedAt = now)

        // Sync to Supabase user_items if authenticated
        if (!token.isNullOrBlank() && !currentUserId.isNullOrBlank()) {
            val payload = SupabaseItemMapper.toContentPayload(record)
            if (record.remoteId != null && !isNew) {
                // Update in Supabase
                val updateRes = supabaseClient.updateItem(
                    accessToken = token,
                    id = record.remoteId,
                    category = record.category,
                    title = record.title,
                    content = payload
                )
                if (updateRes.isFailure) {
                    logActivity(
                        action = "Sync warning",
                        category = "Supabase",
                        details = "Local record updated; remote sync pending: ${updateRes.exceptionOrNull()?.message}",
                        status = "WARNING"
                    )
                }
            } else {
                // Insert into Supabase with the authenticated user ID
                val insertRes = supabaseClient.insertItem(
                    accessToken = token,
                    userId = currentUserId,
                    category = record.category,
                    title = record.title,
                    content = payload
                )
                if (insertRes.isSuccess) {
                    val created = insertRes.getOrThrow()
                    updatedRecord = updatedRecord.copy(
                        remoteId = created.id,
                        userId = currentUserId
                    )
                } else {
                    logActivity(
                        action = "Sync warning",
                        category = "Supabase",
                        details = "Stored locally; remote insert pending: ${insertRes.exceptionOrNull()?.message}",
                        status = "WARNING"
                    )
                }
            }
        }

        val id = dao.insertRecord(updatedRecord)
        val actionText = if (isNew) "Record added" else "Record updated"
        dao.insertActivity(
            VaultActivity(
                action = actionText,
                category = record.category,
                details = "${record.title} safely encrypted into vault compartment",
                timestamp = now,
                status = "SECURE"
            )
        )
        id
    }

    suspend fun toggleFavorite(record: VaultRecord) {
        val updated = record.copy(isFavorite = !record.isFavorite, updatedAt = System.currentTimeMillis())
        dao.updateRecord(updated)
        // If has remote id and session, update payload
        val token = sessionManager.getAccessToken()
        if (!token.isNullOrBlank() && record.remoteId != null) {
            val payload = SupabaseItemMapper.toContentPayload(updated)
            supabaseClient.updateItem(token, record.remoteId, updated.category, updated.title, payload)
        }
    }

    suspend fun deleteRecord(record: VaultRecord) = withContext(Dispatchers.IO) {
        val token = sessionManager.getAccessToken()
        if (!token.isNullOrBlank() && record.remoteId != null) {
            val delRes = supabaseClient.deleteItem(token, record.remoteId)
            if (delRes.isFailure) {
                logActivity(
                    action = "Sync warning",
                    category = "Supabase",
                    details = "Purged locally; remote delete error: ${delRes.exceptionOrNull()?.message}",
                    status = "WARNING"
                )
            }
        }

        dao.deleteRecord(record)
        dao.insertActivity(
            VaultActivity(
                action = "Record purged",
                category = record.category,
                details = "${record.title} securely destroyed from storage",
                timestamp = System.currentTimeMillis(),
                status = "WARNING"
            )
        )
    }

    suspend fun logActivity(action: String, category: String, details: String, status: String = "VERIFIED") {
        dao.insertActivity(
            VaultActivity(
                action = action,
                category = category,
                details = details,
                timestamp = System.currentTimeMillis(),
                status = status
            )
        )
    }

    suspend fun resetWithDemoData() {
        dao.clearAllRecords()
        dao.clearActivities()
        VaultDatabase.populateInitialData(dao)
    }

    suspend fun clearVault() {
        dao.clearAllRecords()
        dao.clearActivities()
        dao.insertActivity(
            VaultActivity(
                action = "Vault wiped",
                category = "Security",
                details = "Emergency data destruction executed by user directive",
                timestamp = System.currentTimeMillis(),
                status = "WARNING"
            )
        )
    }

    suspend fun clearLocalUserCache() {
        dao.clearAllRecords()
    }
}
