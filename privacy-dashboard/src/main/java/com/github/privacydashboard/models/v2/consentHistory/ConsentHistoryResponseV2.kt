package com.github.privacydashboard.models.v2.consentHistory

import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistory
import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistoryResponse
import com.github.privacydashboard.models.v2.PaginationV2
import com.google.gson.annotations.SerializedName

class ConsentHistoryResponseV2(
    @SerializedName("consentRecordHistory")
    var dataAgreementRecordHistory: ArrayList<ConsentHistoryV2?>? = arrayListOf(),
    @SerializedName("pagination")
    var pagination: PaginationV2? = PaginationV2()
) : ConsentHistoryResponse {
    override val mConsentHistory: ArrayList<ConsentHistory?>?
        get() = convertToList(dataAgreementRecordHistory)

    private fun convertToList(consents: ArrayList<ConsentHistoryV2?>?): ArrayList<ConsentHistory?>? {
        return consents?.filterNotNull()?.toCollection(ArrayList())
    }

}