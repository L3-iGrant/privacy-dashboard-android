package com.github.privacydashboard.modules.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.github.privacydashboard.ConsentChangeListener
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.FragmentPrivacyDashboardBinding
import com.github.privacydashboard.models.PurposeConsent
import com.github.privacydashboard.modules.home.PrivacyDashboardActivity
import com.github.privacydashboard.modules.home.PrivacyDashboardDashboardViewModel
import com.github.privacydashboard.modules.home.UsagePurposeAdapter
import com.github.privacydashboard.modules.home.UsagePurposeClickListener
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardReadMoreOption
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class PrivacyDashboardFragment : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(
            dataAgreementIds: ArrayList<String>?,
            consentChangeListener: ConsentChangeListener?,
            title:String?
        ): PrivacyDashboardFragment {
            val fragment = PrivacyDashboardFragment()

            val args = Bundle().apply {
                putStringArrayList("DATA_AGREEMENT_IDS", dataAgreementIds)
                putString("TITLE",title)
            }
            fragment.arguments = args
            fragment.setConsentChangeListener(consentChangeListener)

            return fragment
        }
    }

    private var consentChangeListener: ConsentChangeListener? = null
    private var viewModel: PrivacyDashboardDashboardViewModel? = null
    private lateinit var binding: FragmentPrivacyDashboardBinding
    private var adapter: UsagePurposeAdapter? = null
    private var tvName: TextView? = null
    private var ivClose: ImageView? = null
    private var ivMenuMore: ImageView? = null
    private var isInProgress = false
    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_dashboard, container, false)
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setDimAmount(0.8f)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[PrivacyDashboardDashboardViewModel::class.java]

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        tvName = binding.root.findViewById(R.id.tvName)
        ivClose =  binding.root.findViewById(R.id.ivClose)
        ivMenuMore = binding.root.findViewById(R.id.ivMenuMore)
        ivMenuMore?.visibility = View.VISIBLE

