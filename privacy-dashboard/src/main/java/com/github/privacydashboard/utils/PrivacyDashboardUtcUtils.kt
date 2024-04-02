package com.github.privacydashboard.utils

import java.text.SimpleDateFormat
import java.util.*

object PrivacyDashboardUtcUtils {
    fun localToGMT(date: Date?): Date {
        val sdf =
            SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.ENGLISH)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return Date(sdf.format(date))
    }

    fun gmtToLocalDate(date: Date): Date {
        val timeZone = Calendar.getInstance().timeZone.id
        return Date(date.time + TimeZone.getTimeZone(timeZone).getOffset(date.time))
    }
}