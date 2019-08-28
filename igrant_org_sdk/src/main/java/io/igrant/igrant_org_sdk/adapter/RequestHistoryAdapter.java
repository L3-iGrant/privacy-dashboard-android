package io.igrant.igrant_org_sdk.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequest;
import io.igrant.igrant_org_sdk.utils.DateUtils;

import java.util.ArrayList;

public class RequestHistoryAdapter extends RecyclerView.Adapter<RequestHistoryAdapter.ViewHolder> {

    ArrayList<DataRequest> mList;

    public RequestHistoryAdapter(ArrayList<DataRequest> requestHistories) {
        mList = requestHistories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DataRequest dataRequest = mList.get(position);

        holder.llItem.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#ffffff") : holder.llItem.getContext().getResources().getColor(R.color.xfadedwhite));
        holder.tvRequestType.setText(dataRequest.getTypeStr());
        holder.tvRequestDate.setText(DateUtils.getApiFormatTime(DateUtils.YYYYMMDDHHMMSS, DateUtils.DDMMYYYYHHMMA, dataRequest.getRequestedDate().replace(" +0000 UTC","")));
        holder.tvRequestStatus.setText(dataRequest.getStateStr());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout llItem;
        private CustomTextView tvRequestType;
        private CustomTextView tvRequestDate;
        private CustomTextView tvRequestStatus;

        public ViewHolder(View itemView) {
            super(itemView);

            llItem = itemView.findViewById(R.id.llItem);
            tvRequestType = itemView.findViewById(R.id.tvRequestType);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus);
        }
    }
}
