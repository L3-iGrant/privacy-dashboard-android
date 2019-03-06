package io.igrant.igrant_org_sdk.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by JMAM on 8/16/18.
 */

public class LoginUtils {

    public static boolean isUserLoggedIn() {
//        User userDetail = RealmUtils.getInstance().getUserData();
//        if (userDetail != null && !android.text.TextUtils.isEmpty(RealmUtils.getInstance().getUserToken())) {
//            return true;
//        }
        return false;
    }

    public static void logoutFunction(Activity activity) {
//        RealmUtils.getInstance().clearUserData();
//        Intent loginIntent = new Intent(activity, LoginActivity.class);
//        activity.startActivity(loginIntent);
//        activity.overridePendingTransition(R.anim.fadein, R.anim.fadeout);
//        activity.finish();
    }

}
