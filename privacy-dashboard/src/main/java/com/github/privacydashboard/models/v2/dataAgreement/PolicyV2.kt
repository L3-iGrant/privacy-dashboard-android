package com.github.privacydashboard.models.v2.dataAgreement

import com.google.gson.annotations.SerializedName

data class PolicyV2(

    @SerializedName("name") var name: String? = null,
    @SerializedName("version") var version: String? = null,
    @SerializedName("url") var url: String? = null,
    @SerializedName("jurisdiction") var jurisdiction: String? = null,
    @SerializedName("industrySector") var industrySector: String? = null,
    @SerializedName("dataRetentionPeriodDays") var dataRetentionPeriodDays: Int? = null,
    @SerializedName("geographicRestriction") var geographicRestriction: String? = null,
    @SerializedName("storageLocation") var storageLocation: String? = null,
    @SerializedName("thirdPartyDataSharing") var thirdPartyDataSharing: Boolean? = null,
    @SerializedName("id") var id: String? = null

)