package com.github.privacydashboard.models.interfaces.userRequests

interface UserRequestHistoryResponse {
    val mDataRequests: ArrayList<UserRequest?>?
    val mRequestsOngoing: Boolean?
    val mDataDownloadRequestOngoing: Boolean?
    val mDataDeleteRequestOngoing: Boolean?
}