package com.github.privacydashboard.communication

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PrivacyDashboardLoggingTest {

    private lateinit var server: MockWebServer
    private val lines = mutableListOf<String>()
    private val logger = object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            lines.add(message)
        }
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun callWithLogging(logging: HttpLoggingInterceptor) {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"name":"personal data"}"""))
        val client = OkHttpClient.Builder()
            .addInterceptor(PrivacyDashboardAuthInterceptor(apiKey = "secret-api-key"))
            .addInterceptor(logging)
            .build()
        val request = Request.Builder()
            .url(server.url("/v2/service/data-agreements"))
            .header("X-ConsentBB-IndividualId", "secret-individual")
            .build()
        client.newCall(request).execute().close()
    }

    @Test
    fun noLoggingInAReleaseBuild() {
        assertNull(PrivacyDashboardAPIManager.createLoggingInterceptor(debug = false))
    }

    @Test
    fun debugLoggingIsBasicWithoutHeadersAndBodies() {
        val logging = PrivacyDashboardAPIManager.createLoggingInterceptor(debug = true, logger)!!
        assertEquals(HttpLoggingInterceptor.Level.BASIC, logging.level)

        callWithLogging(logging)

        val log = lines.joinToString("\n")
        assertTrue(log.contains("/v2/service/data-agreements"))
        assertFalse(log.contains("secret-api-key"))
        assertFalse(log.contains("secret-individual"))
        assertFalse(log.contains("personal data"))
    }

    @Test
    fun credentialHeadersAreRedactedAtAHigherLevel() {
        val logging = PrivacyDashboardAPIManager.createLoggingInterceptor(debug = true, logger)!!
        logging.setLevel(HttpLoggingInterceptor.Level.HEADERS)

        callWithLogging(logging)

        val log = lines.joinToString("\n")
        assertTrue(log.contains("Authorization: ██"))
        assertTrue(log.contains("X-ConsentBB-IndividualId: ██"))
        assertFalse(log.contains("secret-api-key"))
        assertFalse(log.contains("secret-individual"))
    }
}
