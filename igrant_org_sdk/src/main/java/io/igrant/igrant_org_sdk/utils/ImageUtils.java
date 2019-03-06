package io.igrant.igrant_org_sdk.utils;

import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.request.RequestOptions;

/**
 * Created by josephmilan on 8/22/18.
 */

public class ImageUtils {

    public static void setImage(ImageView imageView, String url) {
        if (url != null && !url.isEmpty()) {
            LazyHeaders.Builder builder = new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + DataUtils.getStringValue(imageView.getContext(),DataUtils.EXTRA_TAG_TOKEN));
            GlideUrl glideUrl = new GlideUrl(url, builder.build());
//
            Glide.with(imageView.getContext()).load(glideUrl).into(imageView);
        }
    }

    public static void setImage(ImageView imageView, String url, int placeHolder) {
        if (url != null && !url.isEmpty()) {
            LazyHeaders.Builder builder = new LazyHeaders.Builder()
                    .addHeader("Authorization", "Bearer " + DataUtils.getStringValue(imageView.getContext(),DataUtils.EXTRA_TAG_TOKEN));
            GlideUrl glideUrl = new GlideUrl(url, builder.build());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(placeHolder);
//            requestOptions.centerInside();
            requestOptions.error(placeHolder);


            Glide.with(imageView.getContext())
                    .setDefaultRequestOptions(requestOptions)
                    .load(glideUrl)
                    .into(imageView);
        }

    }
}
