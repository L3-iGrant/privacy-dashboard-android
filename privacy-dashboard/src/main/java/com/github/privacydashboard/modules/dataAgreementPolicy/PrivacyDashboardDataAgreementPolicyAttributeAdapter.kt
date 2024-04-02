package com.github.privacydashboard.modules.dataAgreementPolicy

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyItemDataAgreementPolicyAttributeBinding
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.utils.PrivacyDashboardStringUtils

class PrivacyDashboardDataAgreementPolicyAttributeAdapter(
    dataAttributes: ArrayList<DataAgreementPolicyModel>?,
    val width: Float
) :
    RecyclerView.Adapter<PrivacyDashboardDataAgreementPolicyAttributeAdapter.ViewHolder?>() {
    private val mList: ArrayList<DataAgreementPolicyModel>?
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PrivacyItemDataAgreementPolicyAttributeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.privacy_item_data_agreement_policy_attribute, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dataPolicy: DataAgreementPolicyModel? = mList?.get(position)
        holder.bind(dataPolicy, position == (mList?.size ?: 1) - 1, width)
    }

    override fun getItemCount(): Int {
        return mList?.size ?: 0
    }

    class ViewHolder(itemRowBinding: PrivacyItemDataAgreementPolicyAttributeBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: PrivacyItemDataAgreementPolicyAttributeBinding
        fun bind(
            attribute: DataAgreementPolicyModel?,
            isLast: Boolean,
            width: Float,
        ) {
            PrivacyDashboardStringUtils.findTextWidth(
                itemRowBinding.tvAttributeValue,
                itemRowBinding.tvAttributeValue1,
                itemRowBinding.tvAttributeName,
                attribute?.name ?: "",
                attribute?.value ?: "",
                width.toInt()
            )
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
