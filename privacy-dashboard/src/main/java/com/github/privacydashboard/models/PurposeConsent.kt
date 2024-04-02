package com.github.privacydashboard.models

import com.google.gson.annotations.SerializedName

data class PurposeConsent (
    @SerializedName("Purpose")
    var purpose: PurposeV1? = null,
    @SerializedName("Count")
    var count: Count? = null
){}
