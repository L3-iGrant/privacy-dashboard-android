package io.igrant.igrant_org_sdk.customViews;

import android.content.Context;
import android.util.AttributeSet;
import io.igrant.igrant_org_sdk.R;

/**
 * Created by JMAM on 8/15/18.
 */

public class LoginEditText extends CustomEditText {
    public LoginEditText(Context context) {
        super(context);
        init(null);
    }

    public LoginEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public LoginEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.setBackground(getResources().getDrawable(R.drawable.bg_login_edittext));
    }
}
