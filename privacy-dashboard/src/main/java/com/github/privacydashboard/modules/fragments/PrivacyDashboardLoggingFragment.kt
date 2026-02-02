package com.github.privacydashboard.modules.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.github.privacydashboard.R
import com.github.privacydashboard.communication.PrivacyDashboardAPIManager
import com.github.privacydashboard.communication.PrivacyDashboardAPIServices
import com.github.privacydashboard.communication.repositories.GetConsentHistoryApiRepository
import com.github.privacydashboard.databinding.FragmentPrivacyDashboardLoggingBinding
import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistory
import com.github.privacydashboard.modules.logging.PrivacyDashboardHistoryAdapter
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardNetWorkUtil
import com.github.privacydashboard.utils.PrivacyDashboardStringUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paginate.Paginate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PrivacyDashboardLoggingFragment : BasePrivacyDashboardFragment() {
    private lateinit var binding: FragmentPrivacyDashboardLoggingBinding
    private var adapter: PrivacyDashboardHistoryAdapter? = null
    private var mOrgId = ""
    private var startId = 0

    private var isCallLoading = false
    private var hasLoadedAllItems = false

    private var consentHistories: ArrayList<ConsentHistory?>? = ArrayList()
    private lateinit var tvName: TextView
    private lateinit var ivClose: ImageView
    companion object {
        private const val TAG_EXTRA_ORG_ID = "TAG_EXTRA_ORG_ID"

        fun newInstance(orgId: String?): PrivacyDashboardLoggingFragment {
            val fragment = PrivacyDashboardLoggingFragment()
            val args = Bundle()
            args.putString(TAG_EXTRA_ORG_ID, orgId)
            fragment.arguments = args
            return fragment
        }
    }
    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_dashboard_logging, container, false)
        return binding.root
    }
    override fun onStart() {
        super.onStart()

        // Set background drawable and dim amount for the bottom sheet
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.8f)
        }

        // Adjust bottom sheet height and behavior
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val desiredHeight = (screenHeight * 0.86).toInt()

            bottomSheet.layoutParams.height = desiredHeight
            bottomSheet.requestLayout()

            val behavior = BottomSheetBehavior.from(bottomSheet as FrameLayout)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED  // Keep it expanded
            behavior.isDraggable = false  // Disable dragging
            isCancelable = false  // Prevent dismissing by touch
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tvName = binding.root.findViewById(R.id.tvName)
        ivClose =  binding.root.findViewById(R.id.ivClose)
        getIntentData()
        tvName.text = getLocalizedString(R.string.privacy_dashboard_history_consent_history)
        setupRecyclerView()
        fetchConsentHistory(true)
        initListener()
    }
    private fun getIntentData() {
        mOrgId = arguments?.getString(TAG_EXTRA_ORG_ID) ?: ""
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
    private fun fetchConsentHistory(showProgress: Boolean) {
        val safeContext = context ?: return
        if (PrivacyDashboardNetWorkUtil.isConnectedToInternet(safeContext, true)) {
            isCallLoading = true
            if (showProgress) binding.llProgressBar.visibility = View.VISIBLE

            val apiService: PrivacyDashboardAPIServices = PrivacyDashboardAPIManager.getApi(
                apiKey = PrivacyDashboardDataUtils.getStringValue(
                    safeContext,
                    PrivacyDashboardDataUtils.EXTRA_TAG_TOKEN,
                    null
                ),
                accessToken = PrivacyDashboardDataUtils.getStringValue(
                    safeContext,
                    PrivacyDashboardDataUtils.EXTRA_TAG_ACCESS_TOKEN,
                    null
                ),
                baseUrl = PrivacyDashboardDataUtils.getStringValue(
                    safeContext,
                    PrivacyDashboardDataUtils.EXTRA_TAG_BASE_URL
                )
            )?.service!!

            val consentHistoryRepository = GetConsentHistoryApiRepository(apiService)

            GlobalScope.launch {
                val result = consentHistoryRepository.getConsentHistory(
                    userID = PrivacyDashboardDataUtils.getStringValue(
                        safeContext,
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
    private fun initListener() {
        ivClose.setOnClickListener {
            dismiss()
        }
    }
}