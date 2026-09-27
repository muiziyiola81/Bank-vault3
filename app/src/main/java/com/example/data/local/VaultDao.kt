package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.VaultActivity
import com.example.data.model.VaultRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface VaultDao {

    // Records
    @Query("SELECT * FROM vault_records ORDER BY isFavorite DESC, updatedAt DESC")
    fun getAllRecords(): Flow<List<VaultRecord>>

    @Query("SELECT * FROM vault_records WHERE category = :category ORDER BY isFavorite DESC, updatedAt DESC")
    fun getRecordsByCategory(category: String): Flow<List<VaultRecord>>

    @Query("SELECT * FROM vault_records WHERE isFavorite = 1 ORDER BY updatedAt DESC")
    fun getFavoriteRecords(): Flow<List<VaultRecord>>

    @Query("SELECT * FROM vault_records WHERE title LIKE '%' || :query || '%' OR secondaryValue LIKE '%' || :query || '%' OR notes LIKE '%' || :query || '%' ORDER BY updatedAt DESC")
    fun searchRecords(query: String): Flow<List<VaultRecord>>

    @Query("SELECT * FROM vault_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): VaultRecord?

    @Query("SELECT * FROM vault_records WHERE remoteId = :remoteId LIMIT 1")
    suspend fun getRecordByRemoteId(remoteId: Long): VaultRecord?

    @Query("DELETE FROM vault_records WHERE remoteId = :remoteId")
    suspend fun deleteByRemoteId(remoteId: Long)

    @Query("SELECT COUNT(*) FROM vault_records")
    fun getRecordCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vault_records WHERE category = :category")
    fun getCountForCategory(category: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: VaultRecord): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<VaultRecord>)

    @Update
    suspend fun updateRecord(record: VaultRecord)

    @Delete
    suspend fun deleteRecord(record: VaultRecord)

    @Query("DELETE FROM vault_records")
    suspend fun clearAllRecords()

    // Activities
    @Query("SELECT * FROM vault_activities ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentActivities(limit: Int = 30): Flow<List<VaultActivity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: VaultActivity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<VaultActivity>)

    @Query("DELETE FROM vault_activities")
    suspend fun clearActivities()
}
