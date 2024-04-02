package com.github.privacydashboard.communication.repositories

import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.models.Organization
import com.github.privacydashboard.models.OrganizationDetailResponse
import com.github.privacydashboard.models.v2.dataAgreement.organization.OrganizationResponseV2
import retrofit2.Response

class GetOrganisationDetailApiRepository(private val apiService: PrivacyDashboardAPIServices) {

    suspend fun getOrganizationDetail(
        organisationId: String?,
    ): Result<OrganizationDetailResponse?> {
        return try {
            //v2
            val organizationResponse = apiService.getOrganizationDetailV2(organisationId)

            val org = convertV2toBaseModel(
                organizationResponse
            )

            if (organizationResponse.isSuccessful) {
                val data = organizationResponse.body()
                if (data != null) {
                    Result.success(org)
                } else {
                    Result.failure(Exception("Response body is null"))
                }
            } else {
                Result.failure(Exception("Request failed with code: ${organizationResponse.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun convertV2toBaseModel(
        organizationResponse: Response<OrganizationResponseV2>
    ): OrganizationDetailResponse {
        return OrganizationDetailResponse(
            organization = Organization(
                iD = organizationResponse.body()?.organisation?.id,
                name = organizationResponse.body()?.organisation?.name,
                coverImageURL = organizationResponse.body()?.organisation?.coverImageUrl ?: "",
                logoImageURL = organizationResponse.body()?.organisation?.logoImageUrl ?: "",
                type = organizationResponse.body()?.organisation?.sector,
                description = organizationResponse.body()?.organisation?.description,
                location = organizationResponse.body()?.organisation?.location,
                policyURL = organizationResponse.body()?.organisation?.policyUrl
            ), consentID = null
        )
    }
}