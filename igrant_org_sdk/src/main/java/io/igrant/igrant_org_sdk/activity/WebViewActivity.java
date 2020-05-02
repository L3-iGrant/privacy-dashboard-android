package io.igrant.igrant_org_sdk.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.R;

public class WebViewActivity extends AppCompatActivity {

    public static final String TAG_EXTRA_WEB_URL = "io.igrant.mobileapp.activity.WebViewActivity.url";
    public static final String TAG_EXTRA_WEB_MTITLE = "io.igrant.mobileapp.activity.WebViewActivity.mTitle";

    private WebView wbPolicy;

    private String mUrl = "";
    private String mTitle = "";
    private ProgressBar progressBar;
    private LinearLayout llProgressBar;
    private Toolbar toolBarCommon;
    private CustomTextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        if (getIntent().getExtras() != null) {
            mUrl = getIntent().getExtras().getString(TAG_EXTRA_WEB_URL);
            mTitle = getIntent().getExtras().getString(TAG_EXTRA_WEB_MTITLE);
        }
        initViews();
        setUpToolBar();

        wbPolicy.getSettings().setJavaScriptEnabled(true);
        wbPolicy.loadUrl(mUrl);
        wbPolicy.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    llProgressBar.setVisibility(View.GONE);
                } else {
                    llProgressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void initViews() {
        wbPolicy = findViewById(R.id.wbPolicy);
        progressBar = findViewById(R.id.progressBar);
        llProgressBar = findViewById(R.id.llProgressBar);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        tvTitle = findViewById(R.id.tvTitle);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolBarCommon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        tvTitle.setText(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
