package io.igrant.igrant_org_sdk.customViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import io.igrant.igrant_org_sdk.R;

/**
 * Created by JMAM on 8/15/18.
 */

public class CustomEditText extends android.support.v7.widget.AppCompatEditText {

    /**
     * the spacing between the main text and the inner top padding.
     */
    private int extraPaddingTop;

    /**
     * error text for manually invoked {@link #setError(CharSequence)}
     */
    private String errorText;

    /**
     * whether the floating error should be shown. default is false.
     */
    private boolean floatingErrorEnabled;

    /**
     * the floating error's text size.
     */
    private int floatingErrorTextSize;

    /**
     * the floating error's text color.
     */
    private int floatingErrorTextColor;


    TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);

    public CustomEditText(Context context) {
        super(context);
        init(context, null);
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomEditText);
        floatingErrorTextColor = typedArray.getColor(R.styleable.CustomEditText_cet_floatingErrorTextColor, Color.parseColor("#e7492E"));
        floatingErrorTextSize = typedArray.getDimensionPixelSize(R.styleable.CustomEditText_cet_floatingErrorTextSize, getResources().getDimensionPixelSize(R.dimen.text_size_medium));
        typedArray.recycle();
        initPadding();
    }

    private void initPadding() {
        extraPaddingTop = floatingErrorTextSize;
        textPaint.setTextSize(floatingErrorTextSize);
        textPaint.setColor(floatingErrorTextColor);
        Paint.FontMetrics textMetrics = textPaint.getFontMetrics();
    }

    public int getErrorColor() {
        return floatingErrorTextColor;
    }

    public void setErrorColor(int color) {
        floatingErrorTextColor = color;
        postInvalidate();
    }

    @Override
    public void setError(CharSequence errorText) {
        this.errorText = errorText == null ? null : errorText.toString();
//        if (adjustBottomLines()) {
        postInvalidate();
//        }
    }

    @Override
    public CharSequence getError() {
        return errorText;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (errorText == null || errorText.isEmpty())
            super.setPadding(0, getResources().getDimensionPixelSize(R.dimen.pad_custom_textview), 0, getResources().getDimensionPixelSize(R.dimen.pad_custom_textview));
        else
            super.setPadding(0, extraPaddingTop + getResources().getDimensionPixelSize(R.dimen.pad_custom_textview), 0, getResources().getDimensionPixelSize(R.dimen.pad_custom_textview));

        int startX = getScrollX();
        int endX = getScrollX() + getWidth();

        // draw the floating error
        if (true && !TextUtils.isEmpty(errorText)) {
            textPaint.setTextSize(floatingErrorTextSize);
            // calculate the text color
            textPaint.setColor(floatingErrorTextColor != -1 ? floatingErrorTextColor : 0x44000000);

            // calculate the horizontal position
            float floatingLabelWidth = textPaint.measureText(errorText.toString());
            int floatingLabelStartX;
            if ((getGravity() & Gravity.RIGHT) == Gravity.RIGHT) {
                floatingLabelStartX = (int) (endX - floatingLabelWidth);
            } else if ((getGravity() & Gravity.LEFT) == Gravity.LEFT) {
                floatingLabelStartX = startX;
            } else {
                floatingLabelStartX = startX;
            }

            int floatingLabelStartY = (int) getScrollY() + floatingErrorTextSize;

            textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            // draw the floating label
            canvas.drawText(errorText.toString(), floatingLabelStartX, floatingLabelStartY, textPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        errorText = "";
        postInvalidate();
    }
}
