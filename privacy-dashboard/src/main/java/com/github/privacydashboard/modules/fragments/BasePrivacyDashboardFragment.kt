package com.github.privacydashboard.modules.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import com.github.privacydashboard.utils.PrivacyDashboardLocaleHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

open class BasePrivacyDashboardFragment : BottomSheetDialogFragment() {

    // This is the context we will use for strings and inflation
    lateinit var localizedContext: Context

    override fun onAttach(context: Context) {
        // 1. Create the localized context wrapper
        val language = PrivacyDashboardLocaleHelper.getLanguage(context)
        localizedContext = PrivacyDashboardLocaleHelper.setLocale(context, language)

        // 2. Pass the ORIGINAL context to super to keep the Window Token valid
        super.onAttach(context)
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