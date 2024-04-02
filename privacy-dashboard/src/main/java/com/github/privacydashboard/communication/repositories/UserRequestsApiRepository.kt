package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.interfaces.userRequests.UserRequestHistoryResponse

class UserRequestsApiRepository(private var apiService: PrivacyDashboardAPIServices) {

    suspend fun getUserRequests(
        orgId: String?,
        startid: String?
    ): Result<UserRequestHistoryResponse?>? {

        return try {
            val response = apiService.getOrgRequestStatus(
                orgId = orgId,
                startid = startid
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (response.code() ==200 && data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${response?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}