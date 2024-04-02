package com.github.privacydashboard.modules.dataAgreementPolicy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyItemDataAgreementPolicyBinding
import com.github.privacydashboard.models.DataAgreementPolicyModel

class PrivacyDashboardDataAgreementPolicyAdapter(
    dataAttributes: ArrayList<ArrayList<DataAgreementPolicyModel>>,
    val width: Float,
) :
    RecyclerView.Adapter<PrivacyDashboardDataAgreementPolicyAdapter.ViewHolder?>() {
    private val mList: ArrayList<ArrayList<DataAgreementPolicyModel>>
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PrivacyItemDataAgreementPolicyBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.privacy_item_data_agreement_policy, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val policyList: ArrayList<DataAgreementPolicyModel>? = mList[position]
        holder.bind(policyList, width)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: PrivacyItemDataAgreementPolicyBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: PrivacyItemDataAgreementPolicyBinding
        fun bind(
            attribute: ArrayList<DataAgreementPolicyModel>?,
            width: Float,
        ) {
            val adapter = PrivacyDashboardDataAgreementPolicyAttributeAdapter(
                attribute, width
            )
            itemRowBinding.rvAttributes.layoutManager =
                LinearLayoutManager(itemRowBinding.rvAttributes.context)
            itemRowBinding.rvAttributes.adapter = adapter
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }


    init {
        mList = dataAttributes
    }
}
