package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.VaultActivity
import com.example.data.model.VaultRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [VaultRecord::class, VaultActivity::class], version = 2, exportSchema = false)
abstract class VaultDatabase : RoomDatabase() {

    abstract fun vaultDao(): VaultDao

    companion object {
        @Volatile
        private var INSTANCE: VaultDatabase? = null

        fun getDatabase(context: Context): VaultDatabase {
            return INSTANCE ?: synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        VaultDatabase::class.java,
                        "bank_vault_db"
                    )
                        .fallbackToDestructiveMigration(false)
                        .addCallback(object : Callback() {
                            override fun onCreate(db: SupportSQLiteDatabase) {
                                super.onCreate(db)
                                CoroutineScope(Dispatchers.IO).launch {
                                    INSTANCE?.vaultDao()?.let { populateInitialData(it) }
                                }
                            }
                        })
                        .build()
                    INSTANCE = instance
                }
                instance
            }
        }

        suspend fun populateInitialData(dao: VaultDao) {
            val now = System.currentTimeMillis()
            val initialRecords = listOf(
                VaultRecord(
                    title = "Primary Private Banking - Zurich",
                    category = "Financial",
                    secretValue = "CH93 0076 2011 6238 5291 0",
                    secondaryValue = "UBS-SWISS-CH-ZH8",
                    notes = "Numbered private banking vault account. Requires RSA token key #4810 for outbound transfers.",
                    securityLevel = "TOP SECRET",
                    updatedAt = now - 1000 * 60 * 45,
                    isFavorite = true
                ),
                VaultRecord(
                    title = "Corporate Liquid Treasury",
                    category = "Financial",
                    secretValue = "GB29 NWBK 6016 1331 9268 19",
                    secondaryValue = "NatWest Premier Private",
                    notes = "Authorized signatory limit $500,000. Dual-approval enabled.",
                    securityLevel = "CONFIDENTIAL",
                    updatedAt = now - 1000 * 60 * 180,
                    isFavorite = true
                ),
                VaultRecord(
                    title = "Master AWS Infrastructure Key",
                    category = "Credentials",
                    secretValue = "AKIAIOSFODNN7EXAMPLE//wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY",
                    secondaryValue = "iam-root-admin@secure.vault.corp",
                    notes = "Multi-region failover root key. Rotated every 90 days. Hardware MFA attached.",
                    securityLevel = "TOP SECRET",
                    updatedAt = now - 1000 * 60 * 15,
                    isFavorite = true
                ),
                VaultRecord(
                    title = "Proton Encrypted Mail Gateway",
                    category = "Credentials",
                    secretValue = "v4ult-k3y-993!#mZ$99*Lx",
                    secondaryValue = "security-director@protonmail.com",
                    notes = "Zero-access encryption keyphrase stored in secure enclave.",
                    securityLevel = "CONFIDENTIAL",
                    updatedAt = now - 1000 * 60 * 600,
                    isFavorite = false
                ),
                VaultRecord(
                    title = "Diplomatic Passport #D-99418",
                    category = "Identities",
                    secretValue = "P<USASMITH<<JOHN<<<<<<<<<<<<<<<<<<<<<<<<<<<\n9941829014USA8501018M3001018<<<<<<<<<<<<<<06",
                    secondaryValue = "Issuing Authority: Dept of State",
                    notes = "Biometric e-chip registered. Expiration date: OCT 2032.",
                    securityLevel = "TOP SECRET",
                    updatedAt = now - 1000 * 60 * 60 * 24,
                    isFavorite = false
                ),
                VaultRecord(
                    title = "Titanium Centurion Black Card",
                    category = "Payment Cards",
                    secretValue = "3712 884920 10041 | CVV: 8421",
                    secondaryValue = "Exp: 09/31 | J. V. HOLDINGS",
                    notes = "No preset spending limit. Priority concierge direct line: +1-800-525-3355.",
                    securityLevel = "CONFIDENTIAL",
                    updatedAt = now - 1000 * 60 * 360,
                    isFavorite = true
                ),
                VaultRecord(
                    title = "Hardware Cold Storage Ledger - BIP39",
                    category = "Recovery Keys",
                    secretValue = "obsidian crystal bunker fortress shield emerald cipher vault horizon beacon harbor nexus",
                    secondaryValue = "24-Word Master Seed #1",
                    notes = "Stored offline in stainless steel capsule. Never connect to unverified network.",
                    securityLevel = "TOP SECRET",
                    updatedAt = now - 1000 * 60 * 90,
                    isFavorite = true
                ),
                VaultRecord(
                    title = "Offshore Asset Trust Deed - 2026",
                    category = "Documents",
                    secretValue = "DEED-REG-CAYMAN-V9421-B",
                    secondaryValue = "Trustees: Vanguard Global Custody",
                    notes = "Original physical deed held at Vault Chamber 4, Geneva Freeport.",
                    securityLevel = "RESTRICTED",
                    updatedAt = now - 1000 * 60 * 60 * 48,
                    isFavorite = false
                )
            )

            dao.insertRecords(initialRecords)

            val initialActivities = listOf(
                VaultActivity(
                    action = "Record updated",
                    category = "Credentials",
                    details = "Master AWS Infrastructure Key rotated & verified",
                    timestamp = now - 1000 * 60 * 15,
                    status = "VERIFIED"
                ),
                VaultActivity(
                    action = "Record added",
                    category = "Financial",
                    details = "Primary Private Banking - Zurich enrolled into vault",
                    timestamp = now - 1000 * 60 * 45,
                    status = "SECURE"
                ),
                VaultActivity(
                    action = "Security audit",
                    category = "System",
                    details = "Zero-Knowledge Enclave passed cryptographic integrity check",
                    timestamp = now - 1000 * 60 * 95,
                    status = "VERIFIED"
                ),
                VaultActivity(
                    action = "Record added",
                    category = "Payment Cards",
                    details = "Titanium Centurion Black Card registered with tokenized CVV",
                    timestamp = now - 1000 * 60 * 360,
                    status = "SECURE"
                ),
                VaultActivity(
                    action = "Vault locked",
                    category = "Security",
                    details = "Auto-lock triggered after inactivity session timeout",
                    timestamp = now - 1000 * 60 * 1200,
                    status = "WARNING"
                )
            )

            dao.insertActivities(initialActivities)
        }
    }
}
