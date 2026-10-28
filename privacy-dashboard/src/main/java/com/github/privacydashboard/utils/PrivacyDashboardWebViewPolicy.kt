package com.github.privacydashboard.utils

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.browser.customtabs.CustomTabsIntent
import com.github.privacydashboard.BuildConfig
import java.net.URI

/**
 * Rules for the WebView that shows a policy page.
 *
 * - Links to the host of the policy page, and server redirects, open in the
 *   WebView.
 * - Other http and https links open in a Custom Tab, outside the SDK.
 * - Other schemes do not open.
 * - The WebView loads no http content into an https page.
 * - WebView debugging is on only in a debug build of the SDK.
 */
internal object PrivacyDashboardWebViewPolicy {

    private const val TAG = "PrivacyDashboard"

    @SuppressLint("SetJavaScriptEnabled")
    fun configure(webView: WebView) {
        webView.settings.apply {
            javaScriptEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            allowFileAccess = false
            allowContentAccess = false
        }
        // Do not set it to false: the setting is for all WebViews of the host app.
        if (BuildConfig.DEBUG) WebView.setWebContentsDebuggingEnabled(true)
    }

    /** Returns true if [url] has the same host as [policyUrl] and an http or https scheme. */
    fun opensInWebView(policyUrl: String?, url: String?): Boolean {
        val policyHost = hostOf(policyUrl) ?: return false
        if (!isWebUrl(url)) return false
        return hostOf(url).equals(policyHost, ignoreCase = true)
    }

    fun isWebUrl(url: String?): Boolean {
        if (url == null) return false
        val scheme = try {
            URI(url).scheme
        } catch (e: Exception) {
            null
        }
        return scheme.equals("https", ignoreCase = true) || scheme.equals("http", ignoreCase = true)
    }

    /**
     * Handles a navigation of the WebView. Returns true if the WebView must
     * not load [url]. A server redirect of an allowed page (for example from
     * a short link) loads in the WebView, if it is http or https.
     */
    fun shouldOverrideUrlLoading(
        context: Context,
        policyUrl: String?,
        url: String?,
        isRedirect: Boolean
    ): Boolean {
        if (opensInWebView(policyUrl, url)) return false
        if (isRedirect && isWebUrl(url)) return false
        if (isWebUrl(url)) openInCustomTab(context, url!!)
        return true
    }

    private fun openInCustomTab(context: Context, url: String) {
        try {
            CustomTabsIntent.Builder().build().launchUrl(context, Uri.parse(url))
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "No browser to open the link")
        }
    }

    private fun hostOf(url: String?): String? {
        if (url == null) return null
        return try {
            URI(url).host
        } catch (e: Exception) {
            null
        }
    }
}
