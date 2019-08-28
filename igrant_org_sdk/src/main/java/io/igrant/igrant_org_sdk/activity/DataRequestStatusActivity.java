package io.igrant.igrant_org_sdk.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import io.igrant.igrant_org_sdk.Api.ApiManager;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.customViews.stepView.VerticalStepView;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestGenResponse;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequestStatus;
import io.igrant.igrant_org_sdk.utils.DataUtils;
import io.igrant.igrant_org_sdk.utils.DateUtils;
import io.igrant.igrant_org_sdk.utils.MessageUtils;
import io.igrant.igrant_org_sdk.utils.NetWorkUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;
import java.util.List;

public class DataRequestStatusActivity extends AppCompatActivity {

    public static final String TAG_DAT_REQUEST_TYPE = "DataRequestStatusActivity.type";
    public static final String TAG_DAT_REQUEST_ORG_ID = "DataRequestStatusActivity.orgId";

    private Boolean isDownloadData;
    private String mOrgId = "";

    private DataRequestStatus status;

    private LinearLayout llProgressBar;
    private Button btnCancel;
    private VerticalStepView stepView;
    private Toolbar toolBarCommon;
    private CustomTextView tvTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_request_status);
        isDownloadData = getIntent().getBooleanExtra(TAG_DAT_REQUEST_TYPE, false);
        mOrgId = getIntent().getStringExtra(TAG_DAT_REQUEST_ORG_ID);
        initviews();
        getDataRequestStatus();
        setUpToolBar();
        initListener();
    }

    private void initviews() {
        llProgressBar = findViewById(R.id.llProgressBar);
        btnCancel = findViewById(R.id.btnCancel);
        stepView = findViewById(R.id.stepView);
        toolBarCommon = findViewById(R.id.toolBarCommon);
        tvTitle = findViewById(R.id.tvTitle);
    }

    private void getDataRequestStatus() {
        if (NetWorkUtil.isConnectedToInternet(DataRequestStatusActivity.this, true)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestStatus> callback = new Callback<DataRequestStatus>() {
                @Override
                public void onResponse(Call<DataRequestStatus> call, Response<DataRequestStatus> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
//                        if (response.body()!=null&& response.body().size()>0) {
                        status = response.body();
//                        }
                        setUpStepView();
                    } else {
                        MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_unexpected));
                    }
                }

                @Override
                public void onFailure(Call<DataRequestStatus> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_unexpected));
                }
            };
            if (isDownloadData)
                ApiManager.getApi(DataUtils.getStringValue(DataRequestStatusActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getDataDownloadStatus(mOrgId).enqueue(callback);
            else
                ApiManager.getApi(DataUtils.getStringValue(DataRequestStatusActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().getDataDeleteStatus(mOrgId).enqueue(callback);
        }
    }

    private void initListener() {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelDataRequest();
            }
        });
    }

    private void cancelDataRequest() {
        if (NetWorkUtil.isConnectedToInternet(DataRequestStatusActivity.this)) {
            llProgressBar.setVisibility(View.VISIBLE);
            Callback<DataRequestGenResponse> callback = new Callback<DataRequestGenResponse>() {
                @Override
                public void onResponse(Call<DataRequestGenResponse> call, Response<DataRequestGenResponse> response) {
                    llProgressBar.setVisibility(View.GONE);
                    if (response.code() == 200) {
                        Toast.makeText(DataRequestStatusActivity.this, getResources().getString(R.string.txt_request_cancelled), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_unexpected));
                    }
                }

                @Override
                public void onFailure(Call<DataRequestGenResponse> call, Throwable t) {
                    llProgressBar.setVisibility(View.GONE);
                    MessageUtils.showSnackbar(getWindow().getDecorView().findViewById(android.R.id.content), getResources().getString(R.string.err_unexpected));
                }
            };
            if (isDownloadData)
                ApiManager.getApi(DataUtils.getStringValue(DataRequestStatusActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().dataDownloadCancelRequest(mOrgId, status.getID()).enqueue(callback);
            else
                ApiManager.getApi(DataUtils.getStringValue(DataRequestStatusActivity.this, DataUtils.EXTRA_TAG_TOKEN)).getService().dataDeleteCancelRequest(mOrgId, status.getID()).enqueue(callback);
        }
    }

    private void setUpStepView() {
        if (status != null && status.getState() > 0) {
            btnCancel.setVisibility(View.VISIBLE);
            List<String> list0 = new ArrayList<>();
            list0.add(getResources().getString(R.string.txt_data_request_initiated));
            list0.add(getResources().getString(R.string.txt_data_request_acknowledged));
            list0.add(getResources().getString(R.string.txt_data_request_processed));

            List<String> dates = new ArrayList<>();
            dates.add((status.getRequestedDate() != null && !status.getRequestedDate().equalsIgnoreCase("")) ?
                    DateUtils.getApiFormatTime(DateUtils.YYYYMMDDHHMMSS, DateUtils.DDMMYYYYHHMMA, status.getRequestedDate().replace(" +0000 UTC", "")) : "");
            dates.add("");
            dates.add("");

            List<String> comments = new ArrayList<>();
            dates.add("");
            dates.add("");
            dates.add("");

//            list0.add(getResources().getString(R.string.txt_data_request_processed_with_action));
//            list0.add(getResources().getString(R.string.txt_data_request_cancelled_by_user));

            stepView.setStepsViewIndicatorComplectingPosition(getSelectedStatusPosition())
                    .reverseDraw(false)
                    .setStepViewTexts(list0)
//                    .setStepViewComplectedTextColor(R.color.lightBlue)
                    .setStepViewDates(dates)
                    .setStepViewComments(comments)
                    .setLinePaddingProportion(0.85f)
                    .setStepsViewIndicatorCompletedLineColor(ContextCompat.getColor(DataRequestStatusActivity.this, R.color.lightBlue))//设置StepsViewIndicator完成线的颜色
                    .setStepsViewIndicatorUnCompletedLineColor(ContextCompat.getColor(DataRequestStatusActivity.this, R.color.lightBlue))//设置StepsViewIndicator未完成线的颜色
                    .setStepViewComplectedTextColor(ContextCompat.getColor(DataRequestStatusActivity.this, R.color.txt_color))//设置StepsView text完成线的颜色
                    .setStepViewUnComplectedTextColor(ContextCompat.getColor(DataRequestStatusActivity.this, R.color.txt_color))//设置StepsView text未完成线的颜色
                    .setStepsViewIndicatorCompleteIcon(ContextCompat.getDrawable(DataRequestStatusActivity.this, R.drawable.ic_completed_status))//设置StepsViewIndicator CompleteIcon
                    .setStepsViewIndicatorDefaultIcon(ContextCompat.getDrawable(DataRequestStatusActivity.this, R.drawable.ic_uncompleted_status))//设置StepsViewIndicator DefaultIcon
                    .setStepsViewIndicatorAttentionIcon(ContextCompat.getDrawable(DataRequestStatusActivity.this, R.drawable.ic_current_status));//设置StepsViewIndicator AttentionIcon;
        }
    }

    private int getSelectedStatusPosition() {
        switch (status.getState()) {
            case 1:
                return 0;
            case 2:
                return 1;
            case 6:
            case 7:
                return 0;
            default:
                return 0;
        }
    }

    private void setUpToolBar() {
        tvTitle.setText(isDownloadData ? getResources().getString(R.string.txt_more_download_data) : getResources().getString(R.string.txt_more_forgot_me));
        setSupportActionBar(toolBarCommon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.ic_back_white));
        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
}
