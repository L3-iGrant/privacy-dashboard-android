package io.igrant.igrant_org_sdk;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.Events.Event;
import io.igrant.igrant_org_sdk.activity.*;
import io.igrant.igrant_org_sdk.adapter.UsagePurposesAdapter;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.customViews.MySpannable;
import io.igrant.igrant_org_sdk.listener.UsagePurposeClickListener;
import io.igrant.igrant_org_sdk.models.Consent.ConsentListResponse;
import io.igrant.igrant_org_sdk.models.Consent.ConsentStatusRequest;
import io.igrant.igrant_org_sdk.models.Consent.UpdateConsentStatusResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestStatus;
import io.igrant.igrant_org_sdk.models.Organization;
import io.igrant.igrant_org_sdk.models.Organizations.OrganizationDetailResponse;
import io.igrant.igrant_org_sdk.models.Organizations.PurposeConsent;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.ImageUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

import static io.igrant.igrant_org_sdk.activity.ConsentHistoryActivity.*;
import static io.igrant.igrant_org_sdk.activity.DataRequestStatusActivity.TAG_DAT_REQUEST_ORG_ID;
import static io.igrant.igrant_org_sdk.activity.DataRequestStatusActivity.TAG_DAT_REQUEST_TYPE;
import static io.igrant.igrant_org_sdk.activity.RequestHistoryActivity.TAG_REQUEST_HISTORY_ORG_ID;
import static io.igrant.igrant_org_sdk.activity.UsagePurposesActivity.TAG_EXTRA_DESCRIPTION;
import static io.igrant.igrant_org_sdk.activity.WebViewActivity.TAG_EXTRA_WEB_MTITLE;
import static io.igrant.igrant_org_sdk.activity.WebViewActivity.TAG_EXTRA_WEB_URL;

public class OrganizationDetailActivity extends AppCompatActivity {

    private RecyclerView rvUsagePurposes;
    private LinearLayout llProgressBar;
    private Toolbar toolBarCommon;
    private ImageView ivLogo;
    private ImageView ivCoverUrl;
    private CustomTextView ctvName;
    private CustomTextView ctvLocation;
    private CustomTextView ctvDescription;
    private LinearLayout llDescription;

