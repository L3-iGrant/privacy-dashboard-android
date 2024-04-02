package com.github.privacydashboard.modules.logging

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyItemConsentHistoryBinding
import com.github.privacydashboard.models.interfaces.consentHistory.ConsentHistory
import com.github.privacydashboard.utils.PrivacyDashboardDateUtils.getApiFormatDate
import com.github.marlonlom.utilities.timeago.TimeAgo

class PrivacyDashboardHistoryAdapter(
    consentHistories: ArrayList<ConsentHistory?>
) :
    RecyclerView.Adapter<PrivacyDashboardHistoryAdapter.ViewHolder?>() {
    private val mList: ArrayList<ConsentHistory?>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PrivacyItemConsentHistoryBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.privacy_item_consent_history, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consentHistory: ConsentHistory? = mList[position]
        holder.bind(consentHistory, position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: PrivacyItemConsentHistoryBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: PrivacyItemConsentHistoryBinding
        fun bind(consentHistory: ConsentHistory?, position: Int) {
            itemRowBinding.clItem.setBackgroundColor(
                if (position % 2 == 0) Color.parseColor("#ffffff") else Color.parseColor(
                    "#dddddd"
                )
            )

            itemRowBinding.tvLog.text = consentHistory?.mLog?:""
            itemRowBinding.tvTimeStamp.text =
                TimeAgo.using(getApiFormatDate(consentHistory?.mTimeStamp).time)
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = consentHistories
    }
}
