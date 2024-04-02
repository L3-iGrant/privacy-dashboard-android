package com.github.privacydashboard.models.interfaces.userRequests

interface UserRequestStatus {
    val mRequestOngoing: Boolean?
    val mId: String?
    val mState: Int?
    val mStateStr: String?
    val mRequestedDate: String?
}