package com.github.privacydashboard.modules.userRequestStatus

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.github.privacydashboard.R
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.CancelDataRequestApiRepository
import com.github.privacydashboard.communication.repositories.UserRequestStatusApiRepository
import com.github.privacydashboard.models.interfaces.userRequests.UserRequestStatus
import com.github.privacydashboard.modules.base.PrivacyDashboardBaseViewModel
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardNetWorkUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyDashboardUserRequestStatusViewModel() : PrivacyDashboardBaseViewModel() {

    var shouldFinish = MutableLiveData<Boolean>(false)

    init {
        isLoading.value = true
    }

    var status = MutableLiveData<UserRequestStatus?>(null)


    fun cancelDataRequest(isDownloadData: Boolean?, requestId: String?, context: Context) {
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
                val result = if (isDownloadData == true)
                    cancelDataRequestApiRepository.cancelDataRequest(
                       "",//todo need to update these APIs when available
                        requestId
                    )
                else
                    cancelDataRequestApiRepository.cancelDeleteDataRequest(
                       "",//todo need to update these APIs when available
                        requestId
                    )

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        Toast.makeText(
                            context,
                            context.resources.getString(R.string.privacy_dashboard_user_request_request_cancelled),
                            Toast.LENGTH_SHORT
                        ).show()
                        shouldFinish.value = true
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

    fun getDataRequestStatus(mIsDownloadData: Boolean?, context: Context) {
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
                val result = if (mIsDownloadData == true)
                    userRequestStatusApiRepository.dataDownloadStatusRequest(
                        ""//todo need to update these APIs when available
                    )
                else
                    userRequestStatusApiRepository.deleteDataStatusRequest(
                        ""//todo need to update these APIs when available
                    )

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isLoading.value = false
                        status.value = result.getOrNull()
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
}