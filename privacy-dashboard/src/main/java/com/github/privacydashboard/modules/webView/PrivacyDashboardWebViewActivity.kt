package com.github.privacydashboard.modules.webView

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyActivityWebViewBinding
import com.github.privacydashboard.modules.PrivacyDashboardBaseActivity
import com.github.privacydashboard.utils.PrivacyDashboardWebViewPolicy

class PrivacyDashboardWebViewActivity : PrivacyDashboardBaseActivity() {

    private lateinit var binding: PrivacyActivityWebViewBinding

    companion object {
        const val TAG_EXTRA_WEB_URL =
            "com.github.privacyDashboard.modules.webView.PrivacyDashboardWebViewActivity.webUrl"
        const val TAG_EXTRA_WEB_TITLE =
            "com.github.privacyDashboard.modules.webView.PrivacyDashboardWebViewActivity.title"
    }

    private var mTitle = ""
    private var mWebUrl = ""

    // This screen shows only data from the intent.
    override val needsCredentials = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.privacy_activity_web_view)
        binding.clContent.let { setupEdgeToEdge(it) }
        getIntentData()
        setUpToolBar()
        setUpWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {
        PrivacyDashboardWebViewPolicy.configure(binding.webView)
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val isRedirect = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && request?.isRedirect == true
                return PrivacyDashboardWebViewPolicy.shouldOverrideUrlLoading(
                    this@PrivacyDashboardWebViewActivity, mWebUrl, request?.url?.toString(), isRedirect
                )
            }
        }
        binding.webView.loadUrl(mWebUrl)
        binding.webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                binding.progressBar.progress = progress
                if (progress == 100) {
                    binding.llProgressBar.visibility = View.GONE
                } else {
                    binding.llProgressBar.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun getIntentData() {
        if (intent.extras != null) {
            mWebUrl =
                intent.extras?.getString(TAG_EXTRA_WEB_URL) ?: ""
            mTitle =
                intent.extras?.getString(TAG_EXTRA_WEB_TITLE) ?: ""
        }
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolBarCommon)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(
            ContextCompat.getDrawable(
                this,
                R.drawable.ic_back_black_pad
            )
        )
        supportActionBar?.title = mTitle
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
}