package com.github.privacydashboard.models.v2.consentHistory

import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistory
import com.google.gson.annotations.SerializedName

class ConsentHistoryV2(
    @SerializedName("id") var id: String? = null,
    @SerializedName("organisationId") var organisationId: String? = null,
    @SerializedName("dataAgreementId") var dataAgreementId: String? = null,
    @SerializedName("log") var log: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null
) : ConsentHistory {
    override val mId: String?
        get() = id
    override val mOrgId: String?
        get() = organisationId
    override val mPurposeId: String?
        get() = dataAgreementId
    override val mLog: String?
        get() = log
    override val mTimeStamp: String?
        get() = timestamp

}