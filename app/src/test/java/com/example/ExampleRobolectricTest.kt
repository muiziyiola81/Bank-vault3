package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.VaultCategory
import com.example.data.model.VaultRecord
import com.example.data.supabase.SupabaseConfig
import com.example.data.supabase.SupabaseItem
import com.example.data.supabase.SupabaseItemMapper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bank Vault", appName)
  }

  @Test
  fun `verify vault categories are properly defined`() {
    val categories = VaultCategory.ALL
    assertEquals(6, categories.size)
    val financial = VaultCategory.find("Financial")
    assertEquals("Financial & Banking", financial.name)
    assertNotNull(VaultCategory.find("Credentials"))
  }

  @Test
  fun `verify vault record model`() {
    val record = VaultRecord(
      title = "Swiss Private Account",
      category = "Financial",
      secretValue = "CH93 0076",
      securityLevel = "TOP SECRET"
    )
    assertEquals("Swiss Private Account", record.title)
    assertEquals("TOP SECRET", record.securityLevel)
  }

  @Test
  fun `verify Supabase item mapper converts plain content to record`() {
    val supabaseItem = SupabaseItem(
      id = 101L,
      createdAt = "2026-09-26T12:00:00Z",
      category = "Financial",
      title = "Zurich Private Wire",
      content = "CH93 0076 2011 6238",
      userId = "user-123-abc"
    )
    val record = SupabaseItemMapper.toVaultRecord(supabaseItem)
    assertEquals(101L, record.remoteId)
    assertEquals("Zurich Private Wire", record.title)
    assertEquals("CH93 0076 2011 6238", record.secretValue)
    assertEquals("user-123-abc", record.userId)
  }

  @Test
  fun `verify Supabase item mapper converts structured payload`() {
    val record = VaultRecord(
      title = "Master Key",
      category = "Credentials",
      secretValue = "v4ult-s3cr3t-key!",
      secondaryValue = "root@vault",
      notes = "Strict enclave only",
      securityLevel = "TOP SECRET",
      isFavorite = true
    )
    val payload = SupabaseItemMapper.toContentPayload(record)
    assertTrue(payload.contains("v4ult-s3cr3t-key!"))

    val mappedBack = SupabaseItemMapper.toVaultRecord(
      SupabaseItem(
        id = 202L,
        createdAt = "2026-09-26T14:00:00Z",
        category = "Credentials",
        title = "Master Key",
        content = payload,
        userId = "admin-uuid-456"
      )
    )
    assertEquals("v4ult-s3cr3t-key!", mappedBack.secretValue)
    assertEquals("root@vault", mappedBack.secondaryValue)
    assertEquals("Strict enclave only", mappedBack.notes)
    assertEquals("TOP SECRET", mappedBack.securityLevel)
    assertTrue(mappedBack.isFavorite)
  }

  @Test
  fun `verify Supabase admin check`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val config = SupabaseConfig(context)
    config.customAdminUserId = "admin-777-uuid"
    assertTrue(config.isUserAdmin("admin-777-uuid"))
    assertFalse(config.isUserAdmin("user-999-uuid"))
  }
}
