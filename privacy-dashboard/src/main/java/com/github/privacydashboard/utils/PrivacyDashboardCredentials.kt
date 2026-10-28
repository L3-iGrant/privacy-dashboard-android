package com.github.privacydashboard.utils

import android.content.Context
import android.preference.PreferenceManager
import android.util.Log
import com.github.privacydashboard.PrivacyDashboardTokenProvider
import com.github.privacydashboard.communication.PrivacyDashboardTokenManager
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Keeps the credentials of the SDK in memory only: the API key, the user id,
 * the access token and the token provider. The SDK does not write them to
 * storage, so they are not in backups and not on the device after the process
 * stops.
 */
internal object PrivacyDashboardCredentials {

    private const val TAG = "PrivacyDashboard"

    // Older SDK versions wrote these keys to the default SharedPreferences.
    internal val CREDENTIAL_TAGS = setOf(
        PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
        PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
        PrivacyDashboardDataUtils.EXTRA_TAG_USERID
    )

    private val values = ConcurrentHashMap<String, String>()

    private val apiKeyWarningLogged = AtomicBoolean(false)

    /** The token manager of the privacy dashboard UI, or null without a token provider. */
    @Volatile
    var tokenManager: PrivacyDashboardTokenManager? = null
        private set

    // The token manager of the functions that are not part of the UI.
    private var functionTokenManager: PrivacyDashboardTokenManager? = null

    fun isCredential(tag: String?): Boolean = tag != null && tag in CREDENTIAL_TAGS

    fun put(tag: String, value: String?) {
        if (value == null) values.remove(tag) else values[tag] = value
    }

    fun get(tag: String): String? = values[tag]

    /**
     * Sets the token provider of the UI. A new provider always gets a new
     * token manager, so that the SDK asks for a token when it starts.
     */
    @Synchronized
    fun startTokenProvider(provider: PrivacyDashboardTokenProvider?) {
        tokenManager?.clear()
        tokenManager = provider?.let { PrivacyDashboardTokenManager(it) }
        tokenManager?.prefetch()
    }

    /**
     * Returns the token manager for a function that is not part of the UI.
     * The same provider object gets the same token manager, so that the
     * token is used again until it is near its expiry.
     */
    @Synchronized
    fun tokenManagerFor(provider: PrivacyDashboardTokenProvider): PrivacyDashboardTokenManager {
        val current = functionTokenManager
        if (current != null && current.provider === provider) return current
        current?.clear()
        return PrivacyDashboardTokenManager(provider).also { functionTokenManager = it }
    }

    /** Deletes all credentials and the tokens from memory and from old storage. */
    @Synchronized
    fun clear(context: Context?) {
        values.clear()
        tokenManager?.clear()
        tokenManager = null
        functionTokenManager?.clear()
        functionTokenManager = null
        if (context != null) deleteLegacyValues(context)
    }

    /** Deletes the credentials that older SDK versions wrote to SharedPreferences. */
    fun deleteLegacyValues(context: Context) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        if (CREDENTIAL_TAGS.none { sharedPreferences.contains(it) }) return
        val editor = sharedPreferences.edit()
        CREDENTIAL_TAGS.forEach { editor.remove(it) }
        editor.apply()
    }

    /**
     * Returns true if the UI has a credential to call the API: a token
     * provider, an access token or an API key.
     */
    fun hasUsableCredentials(): Boolean =
        tokenManager != null ||
                !values[PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN].isNullOrEmpty() ||
                !values[PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN].isNullOrEmpty()

    /**
     * Returns true if an SDK screen must close at once. The credentials are in
     * memory only. After Android stops the process and restores an SDK screen,
     * there are no credentials, so the screen closes and the user goes back to
     * the host app, which can start the SDK again.
     */
    fun shouldCloseScreen(
        needsCredentials: Boolean,
        hasUsableCredentials: Boolean = hasUsableCredentials()
    ): Boolean = needsCredentials && !hasUsableCredentials

    /** Logs one warning for each process when the app uses the API key path. */
    fun warnApiKeyDeprecated() {
        if (apiKeyWarningLogged.compareAndSet(false, true)) {
            Log.w(
                TAG,
                "withApiKey and withUserId are deprecated. Do not put an API key into an app. " +
                        "Use withTokenProvider with a token from your backend."
            )
        }
    }
}
