package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.interfaces.userRequests.UserRequestStatus

class UserRequestStatusApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun dataDownloadStatusRequest(
        orgId: String?
    ): Result<UserRequestStatus?>? {
        return try {
            val response = apiService.getDataDownloadStatus(
                orgId = orgId
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (response.code() == 200 && data!=null) {
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

    suspend fun deleteDataStatusRequest(
        orgId: String?
    ): Result<UserRequestStatus?>? {
        return try {
            val response = apiService.getDataDeleteStatus(
                orgId = orgId
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (response.code() == 200 && data!=null) {
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