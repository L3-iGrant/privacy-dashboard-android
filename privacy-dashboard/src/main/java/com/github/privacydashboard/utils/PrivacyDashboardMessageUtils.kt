package com.github.privacydashboard.utils

import android.content.Context
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.github.privacydashboard.R
import com.google.android.material.snackbar.Snackbar

object PrivacyDashboardMessageUtils {
    fun showSnackbar(view: View, message: String?) {
        val snackbar = Snackbar
            .make(view, message!!, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(
            ContextCompat.getColor(
            view.context,
            R.color.privacyDashboard_error_red
        ))
        snackbar.show()
    }

    fun showSnackbar(view: View, message: String?, color: Int) {
        val snackbar = Snackbar
            .make(view, message!!, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view
        snackBarView.setBackgroundColor(view.context.resources.getColor(color))
        snackbar.show()
    }

    fun showAlert(activity: Context?, message: String?, posiviteButtonText: String?) {
        val builder = AlertDialog.Builder(
            activity!!
        )
        builder.setMessage(message)
        builder.setPositiveButton(
            posiviteButtonText
        ) { dialog, id -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }
}
