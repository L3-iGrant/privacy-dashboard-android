package com.github.privacydashboard

/**
 * Gives the SDK a short-lived individual access token.
 *
 * The host app gets the token from its own backend. The host backend gets the
 * token from `POST /v3/service/individual/token` with a server-only API key.
 * Do not put an API key into the app.
 *
 * The SDK calls [fetchToken] when it starts, when the token is less than
 * 60 seconds from expiry, and one time after a `401` response. The SDK keeps
 * the token in memory only.
 *
 * Call `callback(token, null)` when you have a token. Call
 * `callback(null, error)` when you cannot get a token. You can call the
 * callback on any thread.
 */
fun interface PrivacyDashboardTokenProvider {
    fun fetchToken(callback: (token: String?, error: Throwable?) -> Unit)
}
