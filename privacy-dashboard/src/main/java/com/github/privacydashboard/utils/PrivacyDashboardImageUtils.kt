package com.github.privacydashboard.utils

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

object PrivacyDashboardImageUtils {
    fun setImage(imageView: ImageView, url: String?) {
        if (url != null && !url.isEmpty()) {
            Glide.with(imageView.context).load(imageModel(url)).into(imageView)
        }
    }

    fun setImage(imageView: ImageView, url: String?, placeHolder: Int) {
        if (url != null && !url.isEmpty()) {
            val requestOptions = RequestOptions()
            requestOptions.placeholder(placeHolder)
            //            requestOptions.centerInside();
            requestOptions.error(placeHolder)
            Glide.with(imageView.context)
                .setDefaultRequestOptions(requestOptions)
                .load(imageModel(url))
                .into(imageView)
        }
    }

    /**
     * The logo and cover URLs come from organisation data and from the host
     * app, so they can point to any server. The model is the plain URL, so
     * Glide sends its default headers only, never the API key, the token or
     * the individual id.
     */
    internal fun imageModel(url: String): Any = url
}
