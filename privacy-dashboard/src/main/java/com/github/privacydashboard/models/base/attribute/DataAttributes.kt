package com.github.privacydashboard.models.base.attribute

import com.github.privacydashboard.models.base.Purpose
import com.github.privacydashboard.models.interfaces.dataAttributesList.DataAttributes
import com.google.gson.annotations.SerializedName

class DataAttributes(
    @SerializedName("Purpose")
    var purpose: Purpose? = null,
    @SerializedName("Consents")
    var consents: ArrayList<DataAttribute?>? = null
) : DataAttributes {
    override val mPurpose: Purpose?
        get() = purpose

}
