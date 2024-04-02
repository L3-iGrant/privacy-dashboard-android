package com.github.privacydashboard.models.v2.dataAgreement

import com.google.gson.annotations.SerializedName

data class SignatureV2(

    @SerializedName("id") var id: String? = null,
    @SerializedName("payload") var payload: String? = null,
    @SerializedName("signature") var signature: String? = null,
    @SerializedName("verificationMethod") var verificationMethod: String? = null,
    @SerializedName("verificationPayload") var verificationPayload: String? = null,
    @SerializedName("verificationPayloadHash") var verificationPayloadHash: String? = null,
    @SerializedName("verificationArtifact") var verificationArtifact: String? = null,
    @SerializedName("verificationSignedBy") var verificationSignedBy: String? = null,
    @SerializedName("verificationSignedAs") var verificationSignedAs: String? = null,
    @SerializedName("verificationJwsHeader") var verificationJwsHeader: String? = null,
    @SerializedName("timestamp") var timestamp: String? = null,
    @SerializedName("signedWithoutObjectReference") var signedWithoutObjectReference: Boolean? = null,
    @SerializedName("objectType") var objectType: String? = null,
    @SerializedName("objectReference") var objectReference: String? = null

)