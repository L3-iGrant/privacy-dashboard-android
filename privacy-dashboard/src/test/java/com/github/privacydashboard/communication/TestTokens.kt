package com.github.privacydashboard.communication

import com.github.privacydashboard.PrivacyDashboardTokenProvider
import java.util.Base64
import java.util.concurrent.atomic.AtomicInteger

/** Makes an unsigned test JWT with the given `exp` (seconds) and a unique id. */
internal fun testJwt(expSeconds: Long?, id: String): String {
    val encoder = Base64.getUrlEncoder().withoutPadding()
    val header = encoder.encodeToString("""{"alg":"ES256","typ":"individual+jwt"}""".toByteArray())
    val claims = if (expSeconds != null) """{"sub":"$id","exp":$expSeconds}""" else """{"sub":"$id"}"""
    val payload = encoder.encodeToString(claims.toByteArray())
    return "$header.$payload.c2lnbmF0dXJl"
}

/** A token provider that gives a new token on each call and counts the calls. */
internal class CountingTokenProvider(
    private val expSeconds: () -> Long? = { null },
) : PrivacyDashboardTokenProvider {
    val calls = AtomicInteger(0)

    override fun fetchToken(callback: (token: String?, error: Throwable?) -> Unit) {
        val n = calls.incrementAndGet()
        callback(testJwt(expSeconds(), "token-$n"), null)
    }
}
