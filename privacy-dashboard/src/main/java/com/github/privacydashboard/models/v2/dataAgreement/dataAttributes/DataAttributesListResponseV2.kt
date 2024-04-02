package com.github.privacydashboard.models.v2.dataAgreement.dataAttributes

import com.github.privacydashboard.models.v2.PaginationV2
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.google.gson.annotations.SerializedName

data class DataAttributesListResponseV2(
    @SerializedName("dataAgreement") var dataAgreement: DataAgreementV2? = DataAgreementV2(),
    @SerializedName("dataAttributes") var dataAttributes: ArrayList<DataAttributesV2> = arrayListOf(),
    @SerializedName("pagination") var pagination: PaginationV2? = PaginationV2()
)