package com.github.privacydashboard.modules.dataAgreementPolicy

import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacydashboard.databinding.PrivacyActivityDataAgreementPolicyBinding
import com.github.privacydashboard.R
import com.github.privacydashboard.models.DataAgreementPolicyList
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.utils.PrivacyDashboardDisplayUtils
import com.google.gson.Gson

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

    private fun getIntentData() {
        if (intent.extras != null)
            list = Gson().fromJson(
                intent?.getStringExtra(TAG_EXTRA_ATTRIBUTE_LIST),
                DataAgreementPolicyList::class.java
            )
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