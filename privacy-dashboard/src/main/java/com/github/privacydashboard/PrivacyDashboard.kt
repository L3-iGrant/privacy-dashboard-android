package com.github.privacydashboard

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.github.privacydashboard.R
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.GetDataAgreementApiRepository
import com.github.privacydashboard.communication.repositories.IndividualApiRepository
import com.github.privacydashboard.communication.repositories.UpdateDataAgreementStatusApiRepository
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacydashboard.modules.dataAgreementPolicy.PrivacyDashboardDataAgreementPolicyActivity
import com.github.privacydashboard.modules.home.PrivacyDashboardActivity
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ENABLE_ASK_ME
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ENABLE_USER_REQUEST
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_ORGANISATIONID
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils.EXTRA_TAG_USERID
import com.github.privacydashboard.utils.PrivacyDashboardLocaleHelper
import com.google.gson.Gson
import kotlin.math.floor


object PrivacyDashboard {

    private var destination: Int? = -1

    private var mUserId: String? = null
    private var mApiKey: String? = null
    private var mAccessToken: String? = null
    private var mBaseUrl: String? = ""
    private var mLocale: String? = ""
    private var mEnableUserRequest: Boolean? = false
    private var mEnableAskMe: Boolean? = false
    private var mEnableAttributeLevelConsent: Boolean? = false
    private var mDataAgreement: String? = null
    private var mIntent: Intent? = null
    private var mOrganisationId: String? = null
    private var consentChangeListener: ConsentChangeListener? = null
    private var dataAgreementIds: ArrayList<String>? = null

    fun showPrivacyDashboard(): PrivacyDashboard {
        destination = 0
        mIntent = Intent()
        return this
    }

    fun showDataAgreementPolicy(): PrivacyDashboard {
        destination = 1
        mIntent = Intent()
        return this
    }

    /**
     * Set data agreement for igrant sdk.
     *
     * @param userId
     */
    fun withDataAgreement(dataAgreement: String?): PrivacyDashboard {
        mDataAgreement = if (dataAgreement == "") null else
            dataAgreement
        return this
    }

    /**
     * Set organisation id for igrant sdk.
     *
     * @param organisationId
     */
    fun withOrganisationId(organisationId: String?): PrivacyDashboard {
        mOrganisationId = if (organisationId == "") null else organisationId
        return this
    }
    /**
     * Set user id for igrant sdk.
     *
     * @param userId
     */
    fun withUserId(userId: String?): PrivacyDashboard {
        mUserId = if (userId == "") null else userId
        return this
    }

    /**
     * Set Api key for the iGrant Sdk.
     *
     * @param apiKey
     */
    fun withApiKey(apiKey: String?): PrivacyDashboard {
        mApiKey = if (apiKey == "") null else apiKey
        return this
    }

    /**
     * Set consent change listener for the iGrant Sdk.
     *
     * @param consentChange
     */
    fun withConsentChangeListener(consentChange: ConsentChangeListener?): PrivacyDashboard {
        consentChangeListener = consentChange
        return this
    }
    fun withDataAgreementIDs(dataAgreementIdList:ArrayList<String>?=null):PrivacyDashboard{
        dataAgreementIds = dataAgreementIdList
        return this
    }

    /**
     * Set Access token for the iGrant Sdk.
     *
     * @param accessToken
     */
    fun withAccessToken(accessToken: String?): PrivacyDashboard {
        mAccessToken = if (accessToken == "") null else accessToken
        return this
    }

    /**
     * Set base url for the iGrant Sdk.
     *
     * @param baseUrl
     */
    fun withBaseUrl(baseUrl: String?): PrivacyDashboard {
        if (baseUrl?.last().toString() == "/")
            mBaseUrl = baseUrl
        else
            mBaseUrl = "$baseUrl/"
        return this
    }

    /**
     * Set Language code for the iGrant Sdk.
     *
     * @param languageCode
     */
    fun withLocale(languageCode: String): PrivacyDashboard {
        mLocale = languageCode
        return this
    }

    /**
     * To enable the user request for the iGrant Sdk.
     */
    fun enableUserRequest(): PrivacyDashboard {
        mEnableUserRequest = true
        return this
    }

    /**
     * To enable the ask me in consent detail screen for the iGrant Sdk.
     */
    fun enableAskMe(): PrivacyDashboard {
        mEnableAskMe = true
        return this
    }

    /**
     * To enable updating attribute level consent in iGrant Sdk.
     */
    fun enableAttributeLevelConsent(): PrivacyDashboard {
        mEnableAttributeLevelConsent = true
        return this
    }

    /**
     * Send the Intent from an Activity
     *
     * @param activity Activity to start activity
     */
    fun start(activity: Activity) {
        if (destination == 0) {
            if (mAccessToken != null || (mApiKey != null && mUserId != null && mOrganisationId !=null))
                activity.startActivity(getIntent(activity))
        } else if (destination == 1) {
            if (mDataAgreement != null)
                activity.startActivity(getIntent(activity))
        }
    }

    /**
     * Send the Intent from an Activity with a custom request code
     *
     * @param context Context to start activity
     */
    fun start(context: Context) {
        if (destination == 0) {
            if (mAccessToken != null || (mApiKey != null && mUserId != null))
                context.startActivity(getIntent(context))
        } else if (destination == 1) {
            if (mDataAgreement != null)
                context.startActivity(getIntent(context))
        }
    }

