package com.github.privacydashboard.models.v2.individual

import com.github.privacydashboard.models.v2.PaginationV2
import com.google.gson.annotations.SerializedName

data class IndividualsListResponse(
    @SerializedName("individuals") var individuals: ArrayList<Individual>? = null,
    @SerializedName("pagination") var pagination: PaginationV2? = null

)
