package com.example.data.supabase

import com.example.data.model.VaultRecord
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

object SupabaseItemMapper {

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val mapAdapter = moshi.adapter(Map::class.java)

    fun toVaultRecord(item: SupabaseItem): VaultRecord {
        val parsedTime = parseIsoTimestamp(item.createdAt) ?: System.currentTimeMillis()
        var secret = item.content
        var secondary = ""
        var notes = ""
        var secLevel = "CONFIDENTIAL"
        var isFav = false

        try {
            if (item.content.trim().startsWith("{") && item.content.trim().endsWith("}")) {
                val map = mapAdapter.fromJson(item.content)
                if (map != null) {
                    secret = (map["secretValue"] as? String) ?: (map["secret"] as? String) ?: (map["value"] as? String) ?: item.content
                    secondary = (map["secondaryValue"] as? String) ?: (map["secondary"] as? String) ?: ""
                    notes = (map["notes"] as? String) ?: ""
                    secLevel = (map["securityLevel"] as? String) ?: (map["level"] as? String) ?: "CONFIDENTIAL"
                    isFav = (map["isFavorite"] as? Boolean) ?: (map["favorite"] as? Boolean) ?: false
                }
            }
        } catch (_: Exception) {
            // Keep content as plain text secret
        }

        return VaultRecord(
            id = 0,
            remoteId = item.id,
            userId = item.userId,
            title = item.title,
            category = item.category,
            secretValue = secret,
            secondaryValue = secondary,
            notes = notes,
            securityLevel = secLevel,
            updatedAt = parsedTime,
            isFavorite = isFav
        )
    }

    fun toContentPayload(record: VaultRecord): String {
        val map = mapOf(
            "secretValue" to record.secretValue,
            "secondaryValue" to record.secondaryValue,
            "notes" to record.notes,
            "securityLevel" to record.securityLevel,
            "isFavorite" to record.isFavorite
        )
        return try {
            mapAdapter.toJson(map)
        } catch (_: Exception) {
            record.secretValue
        }
    }

    private fun parseIsoTimestamp(isoString: String?): Long? {
        if (isoString.isNullOrBlank()) return null
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss"
        )
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US)
                sdf.timeZone = TimeZone.getTimeZone("UTC")
                val date = sdf.parse(isoString)
                if (date != null) return date.time
            } catch (_: Exception) {
                // Try next
            }
        }
        return null
    }
}
