package com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords

import com.google.gson.annotations.SerializedName

data class DataAgreementRecordsV2(

    @SerializedName("id") var id: String? = null,
    @SerializedName("dataAgreementId") var dataAgreementId: String? = null,
    @SerializedName("dataAgreementRevisionId") var dataAgreementRevisionId: String? = null,
    @SerializedName("dataAgreementRevisionHash") var dataAgreementRevisionHash: String? = null,
    @SerializedName("individualId") var individualId: String? = null,
    @SerializedName("optIn") var optIn: Boolean? = null,
    @SerializedName("state") var state: String? = null,
    @SerializedName("signatureId") var signatureId: String? = null

)