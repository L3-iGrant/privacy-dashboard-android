package com.github.privacydashboard.modules.dataSharing

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyActivityDataSharingBinding
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.models.v2.dataAgreement.DataAgreementV2
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.modules.dataAgreementPolicy.PrivacyDashboardDataAgreementPolicyActivity
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity
import com.github.privacydashboard.utils.PrivacyDashboardImageUtils
import com.google.gson.Gson
import kotlin.math.floor

class PrivacyDashboardDataSharingActivity : PrivacyDashboardBaseActivity() {

    private var adapter: PrivacyDashboardDataSharingAttributesAdapter? = null
    private lateinit var binding: PrivacyActivityDataSharingBinding
    private var viewModel: PrivacyDashboardDataSharingViewModel? = null

    companion object {
        const val TAG_EXTRA_OTHER_APPLICATION_NAME =
            "com.github.privacyDashboard.modules.dataSharing.otherApplicationName"
        const val TAG_EXTRA_OTHER_APPLICATION_LOGO =
            "com.github.privacyDashboard.modules.dataSharing.otherApplicationLogo"
        const val TAG_EXTRA_SECONDARY_BUTTON_TEXT =
            "com.github.privacyDashboard.modules.dataSharing.secondaryButtonText"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.privacy_activity_data_sharing)
        viewModel = ViewModelProvider(this)[PrivacyDashboardDataSharingViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.clContent.let { setupEdgeToEdge(it) }
        getIntentData()
        viewModel?.fetchDataAgreementRecord(true, this)
        initListeners()
        setUpToolBar()
    }

    private fun getIntentData() {
        viewModel?.otherApplicationName =
            intent.getStringExtra(TAG_EXTRA_OTHER_APPLICATION_NAME) ?: "Application"
        viewModel?.otherApplicationLogo = intent.getStringExtra(TAG_EXTRA_OTHER_APPLICATION_LOGO)
        val secondaryButtonText = intent.getStringExtra(TAG_EXTRA_SECONDARY_BUTTON_TEXT)
        if (secondaryButtonText != null)
            binding.btnCancel.text = secondaryButtonText
        if (viewModel?.otherApplicationLogo != null) {
            PrivacyDashboardImageUtils.setImage(
                binding.iv3ppLogo,
                viewModel?.otherApplicationLogo,
                R.drawable.privacy_dashboard_default_logo
            )
            binding.cv3ppLogo.visibility = View.VISIBLE
            binding.ivArrows.visibility = View.VISIBLE
        }

        setupTermsOfServicesView()
    }

