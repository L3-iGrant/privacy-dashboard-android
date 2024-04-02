package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementResponseV2

class GetDataAgreementApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun getDataAgreement(
        userId: String?,
        dataAgreementId: String?
    ): Result<DataAgreementResponseV2?> {
        return try {
            val dataAgreementRecord = apiService.getDataAgreementV2(userId, dataAgreementId)

            if (dataAgreementRecord?.isSuccessful == true) {
                val data = dataAgreementRecord.body()
                if (data != null) {
                    Result.success(data)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${dataAgreementRecord?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}