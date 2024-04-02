package com.github.privacydashboard.models.v2

import com.google.gson.annotations.SerializedName

class PaginationV2(
    @SerializedName("currentPage") var currentPage: Int? = null,
    @SerializedName("totalItems") var totalItems: Int? = null,
    @SerializedName("totalPages") var totalPages: Int? = null,
    @SerializedName("limit") var limit: Int? = null,
    @SerializedName("hasPrevious") var hasPrevious: Boolean? = null,
    @SerializedName("hasNext") var hasNext: Boolean? = null
)