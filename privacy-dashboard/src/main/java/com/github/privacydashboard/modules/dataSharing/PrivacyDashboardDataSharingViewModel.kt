package com.github.privacydashboard.modules.dataSharing

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.GetDataAgreementApiRepository
import com.github.privacydashboard.communication.repositories.GetDataAgreementRecordApiRepository
import com.github.privacydashboard.communication.repositories.GetOrganisationDetailApiRepository
import com.github.privacydashboard.communication.repositories.UpdateDataAgreementStatusApiRepository
import com.github.privacydashboard.models.Organization
import com.github.privacydashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementRecordsV2
import com.github.privacydashboard.modules.base.PrivacyDashboardBaseViewModel
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyDashboardDataSharingViewModel : PrivacyDashboardBaseViewModel() {

    val fetchingDataAgreementRecord = MutableLiveData<Boolean>(true)
    val organization = MutableLiveData<Organization?>()
    val dataAgreement = MutableLiveData<DataAgreementV2?>(null)

    val dataAgreementRecord = MutableLiveData<DataAgreementRecordsV2?>(null)

    var otherApplicationName: String? = null
    var otherApplicationLogo: String? = null

    init {
        isLoading.value = false
    }

    fun fetchDataAgreementRecord(showProgress: Boolean, context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val organizationDetailRepository = GetDataAgreementRecordApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getDataAgreementRecord(
                    PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_USERID,
                        null
                    ),
                    PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID,
                        null
                    )
                )

                if (result.isSuccess && result.getOrNull()?.dataAgreementRecord != null) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        dataAgreementRecord.value = result.getOrNull()?.dataAgreementRecord
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        getOrganizationDetail(true, context)
//                        fetchingDataAgreementRecord.value = false
                    }
                }
            }
        }
    }

    fun getOrganizationDetail(showProgress: Boolean, context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val organizationDetailRepository = GetOrganisationDetailApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getOrganizationDetail(
                    PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_ORGANISATIONID,
                        null
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        getDataAgreement(true, context, result.getOrNull()?.organization)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun getDataAgreement(showProgress: Boolean, context: Context, organizationCopy: Organization?) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = showProgress

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val organizationDetailRepository = GetDataAgreementApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getDataAgreement(
                    PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_USERID,
                        null
                    ), PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID,
                        null
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        organization.value = organizationCopy
                        dataAgreement.value = result.getOrNull()?.dataAgreement
                        fetchingDataAgreementRecord.value = false
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }

    fun authorizeRequest(context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true
            val body = ConsentStatusRequestV2()
            body.optIn = true

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val updateDataAgreementStatusApiRepository =
                UpdateDataAgreementStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = updateDataAgreementStatusApiRepository.updateDataAgreementStatus(
                    userId = PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_USERID,
                        null
                    ),
                    dataAgreementId = PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_DATA_AGREEMENT_ID,
                        null
                    ),
                    body = body
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        dataAgreementRecord.value = result.getOrNull()?.dataAgreementRecord
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        }
    }
}