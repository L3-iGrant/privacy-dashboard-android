package com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords

import com.github.privacydashboard.models.v2.PaginationV2
import com.google.gson.annotations.SerializedName

class DataAgreementRecordsResponseV2(
    @SerializedName("consentRecords")
    var dataAgreementRecords: ArrayList<DataAgreementRecordsV2> = arrayListOf(),
    @SerializedName("pagination")
    var pagination: PaginationV2? = PaginationV2()
)