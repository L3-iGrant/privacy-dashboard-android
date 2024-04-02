package com.github.privacydashboard.models.consentHistory

import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistory
import com.google.gson.annotations.SerializedName

class ConsentHistoryV1(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("PurposeID")
    var purposeIDl: String? = null,
    @SerializedName("Log")
    var log: String? = null,
    @SerializedName("TimeStamp")
    var timeStamp: String? = null
): ConsentHistory {
    override val mId: String?
        get() = iD
    override val mOrgId: String?
        get() = orgID
    override val mPurposeId: String?
        get() = purposeIDl
    override val mLog: String?
        get() = log
    override val mTimeStamp: String?
        get() = timeStamp
}

