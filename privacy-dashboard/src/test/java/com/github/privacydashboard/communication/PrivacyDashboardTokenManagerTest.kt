package com.github.privacydashboard.communication

import com.github.privacydashboard.PrivacyDashboardTokenProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PrivacyDashboardTokenManagerTest {

    private var nowMillis = 1_000_000_000_000L

    @Test
    fun readsExpFromJwtPayload() {
        assertEquals(1_700_000_000_000L, PrivacyDashboardTokenManager.jwtExpiresAtMillis(testJwt(1_700_000_000L, "a")))
        assertNull(PrivacyDashboardTokenManager.jwtExpiresAtMillis(testJwt(null, "a")))
        assertNull(PrivacyDashboardTokenManager.jwtExpiresAtMillis("not-a-jwt"))
        assertNull(PrivacyDashboardTokenManager.jwtExpiresAtMillis("a.%%%.c"))
    }

    @Test
    fun keepsTheTokenWhileItIsMoreThan60SecondsFromExpiry() {
        val provider = CountingTokenProvider { nowMillis / 1000 + 600 }
        val manager = PrivacyDashboardTokenManager(provider, clock = { nowMillis })

        val first = manager.token()
        nowMillis += 539_000 // 61 s before expiry
        val second = manager.token()

        assertEquals(first, second)
        assertEquals(1, provider.calls.get())
    }

    @Test
    fun asksForANewTokenWhenTheTokenIsLessThan60SecondsFromExpiry() {
        val provider = CountingTokenProvider { nowMillis / 1000 + 600 }
        val manager = PrivacyDashboardTokenManager(provider, clock = { nowMillis })

        val first = manager.token()
        nowMillis += 541_000 // 59 s before expiry
        val second = manager.token()

        assertNotEquals(first, second)
        assertEquals(2, provider.calls.get())
    }

    @Test
    fun keepsATokenWithoutExpUntilA401() {
        val provider = CountingTokenProvider { null }
        val manager = PrivacyDashboardTokenManager(provider, clock = { nowMillis })

        val first = manager.token()!!
        nowMillis += 3_600_000
        assertEquals(first, manager.token())
        assertEquals(1, provider.calls.get())

        assertNotEquals(first, manager.tokenAfterUnauthorized(first))
        assertEquals(2, provider.calls.get())
    }

    @Test
    fun doesNotAskAgainAfter401WhenAnOtherRequestAlreadyReplacedTheToken() {
        val provider = CountingTokenProvider { null }
        val manager = PrivacyDashboardTokenManager(provider, clock = { nowMillis })

        val rejected = manager.token()!!
        val replaced = manager.tokenAfterUnauthorized(rejected)!!
        // A second request that also had the rejected token gets the new token.
        assertEquals(replaced, manager.tokenAfterUnauthorized(rejected))
        assertEquals(2, provider.calls.get())
    }

    @Test
    fun returnsNullWhenTheProviderGivesAnError() {
        val manager = PrivacyDashboardTokenManager(
            PrivacyDashboardTokenProvider { callback -> callback(null, IllegalStateException("no session")) }
        )
        assertNull(manager.token())
    }

    @Test
    fun returnsNullWhenTheProviderDoesNotAnswerInTime() {
        val manager = PrivacyDashboardTokenManager(
            PrivacyDashboardTokenProvider { },
            fetchTimeoutMillis = 50
        )
        assertNull(manager.token())
    }

    @Test
    fun acceptsACallbackFromAnOtherThread() {
        val manager = PrivacyDashboardTokenManager(
            PrivacyDashboardTokenProvider { callback ->
                Thread { callback(testJwt(null, "async"), null) }.start()
            }
        )
        assertEquals(testJwt(null, "async"), manager.token())
    }

    @Test
    fun clearDeletesTheToken() {
        val provider = CountingTokenProvider { null }
        val manager = PrivacyDashboardTokenManager(provider)

        val first = manager.token()
        manager.clear()

        assertNotEquals(first, manager.token())
        assertEquals(2, provider.calls.get())
    }
}
