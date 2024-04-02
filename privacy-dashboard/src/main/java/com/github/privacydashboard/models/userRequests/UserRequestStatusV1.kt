package com.github.privacydashboard.models.userRequests

import com.github.privacydashboard.models.interfaces.userRequests.UserRequestStatus
import com.google.gson.annotations.SerializedName

data class UserRequestStatusV1(
    @SerializedName("RequestOngoing")
    var requestOngoing: Boolean? = null,
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("State")
    var state: Int? = 0,
    @SerializedName("StateStr")
    var stateStr: String? = null,
    @SerializedName("RequestedDate")
    var requestedDate: String? = null
): UserRequestStatus {
    override val mRequestOngoing: Boolean?
        get() = requestOngoing
    override val mId: String?
        get() = iD
    override val mState: Int?
        get() = state
    override val mStateStr: String?
        get() = stateStr
    override val mRequestedDate: String?
        get() = requestedDate

}
