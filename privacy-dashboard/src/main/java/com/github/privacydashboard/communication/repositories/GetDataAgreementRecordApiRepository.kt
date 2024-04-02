package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementLatestRecordResponseV2

class GetDataAgreementRecordApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun getDataAgreementRecord(
        userId: String?,
        dataAgreementId: String?
    ): Result<DataAgreementLatestRecordResponseV2?> {
        return try {
            val dataAgreementRecord = apiService.getDataAgreementRecordV2(userId, dataAgreementId)

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