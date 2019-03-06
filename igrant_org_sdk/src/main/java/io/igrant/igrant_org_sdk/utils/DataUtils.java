package io.igrant.igrant_org_sdk.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class DataUtils {

    public static final String EXTRA_TAG_USERID = "io.igrant.igrant_org_sdk.utils.DataUtils.userid";
    public static final String EXTRA_TAG_TOKEN = "io.igrant.igrant_org_sdk.utils.DataUtils.token";
    public static final String EXTRA_TAG_EXPIRES_IN = "io.igrant.igrant_org_sdk.utils.DataUtils.expires_in";

    public static void saveStringValues(Context context, String tag, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(tag, value);
        editor.commit();
    }

    public static String getStringValue(Context context, String tag) {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return sharedPreferences.getString(tag, "");
        } catch (Exception e) {
            return "";
        }
    }


}
