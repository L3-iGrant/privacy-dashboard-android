package com.github.privacydashboard.communication

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

/**
 * Adds the authorisation headers to each API request.
 *
 * The order of precedence is:
 * 1. Token provider: `Authorization: Bearer <individual token>`. The request
 *    has no `X-ConsentBB-IndividualId`, because the server takes the
 *    individual from the token. After a `401`, the interceptor asks for a new
 *    token one time and sends the request again one time.
 * 2. Access token: `Authorization: Bearer <access token>`.
 * 3. API key (deprecated): `Authorization: ApiKey <key>`.
 */
internal class PrivacyDashboardAuthInterceptor(
    private val tokenManager: PrivacyDashboardTokenManager? = null,
    private val accessToken: String? = null,
    private val apiKey: String? = null,
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder: Request.Builder = chain.request().newBuilder()
        builder.header("Accept", "application/json")

        if (tokenManager != null) {
            return proceedWithIndividualToken(chain, builder, tokenManager)
        }

        if (!accessToken.isNullOrEmpty()) {
            builder.header(HEADER_AUTHORIZATION, "Bearer $accessToken")
        } else if (!apiKey.isNullOrEmpty()) {
            builder.header(HEADER_AUTHORIZATION, "ApiKey $apiKey")
        }
        return chain.proceed(builder.build())
    }

    private fun proceedWithIndividualToken(
        chain: Interceptor.Chain,
        builder: Request.Builder,
        tokenManager: PrivacyDashboardTokenManager
    ): Response {
        builder.removeHeader(HEADER_INDIVIDUAL_ID)
        val token = tokenManager.token()
            ?: throw IOException("The token provider gave no individual token")

        val response = chain.proceed(builder.header(HEADER_AUTHORIZATION, "Bearer $token").build())
        if (response.code != 401) return response

        val newToken = tokenManager.tokenAfterUnauthorized(token)
        if (newToken == null || newToken == token) return response

        response.close()
        return chain.proceed(builder.header(HEADER_AUTHORIZATION, "Bearer $newToken").build())
    }

    companion object {
        const val HEADER_AUTHORIZATION = "Authorization"
        const val HEADER_INDIVIDUAL_ID = "X-ConsentBB-IndividualId"
    }
}
