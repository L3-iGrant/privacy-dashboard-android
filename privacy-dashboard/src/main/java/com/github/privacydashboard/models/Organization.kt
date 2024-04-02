package com.github.privacydashboard.models

import com.google.gson.annotations.SerializedName

data class Organization(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("CoverImageURL")
    var coverImageURL: String = "",
    @SerializedName("LogoImageURL")
    var logoImageURL: String = "",
    @SerializedName("TypeID")
    var typeID: String? = null,
    @SerializedName("Description")
    var description: String? = null,
    @SerializedName("Location")
    var location: String? = null,
    @SerializedName("Type")
    var type: String? = null,
    @SerializedName("PolicyURL")
    var policyURL: String? = null
) {}

