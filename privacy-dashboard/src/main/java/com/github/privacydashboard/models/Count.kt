package com.github.privacydashboard.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Count(
    @SerializedName("Total")
    var total: Int? = null,
    @SerializedName("Consented")
    var consented: Int? = null
) : Serializable {}
