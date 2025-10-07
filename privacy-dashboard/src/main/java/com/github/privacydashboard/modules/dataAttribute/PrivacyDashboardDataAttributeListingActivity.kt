package com.github.privacydashboard.modules.dataAttribute

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyActivityDataAttributesBinding
import com.github.privacydashboard.events.RefreshList
import com.github.privacydashboard.models.base.attribute.DataAttribute
import com.github.privacydashboard.models.base.attribute.DataAttributesResponse
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.modules.attributeDetail.PrivacyDashboardDataAttributeDetailActivity
import com.github.privacydashboard.modules.attributeDetail.PrivacyDashboardDataAttributeDetailActivity.Companion.EXTRA_TAG_CONSENT
import com.github.privacydashboard.modules.attributeDetail.PrivacyDashboardDataAttributeDetailActivity.Companion.EXTRA_TAG_CONSENTID
import com.github.privacydashboard.modules.attributeDetail.PrivacyDashboardDataAttributeDetailActivity.Companion.EXTRA_TAG_ORGID
import com.github.privacydashboard.modules.attributeDetail.PrivacyDashboardDataAttributeDetailActivity.Companion.EXTRA_TAG_PURPOSEID
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity.Companion.TAG_EXTRA_WEB_TITLE
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity.Companion.TAG_EXTRA_WEB_URL
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardReadMoreOption
import com.github.privacydashboard.utils.PrivacyDashboardStringUtils.toCamelCase
import com.google.gson.Gson
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PrivacyDashboardDataAttributeListingActivity : PrivacyDashboardBaseActivity() {
    private var adapter: PrivacyDashboardDataAttributesAdapter? = null
    private lateinit var binding: PrivacyActivityDataAttributesBinding

    companion object {
        const val TAG_EXTRA_NAME =
            "com.github.privacyDashboard.modules.dataAttribute.PrivacyDashboardDataAttributeListingActivity.name"
        const val TAG_EXTRA_DESCRIPTION =
            "com.github.privacyDashboard.modules.dataAttribute.PrivacyDashboardDataAttributeListingActivity.description"
        const val TAG_DATA_ATTRIBUTES =
            "com.github.privacyDashboard.modules.dataAttribute.PrivacyDashboardDataAttributeListingActivity.dataAttributes"
        var dataAttributesResponse: DataAttributesResponse? = null
    }

    private var mTitle = ""
    private var mDescription = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.privacy_activity_data_attributes)
        binding.clContent.let { setupEdgeToEdge(it) }

        EventBus.getDefault().register(this)
        getIntentData()
        setUpToolBar()
        setUpDescription()
        setUpDataAttributeList()
        initListener()
    }

    private fun setUpDataAttributeList() {
        adapter = PrivacyDashboardDataAttributesAdapter(
            dataAttributesResponse?.consents?.consents ?: ArrayList(),
            object : DataAttributeClickListener {
                override fun onAttributeClick(dataAttribute: DataAttribute?) {
                    val isAttributeLevelConsentEnabled = PrivacyDashboardDataUtils.getBooleanValue(
                        this@PrivacyDashboardDataAttributeListingActivity,
                        PrivacyDashboardDataUtils.EXTRA_TAG_ENABLE_ATTRIBUTE_LEVEL_CONSENT
                    )
                    if (isAttributeLevelConsentEnabled == true) {
                        if (dataAttributesResponse?.consents?.purpose?.lawfulUsage == false
                        ) {
                            val intent: Intent = Intent(
                                this@PrivacyDashboardDataAttributeListingActivity,
                                PrivacyDashboardDataAttributeDetailActivity::class.java
                            )
                            intent.putExtra(
                                EXTRA_TAG_ORGID,
                                dataAttributesResponse?.orgID
                            )
                            intent.putExtra(
                                EXTRA_TAG_CONSENTID,
                                dataAttributesResponse?.consentID
                            )
                            intent.putExtra(
                                EXTRA_TAG_PURPOSEID,
                                dataAttributesResponse?.iD
                            )
                            intent.putExtra(
                                EXTRA_TAG_CONSENT,
                                Gson().toJson(dataAttribute)
                            )
                            startActivity(intent)
                            PrivacyDashboardDataAttributeDetailActivity.mDataAttribute = dataAttribute
                        }
                    }
                }
            })
        binding.rvDataAttributes.layoutManager = LinearLayoutManager(this)
        binding.rvDataAttributes.adapter = adapter
    }

    private fun setUpDescription() {
        if (mDescription.length > 120) {
            val readMoreOption: PrivacyDashboardReadMoreOption = PrivacyDashboardReadMoreOption.Builder(this)
                .textLength(3) // OR
                .textLengthType(PrivacyDashboardReadMoreOption.TYPE_LINE) //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel(resources.getString(R.string.privacy_dashboard_read_more))
                .lessLabel(resources.getString(R.string.privacy_dashboard_read_less))
                .moreLabelColor(
                    ContextCompat.getColor(
                        this,
                        R.color.privacyDashboard_read_more_color
                    )
                )
                .lessLabelColor(
                    ContextCompat.getColor(
                        this,
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

    private fun getIntentData() {
        if (intent.extras != null) {
            mTitle =
                intent.getStringExtra(TAG_EXTRA_NAME) ?: ""
            mDescription =
                intent.getStringExtra(TAG_EXTRA_DESCRIPTION) ?: ""
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
        supportActionBar?.title = toCamelCase(mTitle)
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

    private fun initListener() {
        binding.btnPrivacyPolicy.setOnClickListener {
            val intent = Intent(
                this,
                PrivacyDashboardWebViewActivity::class.java
            )
            intent.putExtra(
                TAG_EXTRA_WEB_URL,
                dataAttributesResponse?.consents?.purpose?.policyURL
            )
            intent.putExtra(
                TAG_EXTRA_WEB_TITLE,
                resources.getString(R.string.privacy_dashboard_web_view_policy)
            )
            startActivity(intent)
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshList?) {
        for (item in dataAttributesResponse?.consents?.consents ?: ArrayList()) {
            if (item?.mId == event?.purposeId) {
                item?.status = event?.status
            }
        }
        adapter?.notifyDataSetChanged()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
        dataAttributesResponse = null
    }
}