package com.github.privacydashboard.models.base.attribute

import com.github.privacydashboard.models.interfaces.dataAttributesList.DataAgreement
import com.google.gson.annotations.SerializedName

class DataAttributesResponse(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("ConsentID")
    var consentID: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("Consents")
    var consents: DataAttributes? = null
) : DataAgreement {
    override val mId: String?
        get() = iD
    override val mConsentId: String?
        get() = consentID
    override val mOrgId: String?
        get() = orgID
    override val mConsents: DataAttributes?
        get() = consents

}
