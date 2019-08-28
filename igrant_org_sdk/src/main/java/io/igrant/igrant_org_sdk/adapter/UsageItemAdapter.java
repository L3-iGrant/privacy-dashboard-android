package io.igrant.igrant_org_sdk.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.listener.ConsentAttributeClickListener;
import io.igrant.igrant_org_sdk.models.Consent.Consent;
import io.igrant.igrant_org_sdk.utils.StringUtils;

import java.util.ArrayList;

public class UsageItemAdapter extends RecyclerView.Adapter<UsageItemAdapter.ViewHolder> {

    private ArrayList<Consent> mList;
    private ConsentAttributeClickListener mListener;

    public UsageItemAdapter(ArrayList<Consent> consents, ConsentAttributeClickListener consentAttributeClickListener) {
        mList = consents;
        mListener = consentAttributeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Consent consent = mList.get(position);
        holder.ctvItemName.setText(consent.getDescription());
        if (consent.getStatus().getConsented() != null && !consent.getStatus().getConsented().equalsIgnoreCase("")) {
            holder.ctvStatus.setText(StringUtils.toCamelCase(consent.getStatus().getConsented()));
        } else {
            holder.ctvStatus.setText(holder.ctvStatus.getContext().getResources().getString(R.string.txt_allow));
        }
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onAttributeClick(consent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextView ctvItemName;
        CustomTextView ctvStatus;
        LinearLayout llItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ctvItemName = itemView.findViewById(R.id.ctvItemName);
            ctvStatus = itemView.findViewById(R.id.ctvStatus);
            llItem = itemView.findViewById(R.id.llItem);
        }
    }
}
