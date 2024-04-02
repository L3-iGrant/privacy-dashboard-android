package com.github.privacydashboard.models.v2.dataAgreement.dataAttributes

import com.google.gson.annotations.SerializedName

data class DataAttributesV2(
    @SerializedName("id") var id: String? = null,
    @SerializedName("version") var version: String? = null,
    @SerializedName("agreementIds") var agreementIds: ArrayList<String> = arrayListOf(),
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("sensitivity") var sensitivity: Boolean? = null,
    @SerializedName("category") var category: String? = null
)