    /**
     * Get Intent to start [PrivacyDashboardActivity]
     *
     * @return Intent for [PrivacyDashboardActivity]
     */
    private fun getIntent(context: Context): Intent? {
        if (destination == 0) {
            mIntent?.setClass(context, PrivacyDashboardActivity::class.java)
            PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_BASE_URL, mBaseUrl)
            PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_USERID, mUserId)
            PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_ORGANISATIONID, mOrganisationId)
            PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_TOKEN, mApiKey)
            PrivacyDashboardDataUtils.saveStringValues(context, EXTRA_TAG_ACCESS_TOKEN, mAccessToken)
            PrivacyDashboardLocaleHelper.setLocale(context, mLocale ?: "en")
            PrivacyDashboardDataUtils.saveBooleanValues(
                context,
                EXTRA_TAG_ENABLE_USER_REQUEST,
                mEnableUserRequest
            )
            PrivacyDashboardDataUtils.saveBooleanValues(
                context,
                EXTRA_TAG_ENABLE_ASK_ME,
                mEnableAskMe
            )
            PrivacyDashboardDataUtils.saveBooleanValues(
                context,
                EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT,
                mEnableAttributeLevelConsent
            )
            PrivacyDashboardActivity.consentChange = consentChangeListener
            PrivacyDashboardActivity.dataAgreementIds = dataAgreementIds
            return mIntent
        } else {
            if (mDataAgreement != null)
                mIntent?.setClass(context, PrivacyDashboardDataAgreementPolicyActivity::class.java)
            PrivacyDashboardLocaleHelper.setLocale(context, mLocale ?: "en")
            mIntent?.putExtra(
                PrivacyDashboardDataAgreementPolicyActivity.TAG_EXTRA_ATTRIBUTE_LIST,
                mDataAgreement
            )
            return mIntent
        }
    }

    fun setLocale(context: Context, languageCode: String) {
        PrivacyDashboardLocaleHelper.setLocale(context, languageCode)
    }

    suspend fun optInToDataAgreement(
        dataAgreementId: String,
        baseUrl: String,
        accessToken: String? = null,
        apiKey: String? = null,
        userId: String? = null,
    ): String? {
        val body = ConsentStatusRequestV2()
        body.optIn = true

        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            apiKey = apiKey,
            accessToken = accessToken,
            baseUrl = (if (baseUrl.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/")
        )?.service!!

        val updateDataAgreementStatusApiRepository =
            UpdateDataAgreementStatusApiRepository(apiService)

        val result = updateDataAgreementStatusApiRepository.updateDataAgreementStatus(
            userId = userId,
            dataAgreementId = dataAgreementId,
            body = body
        )

        return if (result.isSuccess) {
            Gson().toJson(result.getOrNull()?.dataAgreementRecord)
        } else {
            null
        }

    }
    suspend fun updateDataAgreementStatus(
        dataAgreementId: String,
        baseUrl: String,
        accessToken: String? = null,
        apiKey: String? = null,
        userId: String? = null,
        status:Boolean? = true,
    ): String? {
        val body = ConsentStatusRequestV2()
        body.optIn = status

        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            apiKey = apiKey,
            accessToken = accessToken,
            baseUrl = (if (baseUrl.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/")
        )?.service!!

        val updateDataAgreementStatusApiRepository =
            UpdateDataAgreementStatusApiRepository(apiService)

        val result = updateDataAgreementStatusApiRepository.updateDataAgreementStatus(
            userId = userId,
            dataAgreementId = dataAgreementId,
            body = body
        )

        return if (result.isSuccess) {
            Gson().toJson(result.getOrNull()?.dataAgreementRecord)
        } else {
            null
        }

    }
    suspend fun getDataAgreement(
        dataAgreementId: String,
        baseUrl: String,
        accessToken: String? = null,
        apiKey: String? = null,
        userId: String? = null,
    ): String? {

        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            apiKey = apiKey,
            accessToken = accessToken,
            baseUrl = (if (baseUrl.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/")
        )?.service!!

        val dataAgreementApiRepository =
            GetDataAgreementApiRepository(apiService)

        val result = dataAgreementApiRepository.getDataAgreement(
            userId = userId,
            dataAgreementId = dataAgreementId
        )

        return if (result.isSuccess) {
            Gson().toJson(result.getOrNull()?.dataAgreement)
        } else {
            null
        }

    }

    suspend fun createAnIndividual(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        name: String? = null,
        email: String? = null,
        phone: String? = null
    ): String? {
        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.createAnIndividual(
                name, email, phone
            )
        return Gson().toJson(result?.getOrNull())
    }

    suspend fun fetchTheIndividual(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        individualId: String
    ): String? {
        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.readTheIndividual(
                individualId
            )
        return Gson().toJson(result?.getOrNull())
    }

    suspend fun updateTheIndividual(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        name: String,
        email: String,
        phone: String,
        individualId: String
    ): String? {
        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.updateIndividual(
                individualId, name, email, phone
            )
        return Gson().toJson(result?.getOrNull())
    }

    suspend fun getAllIndividuals(
        accessToken: String? = null,
        apiKey: String? = null,
        baseUrl: String? = null,
        offset: Int?,
        limit: Int?
    ): String? {
        val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
            accessToken,
            apiKey,
            if (baseUrl?.last().toString() == "/")
                baseUrl
            else
                "$baseUrl/"
        )?.service!!

        val individualApiRepository = IndividualApiRepository(apiService)

        val result =
            individualApiRepository.getAllIndividuals(
                offset, limit
            )
        return Gson().toJson(result?.getOrNull())
    }
}

interface ConsentChangeListener {
    fun onConsentChange(status: Boolean, dataAgreementId: String,consentRecordId: String) {}
}