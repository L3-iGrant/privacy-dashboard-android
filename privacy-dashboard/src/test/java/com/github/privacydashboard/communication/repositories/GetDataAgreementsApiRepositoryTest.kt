package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class GetDataAgreementsApiRepositoryTest {

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

    private fun agreement(id: String, attributes: Int) = """
        {"id":"$id","purpose":"Purpose $id","lawfulBasis":"consent",
         "dataAttributes":[${(1..attributes).joinToString(",") { """{"id":"a$it","name":"A$it"}""" }}]}
    """

    private fun record(agreementId: String, optIn: Boolean) =
        """{"id":"r-$agreementId","dataAgreementId":"$agreementId","optIn":$optIn}"""

    private fun load(agreements: List<String>, records: List<String>) = runBlocking {
        server.enqueue(MockResponse().setBody("""{"dataAgreements":[${agreements.joinToString(",")}]}"""))
        server.enqueue(MockResponse().setBody("""{"consentRecords":[${records.joinToString(",")}]}"""))
        val service = PrivacyDashboardAPIManager.getApi(
            apiKey = "test-api-key",
            baseUrl = server.url("/v2/").toString()
        )!!.service!!
        GetDataAgreementsApiRepository(service).getOrganizationDetail("individual")
            .getOrNull()!!.purposeConsents!!.associateBy { it.purpose?.iD }
    }

    @Test
    fun optInComesFromTheConsentRecordWhenTheAgreementHasNoDataAttributes() {
        val consents = load(
            agreements = listOf(agreement("allowed", 0), agreement("disallowed", 0)),
            records = listOf(record("allowed", true), record("disallowed", false))
        )

        assertEquals(true, consents["allowed"]?.optIn)
        assertEquals(false, consents["disallowed"]?.optIn)
    }

    @Test
    fun optInComesFromTheConsentRecordWhenTheAgreementHasDataAttributes() {
        val consents = load(
            agreements = listOf(agreement("allowed", 2), agreement("disallowed", 2)),
            records = listOf(record("allowed", true), record("disallowed", false))
        )

        assertEquals(true, consents["allowed"]?.optIn)
        assertEquals(false, consents["disallowed"]?.optIn)
    }

    @Test
    fun optInIsNullWithoutAConsentRecord() {
        val consents = load(agreements = listOf(agreement("no-record", 0)), records = emptyList())

        assertNull(consents["no-record"]?.optIn)
        // Only the two list calls: a consent agreement without a record creates nothing.
        assertEquals(2, server.requestCount)
    }
}
