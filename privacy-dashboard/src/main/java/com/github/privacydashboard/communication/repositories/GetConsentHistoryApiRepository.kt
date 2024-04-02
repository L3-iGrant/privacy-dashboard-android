package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistoryResponse
import com.github.privacydashboard.models.v2.consentHistory.ConsentHistoryResponseV2

class GetConsentHistoryApiRepository(private var apiService: PrivacyDashboardAPIServices) {

    suspend fun getConsentHistory(
        userID: String?,
        offset: Int,
        limit: Int?
    ): Result<ConsentHistoryResponse?>? {

        return try {
            val response = apiService.getConsentHistoryV2(
                userID = userID,
                offset = offset,
                limit = limit ?: 10
            )
            if (response.isSuccessful) {
                val data = response.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.success(ConsentHistoryResponseV2(null))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}