package io.igrant.igrant_org_sdk.Api;

import io.igrant.igrant_org_sdk.models.Consent.ConsentListResponse;
import io.igrant.igrant_org_sdk.models.Consent.ConsentStatusRequest;
import io.igrant.igrant_org_sdk.models.Consent.UpdateConsentStatusResponse;
import io.igrant.igrant_org_sdk.models.ConsentHistory.ConsentHistoryResponse;
import io.igrant.igrant_org_sdk.models.Login.LoginRequest;
import io.igrant.igrant_org_sdk.models.Login.LoginResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequest;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestGenResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestHistoryResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestStatus;
import io.igrant.igrant_org_sdk.models.Organizations.OrganizationDetailResponse;
import io.igrant.igrant_org_sdk.models.ResultResponse;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.ArrayList;

public interface APIService {

    @POST("v1.1/users/login")
    Call<LoginResponse> loginService(@Body LoginRequest request);

    @GET("v1/GetUserOrgsAndConsents")
    Call<OrganizationDetailResponse> getOrganizationDetail(@Query("orgID") String orgID);

    @GET("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}")
    Call<ConsentListResponse> getConsentList(@Path("orgID") String orgID,
                                             @Path("userId") String userId,
                                             @Path("consentId") String consentId,
                                             @Path("purposeId") String purposeId);

    @POST("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/status")
    Call<UpdateConsentStatusResponse> setOverallStatus(@Path("orgID") String orgID,
                                                       @Path("userId") String userId,
                                                       @Path("consentId") String consentId,
                                                       @Path("purposeId") String purposeId,
                                                       @Body ConsentStatusRequest body);

    @PATCH("v1/organizations/{orgID}/users/{userId}/consents/{consentId}/purposes/{purposeId}/attributes/{attributeId}")
    Call<ResultResponse> setAttributeStatus(@Path("orgID") String orgID,
                                            @Path("userId") String userId,
                                            @Path("consentId") String consentId,
                                            @Path("purposeId") String purposeId,
                                            @Path("attributeId") String attributeId,
                                            @Body ConsentStatusRequest body);

    //data download and delete
    @GET("v1/user/organizations/{orgId}/data-download/status")
    Call<DataRequestStatus> getDataDownloadStatus(@Path("orgId") String orgId);

    @POST("v1/user/organizations/{orgId}/data-download")
    Call<Void> dataDownloadRequest(@Path("orgId") String orgId);

    @GET("v1/user/organizations/{orgId}/data-download")
    Call<ArrayList<DataRequest>> dataDownloadStatus(@Path("orgId") String orgId);

    @POST("v1/user/organizations/{orgId}/data-download/{requestId}/cancel")
    Call<DataRequestGenResponse> dataDownloadCancelRequest(@Path("orgId") String orgId,
                                                           @Path("requestId") String requestId);

    @GET("v1/user/organizations/{orgId}/data-delete/status")
    Call<DataRequestStatus> getDataDeleteStatus(@Path("orgId") String orgId);

    @POST("v1/user/organizations/{orgId}/data-delete")
    Call<Void> dataDeleteRequest(@Path("orgId") String orgId);

    @POST("v1/user/organizations/{orgId}/data-delete")
    Call<ArrayList<DataRequest>> dataDeleteStatus(@Path("orgId") String orgId);

    @POST("v1/user/organizations/{orgId}/data-delete/{requestId}/cancel")
    Call<DataRequestGenResponse> dataDeleteCancelRequest(@Path("orgId") String orgId,
                                                         @Path("requestId") String requestId);

    @GET("v1/user/organizations/{orgId}/data-status")
    Call<DataRequestHistoryResponse> getOrgRequestStatus(@Path("orgId") String orgId,
                                                         @Query("startid") String startid);

    @GET("v1/user/consenthistory")
    Call<ConsentHistoryResponse> getConsentHistory(@Query("limit") int limit,
                                                   @Query("orgid") String orgId,
                                                   @Query("startid") String startid);
}
