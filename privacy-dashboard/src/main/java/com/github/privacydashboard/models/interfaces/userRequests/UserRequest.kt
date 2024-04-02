package com.github.privacydashboard.models.interfaces.userRequests

interface UserRequest {
    val mId: String?
    val mUserName: String?
    val mUserID: String?
    val mOrgID: String?
    val mType: Int?
    val mTypeStr: String?
    val mState: Int?
    val mRequestedDate: String?
    val mClosedDate: String?
    val mStateStr: String?
    val mComment: String?
    var mIsOngoing: Boolean?
}