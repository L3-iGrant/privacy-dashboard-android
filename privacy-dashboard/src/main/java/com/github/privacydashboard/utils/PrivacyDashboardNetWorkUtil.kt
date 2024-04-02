package com.github.privacydashboard.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import com.github.privacydashboard.R

object PrivacyDashboardNetWorkUtil {
    fun isConnectedToInternet(context: Context, showError: Boolean?): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
            return true
        } else {
            Toast.makeText(
                context,
                context.resources.getString(R.string.privacy_dashboard_error_internet),
                Toast.LENGTH_SHORT
            ).show()
        }
        return false
    }

    fun isConnectedToInternet(context: Context): Boolean {
        return isConnectedToInternet(context, false)
    }
}