package com.github.privacydashboard.models

import com.google.gson.annotations.SerializedName

data class PurposeConsent (
    @SerializedName("Purpose")
    var purpose: PurposeV1? = null,
    @SerializedName("Count")
    var count: Count? = null,
    // optIn of the individual's consent record; null when there is no record
    @SerializedName("OptIn")
    var optIn: Boolean? = null
){}
