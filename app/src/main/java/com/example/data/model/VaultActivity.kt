package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_activities")
data class VaultActivity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String, // "Record Added", "Record Updated", "Record Accessed", "Security Audit", "Vault Locked"
    val category: String, // Associated category or system component
    val details: String, // Brief description of what happened
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "VERIFIED" // "VERIFIED", "SECURE", "WARNING"
)
