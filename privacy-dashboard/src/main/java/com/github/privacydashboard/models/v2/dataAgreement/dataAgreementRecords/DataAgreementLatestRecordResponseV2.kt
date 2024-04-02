package com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords

import com.google.gson.annotations.SerializedName

data class DataAgreementLatestRecordResponseV2(
    @SerializedName("consentRecord")
    var dataAgreementRecord: DataAgreementRecordsV2? = null,
)
