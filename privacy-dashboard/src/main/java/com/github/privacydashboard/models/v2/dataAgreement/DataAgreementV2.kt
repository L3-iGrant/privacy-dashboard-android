package com.github.privacydashboard.models.v2.dataAgreement

import com.github.privacydashboard.models.v2.dataAgreement.dataAttributes.DataAttributesV2
import com.google.gson.annotations.SerializedName

data class DataAgreementV2(

    @SerializedName("id") var id: String? = null,
    @SerializedName("version") var version: String? = null,
    @SerializedName("controllerId") var controllerId: String? = null,
    @SerializedName("controllerUrl") var controllerUrl: String? = null,
    @SerializedName("controllerName") var controllerName: String? = null,
    @SerializedName("policy") var policy: PolicyV2? = PolicyV2(),
    @SerializedName("purpose") var purpose: String? = null,
    @SerializedName("purposeDescription") var purposeDescription: String? = null,
    @SerializedName("dataAttributes") var dataAttributes:ArrayList<DataAttributesV2?>? =null,
    @SerializedName("lawfulBasis") var lawfulBasis: String? = null,
    @SerializedName("methodOfUse") var methodOfUse: String? = null,
    @SerializedName("dpiaDate") var dpiaDate: String? = null,
    @SerializedName("dpiaSummaryUrl") var dpiaSummaryUrl: String? = null,
    @SerializedName("signature") var signature: SignatureV2? = SignatureV2(),
    @SerializedName("active") var active: Boolean? = null,
    @SerializedName("forgettable") var forgettable: Boolean? = null,
    @SerializedName("compatibleWithVersionId") var compatibleWithVersionId: String? = null,
    @SerializedName("lifecycle") var lifecycle: String? = null

)