    private fun setupTermsOfServicesView() {
        val termsOfServiceClick: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(
                    this@PrivacyDashboardDataSharingActivity,
                    PrivacyDashboardWebViewActivity::class.java
                )
                intent.putExtra(
                    PrivacyDashboardWebViewActivity.TAG_EXTRA_WEB_URL,
                    viewModel?.organization?.value?.policyURL
                )
                intent.putExtra(
                    PrivacyDashboardWebViewActivity.TAG_EXTRA_WEB_TITLE,
                    resources.getString(R.string.privacy_dashboard_web_view_policy)
                )
                startActivity(intent)
            }
        }
        val dataAgreementDetails: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(
                    this@PrivacyDashboardDataSharingActivity,
                    PrivacyDashboardDataAgreementPolicyActivity::class.java
                )
                intent.putExtra(
                    PrivacyDashboardDataAgreementPolicyActivity.TAG_EXTRA_ATTRIBUTE_LIST,
                    buildListForDataAgreementPolicy(viewModel?.dataAgreement?.value)
                )
                startActivity(intent)
            }
        }
        setClickableString(
            resources.getString(R.string.privacy_dashboard_data_sharing_sensitive_info_see_data_agreement_details_and_terms_of_services),
            binding.tvTermsOfServices,
            resources.getStringArray(R.array.privacy_dashboard_data_sharing_links),
            arrayOf(dataAgreementDetails, termsOfServiceClick)
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
        supportActionBar?.title = ""
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                sendResult()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initListeners() {
        viewModel?.dataAgreement?.observe(this, Observer { newData ->
            if (newData != null)
                if (newData.methodOfUse != "data_source" || newData.policy?.thirdPartyDataSharing == false) {
                    val resultIntent = Intent()
                    setResult(RESULT_CANCELED, resultIntent)
                    finish()
                } else {
                    binding.tvMainDesc.text = Html.fromHtml(
                        resources.getString(
                            R.string.privacy_dashboard_data_sharing_main_desc,
                            if ((viewModel?.dataAgreement?.value?.methodOfUse
                                    ?: "") == "data_using_service"
                            ) viewModel?.organization?.value?.name else viewModel?.otherApplicationName,
                            if ((viewModel?.dataAgreement?.value?.methodOfUse
                                    ?: "") == "data_using_service"
                            ) viewModel?.otherApplicationName else viewModel?.organization?.value?.name,
                            viewModel?.dataAgreement?.value?.purpose ?: ""
                        )
                    )

                    PrivacyDashboardImageUtils.setImage(
                        binding.ivAppLogo,
                        viewModel?.organization?.value?.logoImageURL ?: "",
                        R.drawable.privacy_dashboard_default_logo
                    )

                    PrivacyDashboardImageUtils.setImage(
                        binding.iv3ppLogo,
                        viewModel?.otherApplicationLogo,
                        R.drawable.privacy_dashboard_default_logo,
                    )

                    binding.tvMakeSureTrust.text = resources.getString(
                        R.string.privacy_dashboard_data_sharing_make_sure_that_you_trust,
                        viewModel?.otherApplicationName
                    )

                    binding.tvDesc.text = resources.getString(
                        R.string.privacy_dashboard_data_sharing_by_authrizing_text,
                        viewModel?.otherApplicationName
                    )

                    adapter = PrivacyDashboardDataSharingAttributesAdapter(
                        newData?.dataAttributes ?: ArrayList()
                    )
                    binding.rvDataAttributes.layoutManager = LinearLayoutManager(this)
                    binding.rvDataAttributes.adapter = adapter
                }
        })

        viewModel?.dataAgreementRecord?.observe(this, Observer { newData ->
            if (newData != null) {
                sendResult()
            }
        })

        binding.btnAuthorize.setOnClickListener {
            viewModel?.authorizeRequest(this)
        }

        binding.btnCancel.setOnClickListener {
            sendResult()
        }
    }

    private fun sendResult() {
        if (viewModel?.dataAgreementRecord?.value != null) {
            val resultIntent = Intent()
            resultIntent.putExtra(
                "data_agreement_record",
                Gson().toJson(viewModel?.dataAgreementRecord?.value)
            )
            setResult(RESULT_OK, resultIntent)
            finish()
        } else {
            val resultIntent = Intent()
            setResult(RESULT_CANCELED, resultIntent)
            finish()
        }
    }

    private fun setClickableString(
        wholeValue: String,
        textView: TextView,
        clickableValue: Array<String>,
        clickableSpans: Array<ClickableSpan?>
    ) {
        val spannableString = SpannableString(wholeValue)
        for (i in clickableValue.indices) {
            val clickableSpan: ClickableSpan? = clickableSpans[i]
            val link = clickableValue[i]
            val startIndexOfLink = wholeValue.indexOf(link)
            spannableString.setSpan(
                clickableSpan,
                startIndexOfLink,
                startIndexOfLink + link.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        textView.highlightColor =
            Color.TRANSPARENT // prevent TextView change background when highlight
        textView.movementMethod = LinkMovementMethod.getInstance()
        textView.setText(spannableString, TextView.BufferType.SPANNABLE)
    }

    private fun buildListForDataAgreementPolicy(dataAgreement: DataAgreementV2?): String {
        var list: ArrayList<ArrayList<DataAgreementPolicyModel>> = ArrayList()
        var subList: ArrayList<DataAgreementPolicyModel> = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_version),
                dataAgreement?.version
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_purpose),
                dataAgreement?.purpose
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_purpose_description),
                dataAgreement?.purposeDescription
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_lawful_basis_of_processing),
                dataAgreement?.lawfulBasis
            )
        )
        list.add(subList)
        subList = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_policy_url),
                dataAgreement?.policy?.url
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_jurisdiction),
                dataAgreement?.policy?.jurisdiction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_industry_scope),
                dataAgreement?.policy?.industrySector
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_storage_location),
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
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_retention_period),
                retentionPeriod
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_geographic_restriction),
                dataAgreement?.policy?.geographicRestriction
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_third_party_disclosure),
                dataAgreement?.policy?.thirdPartyDataSharing.toString()
            )
        )
        list.add(subList)
        subList = ArrayList()
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_dpia_summary),
                dataAgreement?.dpiaSummaryUrl
            )
        )
        subList.add(
            DataAgreementPolicyModel(
                resources.getString(R.string.privacy_dashboard_data_agreement_policy_dpia_date),
                dataAgreement?.dpiaDate
            )
        )
        list.add(subList)

        return Gson().toJson(list)
    }
}