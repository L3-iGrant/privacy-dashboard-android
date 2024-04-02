package com.github.privacydashboard.communication

import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

class PrivacyDashboardAPIManager private constructor() {
    val service: PrivacyDashboardAPIServices?
        get() = Companion.service

    private class HttpInterceptor(val accessToken: String? = null, val apiKey: String? = null) :
        Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            var request: Request = chain.request()

            //Build new request
            val builder: Request.Builder = request.newBuilder()
            builder.header("Accept", "application/json")
            if (accessToken != null && !accessToken.equals("", ignoreCase = true)) {
                setAccessTokenAuthHeader(builder, accessToken)
            } else if (apiKey != null && apiKey != "") {
                setAuthHeader(builder, apiKey)
            }
            request = builder.build()
            var response: Response =
                chain.proceed(request)
            return response
        }

        private fun setAuthHeader(builder: Request.Builder, token: String?) {
            if (token != null) //Add Auth token to each request if authorized
                builder.header("Authorization", "ApiKey $token")
        }

        private fun setAccessTokenAuthHeader(builder: Request.Builder, token: String?) {
            if (token != null) //Add Auth token to each request if authorized
                builder.header("Authorization", "Bearer $token")
        }
    }

    companion object {
        private var okClient: OkHttpClient? = null
        private var service: PrivacyDashboardAPIServices? = null
        private var httpClient: OkHttpClient.Builder? = null
        private var apiManager: PrivacyDashboardAPIManager? = null
        fun getApi(
            accessToken: String? = null,
            apiKey: String? = null,
            baseUrl: String?
        ): PrivacyDashboardAPIManager? {
            if (apiManager == null) {
                apiManager = PrivacyDashboardAPIManager()
                httpClient = OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                val httpLoggingInterceptor = HttpLoggingInterceptor()
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
                httpClient?.addInterceptor(httpLoggingInterceptor)
                httpClient?.interceptors()?.add(HttpInterceptor(accessToken, apiKey))
                okClient = httpClient?.build()
                val retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(okClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                service = retrofit.create(PrivacyDashboardAPIServices::class.java)
            }
            return apiManager
        }

        fun resetApi() {
            apiManager = null
        }
    }
}
