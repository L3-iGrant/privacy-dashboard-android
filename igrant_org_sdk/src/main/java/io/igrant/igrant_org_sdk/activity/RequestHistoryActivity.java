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

import java.util.ArrayList;

import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.adapter.RequestHistoryAdapter;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.listener.OnUserRequestClickListener;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequest;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestHistoryResponse;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestHistoryActivity extends AppCompatActivity {

    public static final String TAG_REQUEST_HISTORY_ORG_ID = "ConsentHistoryActivity.orgId";
    public static final String TAG_REQUEST_HISTORY_ORG_NAME = "io.igrant.mobileapp.activity.RequestHistoryActivity.orgName";

    private RequestHistoryAdapter adapter;
    private boolean isLoading = false;
    private boolean hasLoadedAllItems = false;
    private ArrayList<DataRequest> requestHistories = new ArrayList<>();
    private String mOrgId = "";
    private String startId = "";

    private LinearLayout llProgressBar;
    private CustomTextView tvTitle;
    private Toolbar toolBarCommon;
    private RecyclerView rvRequestHistory;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_history);
        mOrgId = getIntent().getStringExtra(TAG_REQUEST_HISTORY_ORG_ID);
        initViews();
        setUpToolBar();
        setupRecyclerView();
        getRequestHistory(true);
    }

    private void initViews() {
        llProgressBar = findViewById(R.id.llProgressBar);
        tvTitle = findViewById(R.id.tvTitle);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        rvRequestHistory = findViewById(R.id.rvRequestHistory);
    }

    private void getRequestHistory(Boolean showProgress) {
        if (NetWorkUtil.isConnectedToInternet(RequestHistoryActivity.this)) {
            isLoading = true;
            if (showProgress)
                llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestHistoryResponse> callback = new Callback<DataRequestHistoryResponse>() {
                @Override
                public void onResponse(Call<DataRequestHistoryResponse> call, Response<DataRequestHistoryResponse> response) {
                    isLoading = false;
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getDataRequests() != null) {
                            if (startId.equalsIgnoreCase(""))
                                requestHistories.clear();

                            requestHistories.addAll(response.body().getDataRequests());

                            if (requestHistories.size() > 0) {
                                startId = requestHistories.get(requestHistories.size() - 1).getID();
                            }

                            rvRequestHistory.setVisibility(requestHistories.size() > 0 ? View.VISIBLE : View.GONE);
                        } else {
                            if (startId.equalsIgnoreCase("")) {
                                requestHistories.clear();
                                rvRequestHistory.setVisibility(requestHistories.size() > 0 ? View.VISIBLE : View.GONE);
                            }
                        }

                        if (response.body().getDataRequests() == null) {
                            hasLoadedAllItems = true;
                        }
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<DataRequestHistoryResponse> call, Throwable t) {
                    isLoading = false;
                    llProgressBar.setVisibility(View.GONE);
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(RequestHistoryActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getOrgRequestStatus(mOrgId, startId).enqueue(callback);
        }
    }

    private void setUpToolBar() {
        setSupportActionBar(toolBarCommon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        tvTitle.setText(getResources().getString(R.string.txt_more_request_history));
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

    private void setupRecyclerView() {
        adapter = new RequestHistoryAdapter(requestHistories, new OnUserRequestClickListener() {
            @Override
            public void onRequestClick(DataRequest request) {

            }

            @Override
            public void onRequestCancel(DataRequest request) {

            }
        });
        rvRequestHistory.setLayoutManager(new LinearLayoutManager(RequestHistoryActivity.this));
        rvRequestHistory.setAdapter(adapter);

        Paginate.with(rvRequestHistory, new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                getRequestHistory(false);
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
}
