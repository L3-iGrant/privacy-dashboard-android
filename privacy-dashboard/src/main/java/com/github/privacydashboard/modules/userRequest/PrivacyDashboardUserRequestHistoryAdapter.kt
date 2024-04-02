package com.github.privacydashboard.modules.userRequest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.marlonlom.utilities.timeago.TimeAgo
import com.github.privacydashboard.R
import com.github.privacydashboard.databinding.PrivacyItemUserRequestBinding
import com.github.privacydashboard.models.interfaces.userRequests.UserRequest
import com.github.privacydashboard.utils.PrivacyDashboardDateUtils.YYYYMMDDHHMMSS
import com.github.privacydashboard.utils.PrivacyDashboardDateUtils.getApiFormatDate

class PrivacyDashboardUserRequestHistoryAdapter(
    userRequests: ArrayList<UserRequest?>,
    mClickListener: PrivacyDashboardUserRequestClickListener
) :
    RecyclerView.Adapter<PrivacyDashboardUserRequestHistoryAdapter.ViewHolder?>() {
    private var mList: ArrayList<UserRequest?>
    private val mListener: PrivacyDashboardUserRequestClickListener
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: PrivacyItemUserRequestBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.privacy_item_user_request, parent, false
        )

        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val purposeConsent: UserRequest? = mList[position]
        holder.bind(purposeConsent, mListener)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(itemRowBinding: PrivacyItemUserRequestBinding) :
        RecyclerView.ViewHolder(itemRowBinding.root) {
        var itemRowBinding: PrivacyItemUserRequestBinding
        fun bind(
            userRequest: UserRequest?,
            mListener: PrivacyDashboardUserRequestClickListener,
        ) {
            itemRowBinding.tvRequestType.text = userRequest?.mTypeStr
            itemRowBinding.tvRequestDate.setText(
                TimeAgo.using(
                    getApiFormatDate(
                        userRequest?.mRequestedDate?.replace(" +0000 UTC", ""),
                        YYYYMMDDHHMMSS
                    ).time
                )
            )
            itemRowBinding.tvRequestStatus.text = userRequest?.mStateStr

            itemRowBinding.tvCancel.visibility =
                if (userRequest?.mIsOngoing == true) View.VISIBLE else View.GONE

            itemRowBinding.tvCancel.setOnClickListener {
                mListener.onRequestCancel(
                    userRequest
                )
            }

            itemRowBinding.llItem.setOnClickListener {
                if (userRequest?.mIsOngoing == true) mListener.onRequestClick(
                    userRequest
                )
            }
        }

        init {
            this.itemRowBinding = itemRowBinding
        }
    }

    fun updateList(userRequests: ArrayList<UserRequest?>){
        mList = userRequests
        notifyDataSetChanged()
    }

    init {
        mList = userRequests
        mListener = mClickListener
    }
}