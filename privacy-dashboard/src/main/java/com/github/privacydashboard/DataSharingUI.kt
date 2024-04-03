package com.github.privacydashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.privacydashboard.modules.dataSharing.PrivacyDashboardDataSharingActivity
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ORGANISATIONID
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_USERID
import com.github.privacydashboard.utils.PrivacyDashboardLocaleHelper

object DataSharingUI {

    private var mUserId: String? = null
    private var mApiKey: String? = null
    private var mAccessToken: String? = null
    private var mDataAgreementId: String? = null
    private var mBaseUrl: String? = ""
    private var mLocale: String? = ""

    private var mDataSharingUIIntent: Intent? = null
    private var mOrganisationId: String? = null

    fun showDataSharingUI(): DataSharingUI {
        mDataSharingUIIntent = Intent()
        return this
    }

    /**
     * Set user id for igrant sdk.
     *
     * @param organisationId
     */
    fun withOrganizationId(organisationId: String?): DataSharingUI {
        mOrganisationId = if (organisationId == "") null else organisationId
        return this
    }
    /**
     * Set user id for igrant sdk.
     *
     * @param userId
     */
    fun withUserId(userId: String?): DataSharingUI {
        mUserId = if (userId == "") null else userId
        return this
    }

    /**
     * Set Api key for the iGrant Sdk.
     *
     * @param apiKey
     */
    fun withApiKey(apiKey: String?): DataSharingUI {
        mApiKey = if (apiKey == "") null else apiKey
        return this
    }

    /**
     * Set Access token for the iGrant Sdk.
     *
     * @param accessToken
     */
    fun withAccessToken(accessToken: String?): DataSharingUI {
        mAccessToken = if (accessToken == "") null else accessToken
        return this
    }

    /**
     * Set data agreement id id for the iGrant Sdk.
     *
     * @param dataAgreementId
     */
    fun withDataAgreementId(dataAgreementId: String?): DataSharingUI {
        mDataAgreementId = dataAgreementId
        return this
    }

    /**
     * Set base url for the iGrant Sdk.
     *
     * @param baseUrl
     */
    fun withBaseUrl(baseUrl: String?): DataSharingUI {
        if (baseUrl?.last().toString() == "/")
            mBaseUrl = baseUrl
        else
            mBaseUrl = "$baseUrl/"
        return this
    }

    /**
     * Set other application details for the iGrant Sdk.
     *
     * @param applicationName
     * @param logoUrl
     */
    fun withThirdPartyApplication(applicationName: String?, logoUrl: String?): DataSharingUI {
        mDataSharingUIIntent?.putExtra(
            PrivacyDashboardDataSharingActivity.TAG_EXTRA_OTHER_APPLICATION_NAME,
            applicationName
        )
        mDataSharingUIIntent?.putExtra(
            PrivacyDashboardDataSharingActivity.TAG_EXTRA_OTHER_APPLICATION_LOGO,
            logoUrl
        )
        return this
    }

    /**
     * Set secondaryButtonText for the iGrant Sdk.
     *
     * @param text
     */
    fun secondaryButtonText(text: String?): DataSharingUI {
        mDataSharingUIIntent?.putExtra(
            PrivacyDashboardDataSharingActivity.TAG_EXTRA_SECONDARY_BUTTON_TEXT,
            text
        )
        return this
    }

    /**
     * Set Language code for the iGrant Sdk.
     *
     * @param languageCode
     */
    fun withLocale(languageCode: String): DataSharingUI {
        mLocale = languageCode
        return this
    }

    /**
     * Send the Intent from an Activity
     *
     * @param activity Activity to start activity
     */
    fun get(activity: Activity): Intent? {
        if (mAccessToken != null || (mApiKey != null && mUserId != null && mOrganisationId !=null))
            if (mDataAgreementId != null) {
                return getIntent(activity)
            }

        return null
    }

    /**
     * Send the Intent from an Activity with a custom request code
     *
     * @param context Context to start activity
     */
    fun get(context: Context): Intent? {
        if (mAccessToken != null || (mApiKey != null && mUserId != null && mOrganisationId !=null))
            if (mDataAgreementId != null) {
                return getIntent(context)
            }

        return null
    }

    /**
     * Get Intent to start [PrivacyDashboardDashboardActivity]
     *
     * @return Intent for [PrivacyDashboardDashboardActivity]
     */
    private fun getIntent(context: Context): Intent? {
        mDataSharingUIIntent?.setClass(context, PrivacyDashboardDataSharingActivity::class.java)
        PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_BASE_URL, mBaseUrl)
        PrivacyDashboardDataUtils.saveStringValues(
            context,
            EXTRA_TAG_DATA_AGREEMENT_ID,
            mDataAgreementId
        )
        PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_USERID, mUserId)
        PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_ORGANISATIONID, mOrganisationId)
        PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_TOKEN, mApiKey)
        PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_ACCESS_TOKEN, mAccessToken)
        PrivacyDashboardLocaleHelper.setLocale(context, mLocale ?: "en")
        return mDataSharingUIIntent
    }

    fun setLocale(context: Context, languageCode: String) {
        PrivacyDashboardLocaleHelper.setLocale(context, languageCode)
    }
}