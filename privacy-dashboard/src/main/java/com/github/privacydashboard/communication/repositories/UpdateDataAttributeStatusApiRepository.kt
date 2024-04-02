 package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.consent.ConsentStatusRequest
import com.github.privacydashboard.models.interfaces.consent.ResultResponse

class UpdateDataAttributeStatusApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun updateAttributeStatus(
        orgID: String?,
        userId: String?,
        consentId: String?,
        purposeId: String?,
        attributeId: String?,
        body: ConsentStatusRequest?
    ): Result<ResultResponse?> {
        return try {
            val response = apiService.setAttributeStatus(
                orgID = orgID,
                userId = userId,
                consentId = consentId,
                purposeId = purposeId,
                attributeId = attributeId,
                body = body
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