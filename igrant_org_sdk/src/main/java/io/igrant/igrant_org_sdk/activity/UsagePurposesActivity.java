package io.igrant.igrant_org_sdk.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.devs.readmoreoption.ReadMoreOption;

import io.igrant.igrant_org_sdk.customViews.CustomButton;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.adapter.UsageItemAdapter;
import io.igrant.igrant_org_sdk.listener.ConsentAttributeClickListener;
import io.igrant.igrant_org_sdk.models.Consent.Consent;
import io.igrant.igrant_org_sdk.models.Consent.ConsentListResponse;

import static io.igrant.igrant_org_sdk.activity.ConsentAttributeDetailActivity.EXTRA_TAG_CONSENTID;
import static io.igrant.igrant_org_sdk.activity.ConsentAttributeDetailActivity.EXTRA_TAG_ORGID;
import static io.igrant.igrant_org_sdk.activity.ConsentAttributeDetailActivity.EXTRA_TAG_PURPOSEID;
import static io.igrant.igrant_org_sdk.activity.WebViewActivity.TAG_EXTRA_WEB_MTITLE;
import static io.igrant.igrant_org_sdk.activity.WebViewActivity.TAG_EXTRA_WEB_URL;

public class UsagePurposesActivity extends AppCompatActivity {

    public static final String TAG_EXTRA_NAME = "io.igrant.mobileapp.Activity.UsagePurposesActivity.name";
    public static final String TAG_EXTRA_DESCRIPTION = "io.igrant.mobileapp.Activity.UsagePurposesActivity.description";

    private RecyclerView rvUsagePurposes;
    private CustomButton btnPrivacyPolicy;
    private Toolbar toolBarCommon;
    private CustomTextView tvTitle;
    private CustomTextView tvDesc;

    public static ConsentListResponse consentList;
    private UsageItemAdapter adapter;
    private String mTitle = "";
    private String mDescription = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_purposes);

        if (getIntent().getExtras() != null) {
            mTitle = getIntent().getStringExtra(TAG_EXTRA_NAME);
            mDescription = getIntent().getStringExtra(TAG_EXTRA_DESCRIPTION);
        }

        initViews();
        setUpToolBar();
        setUpTextView();
        initListener();
        setUpRecyclerView();
    }

    private void setUpTextView() {


        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(this)
                .textLength(3, ReadMoreOption.TYPE_LINE) // OR
                //.textLength(300, ReadMoreOption.TYPE_CHARACTER)
                .moreLabel(getResources().getString(R.string.txt_org_read_more))
                .lessLabel(getResources().getString(R.string.txt_org_read_less))
                .moreLabelColor(Color.RED)
                .lessLabelColor(Color.BLUE)
                .labelUnderLine(false)
                .expandAnimation(true)
                .build();

        readMoreOption.addReadMoreTo(tvDesc, mDescription);
    }


    private void initViews() {
        rvUsagePurposes = findViewById(R.id.rvUsagePurposes);
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        tvTitle = findViewById(R.id.tvTitle);
        tvDesc = findViewById(R.id.tvDesc);
    }

    private void setUpRecyclerView() {
        adapter = new UsageItemAdapter(consentList.getConsents().getConsents(), new ConsentAttributeClickListener() {
            @Override
            public void onAttributeClick(Consent consent) {
                if (!consentList.getConsents().getPurpose().getLawfulUsage()) {
                    Intent intent = new Intent(UsagePurposesActivity.this, ConsentAttributeDetailActivity.class);
                    intent.putExtra(EXTRA_TAG_ORGID,consentList.getOrgID());
                    intent.putExtra(EXTRA_TAG_CONSENTID,consentList.getConsentID());
                    intent.putExtra(EXTRA_TAG_PURPOSEID,consentList.getID());
                    ConsentAttributeDetailActivity.consent = consent;
                    startActivity(intent);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }
            }
        });
        rvUsagePurposes.setLayoutManager(new LinearLayoutManager(UsagePurposesActivity.this));
        rvUsagePurposes.setAdapter(adapter);
    }

    private void initListener() {
        btnPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UsagePurposesActivity.this, WebViewActivity.class);
                intent.putExtra(TAG_EXTRA_WEB_URL, consentList.getConsents().getPurpose().getPolicyURL());
                intent.putExtra(TAG_EXTRA_WEB_MTITLE,getResources().getString(R.string.txt_policy));
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });
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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        consentList = null;
    }
}
