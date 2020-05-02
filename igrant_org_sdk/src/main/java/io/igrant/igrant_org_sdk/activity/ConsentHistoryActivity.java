package io.igrant.igrant_org_sdk.activity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import com.paginate.Paginate;
import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.adapter.ConsentHistoryAdapter;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.models.ConsentHistory.ConsentHistory;
import io.igrant.igrant_org_sdk.models.ConsentHistory.ConsentHistoryResponse;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

public class ConsentHistoryActivity extends AppCompatActivity {

    public static final String TAG_CONSENT_HISTORY_CAME_FROM = "ConsentHistoryActivity.camefrom";
    public static final String TAG_CONSENT_HISTORY_ORG_ID = "ConsentHistoryActivity.orgId";
    public static final int FROM_PROFILE = 0;
    public static final int FROM_ORG_DETAIL = 1;
    private static final int VIEW_ALL = 0;
    private static final int VIEW_BY_ORG = 1;
    private static final int VIEW_BY_DATE = 2;

    ArrayList<ConsentHistory> consentHistories;
    ConsentHistoryAdapter adapter;
    String startId = "";

    private int mConsentHistoryType;
    private String mOrgId = "";
//    private String mStartDate = "";
//    private String mEndDate = "";
    private boolean isLoading = false;
    private boolean hasLoadedAllItems = false;

    private int cameFrom = FROM_PROFILE;

    private LinearLayout llFilter;
//    private LinearLayout llViewAll;
//    private LinearLayout llSortByOrg;
    private Toolbar toolBarCommon;
    private CustomTextView tvTitle;
    private LinearLayout llProgressBar;
    private RecyclerView rvConsentHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consent_history);


        consentHistories = new ArrayList<>();
        mConsentHistoryType = VIEW_ALL;
        initView();
        setUpToolBar();
//        initListener();
        setupRecyclerView();

        //IF cameFrom is PROFILE then we need to show the filter option
        //else hide filter option
        cameFrom = getIntent().getIntExtra(TAG_CONSENT_HISTORY_CAME_FROM, FROM_PROFILE);
        if (cameFrom == FROM_ORG_DETAIL) {
            llFilter.setVisibility(View.GONE);
            mOrgId = getIntent().getStringExtra(TAG_CONSENT_HISTORY_ORG_ID);
            mConsentHistoryType = VIEW_BY_ORG;
            getConsentHistoryFromServer(true, mOrgId);
        } else {
//            llFilter.setVisibility(View.VISIBLE);
//            getConsentHistoryFromServer(true);
        }

    }

    private void initView() {
        llFilter = findViewById(R.id.llFilter);
//        llViewAll = findViewById(R.id.llViewAll);
//        llSortByOrg = findViewById(R.id.llSortByOrg);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        tvTitle = findViewById(R.id.tvTitle);
        llProgressBar = findViewById(R.id.llProgressBar);
        rvConsentHistory = findViewById(R.id.rvConsentHistory);
    }

//    private void initListener() {

//        llViewAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startId = "";
//                isLoading = false;
//                hasLoadedAllItems = false;
//                mConsentHistoryType = VIEW_ALL;
//                getConsentHistoryFromServer(true);
//            }
//        });

//        llSortByOrg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SortByOrgBottomSheetDialogFragment bottomSheetDialog = SortByOrgBottomSheetDialogFragment.getInstance();
//                bottomSheetDialog.show(getSupportFragmentManager(), "Custom Bottom Sheet");

//}
//        });

//        llSortByDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                SortByDateBottomSheetDialogFragment bottomSheetDialog = SortByDateBottomSheetDialogFragment.getInstance();
//                bottomSheetDialog.show(getSupportFragmentManager(), "Custom Bottom Sheet");
//
//            }
//        });

