package com.github.privacydashboard.modules.dataAgreementPolicy

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacydashboard.databinding.PrivacyActivityDataAgreementPolicyBinding
import com.github.privacydashboard.R
import com.github.privacydashboard.models.DataAgreementPolicyList
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.utils.PrivacyDashboardDisplayUtils
import com.google.gson.Gson
import kotlin.math.floor

class PrivacyDashboardDataAgreementPolicyActivity : PrivacyDashboardBaseActivity() {
    private lateinit var binding: PrivacyActivityDataAgreementPolicyBinding

    companion object {
        const val TAG_EXTRA_ATTRIBUTE_LIST =
            "com.github.privacyDashboard.modules.dataAgreementPolicy.dataAgreementPolicyAttributes"
    }

    var adapter: PrivacyDashboardDataAgreementPolicyAdapter? = null
    var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.privacy_activity_data_agreement_policy)
        getIntentData()
        setUpToolBar()
        setUpList()
    }

    private fun setUpList() {
        val width = PrivacyDashboardDisplayUtils.getScreenWidth()- PrivacyDashboardDisplayUtils.convertDpToPixel(
            60f,
            binding.rvDataAgreementPolicy.context
        )
        adapter = PrivacyDashboardDataAgreementPolicyAdapter(list,width)
        binding.rvDataAgreementPolicy.adapter = adapter
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

    private fun getIntentData() {
        if (intent.extras != null) {
            val data = intent?.getStringExtra(TAG_EXTRA_ATTRIBUTE_LIST)
            val mDataAgreement= Gson().fromJson(data, DataAgreementV2::class.java)
            list = buildListForDataAgreementPolicy(this,mDataAgreement)
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
        supportActionBar?.title =
            resources.getString(R.string.privacy_dashboard_data_agreement_policy_data_agreement)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}