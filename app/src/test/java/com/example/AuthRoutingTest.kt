package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.supabase.AuthState
import com.example.data.supabase.SupabaseClient
import com.example.data.supabase.SupabaseConfig
import com.example.data.supabase.SupabaseSessionManager
import com.example.data.supabase.SupabaseUser
import com.example.ui.screens.AuthScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthRoutingTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testNormalUserSignupAndLoginRoutesToNormalVault() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = SupabaseConfig(context)
        val client = SupabaseClient(config)
        val sessionManager = SupabaseSessionManager(context, config, client)

        val adminId = "550e8400-e29b-41d4-a716-446655440000"
        config.customAdminUserId = adminId

        // New normal user with unique UUID
        val normalUserId = "normal-user-uuid-98765-abcdef"
        val normalUser = SupabaseUser(id = normalUserId, email = "newkeyholder@test.com")

        // 1. Verify normal user is NOT identified as admin
        assertFalse(config.isUserAdmin(normalUserId))

        // 2. Save session for normal user
        sessionManager.saveSession("test_access_token_123", "test_refresh_token_123", normalUser)

        val state = sessionManager.authState.value
        assertTrue(state is AuthState.Authenticated)
        val authState = state as AuthState.Authenticated
        assertEquals(normalUserId, authState.user.id)
        assertFalse(authState.isAdmin) // Must route to normal vault dashboard
    }

    @Test
    fun testAdminUserLoginRoutesToAdminSystem() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val config = SupabaseConfig(context)
        val client = SupabaseClient(config)
        val sessionManager = SupabaseSessionManager(context, config, client)

        val adminId = "550e8400-e29b-41d4-a716-446655440000"
        config.customAdminUserId = adminId

        val adminUser = SupabaseUser(id = adminId, email = "admin@bankvault.com")

        // 1. Verify admin user matches
        assertTrue(config.isUserAdmin(adminId))

        // 2. Save session for admin user
        sessionManager.saveSession("admin_access_token_456", "admin_refresh_token_456", adminUser)

        val state = sessionManager.authState.value
        assertTrue(state is AuthState.Authenticated)
        val authState = state as AuthState.Authenticated
        assertEquals(adminId, authState.user.id)
        assertTrue(authState.isAdmin) // Must route to Admin System
    }

    @Test
    fun testEmailVerificationPendingStateRendersClearly() {
        var confirmedLoginCalled = false
        var dismissedCalled = false

        composeTestRule.setContent {
            MyApplicationTheme {
                AuthScreen(
                    isLoading = false,
                    errorMessage = null,
                    supabaseUrl = "https://test.supabase.co",
                    verificationPendingEmail = "pending.user@vault.enclave",
                    onLogin = { email, _ ->
                        if (email == "pending.user@vault.enclave") {
                            confirmedLoginCalled = true
                        }
                    },
                    onSignUp = { _, _ -> },
                    onResetPassword = {},
                    onDismissVerificationPending = { dismissedCalled = true },
                    onConfigureSupabase = {}
                )
            }
        }

        // Verify verification pending container is displayed
        composeTestRule.onNodeWithTag("auth_verification_pending_container").assertIsDisplayed()
        composeTestRule.onNodeWithText("Verify Your Email").assertIsDisplayed()
        composeTestRule.onNodeWithText("pending.user@vault.enclave").assertIsDisplayed()

        // Verify "I've Confirmed" button triggers login attempt
        composeTestRule.onNodeWithTag("auth_verify_and_login_button").performClick()
        assertTrue(confirmedLoginCalled)

        // Verify return to sign in
        composeTestRule.onNodeWithText("Back to Sign In").performClick()
        assertTrue(dismissedCalled)
    }
}
