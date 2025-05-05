package com.github.privacydashboard.models

import com.github.privacydashboard.models.base.attribute.DataAttributesResponse

data class PrivacyDashboardBottomSheetData(
    val name: String?,
    val description: String?,
    val dataAttributes: DataAttributesResponse?
)