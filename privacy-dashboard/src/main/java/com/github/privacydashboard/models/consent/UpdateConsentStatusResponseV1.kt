package com.github.privacydashboard.models.consent

import com.github.privacydashboard.models.PurposeConsent
import com.github.privacydashboard.models.interfaces.consent.UpdateConsentStatusResponse
import com.google.gson.annotations.SerializedName

class UpdateConsentStatusResponseV1(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("UserID")
    var userID: String? = null,
    @SerializedName("ConsentsAndPurposes")
    var ConsentsAndPurposes: ArrayList<PurposeConsent>? = null
): UpdateConsentStatusResponse {

}