//        EventBus.getDefault().register(this)
        viewModel?.showBottomSheetEvent?.value = false
        initView()

        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.post {
            val screenHeight = resources.displayMetrics.heightPixels
            val desiredHeight = (screenHeight * 0.75).toInt()

            bottomSheet.layoutParams.height = desiredHeight
            bottomSheet.requestLayout()

            val behavior = BottomSheetBehavior.from(bottomSheet as FrameLayout)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.isDraggable = false
            isCancelable = false
        }



        // Get passed dataAgreementIds from arguments
        val dataAgreementIds = arguments?.getStringArrayList("DATA_AGREEMENT_IDS")
        val title = arguments?.getString("TITLE")
        viewModel?.getOrganizationDetail(true, requireContext())
        viewModel?.getDataAgreements(true, requireContext())

        dataAgreementIds?.let {
            viewModel?.dataAgreementIds?.value = it
        }
        // Add this block to observe showBottomSheetEvent
        viewModel?.showBottomSheetEvent?.observe(viewLifecycleOwner, Observer { shouldShow ->
            if (shouldShow) {
                val bottomSheetData = viewModel?.bottomSheetData?.value
                bottomSheetData?.let { data ->
                    val dataAttributesJson = Gson().toJson(data.dataAttributes)
                    val bottomSheetFragment = PrivacyDashboardDataAttributeListingFragment.newInstance(
                        data.name,
                        data.description,
                        dataAttributesJson
                    )
                    bottomSheetFragment.show(childFragmentManager, bottomSheetFragment.tag)

                }
            }
        })
        tvName?.text = title ?: resources.getString(R.string.privacy_dashboard)
        binding.fragmentToolBar.ivClose.setOnClickListener{
            dismiss() // Dismiss the bottom sheet
        }
        ivMenuMore?.setOnClickListener {
            showPopupMenu(ivMenuMore)
        }
        // Observing the isLoading LiveData
        viewModel?.isLoading?.observe(viewLifecycleOwner) { isLoading ->
            Log.d("Fragment", "isLoading observed: $isLoading")
            if (isLoading == true) {
                binding.llProgressBar.visibility = View.VISIBLE
            } else {
                binding.llProgressBar.visibility = View.GONE
            }
        }



    }

    fun setConsentChangeListener(listener: ConsentChangeListener?) {
        this.consentChangeListener = listener
    }
    private fun showPopupMenu(view: View?) {
        if (view!=null) {
            val popupMenu = PopupMenu(requireContext(), view)
            val isUserRequestAvailable = PrivacyDashboardDataUtils.getBooleanValue(
                requireContext(),
                PrivacyDashboardDataUtils.EXTRA_TAG_ENABLE_USER_REQUEST
            )
            popupMenu.menuInflater.inflate(
                if (isUserRequestAvailable == true) R.menu.menu_more_items else R.menu.menu_more_items_no_user_request,
                popupMenu.getMenu()
            )

            // Handle item clicks in the popup menu
            popupMenu.setOnMenuItemClickListener { item ->
                when (item?.itemId) {
                    R.id.action_webpage -> {
                        val fragment = PrivacyDashboardWebViewFragment.newInstance(
                            viewModel?.organization?.value?.policyURL ?: "",
                            resources.getString(R.string.privacy_dashboard_web_view_privacy_policy)
                        )
                        fragment.show(childFragmentManager, fragment.tag)
                        true
                    }

                    R.id.action_consent_history -> {
                        val fragment = PrivacyDashboardLoggingFragment.newInstance(
                            viewModel?.organization?.value?.iD ?: ""
                        )
                        fragment.show(childFragmentManager, fragment.tag)

                        true
                    }

                    R.id.action_request -> {

                        val orgId = viewModel?.organization?.value?.iD
                        val orgName = viewModel?.organization?.value?.name

                        val fragment =
                            PrivacyDashboardUserRequestFragment.newInstance(orgId, orgName)
                        fragment.show(childFragmentManager, fragment.tag)
                        true
                    }

                    else -> false
                }
            }
            popupMenu.show()
        }
    }
    private fun initView() {
        viewModel?.organization?.observe(viewLifecycleOwner) { newData ->
            try {

                if (viewModel?.organization?.value?.description != null || !viewModel?.organization?.value?.description.equals(
                        ""
                    )
                ) {
                    if ((viewModel?.organization?.value?.description?.length ?: 0) > 120) {
                        val readMoreOption: PrivacyDashboardReadMoreOption =
                            PrivacyDashboardReadMoreOption.Builder(requireContext())
                                .textLength(3) // OR
                                .textLengthType(PrivacyDashboardReadMoreOption.TYPE_LINE) //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                                .moreLabel(resources.getString(R.string.privacy_dashboard_read_more))
                                .lessLabel(resources.getString(R.string.privacy_dashboard_read_less))
                                .moreLabelColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.privacyDashboard_read_more_color
                                    )
                                )
                                .lessLabelColor(
                                    ContextCompat.getColor(
                                        requireContext(),
                                        R.color.privacyDashboard_read_more_color
                                    )
                                )
                                .labelUnderLine(false)
                                .expandAnimation(true)
                                .build()

                        readMoreOption.addReadMoreTo(
                            binding.tvDescription,
                            viewModel?.organization?.value?.description ?: ""
                        )
                    } else {
                        binding.tvDescription.text =
                            viewModel?.organization?.value?.description ?: ""
                    }
                } else {
                    binding.tvDescription.visibility = View.GONE
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        viewModel?.purposeConsents?.observe(viewLifecycleOwner){ newData ->
            try {
                if (newData != null && newData.isNotEmpty()) {
                    adapter = UsagePurposeAdapter(
                        viewModel?.purposeConsents?.value ?: ArrayList(),
                        object : UsagePurposeClickListener {
                            override fun onItemClick(consent: PurposeConsent?) {
                                if (isInProgress) {
                                    return // Skip if already in progress
                                }
                                // Mark the operation as in progress
                                isInProgress = true
                                viewModel?.getConsentList(consent, requireContext())
                                viewModel?.isLoading?.observe(viewLifecycleOwner) { isLoading ->
                                    if (isLoading != true) {
                                        isInProgress = false
                                    }
                                }

                            }

                            override fun onSetStatus(
                                consent: PurposeConsent?,
                                isChecked: Boolean?
                            ) {
                                viewModel?.setOverallStatus(
                                    consent,
                                    isChecked,
                                    requireContext(),
                                    PrivacyDashboardActivity.consentChange
                                )
                            }
                        })
                    binding.rvDataAgreements.adapter = adapter
                    binding.tvEmptyMessage.visibility = View.GONE
                } else {
                    binding.tvEmptyMessage.visibility =
                        if (viewModel?.isListLoading?.value == true || viewModel?.isLoading?.value == true) View.GONE else View.VISIBLE
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }


}