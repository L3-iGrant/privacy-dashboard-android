package com.github.privacydashboard.modules.dataSharing

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacydashboard.databinding.PrivacyItemDataSharingAttributeBinding
import com.github.privacydashboard.R
import com.github.privacydashboard.models.v2.dataAgreement.dataAttributes.DataAttributesV2

class PrivacyDashboardDataSharingAttributesAdapter(
    dataAttributes: ArrayList<DataAttributesV2?>,
) :
    RecyclerView.Adapter<PrivacyDashboardDataSharingAttributesAdapter.ViewHolder?>() {
    private val mList: ArrayList<DataAttributesV2?>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PrivacyItemDataSharingAttributeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.privacy_item_data_sharing_attribute, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purposeConsent: DataAttributesV2? = mList[position]
        holder.bind(purposeConsent, position == mList.size - 1)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: PrivacyItemDataSharingAttributeBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: PrivacyItemDataSharingAttributeBinding
        fun bind(attribute: DataAttributesV2?, isLast: Boolean) {
            itemRowBinding.tvAttributeName.text = attribute?.name
            itemRowBinding.vDivider.visibility = if (isLast) View.GONE else View.VISIBLE
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = dataAttributes
    }
}
