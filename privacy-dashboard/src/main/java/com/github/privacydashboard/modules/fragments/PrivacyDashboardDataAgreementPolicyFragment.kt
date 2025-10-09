package com.github.privacydashboard.modules.fragments

import android.content.Context
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
import com.github.privacydashboard.databinding.FragmentPrivacyDataAreementPolicyBinding
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacydashboard.modules.dataAgreementPolicy.PrivacyDashboardDataAgreementPolicyAdapter
import com.github.privacydashboard.utils.AdapterType
import com.github.privacydashboard.utils.PrivacyDashboardDisplayUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson
import kotlin.math.floor

class PrivacyDashboardDataAgreementPolicyFragment: BottomSheetDialogFragment() {
    var adapter: PrivacyDashboardDataAgreementPolicyAdapter? = null
    var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()


    private lateinit var binding: FragmentPrivacyDataAreementPolicyBinding
    private lateinit var tvName:TextView
    private lateinit var ivClose: ImageView
    companion object {
        private const val TAG_EXTRA_ATTRIBUTE_LIST = "TAG_EXTRA_ATTRIBUTE_LIST"

        fun newInstance(data: String?): PrivacyDashboardDataAgreementPolicyFragment {
            val fragment = PrivacyDashboardDataAgreementPolicyFragment()
            val args = Bundle()
            args.putString(TAG_EXTRA_ATTRIBUTE_LIST, data)
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_data_areement_policy, container, false)
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
        tvName.text = resources.getString(R.string.privacy_dashboard_data_agreement_policy_data_agreement)

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
        setUpList()
        ivClose.setOnClickListener {
            dismiss() // Dismiss the bottom sheet
        }
    }
    private fun getIntentData() {

        val data = arguments?.getString(TAG_EXTRA_ATTRIBUTE_LIST) ?: ""
        val mDataAgreement= Gson().fromJson(data, DataAgreementV2::class.java)
        list = buildListForDataAgreementPolicy(requireContext(),mDataAgreement)
    }
    private fun buildListForDataAgreementPolicy(
        context: Context,
        dataAgreement: DataAgreementV2?
    ): ArrayList<ArrayList<DataAgreementPolicyModel>> {
        var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()
        var subList: ArrayList<DataAgreementPolicyModel> = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_version),
                dataAgreement?.version
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_purpose),
                dataAgreement?.purpose
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_purpose_description),
                dataAgreement?.purposeDescription
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_lawful_basis_of_processing),
                dataAgreement?.lawfulBasis
            )
        )
        list.add(subList)
        subList = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_policy_url),
                dataAgreement?.policy?.url
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_jurisdiction),
                dataAgreement?.policy?.jurisdiction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_industry_scope),
                dataAgreement?.policy?.industrySector
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_storage_location),
                dataAgreement?.policy?.storageLocation
            )
        )
        var retentionPeriod = "${dataAgreement?.policy?.dataRetentionPeriodDays.toString()} days"
        try {
            var years = floor(
                (dataAgreement?.policy?.dataRetentionPeriodDays?.div(365)?.toDouble()
                    ?: 0) as Double
            )
            retentionPeriod = "$years years"
        } catch (e: Exception) {
        }
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_retention_period),
                retentionPeriod
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_geographic_restriction),
                dataAgreement?.policy?.geographicRestriction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_third_party_disclosure),
                dataAgreement?.policy?.thirdPartyDataSharing.toString()
            )
        )
        list.add(subList)
        subList = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_dpia_summary),
                dataAgreement?.dpiaSummaryUrl
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                context.resources.getString(R.string.privacy_dashboard_data_agreement_policy_dpia_date),
                dataAgreement?.dpiaDate
            )
        )
        list.add(subList)

        return list
    }
    private fun setUpList() {
        val width = PrivacyDashboardDisplayUtils.getScreenWidth()- PrivacyDashboardDisplayUtils.convertDpToPixel(
            60f,
            binding.rvDataAgreementPolicy.context
        )
        adapter = PrivacyDashboardDataAgreementPolicyAdapter(list,width, AdapterType.FRAGMENT)
        binding.rvDataAgreementPolicy.adapter = adapter
    }
}