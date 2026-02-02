package com.github.privacydashboard.modules.fragments

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.FragmentPrivacyDataAttributesBinding
import com.github.privacydashboard.models.base.attribute.DataAttribute
import com.github.privacydashboard.models.base.attribute.DataAttributesResponse
import com.github.privacydashboard.modules.attributeDetail.PrivacyDashboardDataAttributeDetailActivity
import com.github.privacydashboard.modules.dataAttribute.DataAttributeClickListener
import com.github.privacydashboard.modules.dataAttribute.PrivacyDashboardDataAttributeListingActivity
import com.github.privacydashboard.modules.dataAttribute.PrivacyDashboardDataAttributesAdapter
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardReadMoreOption
import com.github.privacydashboard.utils.PrivacyDashboardStringUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class PrivacyDashboardDataAttributeListingFragment : BasePrivacyDashboardFragment() {
    companion object {
        private const val ARG_NAME = "ARG_NAME"
        private const val ARG_DESCRIPTION = "ARG_DESCRIPTION"
        private const val ARG_DATA_ATTRIBUTES = "ARG_DATA_ATTRIBUTES"

        fun newInstance(name: String?, description: String?, dataAttributesJson: String?): PrivacyDashboardDataAttributeListingFragment {
            val fragment = PrivacyDashboardDataAttributeListingFragment()
            val args = Bundle()
            args.putString(ARG_NAME, name)
            args.putString(ARG_DESCRIPTION, description)
            args.putString(ARG_DATA_ATTRIBUTES, dataAttributesJson)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var binding: FragmentPrivacyDataAttributesBinding
    private var adapter: PrivacyDashboardDataAttributesAdapter? = null
    private var mTitle = ""
    private var mDescription = ""
    private var dataAttributesResponse: DataAttributesResponse? = null
    private lateinit var tvName: TextView
    private lateinit var ivClose: ImageView
    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_data_attributes, container, false)
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
        tvName = binding.root.findViewById(R.id.tvName)
        ivClose =  binding.root.findViewById(R.id.ivClose)
        getIntentData()
        setUpDescription()
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

        // Retrieve the JSON string from the arguments
        val jsonString = arguments?.getString(ARG_DATA_ATTRIBUTES)

        // Convert the JSON string back to DataAttributesResponse
        dataAttributesResponse= jsonString?.let {
            Gson().fromJson(it, DataAttributesResponse::class.java)
        }
        setUpDataAttributeList()
        // Now you can use the dataAttributes object as needed
        initListener()
        tvName.text =
            PrivacyDashboardStringUtils.toCamelCase(mTitle)

    }

    private fun getIntentData() {
        mTitle = arguments?.getString(ARG_NAME) ?: ""
        mDescription = arguments?.getString(ARG_DESCRIPTION) ?: ""
    }
    private fun setUpDescription() {
        if (mDescription.length > 120) {
            val readMoreOption: PrivacyDashboardReadMoreOption = PrivacyDashboardReadMoreOption.Builder(requireContext())
                .textLength(3) // OR
                .textLengthType(PrivacyDashboardReadMoreOption.TYPE_LINE) //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel(getLocalizedString(R.string.privacy_dashboard_read_more))
                .lessLabel(getLocalizedString(R.string.privacy_dashboard_read_less))
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
                mDescription
            )
        } else {
            binding.tvDescription.text = mDescription
        }
    }
    private fun setUpDataAttributeList() {
        adapter = PrivacyDashboardDataAttributesAdapter(
            dataAttributesResponse?.consents?.consents ?: ArrayList(),
            object : DataAttributeClickListener {
                override fun onAttributeClick(dataAttribute: DataAttribute?) {
                    val isAttributeLevelConsentEnabled = PrivacyDashboardDataUtils.getBooleanValue(
                        requireContext(),
                        PrivacyDashboardDataUtils.EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT
                    )
                    if (isAttributeLevelConsentEnabled == true) {
                        if (dataAttributesResponse?.consents?.purpose?.lawfulUsage == false
                        ) {
                            val intent: Intent = Intent(
                                requireContext(),
                                PrivacyDashboardDataAttributeDetailActivity::class.java
                            )
                            intent.putExtra(
                                PrivacyDashboardDataAttributeDetailActivity.EXTRA_TAG_ORGID,
                                dataAttributesResponse?.orgID
                            )
                            intent.putExtra(
                                PrivacyDashboardDataAttributeDetailActivity.EXTRA_TAG_CONSENTID,
                                dataAttributesResponse?.consentID
                            )
                            intent.putExtra(
                                PrivacyDashboardDataAttributeDetailActivity.EXTRA_TAG_PURPOSEID,
                                dataAttributesResponse?.iD
                            )
                            intent.putExtra(
                                PrivacyDashboardDataAttributeDetailActivity.EXTRA_TAG_CONSENT,
                                Gson().toJson(dataAttribute)
                            )
                            startActivity(intent)
                            PrivacyDashboardDataAttributeDetailActivity.mDataAttribute = dataAttribute
                        }
                    }
                }
            })
        binding.rvDataAttributes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvDataAttributes.adapter = adapter
    }
    private fun initListener() {
        ivClose.setOnClickListener {
            dismiss()
        }
        binding.btnPrivacyPolicy.setOnClickListener {
            val policyUrl = dataAttributesResponse?.consents?.purpose?.policyURL
            val title = getLocalizedString(R.string.privacy_dashboard_web_view_policy)

            val webViewFragment = PrivacyDashboardWebViewFragment.newInstance(policyUrl, title)
            webViewFragment.show(childFragmentManager, webViewFragment.tag)
        }

    }

}