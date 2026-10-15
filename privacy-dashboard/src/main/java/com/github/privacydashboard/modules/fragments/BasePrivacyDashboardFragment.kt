package com.github.privacydashboard.modules.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.github.privacydashboard.R
import com.github.privacydashboard.utils.PrivacyDashboardCredentials
import com.github.privacydashboard.utils.PrivacyDashboardLocaleHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BasePrivacyDashboardFragment : BottomSheetDialogFragment() {

    // This is the context we will use for strings and inflation
    lateinit var localizedContext: Context

    override fun onAttach(context: Context) {
        // 1. Create the localized context wrapper
        val language = PrivacyDashboardLocaleHelper.getLanguage(context)
        val baseLocalizedContext = PrivacyDashboardLocaleHelper.setLocale(context, language)
        // Use 'AppTheme' from  styles.xml to lock in  SDK's colors
        localizedContext = android.view.ContextThemeWrapper(baseLocalizedContext, R.style.AppTheme)

        // 2. Pass the ORIGINAL context to super to keep the Window Token valid
        super.onAttach(context)
    }

    /** False for sheets that show only data from the arguments (no API call). */
    protected open val needsCredentials: Boolean = true

    /**
     * True when this sheet closes at once because there are no credentials,
     * for example when the host activity restores it after Android stopped
     * the process. A subclass must return from onViewCreated when this is
     * true, so that it makes no API request.
     */
    protected var isClosingWithoutCredentials = false
        private set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (PrivacyDashboardCredentials.shouldCloseScreen(needsCredentials)) {
            isClosingWithoutCredentials = true
            dismissAllowingStateLoss()
        }
    }

    override fun onGetLayoutInflater(savedInstanceState: Bundle?): LayoutInflater {
        val inflater = super.onGetLayoutInflater(savedInstanceState)
        // Inflate XML using the localized context
        return inflater.cloneInContext(localizedContext)
    }

    // Helper function for your child fragments to use
    fun getLocalizedString(resId: Int): String {
        return localizedContext.getString(resId)
    }
}