package com.github.privacydashboard.modules.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.FragmentPrivacyUserRequestBinding
import com.github.privacydashboard.models.interfaces.userRequests.UserRequest
import com.github.privacydashboard.modules.userRequest.PrivacyDashboardUserRequestClickListener
import com.github.privacydashboard.modules.userRequest.PrivacyDashboardUserRequestHistoryAdapter
import com.github.privacydashboard.modules.userRequest.PrivacyDashboardUserRequestViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.paginate.Paginate

class PrivacyDashboardUserRequestFragment: BottomSheetDialogFragment() {
    private var adapter: PrivacyDashboardUserRequestHistoryAdapter? = null

    private lateinit var binding: FragmentPrivacyUserRequestBinding

    private var viewModel: PrivacyDashboardUserRequestViewModel? = null
    companion object {
        private const val EXTRA_TAG_USER_REQUEST_ORGID = "EXTRA_TAG_USER_REQUEST_ORGID"
        private const val EXTRA_TAG_USER_REQUEST_ORG_NAME = "EXTRA_TAG_USER_REQUEST_ORG_NAME"

        fun newInstance(orgId: String?, orgName: String?): PrivacyDashboardUserRequestFragment {
            val fragment = PrivacyDashboardUserRequestFragment()
            val args = Bundle()
            args.putString(EXTRA_TAG_USER_REQUEST_ORGID, orgId)
            args.putString(EXTRA_TAG_USER_REQUEST_ORG_NAME, orgName)
            fragment.arguments = args
            return fragment
        }
    }
    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog
    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.8f)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_user_request, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PrivacyDashboardUserRequestViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val desiredHeight = (screenHeight * 0.86).toInt()

            bottomSheet.layoutParams.height = desiredHeight
            bottomSheet.requestLayout()

            val behavior = BottomSheetBehavior.from(bottomSheet as FrameLayout)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            isCancelable = false
        }

        getIntentData()
        initListeners()
        setUpList()
        viewModel?.getRequestHistory(true, requireContext())

    }
    private fun getIntentData() {
        viewModel?.mOrgId = arguments?.getString(EXTRA_TAG_USER_REQUEST_ORGID) ?: ""
        viewModel?.mOrgName  = arguments?.getString(EXTRA_TAG_USER_REQUEST_ORG_NAME) ?: ""
    }
    private fun initListeners() {

        viewModel?.requestHistories?.observe(this, Observer { newData ->
            adapter?.updateList(newData)
        })

        binding.tvNewRequest.setOnClickListener {

            val popupMenu = PopupMenu(requireContext(), binding.tvNewRequest)
            popupMenu.menuInflater.inflate(
                R.menu.menu_new_requests,
                popupMenu.getMenu()
            )

            // Handle item clicks in the popup menu
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.action_download_data -> {
                        viewModel?.downloadDataRequestStatus(requireContext())
                        true
                    }
                    R.id.action_forgot_me -> {
                        viewModel?.deleteDataRequestStatus(requireContext())
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }
    }

    private fun setUpList() {
        adapter = PrivacyDashboardUserRequestHistoryAdapter(
            viewModel?.requestHistories?.value ?: ArrayList(),
            object : PrivacyDashboardUserRequestClickListener {

                override fun onRequestClick(request: UserRequest?) {
                    viewModel?.gotToStatusPage(
                        request?.mType != 1,
                        requireContext()
                    )
                }

                override fun onRequestCancel(request: UserRequest?) {
                    request?.let {
                        viewModel?.cancelDataRequest(
                            request.mType == 2,
                            it,
                            requireContext()
                        )
                    }
                }

            })
        binding.rvRequestHistory.adapter = adapter
        Paginate.with(binding.rvRequestHistory, object : Paginate.Callbacks {
            override fun onLoadMore() {
                viewModel?.getRequestHistory(false, requireContext())
            }

            override fun isLoading(): Boolean {
                Log.d("milan", "isLoading: ${viewModel?.isLoadingData}")
                return viewModel?.isLoadingData == true
            }

            override fun hasLoadedAllItems(): Boolean {
                Log.d("milan", "hasLoadedAllItems: ${viewModel?.hasLoadedAllItems}")

                return viewModel?.hasLoadedAllItems == true
            }
        }).setLoadingTriggerThreshold(1)
            .addLoadingListItem(true)
            .build()
    }
}