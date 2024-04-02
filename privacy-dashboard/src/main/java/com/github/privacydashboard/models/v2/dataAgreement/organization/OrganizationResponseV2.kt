package com.github.privacydashboard.models.v2.dataAgreement.organization

import com.google.gson.annotations.SerializedName

data class OrganizationResponseV2(
    @SerializedName("organisation" ) var organisation : OrganisationV2? = OrganisationV2()
)