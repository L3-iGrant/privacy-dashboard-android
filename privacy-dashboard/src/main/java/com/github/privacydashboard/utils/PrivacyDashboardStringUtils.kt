package com.github.privacydashboard.utils

import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.widget.TextView
import java.util.*

object PrivacyDashboardStringUtils {
    fun toCamelCase(init: String?): String? {
        if (init == null) return null
        val ret = StringBuilder(init.length)
        for (word in init.split(" ").toTypedArray()) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).uppercase(Locale.getDefault()))
                ret.append(word.substring(1).lowercase(Locale.getDefault()))
            }
            if (ret.length != init.length) ret.append(" ")
        }
        return ret.toString()
    }

    fun findTextWidth(
        topTextView: TextView,
        bottomTextView: TextView,
        titleTextView: TextView,
        titleString: String,
        valueString: String,
        width: Int
    ) {
        val paint = Paint()
        paint.textSize = topTextView.textSize
        val result = Rect()

        //calculating the title sting width
        paint.getTextBounds(titleString, 0, titleString.length, result)
        val titleWidth = result.width()

        //setting title to title text view
        titleTextView.text = toCamelCase(titleString)

        //calculating the width of value
        paint.getTextBounds(valueString, 0, valueString.length, result)
        val valueWidth = result.width()

        if ((titleWidth + valueWidth + 30) <= width) {
            topTextView.visibility = View.VISIBLE
            bottomTextView.visibility = View.GONE
            topTextView.text = valueString
        } else {
            topTextView.visibility = View.GONE
            bottomTextView.visibility = View.VISIBLE
            bottomTextView.text = valueString
        }

    }

}