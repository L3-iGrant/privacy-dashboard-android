package com.github.privacydashboard.modules

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.github.privacydashboard.utils.PrivacyDashboardCredentials
import com.github.privacydashboard.utils.PrivacyDashboardLocaleHelper

open class PrivacyDashboardBaseActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        var newBase = newBase
        newBase = PrivacyDashboardLocaleHelper.onAttach(newBase)
        super.attachBaseContext(newBase)
    }

    /** False for screens that show only data from the intent (no API call). */
    protected open val needsCredentials: Boolean = true

    /**
     * True when this screen closes at once because there are no credentials,
     * for example after Android stopped the process. A subclass must return
     * from onCreate when this is true, so that it makes no API request.
     */
    protected var isClosingWithoutCredentials = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials)) {
            isClosingWithoutCredentials = true
            finish()
            return
        }
        checkIfAnimationsSetOrNot(this)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }

    override fun onResume() {
        super.onResume()
        val lang: String = PrivacyDashboardLocaleHelper.getLanguage(this) ?: "en"
        PrivacyDashboardLocaleHelper.setLocale(this, lang)
    }

    private fun checkIfAnimationsSetOrNot(activity: Activity) {
        val entryAnimation = "privacy_dashboard_entry_anim"
        val exitAnimation = "privacy_dashboard_exit_anim"
        try {
            // Try to obtain the style resource ID
            val entryId =
                activity.resources.getIdentifier(entryAnimation, "anim", activity.packageName)
            val exitId =
                activity.resources.getIdentifier(exitAnimation, "anim", activity.packageName)

            if (entryId != 0 && exitId != 0)
                activity.overridePendingTransition(entryId, exitId)
        } catch (e: Resources.NotFoundException) {
            Log.d("milna", "checkIfAnimationsSetOrNot: ")
        }
    }

    fun setupEdgeToEdge(view: View) {
        ViewCompat.setOnApplyWindowInsetsListener(view) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Apply padding to avoid overlap with system bars
            view.updatePadding(
                top = insets.top,
                bottom = insets.bottom
            )

            WindowInsetsCompat.CONSUMED
        }
    }
}