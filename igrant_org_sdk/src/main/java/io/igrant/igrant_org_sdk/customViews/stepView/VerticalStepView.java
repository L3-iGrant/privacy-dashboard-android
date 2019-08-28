package io.igrant.igrant_org_sdk.customViews.stepView;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import io.igrant.igrant_org_sdk.R;

import java.util.List;

/**
 * 日期：16/6/24 11:48
 * 描述：
 */
public class VerticalStepView extends LinearLayout implements VerticalStepViewIndicator.OnDrawIndicatorListener {
    private RelativeLayout mTextContainer;
    private VerticalStepViewIndicator mStepsViewIndicator;
    private List<String> mTexts;
    private List<String> mDates;
    private List<String> mComments;
    private int mComplectingPosition;
    private int mUnComplectedTextColor = ContextCompat.getColor(getContext(), R.color.uncompleted_text_color);//定义默认未完成文字的颜色;
    private int mComplectedTextColor = ContextCompat.getColor(getContext(), android.R.color.white);//定义默认完成文字的颜色;

    private int mTextSize = 14;//default textSize
    private TextView mTextView;

    private int mdateTextSize = 10;//default textSize
    private TextView mDateTextView;
//    private int mCommentTextSize = 10;//default textSize
//    private TextView mCommentTextView;


    public VerticalStepView(Context context) {
        this(context, null);
    }

