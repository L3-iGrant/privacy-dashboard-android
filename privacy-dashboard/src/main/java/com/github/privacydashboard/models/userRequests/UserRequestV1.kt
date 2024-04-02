package com.github.privacydashboard.models.userRequests

import com.github.privacydashboard.models.interfaces.userRequests.UserRequest
import com.google.gson.annotations.SerializedName

class UserRequestV1(
    @SerializedName("ID")
    var iD: String? = null,
    @SerializedName("UserID")
    var userID: String? = null,
    @SerializedName("UserName")
    var userName: String? = null,
    @SerializedName("OrgID")
    var orgID: String? = null,
    @SerializedName("Type")
    var type: Int? = null,
    @SerializedName("TypeStr")
    var typeStr: String? = null,
    @SerializedName("State")
    var state: Int? = null,
    @SerializedName("RequestedDate")
    var requestedDate: String? = null,
    @SerializedName("ClosedDate")
    var closedDate: String? = null,
    @SerializedName("StateStr")
    var stateStr: String? = null,
    @SerializedName("Comment")
    var comment: String? = null,
    var isOnGoing: Boolean? = false
) : UserRequest {
    override val mId: String?
        get() = iD
    override val mUserName: String?
        get() = userName
    override val mUserID: String?
        get() = userID
    override val mOrgID: String?
        get() = orgID
    override val mType: Int?
        get() = type
    override val mTypeStr: String?
        get() = typeStr
    override val mState: Int?
        get() = state
    override val mRequestedDate: String?
        get() = requestedDate
    override val mClosedDate: String?
        get() = closedDate
    override val mStateStr: String?
        get() = stateStr
    override val mComment: String?
        get() = comment
    override var mIsOngoing: Boolean?
        get() = isOnGoing
        set(value) {
            mIsOngoing = value
        }

}
