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
import io.igrant.igrant_org_sdk.models.ConsentHistory.ConsentHistory;
import io.igrant.igrant_org_sdk.utils.DateUtils;

import java.util.ArrayList;

public class ConsentHistoryAdapter extends RecyclerView.Adapter<ConsentHistoryAdapter.ViewHolder> {

    private ArrayList<ConsentHistory> consentHistories;

    public ConsentHistoryAdapter(ArrayList<ConsentHistory> consentHistories) {
        this.consentHistories = consentHistories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consent_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsentHistory consentHistory = consentHistories.get(position);

        holder.llItem.setBackgroundColor(position % 2 == 0 ? Color.parseColor("#ffffff") : Color.parseColor("#dddddd"));

        holder.ctvLog.setText(consentHistory.getLog());
        holder.ctvTimeStamp.setText(DateUtils.getRelativeTime(consentHistory.getTimeStamp()));
    }

    @Override
    public int getItemCount() {
        return consentHistories.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        LinearLayout llItem;
        CustomTextView ctvLog;
        CustomTextView ctvTimeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            llItem = itemView.findViewById(R.id.llItem);
            ctvLog = itemView.findViewById(R.id.ctvLog);
            ctvTimeStamp = itemView.findViewById(R.id.ctvTimeStamp);
        }
    }
}
