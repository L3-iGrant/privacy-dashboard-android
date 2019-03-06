package io.igrant.igrant_org_sdk.activity;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.CustomViews.CustomTextView;
import io.igrant.igrant_org_sdk.Events.Event;
import io.igrant.igrant_org_sdk.Events.GlobalBus;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.models.Consent.Consent;
import io.igrant.igrant_org_sdk.models.Consent.ConsentStatusRequest;
import io.igrant.igrant_org_sdk.models.Consent.Status;
import io.igrant.igrant_org_sdk.models.ResultResponse;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsentAttributeDetailActivity extends AppCompatActivity {


    public static final String EXTRA_TAG_ORGID = "io.igrant.mobileapp.Activity.ConsentAttributeDetailActivity.orgId";
    public static final String EXTRA_TAG_CONSENTID = "io.igrant.mobileapp.Activity.ConsentAttributeDetailActivity.consentId";
    public static final String EXTRA_TAG_PURPOSEID = "io.igrant.mobileapp.Activity.ConsentAttributeDetailActivity.purposeId";

    public static Consent consent;

    private String mOrgId;
    private String mConsentId;
    private String mPurposeId;
    private LinearLayout llProgressBar;
    private Toolbar toolBarCommon;
    private CustomTextView tvTitle;
    private CustomTextView ctvStatusMessage;
    private CustomTextView tvDays;
    private SeekBar sbDays;
    private LinearLayout llAllow;
    private LinearLayout llDisallow;
    private ImageView ivAllow;
    private ImageView ivDisallow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_attribute_detail);

        initViews();
        if (getIntent().getExtras() != null) {
            mOrgId = getIntent().getStringExtra(EXTRA_TAG_ORGID);
            mConsentId = getIntent().getStringExtra(EXTRA_TAG_CONSENTID);
            mPurposeId = getIntent().getStringExtra(EXTRA_TAG_PURPOSEID);
        }
        setUpToolBar();
        initValues();

        initClick();
    }

    private void initViews() {
        llProgressBar = findViewById(R.id.llProgressBar);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        tvTitle = findViewById(R.id.tvTitle);
        ctvStatusMessage = findViewById(R.id.ctvStatusMessage);
        tvDays = findViewById(R.id.tvDays);
        sbDays = findViewById(R.id.sbDays);
        llAllow = findViewById(R.id.llAllow);
        llDisallow = findViewById(R.id.llDisallow);
        ivAllow = findViewById(R.id.ivAllow);
        ivDisallow = findViewById(R.id.ivDisallow);
    }

    private void initValues() {
        setChecked();
        tvDays.setText(consent.getStatus().getRemaining() + " Days");
        sbDays.setProgress(consent.getStatus().getRemaining());
    }

    private void initClick() {
        llAllow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsentStatusRequest body = new ConsentStatusRequest();
                body.setConsented("Allow");
                AllowConsentService(body);
            }
        });

        llDisallow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsentStatusRequest body = new ConsentStatusRequest();
                body.setConsented("DisAllow");
                AllowConsentService(body);
            }
        });

        sbDays.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ConsentStatusRequest body = new ConsentStatusRequest();
                tvDays.setText(seekBar.getProgress() + " Days");
                ctvStatusMessage.setText(getResources().getString(R.string.txt_attribute_askme_consent_rule));
                body.setDays(seekBar.getProgress());
                AllowConsentService(body);
            }
        });
    }

    private void AllowConsentService(final ConsentStatusRequest body) {
        if (NetWorkUtil.isConnectedToInternet(ConsentAttributeDetailActivity.this)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<ResultResponse> callback = new Callback<ResultResponse>() {
                @Override
                public void onResponse(Call<ResultResponse> call, Response<ResultResponse> response) {

                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        Status status = consent.getStatus();
                        status.setConsented(body.getConsented());
                        status.setRemaining(body.getDays());
                        consent.setStatus(status);

                        try {
                            setChecked();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        GlobalBus.getBus().post(new Event.RefreshList());
                    }
                }

                @Override
                public void onFailure(Call<ResultResponse> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                }
            };

            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String value = (String) ai.metaData.get("io.igrant.igrant_org_sdk.orgid");

            //todo user id
            ApiManager.getApi(DataUtils.getStringValue(ConsentAttributeDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().setAttributeStatus(
                    value,
                    DataUtils.getStringValue(ConsentAttributeDetailActivity.this, DataUtils.EXTRA_TAG_USERID),
                    mConsentId,
                    mPurposeId,
                    consent.getID(),
                    body).enqueue(callback);
        }
    }

    private void setUpToolBar() {
        setSupportActionBar(toolBarCommon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        tvTitle.setText(consent.getDescription());
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

    private void setChecked() {
        try {
            if (consent.getStatus().getConsented().equalsIgnoreCase("Allow")) {
                ivAllow.setVisibility(View.VISIBLE);
                ivDisallow.setVisibility(View.GONE);
                ctvStatusMessage.setText(getResources().getString(R.string.txt_attribute_allow_consent_rule));
            } else if (consent.getStatus().getConsented().equalsIgnoreCase("disAllow")) {
                ivDisallow.setVisibility(View.VISIBLE);
                ivAllow.setVisibility(View.GONE);
                ctvStatusMessage.setText(getResources().getString(R.string.txt_attribute_disallow_consent_rule));
            } else {
                ivDisallow.setVisibility(View.GONE);
                ivAllow.setVisibility(View.GONE);
                ctvStatusMessage.setText(getResources().getString(R.string.txt_attribute_askme_consent_rule));
            }
        } catch (Exception e) {
            ivDisallow.setVisibility(View.GONE);
            ivAllow.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        consent = null;
    }
}
