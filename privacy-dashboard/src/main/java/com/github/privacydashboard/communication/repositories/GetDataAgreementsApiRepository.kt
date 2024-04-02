package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.Count
import com.github.privacydashboard.models.OrganizationDetailResponse
import com.github.privacydashboard.models.PurposeConsent
import com.github.privacydashboard.models.PurposeV1
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementsResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsV2
import retrofit2.Response

class GetDataAgreementsApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun getOrganizationDetail(
        userId: String?,
    ): Result<OrganizationDetailResponse?> {
        return try {
            //v2
            val dataAgreementsResponse = apiService.getDataAgreementsV2(userId, 0, 10000)

            val dataAgreementRecordResponseV2 =
                apiService.getDataAgreementRecordsV2(userId, 0, 10000)

            val org = convertV2toBaseModel(
                dataAgreementsResponse,
                dataAgreementRecordResponseV2,
                userId
            )

            if (dataAgreementsResponse?.isSuccessful == true) {
                val data = dataAgreementsResponse.body()
                if (data != null) {
                    Result.success(org)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${dataAgreementsResponse?.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun convertV2toBaseModel(
        dataAgreementsResponse: Response<DataAgreementsResponseV2>,
        dataAgreementRecordResponseV2: Response<DataAgreementRecordsResponseV2>,
        userId: String?
    ): OrganizationDetailResponse {
        return OrganizationDetailResponse(
            organization = null, consentID = null,
            purposeConsents = convertV2ListToBaseModel(
                dataAgreementsResponse.body()?.dataAgreements,
                dataAgreementRecordResponseV2.body()?.dataAgreementRecords, userId
            )
        )
    }

    private suspend fun convertV2ListToBaseModel(
        consents: ArrayList<DataAgreementV2>?,
        dataAgreementRecords: ArrayList<DataAgreementRecordsV2>?,
        userId: String?
    ): ArrayList<PurposeConsent> {
        var purposeConsents = consents?.map {

            var dataAgreementRecordsV2: DataAgreementRecordsV2? = null
            try {
                dataAgreementRecordsV2 =
                    dataAgreementRecords?.last { dataAgreementRecordsV2 -> dataAgreementRecordsV2.dataAgreementId == it.id }
            } catch (e: Exception) {
                if (!(it.lawfulBasis == "consent" || it.lawfulBasis == "legitimate_interest")) {
                    val createDataAgreementResponse =
                        apiService.createDataAgreementRecordV2(userId, it.id)
                    dataAgreementRecordsV2 =
                        createDataAgreementResponse?.body()?.dataAgreementRecord
                }
            }

            PurposeConsent(
                purpose = PurposeV1(
                    iD = it.id,
                    name = it.purpose,
                    description = it.purposeDescription,
                    lawfulUsage = !(it.lawfulBasis == "consent" || it.lawfulBasis == "legitimate_interest"),
                    policyURL = it.policy?.url
                ),
                count = Count(
                    total = it.dataAttributes?.size,
                    consented = if (dataAgreementRecordsV2?.optIn == true) it.dataAttributes?.size else 0
                )
            )
        }
        return ArrayList(purposeConsents)
    }
}