package com.github.privacydashboard.communication

import com.github.privacydashboard.PrivacyDashboardTokenProvider
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import java.io.IOException

class PrivacyDashboardAuthInterceptorTest {

    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
        PrivacyDashboardAPIManager.resetApi()
    }

    private fun call(interceptor: PrivacyDashboardAuthInterceptor): Int {
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val request = Request.Builder()
            .url(server.url("/v2/service/data-agreements"))
            .header("X-ConsentBB-IndividualId", "individual-from-app")
            .build()
        client.newCall(request).execute().use { return it.code }
    }

    @Test
    fun tokenProviderSendsBearerTokenAndNoIndividualIdHeader() {
        server.enqueue(MockResponse().setResponseCode(200))
        val provider = CountingTokenProvider()

        call(PrivacyDashboardAuthInterceptor(tokenManager = PrivacyDashboardTokenManager(provider), apiKey = "unused-key"))

        val recorded = server.takeRequest()
        assertEquals("Bearer ${testJwt(null, "token-1")}", recorded.getHeader("Authorization"))
        assertNull(recorded.getHeader("X-ConsentBB-IndividualId"))
    }

    @Test
    fun apiKeyPathSendsApiKeyAndIndividualIdHeader() {
        server.enqueue(MockResponse().setResponseCode(200))

        call(PrivacyDashboardAuthInterceptor(apiKey = "test-api-key"))

        val recorded = server.takeRequest()
        assertEquals("ApiKey test-api-key", recorded.getHeader("Authorization"))
        assertEquals("individual-from-app", recorded.getHeader("X-ConsentBB-IndividualId"))
    }

    @Test
    fun accessTokenHasPrecedenceOverApiKey() {
        server.enqueue(MockResponse().setResponseCode(200))

        call(PrivacyDashboardAuthInterceptor(accessToken = "test-access-token", apiKey = "test-api-key"))

        assertEquals("Bearer test-access-token", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun after401AsksForANewTokenOnceAndSendsTheRequestAgainOnce() {
        server.enqueue(MockResponse().setResponseCode(401))
        server.enqueue(MockResponse().setResponseCode(200))
        val provider = CountingTokenProvider()

        val code = call(PrivacyDashboardAuthInterceptor(tokenManager = PrivacyDashboardTokenManager(provider)))

        assertEquals(200, code)
        assertEquals(2, server.requestCount)
        assertEquals(2, provider.calls.get())
        assertEquals("Bearer ${testJwt(null, "token-1")}", server.takeRequest().getHeader("Authorization"))
        assertEquals("Bearer ${testJwt(null, "token-2")}", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun aSecond401IsReturnedWithoutALoop() {
        repeat(5) { server.enqueue(MockResponse().setResponseCode(401)) }
        val provider = CountingTokenProvider()

        val code = call(PrivacyDashboardAuthInterceptor(tokenManager = PrivacyDashboardTokenManager(provider)))

        assertEquals(401, code)
        assertEquals(2, server.requestCount)
        assertEquals(2, provider.calls.get())
    }

    @Test
    fun after401WithTheSameTokenFromTheProviderTheRequestIsNotSentAgain() {
        server.enqueue(MockResponse().setResponseCode(401))
        val sameToken = testJwt(null, "same")
        val provider = PrivacyDashboardTokenProvider { callback -> callback(sameToken, null) }

        val code = call(PrivacyDashboardAuthInterceptor(tokenManager = PrivacyDashboardTokenManager(provider)))

        assertEquals(401, code)
        assertEquals(1, server.requestCount)
    }

    @Test
    fun withoutATokenTheRequestFailsAndIsNotSent() {
        val provider = PrivacyDashboardTokenProvider { callback -> callback(null, IOException("offline")) }
        try {
            call(PrivacyDashboardAuthInterceptor(tokenManager = PrivacyDashboardTokenManager(provider)))
            fail("expected IOException")
        } catch (e: IOException) {
            assertTrue(e.message!!.contains("no individual token"))
        }
        assertEquals(0, server.requestCount)
    }

    @Test
    fun retrofitServiceWithTokenProviderSendsNoIndividualId() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"dataAgreements":[]}"""))
        val manager = PrivacyDashboardTokenManager(CountingTokenProvider())
        val service = PrivacyDashboardAPIManager.getApi(
            baseUrl = server.url("/v2/").toString(),
            tokenManager = manager
        )!!.service!!

        service.getDataAgreementsV2("individual-from-app", 0, 10)

        val recorded = server.takeRequest()
        assertEquals("/v2/service/data-agreements?offset=0&limit=10", recorded.path)
        assertEquals("Bearer ${testJwt(null, "token-1")}", recorded.getHeader("Authorization"))
        assertNull(recorded.getHeader("X-ConsentBB-IndividualId"))
    }

    @Test
    fun retrofitServiceWithApiKeyKeepsTheOldHeaders() = runBlocking {
        server.enqueue(MockResponse().setResponseCode(200).setBody("""{"dataAgreements":[]}"""))
        val service = PrivacyDashboardAPIManager.getApi(
            apiKey = "test-api-key",
            baseUrl = server.url("/v2/").toString()
        )!!.service!!

        service.getDataAgreementsV2("individual-from-app", 0, 10)

        val recorded = server.takeRequest()
        assertEquals("ApiKey test-api-key", recorded.getHeader("Authorization"))
        assertEquals("individual-from-app", recorded.getHeader("X-ConsentBB-IndividualId"))
    }
}
