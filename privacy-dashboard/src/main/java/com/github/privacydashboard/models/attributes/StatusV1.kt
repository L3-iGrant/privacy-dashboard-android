package com.github.privacydashboard.models.attributes

import com.github.privacydashboard.models.interfaces.dataAttributesList.Status
import com.google.gson.annotations.SerializedName

class StatusV1(
    @SerializedName("Consented")
    var consented: String? = null,
    @SerializedName("TimeStamp")
    var timeStamp: String? = null,
    @SerializedName("Days")
    var days: Int? = null,
    @SerializedName("Remaining")
    var remaining: Int? = 0
) : Status {
    override val mConsented: String?
        get() = consented

    override val mRemaining: Int?
        get() = remaining

}
