package com.github.privacydashboard.models.v2.individual

import com.google.gson.annotations.SerializedName

data class Individual(
    @SerializedName("id") var id: String? = null,
    @SerializedName("externalId") var externalId: String? = null,
    @SerializedName("externalIdType") var externalIdType: String? = null,
    @SerializedName("identityProviderId") var identityProviderId: String? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("iamId") var iamId: String? = null,
    @SerializedName("email") var email: String? = null,
    @SerializedName("phone") var phone: String? = null,
    @SerializedName("pushNotificationToken") var pushNotificationToken: String? = null,
    @SerializedName("deviceType") var deviceType: String? = null,
)