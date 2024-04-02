package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.interfaces.userRequests.UserRequestGenResponse

class CancelDataRequestApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun cancelDataRequest(
        orgId: String?,
        requestId: String?
    ): Result<UserRequestGenResponse?>? {
        return try {
            val response = apiService.dataDownloadCancelRequest(
                orgId = orgId,
                requestId = requestId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
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

    suspend fun cancelDeleteDataRequest(
        orgId: String?,
        requestId: String?
    ): Result<UserRequestGenResponse?>? {
        return try {
            val response = apiService.dataDeleteCancelRequest(
                orgId = orgId,
                requestId = requestId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
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