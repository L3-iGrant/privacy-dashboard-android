package io.igrant.igrant_org_sdk.utils;

import android.util.Patterns;

/**
 * Created by JMAM on 8/15/18.
 */

public class TextUtils {

    public static Boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches() ? true : false;
    }

    public static Boolean isValidPhone(String phone) {
        return Patterns.PHONE.matcher(phone).matches() ? true : false;
    }

    public static Boolean isFieldNotEmpty(String text) {
        return text.isEmpty() ? false : true;
    }
}
