package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_records")
data class VaultRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // "Financial", "Credentials", "Identities", "Documents", "Payment Cards", "Recovery Keys"
    val secretValue: String, // Password, Account number, Private key, Seed phrase, PIN
    val secondaryValue: String = "", // Username, IBAN, Expiration date, Holder name
    val notes: String = "",
    val securityLevel: String = "CONFIDENTIAL", // "RESTRICTED", "CONFIDENTIAL", "TOP SECRET"
    val updatedAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val remoteId: Long? = null,
    val userId: String = ""
)
