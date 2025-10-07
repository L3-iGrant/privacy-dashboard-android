package com.github.privacydashboard.utils

import android.widget.ImageView

fun adjustImageViewHeightBasedOnWidth(imageView: ImageView) {
    imageView.viewTreeObserver.addOnGlobalLayoutListener {
        // Check if the ImageView is not null and get the width after layout
        imageView.let {
            val width = it.width

            // Calculate the height as 1/3 of the width (3:1 ratio)
            val calculatedHeight = width / 3

            // Apply the calculated height
            it.layoutParams = it.layoutParams.apply {
                height = calculatedHeight
            }
        }
    }
}