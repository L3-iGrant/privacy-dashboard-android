package com.github.privacydashboard.models.base.attribute

import com.github.privacydashboard.models.interfaces.dataAttributesList.Status
import com.google.gson.annotations.SerializedName

class Status(
    @SerializedName("Consented")
    var consented: String? = null,
    @SerializedName("Remaining")
    var remaining: Int? = 0
) : Status {
    override val mConsented: String?
        get() = consented
    override val mRemaining: Int?
        get() = remaining
}
