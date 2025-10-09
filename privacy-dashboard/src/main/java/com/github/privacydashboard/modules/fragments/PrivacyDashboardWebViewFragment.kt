package com.github.privacydashboard.modules.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.FragmentPrivacyWebViewBinding
import com.github.privacydashboard.models.base.attribute.DataAttributesResponse
import com.github.privacydashboard.utils.PrivacyDashboardStringUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.gson.Gson

class PrivacyDashboardWebViewFragment : BottomSheetDialogFragment() {
    companion object {
        private const val TAG_EXTRA_WEB_URL = "TAG_EXTRA_WEB_URL"
        private const val TAG_EXTRA_WEB_TITLE = "TAG_EXTRA_WEB_TITLE"

        fun newInstance(url: String?, title: String?): PrivacyDashboardWebViewFragment {
            val fragment = PrivacyDashboardWebViewFragment()
            val args = Bundle()
            args.putString(TAG_EXTRA_WEB_URL, url)
            args.putString(TAG_EXTRA_WEB_TITLE, title)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var binding: FragmentPrivacyWebViewBinding
    private var mTitle = ""
    private var mWebUrl = ""
    private lateinit var tvName: TextView
    private lateinit var ivClose: ImageView
    override fun getTheme(): Int = R.style.RoundedBottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_privacy_web_view, container, false)
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

        setUpWebView()
        initListener()
        tvName.text =
            PrivacyDashboardStringUtils.toCamelCase(mTitle)

    }

    private fun initListener() {
        ivClose.setOnClickListener {
            dismiss()
        }
    }

    private fun getIntentData() {
        mWebUrl = arguments?.getString(TAG_EXTRA_WEB_URL) ?: ""
        mTitle = arguments?.getString(TAG_EXTRA_WEB_TITLE) ?: ""
    }
    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpWebView() {

        val webView = binding.webView

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            loadWithOverviewMode = true
            useWideViewPort = true
        }

        WebView.setWebContentsDebuggingEnabled(true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url.toString()
                view?.loadUrl(url)
                return true // Important to prevent default handling (i.e., opening in browser)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                binding.llProgressBar.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                binding.llProgressBar.postDelayed({
                    binding.llProgressBar.visibility = View.GONE
                }, 500)
            }

            override fun onReceivedError(
                view: WebView?, request: WebResourceRequest?, error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onConsoleMessage(message: ConsoleMessage?): Boolean {
                return true
            }

            override fun onProgressChanged(view: WebView, progress: Int) {
                binding.progressBar.progress = progress
            }
        }

        if (mWebUrl.isNotEmpty()) {
            webView.loadUrl(mWebUrl)
        }
    }

}