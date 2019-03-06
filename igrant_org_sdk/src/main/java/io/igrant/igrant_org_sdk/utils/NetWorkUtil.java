package io.igrant.igrant_org_sdk.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import io.igrant.igrant_org_sdk.R;


/**
 * Created by JMAM on 30-12-2017.
 */

public class NetWorkUtil {

    public static boolean isConnectedToInternet(Context context, Boolean showError) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(context,context.getResources().getString(R.string.err_internet),Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public static boolean isConnectedToInternet(Context context) {
        return isConnectedToInternet(context,false);
    }
}
