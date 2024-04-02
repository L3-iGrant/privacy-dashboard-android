package com.github.privacydashboard.modules.userRequest

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.ContextThemeWrapper
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.github.privacydashboard.communication.repositories.UserRequestApiRepository
import com.github.privacydashboard.communication.repositories.UserRequestStatusApiRepository
import com.github.privacydashboard.communication.repositories.UserRequestsApiRepository
import com.github.privacydashboard.R
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.*
import com.github.privacydashboard.models.interfaces.userRequests.UserRequest
import com.github.privacydashboard.modules.base.PrivacyDashboardBaseViewModel
import com.github.privacydashboard.modules.userRequestStatus.PrivacyDashboardUserRequestStatusActivity
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyDashboardUserRequestViewModel() : PrivacyDashboardBaseViewModel() {

    var mOrgName: String? = ""
    var mOrgId: String? = ""

    var isLoadingData = false
    var hasLoadedAllItems = false

    private var startId = ""

    private var isDownloadRequestOngoing: Boolean? = false
    private var isDeleteRequestOngoing: Boolean? = false

    var requestHistories = MutableLiveData<ArrayList<UserRequest?>>(ArrayList())
    var listVisibility = MutableLiveData<Boolean>(false)

    init {
        isLoading.value = true
    }

    fun cancelDataRequest(isDownloadData: Boolean, request: UserRequest, context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoading.value = true

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

            val cancelDataRequestApiRepository = CancelDataRequestApiRepository(apiService)

            GlobalScope.launch {
                val result = if (isDownloadData)
                    cancelDataRequestApiRepository.cancelDataRequest(mOrgId, request.mId)
                else
                    cancelDataRequestApiRepository.cancelDeleteDataRequest(mOrgId, request.mId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_user_request_request_cancelled),
                            Toast.LENGTH_SHORT
                        ).show()
                        refreshData(context)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun getRequestHistory(showProgress: Boolean, context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context)) {
            isLoadingData = true
            if (showProgress) isLoading.value = true

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

            val userRequestApiRepository = UserRequestsApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestApiRepository.getUserRequests(mOrgId, startId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoadingData = false
                        isLoading.value = false
                        if (result.getOrNull()?.mDataRequests != null) {
                            if (startId.equals("", ignoreCase = true)) {
                                requestHistories.value = ArrayList()
                                isDownloadRequestOngoing =
                                    result.getOrNull()?.mDataDownloadRequestOngoing
                                isDeleteRequestOngoing =
                                    result.getOrNull()?.mDataDeleteRequestOngoing
                                val tempList = requestHistories.value
                                tempList?.addAll(
                                    setOngoingRequests(
                                        result.getOrNull()?.mDataRequests ?: ArrayList()
                                    )
                                )
                                requestHistories.value = tempList
                            } else {
                                val tempList = requestHistories.value
                                tempList?.addAll(result.getOrNull()?.mDataRequests ?: ArrayList())
                                requestHistories.value = tempList
                            }
                            if ((requestHistories.value?.size ?: 0) > 0) {
                                startId =
                                    requestHistories.value?.get(
                                        (requestHistories.value?.size ?: 1) - 1
                                    )?.mId ?: ""
                            }
                            listVisibility.value = (requestHistories.value?.size ?: 0) > 0

                        } else {
                            if (startId.equals("", ignoreCase = true)) {
                                requestHistories.value = ArrayList()
                                listVisibility.value = (requestHistories.value?.size ?: 0) > 0

                            }
                        }
                        if (result.getOrNull()?.mDataRequests == null) {
                            hasLoadedAllItems = true
                        }

                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoadingData = false
                        isLoading.value = false
                    }
                }
            }
        }
    }

    private fun setOngoingRequests(dataRequests: ArrayList<UserRequest?>?): Collection<UserRequest?> {
        if (dataRequests != null) {
            for (data in dataRequests) {
                if (data?.mType == 1 && isDeleteRequestOngoing!!) {
                    data.mIsOngoing = (true)
                    isDeleteRequestOngoing = false
                }
                if (data?.mType == 2 && isDownloadRequestOngoing!!) {
                    data.mIsOngoing = (true)
                    isDownloadRequestOngoing = false
                }
                if (!isDownloadRequestOngoing!! && !isDeleteRequestOngoing!!) {
                    return dataRequests
                }
            }
        }
        return dataRequests ?: ArrayList()
    }

    fun refreshData(context: Context) {
        requestHistories.value?.clear()
        startId = ""
        getRequestHistory(true, context)
    }

    fun dataDeleteRequest(context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context, true)) {
            isLoading.value = true
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

            val userRequestApiRepository = UserRequestApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestApiRepository.deleteDataRequest(mOrgId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        refreshData(context)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun dataDownloadRequest(context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context, true)) {
            isLoading.value = true
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

            val userRequestApiRepository = UserRequestApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestApiRepository.dataDownloadRequest(mOrgId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        refreshData(context)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun deleteDataRequestStatus(context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context, true)) {
            isLoading.value = true

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

            val userRequestStatusApiRepository = UserRequestStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestStatusApiRepository.deleteDataStatusRequest(mOrgId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        if (result.getOrNull()?.mRequestOngoing == true) {
                            gotToStatusPage(false, context)
                        } else {
                            confirmationAlert(true, context)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    fun downloadDataRequestStatus(context: Context) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(context, true)) {
            isLoading.value = true

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

            val userRequestStatusApiRepository = UserRequestStatusApiRepository(apiService)

            GlobalScope.launch {
                val result = userRequestStatusApiRepository.dataDownloadStatusRequest(mOrgId)

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        if (result.getOrNull()?.mRequestOngoing == true) {
                            gotToStatusPage(false, context)
                        } else {
                            confirmationAlert(false, context)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_error_unexpected),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun confirmationAlert(isDelete: Boolean, context: Context) {
        AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogCustom))
            .setTitle(context.resources.getString(R.string.privacy_dashboard_user_request_confirmation))
            .setMessage(
                context.resources.getString(
                    R.string.privacy_dashboard_user_request_confirmation_message,
                    if (isDelete) context.resources.getString(R.string.privacy_dashboard_user_request_data_delete) else context.resources.getString(
                        R.string.privacy_dashboard_user_request_download_data
                    ),
                    mOrgName
                )
            ).setPositiveButton(R.string.privacy_dashboard_user_request_confirm,
                DialogInterface.OnClickListener { dialog, which ->
                    if (isDelete) {
                        dataDeleteRequest(context)
                    } else {
                        dataDownloadRequest(context)
                    }
                }).setNegativeButton(R.string.privacy_dashboard_general_cancel, null).show()

    }

    fun gotToStatusPage(isDownloadRequest: Boolean, context: Context) {
        val intent = Intent(context, PrivacyDashboardUserRequestStatusActivity::class.java)
        intent.putExtra(
            PrivacyDashboardUserRequestStatusActivity.EXTRA_DATA_REQUEST_TYPE,
            isDownloadRequest
        )
        context.startActivity(intent)
    }

}