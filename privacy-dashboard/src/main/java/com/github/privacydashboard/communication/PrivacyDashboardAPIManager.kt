package com.github.privacydashboard.communication

import com.github.privacydashboard.BuildConfig
import com.github.privacydashboard.communication.PrivacyDashboardAuthInterceptor.Companion.HEADER_AUTHORIZATION
import com.github.privacydashboard.communication.PrivacyDashboardAuthInterceptor.Companion.HEADER_INDIVIDUAL_ID
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// Each instance keeps its own service, so that a later getApi call with other
// credentials does not change the client of an earlier caller.
class PrivacyDashboardAPIManager private constructor(
    val service: PrivacyDashboardAPIServices?
) {

    companion object {
        private var okClient: OkHttpClient? = null
        private var service: PrivacyDashboardAPIServices? = null
        private var httpClient: OkHttpClient.Builder? = null
        private var apiManager: PrivacyDashboardAPIManager? = null

        // Store the last used config
        private var previousAccessToken: String? = null
        private var previousApiKey: String? = null
        private var previousBaseUrl: String? = null
        private var previousTokenManager: PrivacyDashboardTokenManager? = null

        fun getApi(
            accessToken: String? = null,
            apiKey: String? = null,
            baseUrl: String?
        ): PrivacyDashboardAPIManager? = getApi(accessToken, apiKey, baseUrl, null)

        @Synchronized
        internal fun getApi(
            accessToken: String? = null,
            apiKey: String? = null,
            baseUrl: String?,
            tokenManager: PrivacyDashboardTokenManager?
        ): PrivacyDashboardAPIManager? {
            val refreshApiManager = apiManager == null ||
                    accessToken != previousAccessToken ||
                    apiKey != previousApiKey ||
                    baseUrl != previousBaseUrl ||
                    tokenManager !== previousTokenManager
            if (refreshApiManager) {
                httpClient = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                httpClient?.addInterceptor(
                    PrivacyDashboardAuthInterceptor(tokenManager, accessToken, apiKey)
                )
                // After the auth interceptor, so that the redaction applies to the auth headers.
                createLoggingInterceptor(BuildConfig.DEBUG)?.let { httpClient?.addInterceptor(it) }
                okClient = httpClient?.build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                service = retrofit.create(PrivacyDashboardAPIServices::class.java)
                apiManager = PrivacyDashboardAPIManager(service)
                previousAccessToken = accessToken
                previousApiKey = apiKey
                previousBaseUrl = baseUrl
                previousTokenManager = tokenManager
            }
            return apiManager
        }

        /**
         * Returns the HTTP logging interceptor, or null when [debug] is false.
         * The level is BASIC (no headers, no bodies), and the credential
         * headers are redacted if a later change sets a higher level.
         */
        internal fun createLoggingInterceptor(
            debug: Boolean,
            logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger.DEFAULT
        ): HttpLoggingInterceptor? {
            if (!debug) return null
            val httpLoggingInterceptor = HttpLoggingInterceptor(logger)
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC)
            httpLoggingInterceptor.redactHeader(HEADER_AUTHORIZATION)
            httpLoggingInterceptor.redactHeader(HEADER_INDIVIDUAL_ID)
            return httpLoggingInterceptor
        }

        @Synchronized
        fun resetApi() {
            apiManager = null
            service = null
            okClient = null
            httpClient = null
            previousAccessToken = null
            previousApiKey = null
            previousBaseUrl = null
            previousTokenManager = null
        }
    }
}
