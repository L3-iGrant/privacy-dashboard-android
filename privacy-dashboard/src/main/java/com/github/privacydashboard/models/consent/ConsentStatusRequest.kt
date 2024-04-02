package com.github.privacydashboard.models.consent

import com.google.gson.annotations.SerializedName

data class ConsentStatusRequest(
    @SerializedName("Consented")
    var consented: String? = null,
    @SerializedName("Days")
    var days: Int? = 0
) {

}
