package com.github.privacydashboard.modules.home

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.github.privacydashboard.ConsentChangeListener
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.GetConsentsByIdApiRepository
import com.github.privacydashboard.communication.repositories.GetDataAgreementsApiRepository
import com.github.privacydashboard.communication.repositories.GetOrganisationDetailApiRepository
import com.github.privacydashboard.communication.repositories.UpdateDataAgreementStatusApiRepository
import com.github.privacydashboard.models.Organization
import com.github.privacydashboard.models.OrganizationDetailResponse
import com.github.privacydashboard.models.PurposeConsent
import com.github.privacydashboard.models.v2.consent.ConsentStatusRequestV2
import com.github.privacydashboard.models.v2.dataAgreement.dataAgreementRecords.DataAgreementLatestRecordResponseV2
import com.github.privacydashboard.modules.base.PrivacyDashboardBaseViewModel
import com.github.privacydashboard.modules.dataAttribute.PrivacyDashboardDataAttributeListingActivity
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.github.privacydashboard.models.PrivacyDashboardBottomSheetData
import com.github.privacydashboard.utils.ViewMode

class PrivacyDashboardDashboardViewModel() : PrivacyDashboardBaseViewModel() {

    var orgName = MutableLiveData<String>("")
    var orgLocation = MutableLiveData<String>("")
    var orgDescription = MutableLiveData<String>("")

    val organization = MutableLiveData<Organization?>()
    var consentId: String? = null
    val purposeConsents = MutableLiveData<ArrayList<PurposeConsent>>()

    var isListLoading = MutableLiveData<Boolean>()
    var dataAgreementIds: MutableLiveData<List<String>?> = MutableLiveData(null)
    val showBottomSheetEvent = MutableLiveData<Boolean>()
    val bottomSheetData = MutableLiveData<PrivacyDashboardBottomSheetData?>()

    private fun updateUI(orgDetail: OrganizationDetailResponse?) {
        consentId = orgDetail?.consentID

        val agreementIds = dataAgreementIds.value

        // If dataAgreementIds is null, show all purposeConsents
        val dataAgreements = if (agreementIds == null) {
            orgDetail?.purposeConsents
        } else {
            // Filter only those whose purpose.id matches any id in dataAgreementIds
            orgDetail?.purposeConsents?.filter { consent ->
                agreementIds.contains(consent.purpose?.iD)
            }
        }
        purposeConsents.value = dataAgreements?.let { ArrayList(it) } ?: ArrayList()
    }


    private fun updateOrganization(orgDetail: OrganizationDetailResponse?) {
        organization.value = orgDetail?.organization
        orgName.value = orgDetail?.organization?.name
        orgLocation.value = orgDetail?.organization?.location
        orgDescription.value = orgDetail?.organization?.description
    }

    init {
        isLoading.value = true
        isListLoading.value = false
        organization.value = null
        purposeConsents.value = ArrayList()
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
                        isListLoading.value = purposeConsents.value?.isEmpty()
                        updateOrganization(result.getOrNull())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        isListLoading.value = purposeConsents.value?.isEmpty()
                    }
                }
            }
        }
    }

    fun getDataAgreements(showProgress: Boolean, context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
//            isLoading.value = showProgress

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

            val organizationDetailRepository = GetDataAgreementsApiRepository(apiService)

            GlobalScope.launch {
                val result = organizationDetailRepository.getOrganizationDetail(
                    PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_USERID,
                        null
                    )
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isListLoading.value = false
                        updateUI(result.getOrNull())
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isListLoading.value = false
                    }
                }
            }
        }
    }

    fun setOverallStatus(
        consent: PurposeConsent?,
        isChecked: Boolean?,
        context: Context,
        consentChange: ConsentChangeListener?
    ) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true
            val body = ConsentStatusRequestV2()
            body.optIn = isChecked == true

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    context,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ) ,
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
                    dataAgreementId = consent?.purpose?.iD,
                    body = body
                )

                if (result.isSuccess) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
//                        getDataAgreements(false, context)
                        updatePurposeConsent(result.getOrNull())
                        consentChange?.onConsentChange(isChecked == true,consent?.purpose?.iD?:"", result.getOrNull()?.dataAgreementRecord?.id ?: "")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                    }
                }
            }
        } else {
//            adapter!!.notifyDataSetChanged()
        }
    }

    private fun updatePurposeConsent(record: DataAgreementLatestRecordResponseV2?) {
        val count =
            purposeConsents.value?.find { it.purpose?.iD == record?.dataAgreementRecord?.dataAgreementId }?.count?.total

        purposeConsents.value?.find { it.purpose?.iD == record?.dataAgreementRecord?.dataAgreementId }?.count?.consented =
            if (record?.dataAgreementRecord?.optIn == true) count else 0

        purposeConsents.value = purposeConsents.value
    }

    fun getConsentList(consent: PurposeConsent?, context: Context) {
        isLoading.value = true
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {

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

            val consentListRepository = GetConsentsByIdApiRepository(apiService)

            GlobalScope.launch {
                val result = consentListRepository.getConsentsById(
                    userId = PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_USERID,
                        null
                    ),
                    dataAgreementId = consent?.purpose?.iD,
                    isAllAllowed = consent?.count?.consented == consent?.count?.total
                )

                if (result.isSuccess) {
                    val mUIMode = PrivacyDashboardDataUtils.getStringValue(
                        context,
                        PrivacyDashboardDataUtils.EXTRA_TAG_UIMODE,
                        null
                    )
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        if (mUIMode == ViewMode.BottomSheet.mode)
                        {
                            val data = PrivacyDashboardBottomSheetData(
                                name = consent?.purpose?.name,
                                description = consent?.purpose?.description,
                                dataAttributes = result.getOrNull()
                            )

                            bottomSheetData.value = data

                            showBottomSheetEvent.value = true

                        }else{
                        val intent = Intent(
                            context,
                            PrivacyDashboardDataAttributeListingActivity::class.java
                        )
                        intent.putExtra(
                            PrivacyDashboardDataAttributeListingActivity.TAG_EXTRA_NAME,
                            consent?.purpose?.name
                        )
                        intent.putExtra(
                            PrivacyDashboardDataAttributeListingActivity.TAG_EXTRA_DESCRIPTION,
                            consent?.purpose?.description
                        )
                        context.startActivity(intent)
                        PrivacyDashboardDataAttributeListingActivity.dataAttributesResponse =
                            result.getOrNull()
                    }
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