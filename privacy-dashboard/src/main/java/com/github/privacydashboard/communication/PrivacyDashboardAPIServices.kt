package com.github.privacydashboard.communication

import com.github.privacydashboard.models.OrganizationDetailResponse
import com.github.privacydashboard.models.attributes.DataAttributesResponseV1
import com.github.privacydashboard.models.consent.ConsentStatusRequest
import com.github.privacydashboard.models.consent.ResultResponseV1
import com.github.privacydashboard.models.consent.UpdateConsentStatusResponseV1
import com.github.privacydashboard.models.consentHistory.ConsentHistoryResponseV1
import com.github.privacydashboard.models.userRequests.UserRequestGenResponseV1
import com.github.privacydashboard.models.userRequests.UserRequestHistoryResponseV1
import com.github.privacydashboard.models.userRequests.UserRequestStatusV1
import com.github.privacydashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacydashboard.models.v2.consentHistory.ConsentHistoryResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementsResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementLatestRecordResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAttributes.DataAttributesListResponseV2
import com.github.privacydashboard.models.v2.dataAgreement.organization.OrganizationResponseV2
import com.github.privacydashboard.models.v2.individual.IndividualRequest
import retrofit2.Response
import retrofit2.http.*

interface PrivacyDashboardAPIServices {
    @GET("GetUserOrgsAndConsents")
    suspend fun getOrganizationDetail(
        @Query("orgID") orgID: String?
    ): Response<OrganizationDetailResponse?>?

    @GET("service/organisation")
    suspend fun getOrganizationDetailV2(
        @Header("organizationId") organisationId: String?,
    ): Response<OrganizationResponseV2>

    @GET("service/data-agreements")
    suspend fun getDataAgreementsV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): Response<DataAgreementsResponseV2>

    @GET("service/data-agreement/{dataAgreementId}")
    suspend fun getDataAgreementV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Path("dataAgreementId") dataAgreementId: String?
    ): Response<DataAgreementResponseV2?>?

    @GET("service/individual/record/consent-record")
    suspend fun getDataAgreementRecordsV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): Response<DataAgreementRecordsResponseV2>

    @POST("service/individual/record/data-agreement/{dataAgreementId}")
    suspend fun createDataAgreementRecordV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Path("dataAgreementId") dataAgreementId: String?
    ): Response<DataAgreementLatestRecordResponseV2?>?

    @GET("users/{userID}/consenthistory")
    suspend fun getConsentHistory(
        @Path("userID") userID: String?,
        @Query("limit") limit: Int,
        @Query("orgid") orgId: String?,
        @Query("startid") startid: String?
    ): Response<ConsentHistoryResponseV1?>

    @GET("service/individual/record/consent-record/history")
    suspend fun getConsentHistoryV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): Response<ConsentHistoryResponseV2?>

    @GET("organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}")
    suspend fun getConsentList(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?
    ): Response<DataAttributesResponseV1?>?

    @GET("service/data-agreement/{dataAgreementId}/data-attributes")
    suspend fun getAttributeListV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Path("dataAgreementId") dataAgreementId: String?
    ): Response<DataAttributesListResponseV2?>?

    @PATCH("organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/attributes/{attributeId}")
    suspend fun setAttributeStatus(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?,
        @Path("attributeId") attributeId: String?,
        @Body body: ConsentStatusRequest?
    ): Response<ResultResponseV1?>?

    @POST("organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/status")
    suspend fun setOverallStatus(
        @Path("orgID") orgID: String?,
        @Path("userId") userId: String?,
        @Path("consentId") consentId: String?,
        @Path("purposeId") purposeId: String?,
        @Body body: ConsentStatusRequest?
    ): Response<UpdateConsentStatusResponseV1?>?

    @GET("service/individual/record/data-agreement/{dataAgreementId}")
    suspend fun getDataAgreementRecordV2(
        @Header("X-ConsentBB-IndividualId") userID: String?,
        @Path("dataAgreementId") dataAgreementId: String?,
    ): Response<DataAgreementLatestRecordResponseV2?>?

    @PUT("service/individual/record/consent-record/{dataAgreementRecordId}")
    suspend fun setOverallStatusV2(
        @Header("X-ConsentBB-IndividualId") userID: String? = null,
        @Path("dataAgreementRecordId") dataAgreementRecordId: String?,
        @Query("dataAgreementId") dataAgreementId: String?,
        @Body body: ConsentStatusRequestV2?
    ): Response<DataAgreementLatestRecordResponseV2?>?

    @GET("user/organizations/{orgId}/data-status")
    suspend fun getOrgRequestStatus(
        @Path("orgId") orgId: String?,
        @Query("startid") startid: String?
    ): Response<UserRequestHistoryResponseV1>

    //data download and delete
    @GET("user/organizations/{orgId}/data-download/status")
    suspend fun getDataDownloadStatus(
        @Path("orgId") orgId: String?
    ): Response<UserRequestStatusV1?>

    @GET("user/organizations/{orgId}/data-delete/status")
    suspend fun getDataDeleteStatus(
        @Path("orgId") orgId: String?
    ): Response<UserRequestStatusV1?>

    @POST("user/organizations/{orgId}/data-delete")
    suspend fun dataDeleteRequest(
        @Path("orgId") orgId: String?
    ): Response<Void>

    @POST("user/organizations/{orgId}/data-download")
    suspend fun dataDownloadRequest(
        @Path("orgId") orgId: String?
    ): Response<Void>

    @POST("user/organizations/{orgId}/data-delete/{requestId}/cancel")
    suspend fun dataDeleteCancelRequest(
        @Path("orgId") orgId: String?,
        @Path("requestId") requestId: String?
    ): Response<UserRequestGenResponseV1?>

    @POST("user/organizations/{orgId}/data-download/{requestId}/cancel")
    suspend fun dataDownloadCancelRequest(
        @Path("orgId") orgId: String?,
        @Path("requestId") requestId: String?
    ): Response<UserRequestGenResponseV1?>?

    @POST("service/individual")
    suspend fun createAnIndividual(
        @Body body: IndividualRequest?
    ): Response<IndividualRequest?>?

    @GET("service/individual/{individualId}")
    suspend fun readAnIndividual(
        @Path("individualId") individualId: String?,
    ): Response<IndividualRequest?>?

    @PUT("service/individual/{individualId}")
    suspend fun updateAnIndividual(
        @Body body: IndividualRequest?,
        @Path("individualId") individualId: String?,
    ): Response<IndividualRequest?>?

    @GET("service/individuals")
    suspend fun getAllIndividual(
        @Query("offset") offset: Int?,
        @Query("limit") limit: Int?,
    ): Response<IndividualRequest?>?

    @DELETE("service/individual/{individualId}")
    suspend fun deleteAnIndividual(
        @Path("individualId") individualId: String?,
    ): Response<IndividualRequest?>?
}