    public static Organization organization;
    private String consentId;
    private ArrayList<PurposeConsent> purposeConsents = new ArrayList<>();
    private UsagePurposesAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organization_detail);
        initViews();
        setUpToolBar();
        setUprecyclerView();
        initView(organization);
        getOrganizationDetail();
    }

    private void initViews() {
        rvUsagePurposes = findViewById(R.id.rvUsagePurposes);
        llProgressBar = findViewById(R.id.llProgressBar);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        ivLogo = findViewById(R.id.ivLogo);
        ivCoverUrl = findViewById(R.id.ivCoverUrl);
        ctvName = findViewById(R.id.ctvName);
        ctvLocation = findViewById(R.id.ctvLocation);
        ctvDescription = findViewById(R.id.ctvDescription);
        llDescription = findViewById(R.id.llDescription);
    }

    private void setUprecyclerView() {

        rvUsagePurposes.setLayoutManager(new LinearLayoutManager(OrganizationDetailActivity.this));
        rvUsagePurposes.setNestedScrollingEnabled(false);

    }

    private void setOverallStatus(PurposeConsent consent, Boolean isChecked) {
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this)) {
            ConsentStatusRequest body = new ConsentStatusRequest();
            body.setConsented(isChecked ? "Allow" : "DisAllow");
            Callback<UpdateConsentStatusResponse> callback = new Callback<UpdateConsentStatusResponse>() {
                @Override
                public void onResponse(Call<UpdateConsentStatusResponse> call, Response<UpdateConsentStatusResponse> response) {
                    if (response.code() == 200) {
                        getOrganizationDetail();
                    }
                }

                @Override
                public void onFailure(Call<UpdateConsentStatusResponse> call, Throwable t) {

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
            ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().setOverallStatus(value, DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_USERID), consentId, consent.getPurpose().getID(), body).enqueue(callback);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    private void getConsentList(final PurposeConsent consent) {
        llProgressBar.setVisibility(View.VISIBLE);
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this)) {
            Callback<ConsentListResponse> callback = new Callback<ConsentListResponse>() {
                @Override
                public void onResponse(Call<ConsentListResponse> call, Response<ConsentListResponse> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        Intent intent = new Intent(OrganizationDetailActivity.this, UsagePurposesActivity.class);
                        UsagePurposesActivity.consentList = response.body();
                        intent.putExtra(TAG_EXTRA_DESCRIPTION, consent.getPurpose().getDescription());
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                }

                @Override
                public void onFailure(Call<ConsentListResponse> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                }
            };
            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String value = (String) ai.metaData.get("io.igrant.igrant_org_sdk.orgid");

            try {
                ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getConsentList(value, DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_USERID), consentId, consent.getPurpose().getID()).enqueue(callback);
            } catch (Exception e) {
                llProgressBar.setVisibility(View.GONE);
                e.printStackTrace();
            }
        }
    }

    private void initView(Organization mOrg) {

        try {
            ImageUtils.setImage(ivLogo, mOrg.getLogoImageURL(), R.drawable.ic_back_bg);
            ImageUtils.setImage(ivCoverUrl, mOrg.getCoverImageURL(), R.drawable.ic_back_bg);
            ctvName.setText(mOrg.getName());
            ctvLocation.setText(mOrg.getLocation());

            if (mOrg.getDescription() != null || !mOrg.getDescription().equals("")) {
                ctvDescription.setText(mOrg.getDescription());
                makeTextViewResizable(ctvDescription, 3, getResources().getString(R.string.txt_org_read_more), true);
            } else {
                llDescription.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getOrganizationDetail() {
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<OrganizationDetailResponse> callback = new Callback<OrganizationDetailResponse>() {
                @Override
                public void onResponse(Call<OrganizationDetailResponse> call, Response<OrganizationDetailResponse> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {

                        try {
                            purposeConsents.clear();
                            purposeConsents.addAll(response.body().getPurposeConsents());


                            adapter = new UsagePurposesAdapter(purposeConsents, new UsagePurposeClickListener() {
                                @Override
                                public void onItemClick(PurposeConsent consent) {
                                    getConsentList(consent);
                                }

                                @Override
                                public void onSetStatus(PurposeConsent consent, Boolean isChecked) {
                                    setOverallStatus(consent, isChecked);
                                }
                            });
                            rvUsagePurposes.setAdapter(adapter);

                            consentId = response.body().getConsentID();
                            organization = response.body().getOrganization();
                            initView(response.body().getOrganization());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                }

                @Override
                public void onFailure(Call<OrganizationDetailResponse> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                }
            };

            ApplicationInfo ai = null;
            try {
                ai = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            String value = (String) ai.metaData.get("io.igrant.igrant_org_sdk.orgid");

            ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getOrganizationDetail(value).enqueue(callback);
        }
    }

    private void setUpToolBar() {
        setSupportActionBar(toolBarCommon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_bg));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        } else if (item.getItemId() == R.id.menu_more) {
            PopupMenu popup = new PopupMenu(OrganizationDetailActivity.this, findViewById(R.id.menu_more));
            popup.getMenuInflater()
                    .inflate(R.menu.menu_more_items, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.action_download_data) {
                        downloadDataRequestStatus();
                    } else if (item.getItemId() == R.id.action_webpage) {
                        Intent intent = new Intent(OrganizationDetailActivity.this, WebViewActivity.class);
//                                try {
                        intent.putExtra(TAG_EXTRA_WEB_URL, organization.getPolicyURL());
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
                        intent.putExtra(TAG_EXTRA_WEB_MTITLE, getResources().getString(R.string.txt_profile_privacy_policy));
                        startActivity(intent);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    } else if (item.getItemId() == R.id.action_forgot_me) {
                        deleteDataRequestStatus();
                    } else if (item.getItemId() == R.id.action_request_history) {
                        Intent orgHistory = new Intent(OrganizationDetailActivity.this, RequestHistoryActivity.class);
                        orgHistory.putExtra(TAG_REQUEST_HISTORY_ORG_ID, organization.getID());
                        startActivity(orgHistory);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    } else if (item.getItemId() == R.id.action_consent_history) {
                        Intent consentHistory = new Intent(OrganizationDetailActivity.this, ConsentHistoryActivity.class);
                        consentHistory.putExtra(TAG_CONSENT_HISTORY_CAME_FROM, FROM_ORG_DETAIL);
                        consentHistory.putExtra(TAG_CONSENT_HISTORY_ORG_ID, organization.getID());
                        startActivity(consentHistory);
                        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    }
                    return true;
                }
            });

            popup.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadDataRequestStatus() {
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestStatus> callback = new Callback<DataRequestStatus>() {
                @Override
                public void onResponse(Call<DataRequestStatus> call, Response<DataRequestStatus> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getRequestOngoing()) {
                            gotToStatusPage(true);
                        } else {
                            dataDownloadRequest();
                        }
                    } else {
                        Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DataRequestStatus> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getDataDownloadStatus(organization.getID()).enqueue(callback);
        }
    }

    private void dataDownloadRequest() {
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<Void> callback = new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        gotToStatusPage(true);
                    } else {
                        Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();

                }
            };
            ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().dataDownloadRequest(organization.getID()).enqueue(callback);
        }
    }

    private void deleteDataRequestStatus() {
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestStatus> callback = new Callback<DataRequestStatus>() {
                @Override
                public void onResponse(Call<DataRequestStatus> call, Response<DataRequestStatus> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getRequestOngoing()) {
                            gotToStatusPage(false);
                        } else {
                            dataDeleteRequest();
                        }
                    } else {
                        Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DataRequestStatus> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getDataDeleteStatus(organization.getID()).enqueue(callback);
        }
    }

    private void dataDeleteRequest() {
        if (NetWorkUtil.isConnectedToInternet(OrganizationDetailActivity.this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<Void> callback = new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        gotToStatusPage(false);
                    } else {
                        Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(OrganizationDetailActivity.this, getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(OrganizationDetailActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().dataDeleteRequest(organization.getID()).enqueue(callback);
        }
    }

    private void gotToStatusPage(boolean isDownloadRequest) {
        Intent intent = new Intent(OrganizationDetailActivity.this, DataRequestStatusActivity.class);
        intent.putExtra(TAG_DAT_REQUEST_TYPE, isDownloadRequest);
        intent.putExtra(TAG_DAT_REQUEST_ORG_ID, organization.getID());
        startActivity(intent);
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                     final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, getResources().getString(R.string.txt_org_read_less), false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, getResources().getString(R.string.txt_org_read_more), true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        organization = null;
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe
    public void onEvent(Event.RefreshList event) {
        getOrganizationDetail();
    }
}
