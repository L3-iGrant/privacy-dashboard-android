package com.github.privacydashboard.modules.logging

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacydashboard.R
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.GetConsentHistoryApiRepository
import com.github.privacydashboard.databinding.PrivacyActivityLoggingBinding
import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistory
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardNetWorkUtil
import com.paginate.Paginate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyDashboardLoggingActivity : PrivacyDashboardBaseActivity() {

    private var adapter: PrivacyDashboardHistoryAdapter? = null
    private lateinit var binding: PrivacyActivityLoggingBinding

    companion object {
        const val TAG_EXTRA_ORG_ID =
            "com.github.privacyDashboard.modules.logging.PrivacyDashboardLoggingActivity.orgId"
    }

    private var mOrgId = ""
    private var startId = 0

    private var isCallLoading = false
    private var hasLoadedAllItems = false

    private var consentHistories: ArrayList<ConsentHistory?>? = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.privacy_activity_logging)
        getIntentData()
        setUpToolBar()
        setupRecyclerView()
        fetchConsentHistory(true)
    }

    private fun fetchConsentHistory(showProgress: Boolean) {
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(this, true)) {
            isCallLoading = true
            if (showProgress) binding.llProgressBar.visibility = View.VISIBLE

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    this,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = PrivacyDashboardDataUtils.getStringValue(
                    this,
                    PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = PrivacyDashboardDataUtils.getStringValue(
                    this,
                    PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val consentHistoryRepository = GetConsentHistoryApiRepository(apiService)

            GlobalScope.launch {
                val result = consentHistoryRepository.getConsentHistory(
                    userID = PrivacyDashboardDataUtils.getStringValue(
                        this@PrivacyDashboardLoggingActivity,
                        PrivacyDashboardDataUtils.EXTRA_TAG_USERID,
                        null
                    ), offset = startId, limit = 10
                )

                if (result?.isSuccess == true) {
                    withContext(Dispatchers.Main) {
                        isCallLoading = false
                        binding.llProgressBar.visibility = View.GONE
                        if (result.getOrNull()?.mConsentHistory != null) {
                            if (startId == 0) consentHistories!!.clear()

                            consentHistories!!.addAll(
                                result.getOrNull()?.mConsentHistory ?: ArrayList()
                            )
                            binding.rvConsentHistory.visibility =
                                if (consentHistories!!.size > 0) View.VISIBLE else View.GONE
                            if (consentHistories!!.size > 0) {
                                startId += 10
//                                    consentHistories!![consentHistories!!.size - 1]?.mId ?: ""
                            }

                        } else {
                            if (startId == 0) {
                                consentHistories!!.clear()
                                binding.rvConsentHistory.visibility =
                                    if (consentHistories!!.size > 0) View.VISIBLE else View.GONE
                            }
                        }

                        if (result.getOrNull()?.mConsentHistory == null) {
                            hasLoadedAllItems = true
                        }
                        adapter!!.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        isCallLoading = false
                        binding.llProgressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = PrivacyDashboardHistoryAdapter(consentHistories ?: ArrayList())
        binding.rvConsentHistory.adapter = adapter
        Paginate.with(binding.rvConsentHistory, object : Paginate.Callbacks {
            override fun onLoadMore() {
                fetchConsentHistory(false)
            }

            override fun isLoading(): Boolean {
                return isCallLoading
            }

            override fun hasLoadedAllItems(): Boolean {
                return hasLoadedAllItems
            }
        }).setLoadingTriggerThreshold(1)
            .addLoadingListItem(true)
            .build()
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mOrgId =
                intent.getStringExtra(TAG_EXTRA_ORG_ID) ?: ""
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolBarCommon)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_back_black_pad
            )
        )
        supportActionBar?.title = resources.getString(R.string.privacy_dashboard_history_consent_history)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}