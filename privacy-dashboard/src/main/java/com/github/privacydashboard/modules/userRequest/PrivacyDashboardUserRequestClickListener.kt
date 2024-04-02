package com.github.privacydashboard.modules.userRequest

import com.github.privacydashboard.models.interfaces.userRequests.UserRequest

interface PrivacyDashboardUserRequestClickListener {
    fun onRequestClick(request: UserRequest?)
    fun onRequestCancel(request: UserRequest?)
}