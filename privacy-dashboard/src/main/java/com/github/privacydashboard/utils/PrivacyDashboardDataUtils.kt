package com.github.privacydashboard.utils

import android.content.Context
import android.preference.PreferenceManager

object PrivacyDashboardDataUtils {

    const val EXTRA_TAG_USERID = "PrivacyDashboardDataUtils.userid"
    const val EXTRA_TAG_ORGANISATIONID = "PrivacyDashboardDataUtils.organisationId"
    const val EXTRA_TAG_TOKEN = "PrivacyDashboardDataUtils.token"
    const val EXTRA_TAG_ACCESS_TOKEN = "PrivacyDashboardDataUtils.accessToken"
    const val EXTRA_TAG_DATA_AGREEMENT_ID = "PrivacyDashboardDataUtils.dataAgreementId"
    const val EXTRA_TAG_BASE_URL = "PrivacyDashboardDataUtils.baseUrl"
    const val EXTRA_TAG_ENABLE_USER_REQUEST = "PrivacyDashboardDataUtils.enableUserRequest"
    const val EXTRA_TAG_ENABLE_ASK_ME = "PrivacyDashboardDataUtils.enableAskMe"
    const val EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT = "PrivacyDashboardDataUtils.enableAttributeLevelConsent"
    const val EXTRA_TAG_UIMODE = "PrivacyDashboardDataUtils.uiMode"

    //todo update the deprecated preference manager
    fun saveStringValues(context: Context?, tag: String?, value: String?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putString(tag, value)
        editor.commit()
    }

    fun getStringValue(context: Context?, tag: String?, defaultValue:String? = ""): String? {
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.getString(tag, defaultValue)
        } catch (e: Exception) {
            ""
        }
    }

    fun saveBooleanValues(context: Context?, tag: String?, value: Boolean?) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        editor.putBoolean(tag, value?:false)
        editor.commit()
    }

    fun getBooleanValue(context: Context?, tag: String?): Boolean? {
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            sharedPreferences.getBoolean(tag, false)
        } catch (e: Exception) {
            false
        }
    }
}
