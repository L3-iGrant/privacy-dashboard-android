package io.igrant.igrant_org_sdk.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.BuildConfig;
import io.igrant.igrant_org_sdk.OrganizationDetailActivity;
import io.igrant.igrant_org_sdk.models.anonymous.AnonymousUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IgrantSdk {

    private static final String EXTRA_PREFIX = BuildConfig.APPLICATION_ID;

    public static final String EXTRA_USER_ID = EXTRA_PREFIX + ".UserId";
    public static final String EXTRA_API_KEY = EXTRA_PREFIX + ".ApiKey";
    public static final String EXTRA_ORG_ID = EXTRA_PREFIX + ".OrgId";
    private static final int REQUEST_IGRANT_SDK = 101;

    private Intent mIgrantSdkIntent;
    private Bundle mIgrantSdkOptionsBundle;


    public IgrantSdk() {
        mIgrantSdkIntent = new Intent();
        mIgrantSdkOptionsBundle = new Bundle();
    }

    /**
     * Set user id for igrant sdk.
     *
     * @param userId
     */
    public IgrantSdk withUserId(String userId) {
        mIgrantSdkOptionsBundle.putString(EXTRA_USER_ID, userId);
        return this;
    }

    /**
     * Set Api key for the iGrant Sdk.
     */
    public IgrantSdk withApiKey(String apiKey) {
        mIgrantSdkOptionsBundle.putString(EXTRA_API_KEY, apiKey);
        return this;
    }

    /**
     * Set Api key for the iGrant Sdk.
     */
    public IgrantSdk withOrgId(String orgId) {
        mIgrantSdkOptionsBundle.putString(EXTRA_ORG_ID, orgId);
        return this;
    }

    /**
     * Send the Intent from an Activity
     *
     * @param activity Activity to receive result
     */
    public void start(@NonNull Activity activity) {
        start(activity, REQUEST_IGRANT_SDK);
    }

    /**
     * Send the Intent from an Activity with a custom request code
     *
     * @param activity    Activity to receive result
     * @param requestCode requestCode for result
     */
    public void start(@NonNull Activity activity, int requestCode) {
        activity.startActivityForResult(getIntent(activity), requestCode);
    }

    /**
     * Send the Intent from a Fragment
     *
     * @param fragment Fragment to receive result
     */
    public void start(@NonNull Context context, @NonNull Fragment fragment) {
        start(context, fragment, REQUEST_IGRANT_SDK);
    }

    /**
     * Send the Intent with a custom request code
     *
     * @param fragment    Fragment to receive result
     * @param requestCode requestCode for result
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void start(@NonNull Context context, @NonNull Fragment fragment, int requestCode) {
        fragment.startActivityForResult(getIntent(context), requestCode);
    }

    /**
     * Get Intent to start {@link OrganizationDetailActivity}
     *
     * @return Intent for {@link OrganizationDetailActivity}
     */
    public Intent getIntent(@NonNull Context context) {
        mIgrantSdkIntent.setClass(context, OrganizationDetailActivity.class);
        mIgrantSdkIntent.putExtras(mIgrantSdkOptionsBundle);
        return mIgrantSdkIntent;
    }

    public static void createIGrantUser(String orgID, String apiKey, final IgrantUserResponseHandler handler) {
        Callback<AnonymousUser> callback = new Callback<AnonymousUser>() {
            @Override
            public void onResponse(Call<AnonymousUser> call, Response<AnonymousUser> response) {
                try {
                    if (response.body() != null) {
                        handler.onSuccess(response.body());
                    } else {
                        handler.onCreationFailed();
                    }
                } catch (Exception e) {
                    handler.onCreationFailed();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<AnonymousUser> call, Throwable t) {
                handler.onCreationFailed();
            }
        };
        ApiManager.getApi(apiKey).getService().createIgrantUser(orgID).enqueue(callback);
    }
}
