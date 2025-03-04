package com.github.privacydashboard.modules.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.github.privacydashboard.ConsentChangeListener
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyActivityDashboardBinding
import com.github.privacydashboard.events.RefreshHome
import com.github.privacydashboard.models.PurposeConsent
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.modules.logging.PrivacyDashboardLoggingActivity
import com.github.privacydashboard.modules.logging.PrivacyDashboardLoggingActivity.Companion.TAG_EXTRA_ORG_ID
import com.github.privacydashboard.modules.userRequest.PrivacyDashboardUserRequestActivity
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity.Companion.TAG_EXTRA_WEB_TITLE
import com.github.privacydashboard.modules.webView.PrivacyDashboardWebViewActivity.Companion.TAG_EXTRA_WEB_URL
import com.github.privacydashboard.utils.PrivacyDashboardDataUtils
import com.github.privacydashboard.utils.PrivacyDashboardImageUtils
import com.github.privacydashboard.utils.PrivacyDashboardReadMoreOption
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class PrivacyDashboardActivity : PrivacyDashboardBaseActivity() {

    private lateinit var binding: PrivacyActivityDashboardBinding

    private var adapter: UsagePurposeAdapter? = null

    private var viewModel: PrivacyDashboardDashboardViewModel? = null

    companion object{
        var consentChange: ConsentChangeListener? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.privacy_activity_dashboard)
        viewModel = ViewModelProvider(this)[PrivacyDashboardDashboardViewModel::class.java]
        binding.viewModel = viewModel;
        binding.lifecycleOwner = this;
        EventBus.getDefault().register(this)
        setUpToolBar()
        initView()
        viewModel?.getOrganizationDetail(true, this)
        viewModel?.getDataAgreements(true, this)
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolBarCommon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_back_bg
            )
        )
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_more, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        } else if (item.itemId == R.id.menu_more) {
            val view = findViewById<View>(R.id.menu_more)
            showPopupMenu(view)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        val isUserRequestAvailable = PrivacyDashboardDataUtils.getBooleanValue(
            this,
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
                    val intent = Intent(
                        this,
                        PrivacyDashboardWebViewActivity::class.java
                    )
                    intent.putExtra(
                        TAG_EXTRA_WEB_URL,
                        viewModel?.organization?.value?.policyURL ?: ""
                    )
                    intent.putExtra(
                        TAG_EXTRA_WEB_TITLE,
                        resources.getString(R.string.privacy_dashboard_web_view_privacy_policy)
                    )
                    startActivity(intent)
                    true
                }
                R.id.action_consent_history -> {
                    val consentHistory = Intent(
                        this,
                        PrivacyDashboardLoggingActivity::class.java
                    )
                    consentHistory.putExtra(
                        TAG_EXTRA_ORG_ID,
                        viewModel?.organization?.value?.iD
                    )
                    startActivity(consentHistory)
                    true
                }
                R.id.action_request -> {
                    val userOrgRequestIntent = Intent(
                        this,
                        PrivacyDashboardUserRequestActivity::class.java
                    )
                    userOrgRequestIntent.putExtra(
                        PrivacyDashboardUserRequestActivity.EXTRA_TAG_USER_REQUEST_ORGID,
                        viewModel?.organization?.value?.iD
                    )
                    userOrgRequestIntent.putExtra(
                        PrivacyDashboardUserRequestActivity.EXTRA_TAG_USER_REQUEST_ORG_NAME,
                        viewModel?.organization?.value?.name
                    )
                    startActivity(userOrgRequestIntent)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun initView() {
        viewModel?.organization?.observe(this, Observer { newData ->
            try {
                PrivacyDashboardImageUtils.setImage(
                    binding.ivLogo,
                    newData?.logoImageURL ?: "",
                    R.drawable.privacy_dashboard_default_logo
                )
                PrivacyDashboardImageUtils.setImage(
                    binding.ivCoverUrl,
                    newData?.coverImageURL ?: "",
                    R.drawable.privacy_dashboard_default_cover
                )
                if (viewModel?.organization?.value?.description != null || !viewModel?.organization?.value?.description.equals(
                        ""
                    )
                ) {
                    if ((viewModel?.organization?.value?.description?.length ?: 0) > 120) {
                        val readMoreOption: PrivacyDashboardReadMoreOption =
                            PrivacyDashboardReadMoreOption.Builder(this)
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
        })

        viewModel?.purposeConsents?.observe(this, Observer { newData ->
            try {
                if (newData != null && newData.isNotEmpty()) {
                    adapter = UsagePurposeAdapter(
                        viewModel?.purposeConsents?.value ?: ArrayList(),
                        object : UsagePurposeClickListener {
                            override fun onItemClick(consent: PurposeConsent?) {
                                viewModel?.getConsentList(consent, this@PrivacyDashboardActivity)
                            }

                            override fun onSetStatus(
                                consent: PurposeConsent?,
                                isChecked: Boolean?
                            ) {
                                viewModel?.setOverallStatus(
                                    consent,
                                    isChecked,
                                    this@PrivacyDashboardActivity,
                                    consentChange
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
        })

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: RefreshHome?) {
        viewModel?.getDataAgreements(false, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }
}