package io.igrant.igrant_org_sdk.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    public static final String API_FORMAT = "yyyy-MM-dd'T'kk:mm:ss'Z'";

    public static final String DDMMYYYY = "dd-MM-yyyy";

    public static final String DDMMYYYYTKKMMSS = "dd-MM-yyyy kk:mm";


    public static String getRelativeTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(API_FORMAT, Locale.ENGLISH);
        Date dDate = null;
        try {
            dDate = UtcUtils.gmttoLocalDate(sdf.parse(date));
        } catch (Exception e) {
            return "nil";
        }
        return android.text.format.DateUtils.getRelativeTimeSpanString(dDate.getTime()).toString();
    }

    public static String getApiFormatTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DDMMYYYY, Locale.ENGLISH);
        Date dDate = null;
        try {
            dDate = UtcUtils.localToGMT(sdf.parse(date));
        } catch (Exception e) {
            return "";
        }

        SimpleDateFormat sdf1 = new SimpleDateFormat(API_FORMAT, Locale.ENGLISH);
        return sdf1.format(dDate);
    }

    public static String getApiFormatStartDate(String date) {
        String strdate = date + " 00:00";
        SimpleDateFormat sdf = new SimpleDateFormat(DDMMYYYYTKKMMSS, Locale.ENGLISH);
        Date dDate = null;
        try {
            dDate = UtcUtils.localToGMT(sdf.parse(strdate));

        } catch (Exception e) {
            return "";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(API_FORMAT, Locale.ENGLISH);
        return sdf1.format(dDate);
    }

    public static String getApiFormatEndDate(String date) {
        String strdate = date + " 23:59";
        SimpleDateFormat sdf = new SimpleDateFormat(DDMMYYYYTKKMMSS, Locale.ENGLISH);
        Date dDate = null;
        try {
            dDate = UtcUtils.localToGMT(sdf.parse(strdate));
        } catch (Exception e) {
            return "";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(API_FORMAT, Locale.ENGLISH);
        return sdf1.format(dDate);
    }

    public static String getApiFormatTime(Date date) {

        SimpleDateFormat sdf1 = new SimpleDateFormat(API_FORMAT, Locale.ENGLISH);
        return sdf1.format(date);
    }

    public static String getFormatDate(String format, Date date) {

        SimpleDateFormat sdf1 = new SimpleDateFormat(format, Locale.ENGLISH);
        return sdf1.format(date);
    }

    public static String getLastSeenTime(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat(API_FORMAT, Locale.ENGLISH);
        Date dDate = null;
        try {
            dDate = UtcUtils.gmttoLocalDate(sdf.parse(date));
        } catch (Exception e) {
            return "nil";
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.ENGLISH);
        return sdf1.format(dDate);
    }


}
