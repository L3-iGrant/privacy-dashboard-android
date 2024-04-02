package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.attributes.DataAttributesResponseV1
import com.github.privacydashboard.models.base.Purpose
import com.github.privacydashboard.models.base.attribute.DataAttribute
import com.github.privacydashboard.models.base.attribute.DataAttributes
import com.github.privacydashboard.models.base.attribute.DataAttributesResponse
import com.github.privacydashboard.models.base.attribute.Status
import com.github.privacydashboard.models.v2.dataAgreement.dataAttributes.DataAttributesListResponseV2

class GetConsentsByIdApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun getConsentsById(
        userId: String?,
        dataAgreementId: String?,
        isAllAllowed:Boolean
    ): Result<DataAttributesResponse?> {

        return try {
            val response = apiService.getAttributeListV2(
                userID = userId, dataAgreementId = dataAgreementId
            )
            if (response?.isSuccessful == true) {
                val data = response.body()
                if (data != null) {
                    val processedData = migrateV2ToBaseModel(data,isAllAllowed)
                    Result.success(processedData)
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

    private fun v1ToModel(data: DataAttributesResponseV1): DataAttributesResponse {
        val newList: List<DataAttribute?>? = data.consents?.consents?.map { original ->
            DataAttribute(
                original?.iD,
                original?.description,
                Status(original?.status?.consented, original?.status?.remaining)
            )
        }
        return DataAttributesResponse(
            data.iD, data.consentID, data.orgID,
            DataAttributes(
                purpose = Purpose(
                    data.consents?.purpose?.iD,
                    name = data.consents?.purpose?.name,
                    description = data.consents?.purpose?.description,
                    lawfulUsage = data.consents?.purpose?.lawfulUsage,
                    policyURL = data.consents?.purpose?.policyURL
                ),
                consents = ArrayList(newList)
            )
        )
    }

    private fun migrateV2ToBaseModel(data: DataAttributesListResponseV2, isAllAllowed: Boolean): DataAttributesResponse {
        val newList: List<DataAttribute?>? = data.dataAttributes?.map { original ->
            DataAttribute(
                original.id,
                original.name,
                Status(consented = if (isAllAllowed) "Allow" else "Disallow")
            )
        }
        return DataAttributesResponse(
            "", data.dataAgreement?.id, "",
            DataAttributes(
                purpose = Purpose(
                    iD = data.dataAgreement?.id,
                    name = data.dataAgreement?.purpose,
                    description = data.dataAgreement?.purposeDescription,
                    lawfulUsage = !(data.dataAgreement?.lawfulBasis == "consent" || data.dataAgreement?.lawfulBasis == "legitimate_interest"),
                    policyURL = data.dataAgreement?.policy?.url
                ),
                consents = ArrayList(newList)
            )
        )
    }
}