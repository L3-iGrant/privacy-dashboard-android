package com.github.privacydashboard.modules.dataAgreementPolicy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyItemDataAgreementPolicyBinding
import com.github.privacydashboard.databinding.FragmentItemDataAgreementPolicyBinding
import com.github.privacydashboard.models.DataAgreementPolicyModel
import com.github.privacydashboard.utils.AdapterType

class PrivacyDashboardDataAgreementPolicyAdapter(
    dataAttributes: ArrayList<ArrayList<DataAgreementPolicyModel>>,
    val width: Float,
    private val adapterType: AdapterType = AdapterType.ACTIVITY
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val mList: ArrayList<ArrayList<DataAgreementPolicyModel>> = dataAttributes

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (adapterType) {
            AdapterType.ACTIVITY -> {
                val binding: PrivacyItemDataAgreementPolicyBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.privacy_item_data_agreement_policy,
                    parent,
                    false
                )
                ActivityViewHolder(binding)
            }

            AdapterType.FRAGMENT -> {
                val binding: FragmentItemDataAgreementPolicyBinding = DataBindingUtil.inflate(
                    inflater,
                    R.layout.fragment_item_data_agreement_policy,
                    parent,
                    false
                )
                FragmentViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val policyList: ArrayList<DataAgreementPolicyModel>? = mList[position]
        when (holder) {
            is ActivityViewHolder -> holder.bind(policyList, width)
            is FragmentViewHolder -> holder.bind(policyList, width)
        }
    }

    override fun getItemCount(): Int = mList.size

    class ActivityViewHolder(private val itemRowBinding: PrivacyItemDataAgreementPolicyBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        fun bind(attribute: ArrayList<DataAgreementPolicyModel>?, width: Float) {
            val adapter = PrivacyDashboardDataAgreementPolicyAttributeAdapter(attribute, width)
            itemRowBinding.rvAttributes.layoutManager =
                LinearLayoutManager(itemRowBinding.rvAttributes.context)
            itemRowBinding.rvAttributes.adapter = adapter
        }
    }

    class FragmentViewHolder(private val itemRowBinding: FragmentItemDataAgreementPolicyBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        fun bind(attribute: ArrayList<DataAgreementPolicyModel>?, width: Float) {
            val adapter = PrivacyDashboardDataAgreementPolicyAttributeAdapter(attribute, width)
            itemRowBinding.rvAttributes.layoutManager =
                LinearLayoutManager(itemRowBinding.rvAttributes.context)
            itemRowBinding.rvAttributes.adapter = adapter
        }
    }
}
