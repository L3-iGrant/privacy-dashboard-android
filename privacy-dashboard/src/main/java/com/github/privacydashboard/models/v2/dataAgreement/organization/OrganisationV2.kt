package com.github.privacydashboard.models.v2.dataAgreement.organization

import com.google.gson.annotations.SerializedName

data class OrganisationV2(

    @SerializedName("id") var id: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("description") var description: String? = null,
    @SerializedName("sector") var sector: String? = null,
    @SerializedName("location") var location: String? = null,
    @SerializedName("policyUrl") var policyUrl: String? = null,
    @SerializedName("coverImageId") var coverImageId: String? = null,
    @SerializedName("coverImageUrl") var coverImageUrl: String? = null,
    @SerializedName("logoImageId") var logoImageId: String? = null,
    @SerializedName("logoImageUrl") var logoImageUrl: String? = null

)