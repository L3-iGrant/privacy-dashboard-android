package com.github.privacydashboard.utils

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivacyDashboardUtilsSecurityTest {

    @After
    fun tearDown() {
        PrivacyDashboardCredentials.clear(null)
    }

    @Test
    fun imageRequestsHaveNoAuthHeaders() {
        // A GlideUrl model can carry headers; a plain String model has only the Glide defaults.
        val url = "https://images.example.org/logo.png"
        assertEquals(url, PrivacyDashboardImageUtils.imageModel(url))
    }

    @Test
    fun credentialsAreKeptInMemoryAndNotInSharedPreferences() {
        // A null context would fail in SharedPreferences: the credentials do not go there.
        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN, "test-api-key")
        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_USERID, "test-user")
        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN, "test-token")

        assertEquals("test-api-key", PrivacyDashboardDataUtils.getStringValue(null, PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN, null))
        assertEquals("test-user", PrivacyDashboardDataUtils.getStringValue(null, PrivacyDashboardDataUtils.EXTRA_TAG_USERID, null))
        assertEquals("test-token", PrivacyDashboardDataUtils.getStringValue(null, PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN, null))
    }

    @Test
    fun clearDeletesCredentialsAndTokenManager() {
        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN, "test-api-key")
        PrivacyDashboardCredentials.startTokenProvider { callback -> callback(null, null) }

        PrivacyDashboardCredentials.clear(null)

        assertNull(PrivacyDashboardDataUtils.getStringValue(null, PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN, null))
        assertNull(PrivacyDashboardCredentials.tokenManager)
    }

    @Test
    fun aScreenRestoredWithoutCredentialsCloses() {
        // A new process (for example after Android stopped it) has no credentials.
        PrivacyDashboardCredentials.clear(null)
        assertFalse(PrivacyDashboardCredentials.hasUsableCredentials())
        assertTrue(PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials = true))
        // Screens that show only intent data stay open.
        assertFalse(PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials = false))
    }

    @Test
    fun aScreenWithCredentialsStaysOpen() {
        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN, "test-api-key")
        assertFalse(PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials = true))
        PrivacyDashboardCredentials.clear(null)

        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN, "test-token")
        assertFalse(PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials = true))
        PrivacyDashboardCredentials.clear(null)

        PrivacyDashboardCredentials.startTokenProvider { callback -> callback(null, null) }
        assertFalse(PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials = true))
    }

    @Test
    fun aUserIdAloneIsNotAUsableCredential() {
        PrivacyDashboardDataUtils.saveStringValues(null, PrivacyDashboardDataUtils.EXTRA_TAG_USERID, "test-user")
        assertTrue(PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials = true))
    }

    @Test
    fun webViewOpensOnlyTheHostOfThePolicyPage() {
        val policy = "https://policy.example.org/privacy"
        assertTrue(PrivacyDashboardWebViewPolicy.opensInWebView(policy, "https://policy.example.org/terms"))
        assertTrue(PrivacyDashboardWebViewPolicy.opensInWebView(policy, "https://POLICY.example.org/x"))
        assertFalse(PrivacyDashboardWebViewPolicy.opensInWebView(policy, "https://other.example.org/"))
        assertFalse(PrivacyDashboardWebViewPolicy.opensInWebView(policy, "https://policy.example.org.evil.test/"))
        assertFalse(PrivacyDashboardWebViewPolicy.opensInWebView(policy, "javascript:alert(1)"))
        assertFalse(PrivacyDashboardWebViewPolicy.opensInWebView(policy, "intent://policy.example.org/#Intent;end"))
        assertFalse(PrivacyDashboardWebViewPolicy.opensInWebView(null, "https://policy.example.org/"))
    }

    @Test
    fun onlyHttpAndHttpsAreWebUrls() {
        assertTrue(PrivacyDashboardWebViewPolicy.isWebUrl("https://example.org"))
        assertTrue(PrivacyDashboardWebViewPolicy.isWebUrl("http://example.org"))
        assertFalse(PrivacyDashboardWebViewPolicy.isWebUrl("file:///data/data/app/x"))
        assertFalse(PrivacyDashboardWebViewPolicy.isWebUrl("mailto:a@example.org"))
        assertFalse(PrivacyDashboardWebViewPolicy.isWebUrl(null))
    }
}
