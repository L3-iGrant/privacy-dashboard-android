package io.igrant.igrant_org_sdk.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;

import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.listener.OnUserRequestClickListener;
import io.igrant.igrant_org_sdk.models.OrgData.DataRequest;
import io.igrant.igrant_org_sdk.utils.DateUtils;

public class RequestHistoryAdapter extends RecyclerView.Adapter<RequestHistoryAdapter.ViewHolder> {

    ArrayList<DataRequest> mList;
    OnUserRequestClickListener mListener;

    public RequestHistoryAdapter(ArrayList<DataRequest> requestHistories, OnUserRequestClickListener onUserRequestClickListener) {
        mList = requestHistories;
        mListener = onUserRequestClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final DataRequest dataRequest = mList.get(position);

        holder.llItem.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#ffffff") : holder.llItem.getContext().getResources().getColor(R.color.xfadedwhite));
        holder.tvRequestType.setText(dataRequest.getTypeStr());
        holder.tvRequestDate.setText(DateUtils.getApiFormatTime(DateUtils.YYYYMMDDHHMMSS, DateUtils.DDMMYYYYHHMMA, dataRequest.getRequestedDate().replace(" +0000 UTC", "")));
        holder.tvRequestStatus.setText(dataRequest.getStateStr());

        holder.tvCancel.setVisibility(dataRequest.getOnGoing() ? View.VISIBLE : View.GONE);

        holder.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onRequestCancel(dataRequest);
            }
        });

        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dataRequest.getOnGoing())
                    mListener.onRequestClick(dataRequest);
            }
        });

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
        private CustomTextView tvCancel;

        public ViewHolder(View itemView) {
            super(itemView);

            llItem = itemView.findViewById(R.id.llItem);
            tvRequestType = itemView.findViewById(R.id.tvRequestType);
            tvRequestDate = itemView.findViewById(R.id.tvRequestDate);
            tvRequestStatus = itemView.findViewById(R.id.tvRequestStatus);
            tvCancel = itemView.findViewById(R.id.tvCancel);
        }
    }
}
