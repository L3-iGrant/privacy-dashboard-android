package io.igrant.igrant_org_sdk.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.suke.widget.SwitchButton;
import io.igrant.igrant_org_sdk.customViews.CustomTextView;
import io.igrant.igrant_org_sdk.R;
import io.igrant.igrant_org_sdk.listener.UsagePurposeClickListener;
import io.igrant.igrant_org_sdk.models.Organizations.PurposeConsent;

import java.util.ArrayList;

public class UsagePurposesAdapter extends RecyclerView.Adapter<UsagePurposesAdapter.ViewHolder> {

    private ArrayList<PurposeConsent> mList;
    private UsagePurposeClickListener mListener;

    public UsagePurposesAdapter(ArrayList<PurposeConsent> purposeConsents, UsagePurposeClickListener usagePurposeClickListener) {
        mList = purposeConsents;
        mListener = usagePurposeClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usage_purpose, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PurposeConsent purposeConsent = mList.get(position);

        holder.ctvItemName.setText(purposeConsent.getPurpose().getName());
        holder.ctvStatus.setText(purposeConsent.getCount().getConsented() == 0 ?
                holder.ctvStatus.getContext().getResources().getString(R.string.txt_org_disallow) :
                holder.ctvStatus.getContext().getResources().getString(R.string.txt_org_allow,
                        purposeConsent.getCount().getConsented(),
                        purposeConsent.getCount().getTotal()));

        holder.llDisable.setVisibility(purposeConsent.getPurpose().getLawfulUsage() ? View.VISIBLE : View.GONE);

        holder.switchButton.setOnCheckedChangeListener(null);

        holder.switchButton.setChecked(purposeConsent.getCount().getConsented() != 0);

        holder.switchButton.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                mListener.onSetStatus(purposeConsent, isChecked);
            }
        });
        holder.llItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onItemClick(purposeConsent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CustomTextView ctvItemName;
        CustomTextView ctvStatus;
        LinearLayout llDisable;
        SwitchButton switchButton;
        LinearLayout llItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ctvItemName = itemView.findViewById(R.id.ctvItemName);
            ctvStatus = itemView.findViewById(R.id.ctvStatus);
            llDisable = itemView.findViewById(R.id.llDisable);
            switchButton = itemView.findViewById(R.id.switch_button);
            llItem = itemView.findViewById(R.id.llItem);
        }
    }
}
