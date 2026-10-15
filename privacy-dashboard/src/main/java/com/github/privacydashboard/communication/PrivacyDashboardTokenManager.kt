package com.github.privacydashboard.communication

import android.util.Log
import com.github.privacydashboard.PrivacyDashboardTokenProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import okio.ByteString.Companion.decodeBase64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

/**
 * Keeps the individual access token from a [PrivacyDashboardTokenProvider] in
 * memory and asks the provider for a new token when it is necessary.
 *
 * Only one request at a time asks the provider. The other requests wait for
 * that token.
 */
internal class PrivacyDashboardTokenManager(
    val provider: PrivacyDashboardTokenProvider,
    private val clock: () -> Long = { System.currentTimeMillis() },
    private val fetchTimeoutMillis: Long = FETCH_TIMEOUT_MILLIS,
) {
    private val lock = Any()

    @Volatile
    private var token: String? = null

    @Volatile
    private var expiresAtMillis: Long? = null

    // clear() increments the generation. A fetch that started before clear()
    // does not keep its token.
    @Volatile
    private var generation = 0

    /**
     * Returns a token that is not near its expiry. Asks the provider if there
     * is no token or if the token expires in less than 60 seconds. Returns
     * null if the provider gives no token.
     *
     * This call can block. Do not call it on the main thread.
     */
    fun token(): String? = synchronized(lock) {
        val current = token
        if (current != null && !expiresSoon()) current else fetchLocked()
    }

    /**
     * Returns a new token after the server refused [rejectedToken] with 401.
     * If an other request already replaced [rejectedToken], returns that
     * token and does not ask the provider again.
     */
    fun tokenAfterUnauthorized(rejectedToken: String): String? = synchronized(lock) {
        val current = token
        if (current != null && current != rejectedToken && !expiresSoon()) current
        else fetchLocked()
    }

    /**
     * Starts a token request on a background thread, so that the token is
     * ready for the first API request.
     */
    fun prefetch() {
        Thread({ token() }, "privacy-dashboard-token").start()
    }

    /** Deletes the token from memory. */
    fun clear() {
        generation++
        token = null
        expiresAtMillis = null
    }

    private fun expiresSoon(): Boolean {
        val expiresAt = expiresAtMillis ?: return false
        return clock() >= expiresAt - EXPIRY_MARGIN_MILLIS
    }

    private fun fetchLocked(): String? {
        val startGeneration = generation
        token = null
        expiresAtMillis = null

        val result = AtomicReference<String?>(null)
        val done = CountDownLatch(1)
        try {
            provider.fetchToken { newToken, error ->
                if (done.count > 0L) {
                    if (error == null && !newToken.isNullOrBlank()) result.set(newToken)
                    else Log.w(TAG, "The token provider gave no token: ${error?.javaClass?.simpleName ?: "empty token"}")
                    done.countDown()
                }
            }
            if (!done.await(fetchTimeoutMillis, TimeUnit.MILLISECONDS)) {
                Log.w(TAG, "The token provider did not answer in time")
                return null
            }
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            return null
        } catch (e: RuntimeException) {
            Log.w(TAG, "The token provider failed: ${e.javaClass.simpleName}")
            return null
        }

        val newToken = result.get() ?: return null
        if (generation != startGeneration) return null
        token = newToken
        expiresAtMillis = jwtExpiresAtMillis(newToken)
        return newToken
    }

    companion object {
        private const val TAG = "PrivacyDashboard"
        const val EXPIRY_MARGIN_MILLIS = 60_000L
        private const val FETCH_TIMEOUT_MILLIS = 30_000L

        /**
         * Reads `exp` from the JWT payload, in milliseconds. The SDK does not
         * verify the token: it uses `exp` only to know when to ask for a new
         * token. Returns null if the token is not a JWT with a numeric `exp`.
         */
        fun jwtExpiresAtMillis(jwt: String): Long? {
            val parts = jwt.split('.')
            if (parts.size != 3) return null
            return try {
                val payload = parts[1].decodeBase64()?.utf8() ?: return null
                val exp = Gson().fromJson(payload, JsonObject::class.java)?.get("exp") ?: return null
                if (!exp.isJsonPrimitive || !exp.asJsonPrimitive.isNumber) return null
                exp.asLong * 1000L
            } catch (e: RuntimeException) {
                null
            }
        }
    }
}
