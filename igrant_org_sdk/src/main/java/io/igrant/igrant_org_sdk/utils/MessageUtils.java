package io.igrant.igrant_org_sdk.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import io.igrant.igrant_org_sdk.R;

public class MessageUtils {

    public static void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(view.getContext().getResources().getColor(R.color.txt_red));
        snackbar.show();
    }

    public static void showSnackbar(View view, String message, int color) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(view.getContext().getResources().getColor(color));
        snackbar.show();
    }

    public static void showAlert(Context activity, String message, String posiviteButtonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setMessage(message);


        builder.setPositiveButton(posiviteButtonText, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
