package io.igrant.igrant_org_sdk.Api;

import io.igrant.igrant_org_sdk.models.Consent.ConsentListResponse;
import io.igrant.igrant_org_sdk.models.Consent.ConsentStatusRequest;
import io.igrant.igrant_org_sdk.models.Consent.UpdateConsentStatusResponse;
import io.igrant.igrant_org_sdk.models.Login.LoginRequest;
import io.igrant.igrant_org_sdk.models.Login.LoginResponse;
import io.igrant.igrant_org_sdk.models.Organizations.OrganizationDetailResponse;
import io.igrant.igrant_org_sdk.models.ResultResponse;
import retrofit2.Call;
import retrofit2.http.*;

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

}