    public VerticalStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.widget_vertical_stepsview, this);
        mStepsViewIndicator = (VerticalStepViewIndicator) rootView.findViewById(R.id.steps_indicator);
        mStepsViewIndicator.setOnDrawListener(this);
        mTextContainer = (RelativeLayout) rootView.findViewById(R.id.rl_text_container);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     * @return
     */
    public VerticalStepView setStepViewTexts(List<String> texts) {
        mTexts = texts;
        if (texts != null) {
            mStepsViewIndicator.setStepNum(mTexts.size());
        } else {
            mStepsViewIndicator.setStepNum(0);
        }
        return this;
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     * @return
     */
    public VerticalStepView setStepViewDates(List<String> texts) {
        mDates = texts;
        return this;
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     * @return
     */
    public VerticalStepView setStepViewComments(List<String> texts) {
        mComments = texts;
        return this;
    }

    /**
     * 设置正在进行的position
     *
     * @param complectingPosition
     * @return
     */
    public VerticalStepView setStepsViewIndicatorComplectingPosition(int complectingPosition) {
        mComplectingPosition = complectingPosition;
        mStepsViewIndicator.setComplectingPosition(complectingPosition);
        return this;
    }

    /**
     * 设置未完成文字的颜色
     *
     * @param unComplectedTextColor
     * @return
     */
    public VerticalStepView setStepViewUnComplectedTextColor(int unComplectedTextColor) {
        mUnComplectedTextColor = unComplectedTextColor;
        return this;
    }

    /**
     * 设置完成文字的颜色
     *
     * @param complectedTextColor
     * @return
     */
    public VerticalStepView setStepViewComplectedTextColor(int complectedTextColor) {
        this.mComplectedTextColor = complectedTextColor;
        return this;
    }

    /**
     * 设置StepsViewIndicator未完成线的颜色
     *
     * @param unCompletedLineColor
     * @return
     */
    public VerticalStepView setStepsViewIndicatorUnCompletedLineColor(int unCompletedLineColor) {
        mStepsViewIndicator.setUnCompletedLineColor(unCompletedLineColor);
        return this;
    }

    /**
     * 设置StepsViewIndicator完成线的颜色
     *
     * @param completedLineColor
     * @return
     */
    public VerticalStepView setStepsViewIndicatorCompletedLineColor(int completedLineColor) {
        mStepsViewIndicator.setCompletedLineColor(completedLineColor);
        return this;
    }

    /**
     * 设置StepsViewIndicator默认图片
     *
     * @param defaultIcon
     */
    public VerticalStepView setStepsViewIndicatorDefaultIcon(Drawable defaultIcon) {
        mStepsViewIndicator.setDefaultIcon(defaultIcon);
        return this;
    }

    /**
     * 设置StepsViewIndicator已完成图片
     *
     * @param completeIcon
     */
    public VerticalStepView setStepsViewIndicatorCompleteIcon(Drawable completeIcon) {
        mStepsViewIndicator.setCompleteIcon(completeIcon);
        return this;
    }

    /**
     * 设置StepsViewIndicator正在进行中的图片
     *
     * @param attentionIcon
     */
    public VerticalStepView setStepsViewIndicatorAttentionIcon(Drawable attentionIcon) {
        mStepsViewIndicator.setAttentionIcon(attentionIcon);
        return this;
    }

    /**
     * is reverse draw 是否倒序画
     *
     * @param isReverSe default is true
     * @return
     */
    public VerticalStepView reverseDraw(boolean isReverSe) {
        this.mStepsViewIndicator.reverseDraw(isReverSe);
        return this;
    }

    /**
     * set linePadding  proportion 设置线间距的比例系数
     *
     * @param linePaddingProportion
     * @return
     */
    public VerticalStepView setLinePaddingProportion(float linePaddingProportion) {
        this.mStepsViewIndicator.setIndicatorLinePaddingProportion(linePaddingProportion);
        return this;
    }


    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    public VerticalStepView setTextSize(int textSize) {
        if (textSize > 0) {
            mTextSize = textSize;
        }
        return this;
    }

    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    public VerticalStepView setDateTextSize(int textSize) {
        if (textSize > 0) {
            mdateTextSize = textSize;
        }
        return this;
    }

    @Override
    public void ondrawIndicator() {
        if (mTextContainer != null) {
            mTextContainer.removeAllViews();//clear ViewGroup
            List<Float> complectedXPosition = mStepsViewIndicator.getCircleCenterPointPositionList();
            if (mTexts != null && complectedXPosition != null && complectedXPosition.size() > 0) {
                for (int i = 0; i < mTexts.size(); i++) {
                    mTextView = new TextView(getContext());
                    mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize);
                    mTextView.setText(mTexts.get(i));
                    mTextView.setY(complectedXPosition.get(i) - mStepsViewIndicator.getCircleRadius() / 2);
                    mTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    mDateTextView = new TextView(getContext());
                    mDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mdateTextSize);
                    mDateTextView.setText(mDates.get(i));

//                    mDateTextView.setText(mDates.get(i)+ (i==2?"\n"+"Comment":""));
                    mDateTextView.setY(mTextView.getY() + 50);
                    mDateTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

//                    mCommentTextView = new TextView(getContext());
//                    mCommentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mCommentTextSize);
//                    mCommentTextView.setText(mComments.get(i));
//                    mCommentTextView.setY(mDateTextView.getY() + 40);
//                    mCommentTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    if (i <= mComplectingPosition) {
                        mTextView.setTypeface(null, Typeface.BOLD);
                        mTextView.setTextColor(i == mComplectingPosition ? ContextCompat.getColor(getContext(), R.color.lightBlue) : mComplectedTextColor);
                        mDateTextView.setTypeface(null, Typeface.BOLD);
                        mDateTextView.setTextColor(mComplectedTextColor);
//                        mCommentTextView.setTypeface(null, Typeface.BOLD);
//                        mCommentTextView.setTextColor(mComplectedTextColor);
                    } else {
                        mTextView.setTextColor(mUnComplectedTextColor);
                        mDateTextView.setTextColor(mUnComplectedTextColor);
//                        mCommentTextView.setTextColor(mUnComplectedTextColor);
                    }

                    mTextContainer.addView(mTextView);
                    mTextContainer.addView(mDateTextView);
//                    mTextContainer.addView(mCommentTextView);
                }
            }
        }
    }
}