//    }

    private void setUpToolBar() {
        setSupportActionBar(toolBarCommon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        tvTitle.setText(getResources().getString(R.string.txt_consent_history));
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


//    private void getConsentHistoryFromServer(Boolean showProgress) {
//        if (NetWorkUtil.isConnectedToInternet(ConsentHistoryActivity.this, true)) {
//            isLoading = true;
//            if (showProgress)
//                llProgressBar.setVisibility(View.VISIBLE);
//            Callback<ConsentHistoryResponse> callback = new Callback<ConsentHistoryResponse>() {
//                @Override
//                public void onResponse(Call<ConsentHistoryResponse> call, Response<ConsentHistoryResponse> response) {
//                    isLoading = false;
//                    llProgressBar.setVisibility(View.GONE);
//                    if (response.code() == 200) {
//                        if (response.body().getConsentHistory() != null) {
//                            if (startId.equalsIgnoreCase(""))
//                                consentHistories.clear();
//
//                            consentHistories.addAll(response.body().getConsentHistory());
//
//                            if (consentHistories.size() > 0) {
//                                startId = consentHistories.get(consentHistories.size() - 1).getID();
//                            }
//
//                            rvConsentHistory.setVisibility(consentHistories.size() > 0 ? View.VISIBLE : View.GONE);
////                            adapter.notifyDataSetChanged();
//                        } else {
//                            if (startId.equalsIgnoreCase("")) {
//                                consentHistories.clear();
//                                rvConsentHistory.setVisibility(consentHistories.size() > 0 ? View.VISIBLE : View.GONE);
//                            }
//                        }
//
//                        if (response.body().getConsentHistory() == null) {
//                            hasLoadedAllItems = true;
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ConsentHistoryResponse> call, Throwable t) {
//                    isLoading = false;
//                    llProgressBar.setVisibility(View.GONE);
//                }
//            };
//            ApiManager.getApi(DataUtils.getStringValue(ConsentHistoryActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getConsentHistory(8, startId).enqueue(callback);
//        }
//    }

    private void getConsentHistoryFromServer(Boolean showProgress, String orgId) {
        if (NetWorkUtil.isConnectedToInternet(ConsentHistoryActivity.this, true)) {
            isLoading = true;
            if (showProgress)
                llProgressBar.setVisibility(View.VISIBLE);
            Callback<ConsentHistoryResponse> callback = new Callback<ConsentHistoryResponse>() {
                @Override
                public void onResponse(Call<ConsentHistoryResponse> call, Response<ConsentHistoryResponse> response) {
                    isLoading = false;
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getConsentHistory() != null) {
                            if (startId.equalsIgnoreCase(""))
                                consentHistories.clear();

                            consentHistories.addAll(response.body().getConsentHistory());
                            rvConsentHistory.setVisibility(consentHistories.size() > 0 ? View.VISIBLE : View.GONE);

                            if (consentHistories.size() > 0) {
                                startId = consentHistories.get(consentHistories.size() - 1).getID();
                            }

//                            adapter.notifyDataSetChanged();
                        } else {
                            if (startId.equalsIgnoreCase("")) {
                                consentHistories.clear();
                                rvConsentHistory.setVisibility(consentHistories.size() > 0 ? View.VISIBLE : View.GONE);
                            }
                        }

                        if (response.body().getConsentHistory() == null) {
                            hasLoadedAllItems = true;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ConsentHistoryResponse> call, Throwable t) {
                    isLoading = false;
                    llProgressBar.setVisibility(View.GONE);
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(ConsentHistoryActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getConsentHistory(8, orgId, startId).enqueue(callback);
        }
    }

//    private void getConsentHistoryFromServer(Boolean showProgress, String startdate, String endDate) {
//        if (NetWorkUtil.isConnectedToInternet(ConsentHistoryActivity.this, true)) {
//            isLoading = true;
//            if (showProgress)
//                llProgressBar.setVisibility(View.VISIBLE);
//            Callback<ConsentHistoryResponse> callback = new Callback<ConsentHistoryResponse>() {
//                @Override
//                public void onResponse(Call<ConsentHistoryResponse> call, Response<ConsentHistoryResponse> response) {
//                    isLoading = false;
//                    llProgressBar.setVisibility(View.GONE);
//                    if (response.code() == 200) {
//                        if (response.body().getConsentHistory() != null) {
//                            if (startId.equalsIgnoreCase(""))
//                                consentHistories.clear();
//
//                            consentHistories.addAll(response.body().getConsentHistory());
//                            rvConsentHistory.setVisibility(consentHistories.size() > 0 ? View.VISIBLE : View.GONE);
//
//                            if (consentHistories.size() > 0) {
//                                startId = consentHistories.get(consentHistories.size() - 1).getID();
//                            }
//
//                        } else {
//                            if (startId.equalsIgnoreCase("")) {
//                                consentHistories.clear();
//                                rvConsentHistory.setVisibility(consentHistories.size() > 0 ? View.VISIBLE : View.GONE);
//                            }
//                        }
//
//                        if (response.body().getConsentHistory() == null) {
//                            hasLoadedAllItems = true;
//                        }
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<ConsentHistoryResponse> call, Throwable t) {
//                    isLoading = false;
//                    llProgressBar.setVisibility(View.GONE);
//                }
//            };
//            ApiManager.getApi().getService().getConsentHistory(8, startdate, endDate, startId).enqueue(callback);
//        }
//    }

    private void setupRecyclerView() {
        adapter = new ConsentHistoryAdapter(consentHistories);
        rvConsentHistory.setLayoutManager(new LinearLayoutManager(ConsentHistoryActivity.this));
        rvConsentHistory.setAdapter(adapter);

        Paginate.with(rvConsentHistory, new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                switch (mConsentHistoryType) {
                    case VIEW_ALL:
//                        getConsentHistoryFromServer(false);
                        break;
                    case VIEW_BY_ORG:
                        getConsentHistoryFromServer(false, mOrgId);
                        break;
                    case VIEW_BY_DATE:
//                        getConsentHistoryFromServer(false, mStartDate, mEndDate);
                        break;
                    default:
//                        getConsentHistoryFromServer(false);
                        break;
                }
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return hasLoadedAllItems;
            }
        }).setLoadingTriggerThreshold(1)
                .addLoadingListItem(true)
                .build();
    }

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onOrganizationSelect(SortByOrgEvent org) {
//        /* Do something */
//        startId = "";
//        isLoading = false;
//        hasLoadedAllItems = false;
//        mOrgId = org.organization.getOrgID();
//        mConsentHistoryType = VIEW_BY_ORG;
//        getConsentHistoryFromServer(true, org.organization.getOrgID());
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onDateSelect(SortByDateEvent org) {
//        /* Do something */
//        startId = "";
//        isLoading = false;
//        hasLoadedAllItems = false;
//        mStartDate = org.startDate;
//        mEndDate = org.endDate;
//        mConsentHistoryType = VIEW_BY_DATE;
//        getConsentHistoryFromServer(true, org.startDate, org.endDate);
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        EventBus.getDefault().unregister(this);
//    }
}
