package com.github.privacydashboard.modules.home

import com.github.privacydashboard.models.PurposeConsent

interface UsagePurposeClickListener {
    fun onItemClick(consent: PurposeConsent?)
    fun onSetStatus(consent: PurposeConsent?, isChecked: Boolean?)
}