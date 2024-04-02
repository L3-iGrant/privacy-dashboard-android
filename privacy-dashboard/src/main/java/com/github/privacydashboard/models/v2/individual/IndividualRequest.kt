package com.github.privacydashboard.models.v2.individual

import com.google.gson.annotations.SerializedName

data class IndividualRequest(
    @SerializedName("individual") var individual: Individual? = null
)
