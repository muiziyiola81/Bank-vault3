package com.example.data.model

data class VaultCategory(
    val id: String,
    val name: String,
    val description: String,
    val iconKey: String,
    val accentHex: Long
) {
    companion object {
        val ALL = listOf(
            VaultCategory(
                id = "Financial",
                name = "Financial & Banking",
                description = "Bank accounts, IBANs, routing numbers & wires",
                iconKey = "account_balance",
                accentHex = 0xFF10B981
            ),
            VaultCategory(
                id = "Credentials",
                name = "Credentials & Logins",
                description = "Master passwords, SSH keys & API credentials",
                iconKey = "key",
                accentHex = 0xFF34D399
            ),
            VaultCategory(
                id = "Identities",
                name = "Identities & Passports",
                description = "National IDs, biometric passports & licenses",
                iconKey = "badge",
                accentHex = 0xFF06B6D4
            ),
            VaultCategory(
                id = "Documents",
                name = "Secure Documents",
                description = "Contracts, deeds, trusts & insurance policies",
                iconKey = "description",
                accentHex = 0xFFD4AF37
            ),
            VaultCategory(
                id = "Payment Cards",
                name = "Payment Cards",
                description = "Titanium debit, corporate credit & CVVs",
                iconKey = "credit_card",
                accentHex = 0xFFA78BFA
            ),
            VaultCategory(
                id = "Recovery Keys",
                name = "Recovery Keys & Crypto",
                description = "Hardware wallet seeds, BIP-39 phrases & 2FA",
                iconKey = "shield",
                accentHex = 0xFFF59E0B
            )
        )

        fun find(id: String): VaultCategory {
            return ALL.find { it.id.equals(id, ignoreCase = true) } ?: ALL[0]
        }
    }
}
