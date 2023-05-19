package io.igrant.igrant_org_sdk.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet.Builder;
import com.paginate.Paginate;

import java.util.ArrayList;

import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.OrganizationDetailActivity;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.adapter.RequestHistoryAdapter;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.listener.OnUserRequestClickListener;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequest;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestGenResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestHistoryResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestStatus;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.MessageUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static io.igrant.igrant_org_sdk.activity.RequestHistoryActivity.TAG_REQUEST_HISTORY_ORG_ID;
import static io.igrant.igrant_org_sdk.activity.RequestHistoryActivity.TAG_REQUEST_HISTORY_ORG_NAME;

public class UserOrgRequestActivity extends AppCompatActivity {

    private String mOrgId = "";
    private String mOrgName = "";
    private CustomTextView ctvNewRequest;
    private LinearLayout llProgressBar;
    private RelativeLayout rlRoot;
    private RecyclerView rvRequestHistory;
    private Toolbar toolbar;
    private CustomTextView tvTitle;

    private RequestHistoryAdapter adapter;

    private ArrayList<DataRequest> requestHistories = new ArrayList<>();

    private Boolean isLoadingData = false;
    private Boolean hasLoadedAllItems = false;

    private String startId = "";

    private Boolean isDownloadRequestOngoing = false;
    private Boolean isDeleteRequestOngoing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_org_request);
        mOrgId = getIntent().getStringExtra(TAG_REQUEST_HISTORY_ORG_ID);
        mOrgName = getIntent().getStringExtra(TAG_REQUEST_HISTORY_ORG_NAME);
        initViews();
        initListeners();
        setUpToolBar();
        setUpList();
        getRequestHistory(true);
    }

    private void setUpToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        tvTitle.setText(getResources().getString(R.string.txt_user_requests));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initListeners() {
        ctvNewRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Builder(UserOrgRequestActivity.this, R.style.BottomSheet_Dialog)
                        .sheet(R.menu.menu_new_requests)
                        .listener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == R.id.action_download_data) {
                                    downloadDataRequestStatus();
                                } else if (which == R.id.action_forgot_me) {
                                    deleteDataRequestStatus();
                                }
                            }
                        }).show();
            }
        });
    }

    private void downloadDataRequestStatus() {
        if (NetWorkUtil.isConnectedToInternet(this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestStatus> callback = new Callback<DataRequestStatus>() {
                @Override
                public void onResponse(Call<DataRequestStatus> call, Response<DataRequestStatus> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getRequestOngoing()) {
                            gotToStatusPage(true);
                        } else {
                            confirmationAlert(false);
                        }
                    } else {
                        MessageUtils.showSnackbar(
                                rlRoot,
                                getResources().getString(R.string.err_unexpected)
                        );
                    }
                }

                @Override
                public void onFailure(Call<DataRequestStatus> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    MessageUtils.showSnackbar(rlRoot, getResources().getString(R.string.err_unexpected));
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(
                    this,
                    DataUtils.EXTRA_TAG_TOKEN
            ),
                    DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)).getService().getDataDownloadStatus(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),OrganizationDetailActivity.organization.getID()).enqueue(callback);
        }
    }

    private void gotToStatusPage(Boolean isDownloadRequest) {
        Intent intent = new Intent(this, DataRequestStatusActivity.class);
        intent.putExtra(DataRequestStatusActivity.TAG_DAT_REQUEST_TYPE, isDownloadRequest);
        intent.putExtra(DataRequestStatusActivity.TAG_DAT_REQUEST_ORG_ID, mOrgId);
        startActivity(intent);
    }

    private void confirmationAlert(final Boolean isDelete) {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogCustom))
                .setTitle(getResources().getString(R.string.txt_gen_confirmation))
                .setMessage(
                        getResources().getString(
                                R.string.txt_data_request_confirmation_message,
                                isDelete ? getResources().getString(R.string.txt_data_delete) : getResources().getString(
                                        R.string.txt_data_delete
                                ),
                                mOrgName
                        )
                ).setPositiveButton(R.string.txt_register_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isDelete) {
                    dataDeleteRequest();
                } else {
                    dataDownloadRequest();
                }
            }
        }).setNegativeButton(R.string.txt_register_confirm, null).show();


    }

    private void dataDownloadRequest() {
        if (NetWorkUtil.isConnectedToInternet(this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<Void> callback = new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        refreshData();
                    } else {
                        MessageUtils.showSnackbar(
                                rlRoot,
                                getResources().getString(R.string.err_unexpected)
                        );
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    MessageUtils.showSnackbar(rlRoot, getResources().getString(R.string.err_unexpected));
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(
                    this,
                    DataUtils.EXTRA_TAG_TOKEN
            ),
                            DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)).getService().dataDownloadRequest(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),OrganizationDetailActivity.organization.getID())
                    .enqueue(callback);
        }
    }

    private void refreshData() {
        requestHistories.clear();
        startId = "";
        getRequestHistory(true);
    }

    private void deleteDataRequestStatus() {
        if (NetWorkUtil.isConnectedToInternet(this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestStatus> callback = new Callback<DataRequestStatus>() {
                @Override
                public void onResponse(Call<DataRequestStatus> call, Response<DataRequestStatus> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getRequestOngoing()) {
                            gotToStatusPage(false);
                        } else {
                            confirmationAlert(true);
                        }
                    } else {
                        MessageUtils.showSnackbar(
                                rlRoot,
                                getResources().getString(R.string.err_unexpected)
                        );
                    }
                }

                @Override
                public void onFailure(Call<DataRequestStatus> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    MessageUtils.showSnackbar(rlRoot, getResources().getString(R.string.err_unexpected));
                }
            };
            ApiManager.getApi(DataUtils.getStringValue(
                    this,
                    DataUtils.EXTRA_TAG_TOKEN
            ),
                            DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)).getService().getDataDeleteStatus(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),OrganizationDetailActivity.organization.getID())
                    .enqueue(callback);
        }
    }

    private void dataDeleteRequest() {
        if (NetWorkUtil.isConnectedToInternet(this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<Void> callback = new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        refreshData();
                    } else {
                        MessageUtils.showSnackbar(
                                rlRoot,
                                getResources().getString(R.string.err_unexpected)
                        );
                    }
                }

                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    MessageUtils.showSnackbar(rlRoot, getResources().getString(R.string.err_unexpected));
                }
            };
            ApiManager.getApi(
                    DataUtils.getStringValue(
                            this,
                            DataUtils.EXTRA_TAG_TOKEN
                    ),
                            DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)
            ).getService().dataDeleteRequest(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),OrganizationDetailActivity.organization.getID())
                    .enqueue(callback);
        }
    }

    private void initViews() {
        ctvNewRequest = findViewById(R.id.ctvNewRequest);
        llProgressBar = findViewById(R.id.llProgressBar);
        rlRoot = findViewById(R.id.rlRoot);
        rvRequestHistory = findViewById(R.id.rvRequestHistory);
        toolbar = findViewById(R.id.toolBarCommon);
        tvTitle = findViewById(R.id.tvTitle);
    }

    private void setUpList() {

        adapter = new RequestHistoryAdapter(requestHistories, new OnUserRequestClickListener() {
            @Override
            public void onRequestClick(DataRequest request) {
                gotToStatusPage(request.getType() != 1);
            }

            @Override
            public void onRequestCancel(DataRequest request) {
                cancelDataRequest(request.getType() == 2, request);
            }
        });
        rvRequestHistory.setLayoutManager(new LinearLayoutManager(this));
        rvRequestHistory.setAdapter(adapter);

        Paginate.with(rvRequestHistory, new Paginate.Callbacks() {
            @Override
            public void onLoadMore() {
                getRequestHistory(false);
            }

            @Override
            public boolean isLoading() {
                return isLoadingData;
            }

            @Override
            public boolean hasLoadedAllItems() {
                return hasLoadedAllItems;
            }
        }).setLoadingTriggerThreshold(1)
                .addLoadingListItem(true)
                .build();
    }

    private void cancelDataRequest(Boolean isDownloadData, DataRequest request) {
        if (NetWorkUtil.isConnectedToInternet(this)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestGenResponse> callback = new Callback<DataRequestGenResponse>() {
                @Override
                public void onResponse(Call<DataRequestGenResponse> call, Response<DataRequestGenResponse> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        Toast.makeText(UserOrgRequestActivity.this, getResources().getString(R.string.txt_request_cancelled), Toast.LENGTH_SHORT).show();
                        refreshData();
                    } else {
                        Toast.makeText(UserOrgRequestActivity.this,
                                getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<DataRequestGenResponse> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    Toast.makeText(UserOrgRequestActivity.this,
                            getResources().getString(R.string.err_unexpected), Toast.LENGTH_SHORT).show();
                }
            };
            if (isDownloadData)
                ApiManager.getApi(DataUtils.getStringValue(
                        this,
                        DataUtils.EXTRA_TAG_TOKEN
                ),
                        DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)).getService().dataDownloadCancelRequest(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),mOrgId, request.getID()).enqueue(callback);
            else
                ApiManager.getApi(DataUtils.getStringValue(
                        this,
                        DataUtils.EXTRA_TAG_TOKEN
                ),
                        DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)).getService().dataDeleteCancelRequest(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),mOrgId, request.getID()).enqueue(callback);
        }
    }

    private void getRequestHistory(Boolean showProgress) {
        if (NetWorkUtil.isConnectedToInternet(this)) {
            isLoadingData = true;
            if (showProgress) llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestHistoryResponse> callback = new Callback<DataRequestHistoryResponse>() {
                @Override
                public void onResponse(Call<DataRequestHistoryResponse> call, Response<DataRequestHistoryResponse> response) {
                    isLoadingData = false;
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        if (response.body().getDataRequests() != null) {
                            if (startId.equalsIgnoreCase("")) {
                                requestHistories.clear();
                                isDownloadRequestOngoing =
                                        response.body().getDataDownloadRequestOngoing();
                                isDeleteRequestOngoing =
                                        response.body().getDataDeleteRequestOngoing();
                                requestHistories.addAll(setOngoingRequests(response.body().getDataRequests()));
                            } else {
                                requestHistories.addAll(response.body().getDataRequests());
                            }
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
                    isLoadingData = false;
                    llProgressBar.setVisibility(View.GONE);
                }
            };

            ApiManager.getApi(DataUtils.getStringValue(
                    this,
                    DataUtils.EXTRA_TAG_TOKEN
            ),
                    DataUtils.getStringValue(this, DataUtils.EXTRA_TAG_BASE_URL)).getService().getOrgRequestStatus(DataUtils.getStringValue(UserOrgRequestActivity.this, DataUtils.EXTRA_TAG_USERID),mOrgId, startId).enqueue(callback);
        }
    }

    private ArrayList<DataRequest> setOngoingRequests(ArrayList<DataRequest> dataRequests) {
        if (dataRequests != null) {
            for (DataRequest data : dataRequests) {
                if (data.getType() == 1 && isDeleteRequestOngoing) {
                    data.setOnGoing(true);
                    isDeleteRequestOngoing = false;
                }

                if (data.getType() == 2 && isDownloadRequestOngoing) {
                    data.setOnGoing(true);
                    isDownloadRequestOngoing = false;
                }

                if (!isDownloadRequestOngoing && !isDeleteRequestOngoing) {
                    return dataRequests;
                }
            }
        }
        return dataRequests != null ? dataRequests : new ArrayList<DataRequest>();
    }
}
