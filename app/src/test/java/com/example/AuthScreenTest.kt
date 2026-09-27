package com.example

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import com.example.ui.screens.AuthScreen
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AuthScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAuthScreenControlsAreClickableAndResponsive() {
        var loginCalled = false
        var loggedEmail = ""
        var loggedPass = ""
        var configCalled = false

        composeTestRule.setContent {
            MyApplicationTheme {
                AuthScreen(
                    isLoading = false,
                    errorMessage = null,
                    supabaseUrl = "https://test.supabase.co",
                    onLogin = { email, pass ->
                        loginCalled = true
                        loggedEmail = email
                        loggedPass = pass
                    },
                    onSignUp = { _, _ -> },
                    onResetPassword = {},
                    onConfigureSupabase = {
                        configCalled = true
                    }
                )
            }
        }

        // 1. Verify inputs and tabs are displayed and respond to clicks
        composeTestRule.onNodeWithTag("tab_sign_in").assertIsDisplayed()
        composeTestRule.onNodeWithTag("tab_sign_up").assertIsDisplayed().performClick()

        // 2. Confirm password field must now exist and be displayable in Sign Up mode
        composeTestRule.onNodeWithTag("auth_confirm_password_input").performScrollTo().assertIsDisplayed()

        // 3. Click back to Sign In mode
        composeTestRule.onNodeWithTag("tab_sign_in").performScrollTo().performClick()

        // 4. Input email and password
        composeTestRule.onNodeWithTag("auth_email_input").performScrollTo().performTextInput("test@vault.security")
        composeTestRule.onNodeWithTag("auth_password_input").performScrollTo().performTextInput("MasterKey123!")

        // 5. Submit form
        composeTestRule.onNodeWithTag("auth_submit_button").performScrollTo().performClick()

        assertTrue(loginCalled)
        assertEquals("test@vault.security", loggedEmail)
        assertEquals("MasterKey123!", loggedPass)

        // 6. Test configuration pill click
        composeTestRule.onNodeWithTag("auth_supabase_config_pill").performScrollTo().performClick()
        assertTrue(configCalled)
    }
}
