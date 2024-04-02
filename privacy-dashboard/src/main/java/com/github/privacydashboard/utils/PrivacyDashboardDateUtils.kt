package com.github.privacydashboard.utils

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object PrivacyDashboardDateUtils {
    const val API_FORMAT = "yyyy-MM-dd'T'kk:mm:ss'Z'"
    const val DDMMYYYY = "dd-MM-yyyy"
    const val DDMMYYYYTKKMMSS = "dd-MM-yyyy kk:mm"
    const val DDMMYYYYHHMMA = "dd-MM-yyyy hh:mm a"
    const val YYYYMMDDHHMMSS = "yyyy-MM-dd kk:mm:ss"
    fun getRelativeTime(date: String?): String {
        val sdf = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.gmtToLocalDate(sdf.parse(date))
        } catch (e: Exception) {
            return "nil"
        }
        return DateUtils.getRelativeTimeSpanString(dDate?.time ?: Date().time).toString()
    }

    fun getApiFormatTime(date: String?): String {
        val sdf = SimpleDateFormat(DDMMYYYY, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.localToGMT(sdf.parse(date))
        } catch (e: Exception) {
            return ""
        }
        val sdf1 = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        return sdf1.format(dDate)
    }

    fun getApiFormatDate(date: String?): Date {
        val sdf = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.gmtToLocalDate(sdf.parse(date))
        } catch (e: Exception) {
            Date()
        }
        return dDate?:Date()
    }

    fun getApiFormatDate(date: String?,format: String?): Date {
        val sdf = SimpleDateFormat(format, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.gmtToLocalDate(sdf.parse(date))
        } catch (e: Exception) {
            Date()
        }
        return dDate?:Date()
    }

    fun getApiFormatTime(fromFormat: String?, toFormat: String?, date: String?): String {
        val sdf = SimpleDateFormat(fromFormat, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.gmtToLocalDate(sdf.parse(date))
        } catch (e: Exception) {
            return ""
        }
        val sdf1 = SimpleDateFormat(toFormat, Locale.ENGLISH)
        return sdf1.format(dDate)
    }

    fun getApiFormatStartDate(date: String): String {
        val strdate = "$date 00:00"
        val sdf = SimpleDateFormat(DDMMYYYYTKKMMSS, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.localToGMT(sdf.parse(strdate))
        } catch (e: Exception) {
            return ""
        }
        val sdf1 = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        return sdf1.format(dDate)
    }

    fun getApiFormatEndDate(date: String): String {
        val strdate = "$date 23:59"
        val sdf = SimpleDateFormat(DDMMYYYYTKKMMSS, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.localToGMT(sdf.parse(strdate))
        } catch (e: Exception) {
            return ""
        }
        val sdf1 = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        return sdf1.format(dDate)
    }

    fun getApiFormatTime(date: Date?): String {
        val sdf1 = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        return sdf1.format(date)
    }

    fun getFormatDate(format: String?, date: Date?): String {
        val sdf1 = SimpleDateFormat(format, Locale.ENGLISH)
        return sdf1.format(date)
    }

    fun getLastSeenTime(date: String?): String {
        val sdf = SimpleDateFormat(API_FORMAT, Locale.ENGLISH)
        var dDate: Date? = null
        dDate = try {
            PrivacyDashboardUtcUtils.gmtToLocalDate(sdf.parse(date))
        } catch (e: Exception) {
            return "nil"
        }
        val sdf1 = SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.ENGLISH)
        return sdf1.format(dDate)
    }
}