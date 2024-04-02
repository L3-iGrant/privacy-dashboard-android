package com.github.privacydashboard.customView.stepView

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.privacydashboard.R

class VerticalStepView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr), VerticalStepViewIndicator.OnDrawIndicatorListener {
    private var mTextContainer: RelativeLayout? = null
    private var mStepsViewIndicator: VerticalStepViewIndicator? = null
    private var mTexts: List<String>? = null
    private var mDates: List<String>? = null
    private var mComments: List<String>? = null
    private var mComplectingPosition = 0
    private var mUnComplectedTextColor =
        ContextCompat.getColor(getContext(), R.color.privacyDashboard_stepview_uncompleted_text_color) //定义默认未完成文字的颜色;
    private var mComplectedTextColor =
        ContextCompat.getColor(getContext(), R.color.privacyDashboardWhite) //定义默认完成文字的颜色;
    private var mTextSize = 14 //default textSize
    private var mTextView: TextView? = null
    private var mdateTextSize = 10 //default textSize
    private var mDateTextView: TextView? = null
    private fun init() {
        val rootView: View =
            LayoutInflater.from(context).inflate(R.layout.widget_vertical_stepsview, this)
        mStepsViewIndicator =
            rootView.findViewById<View>(R.id.steps_indicator) as VerticalStepViewIndicator
        mStepsViewIndicator?.setOnDrawListener(this)
        mTextContainer = rootView.findViewById<View>(R.id.rl_text_container) as RelativeLayout
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     * @return
     */
    fun setStepViewTexts(texts: List<String>?): VerticalStepView {
        mTexts = texts
        if (texts != null) {
            mStepsViewIndicator?.setStepNum(mTexts!!.size)
        } else {
            mStepsViewIndicator?.setStepNum(0)
        }
        return this
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     * @return
     */
    fun setStepViewDates(texts: List<String>?): VerticalStepView {
        mDates = texts
        return this
    }

    /**
     * 设置显示的文字
     *
     * @param texts
     * @return
     */
    fun setStepViewComments(texts: List<String>?): VerticalStepView {
        mComments = texts
        return this
    }

    /**
     * 设置正在进行的position
     *
     * @param complectingPosition
     * @return
     */
    fun setStepsViewIndicatorComplectingPosition(complectingPosition: Int): VerticalStepView {
        mComplectingPosition = complectingPosition
        mStepsViewIndicator?.setComplectingPosition(complectingPosition)
        return this
    }

    /**
     * 设置未完成文字的颜色
     *
     * @param unComplectedTextColor
     * @return
     */
    fun setStepViewUnComplectedTextColor(unComplectedTextColor: Int): VerticalStepView {
        mUnComplectedTextColor = unComplectedTextColor
        return this
    }

    /**
     * 设置完成文字的颜色
     *
     * @param complectedTextColor
     * @return
     */
    fun setStepViewComplectedTextColor(complectedTextColor: Int): VerticalStepView {
        mComplectedTextColor = complectedTextColor
        return this
    }

    /**
     * 设置StepsViewIndicator未完成线的颜色
     *
     * @param unCompletedLineColor
     * @return
     */
    fun setStepsViewIndicatorUnCompletedLineColor(unCompletedLineColor: Int): VerticalStepView {
        mStepsViewIndicator?.setUnCompletedLineColor(unCompletedLineColor)
        return this
    }

    /**
     * 设置StepsViewIndicator完成线的颜色
     *
     * @param completedLineColor
     * @return
     */
    fun setStepsViewIndicatorCompletedLineColor(completedLineColor: Int): VerticalStepView {
        mStepsViewIndicator?.setCompletedLineColor(completedLineColor)
        return this
    }

    /**
     * 设置StepsViewIndicator默认图片
     *
     * @param defaultIcon
     */
    fun setStepsViewIndicatorDefaultIcon(defaultIcon: Drawable?): VerticalStepView {
        mStepsViewIndicator?.setDefaultIcon(defaultIcon)
        return this
    }

    /**
     * 设置StepsViewIndicator已完成图片
     *
     * @param completeIcon
     */
    fun setStepsViewIndicatorCompleteIcon(completeIcon: Drawable?): VerticalStepView {
        mStepsViewIndicator?.setCompleteIcon(completeIcon)
        return this
    }

    /**
     * 设置StepsViewIndicator正在进行中的图片
     *
     * @param attentionIcon
     */
    fun setStepsViewIndicatorAttentionIcon(attentionIcon: Drawable?): VerticalStepView {
        mStepsViewIndicator?.setAttentionIcon(attentionIcon)
        return this
    }

    /**
     * is reverse draw 是否倒序画
     *
     * @param isReverSe default is true
     * @return
     */
    fun reverseDraw(isReverSe: Boolean): VerticalStepView {
        mStepsViewIndicator?.reverseDraw(isReverSe)
        return this
    }

    /**
     * set linePadding  proportion 设置线间距的比例系数
     *
     * @param linePaddingProportion
     * @return
     */
    fun setLinePaddingProportion(linePaddingProportion: Float): VerticalStepView {
        mStepsViewIndicator?.setIndicatorLinePaddingProportion(linePaddingProportion)
        return this
    }

    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    fun setTextSize(textSize: Int): VerticalStepView {
        if (textSize > 0) {
            mTextSize = textSize
        }
        return this
    }

    /**
     * set textSize
     *
     * @param textSize
     * @return
     */
    fun setDateTextSize(textSize: Int): VerticalStepView {
        if (textSize > 0) {
            mdateTextSize = textSize
        }
        return this
    }

    override fun ondrawIndicator() {
        if (mTextContainer != null) {
            mTextContainer!!.removeAllViews() //clear ViewGroup
            val complectedXPosition: List<Float>? =
                mStepsViewIndicator?.circleCenterPointPositionList
            if (mTexts != null && complectedXPosition != null && complectedXPosition.isNotEmpty()) {
                for (i in mTexts!!.indices) {
                    mTextView = TextView(context)
                    mTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, mTextSize.toFloat())
                    mTextView!!.text = mTexts!![i]
                    mTextView!!.y =
                        complectedXPosition[i] - (mStepsViewIndicator?.circleRadius ?: 0f)
                    mTextView!!.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    mDateTextView = TextView(context)
                    mDateTextView!!.setTextSize(TypedValue.COMPLEX_UNIT_SP, mdateTextSize.toFloat())
                    mDateTextView!!.text = mDates!![i]

//                    mDateTextView.setText(mDates.get(i)+ (i==2?"\n"+"Comment":""));
                    mDateTextView!!.y = mTextView!!.y + 60
                    mDateTextView!!.layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )

//                    mCommentTextView = new TextView(getContext());
//                    mCommentTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, mCommentTextSize);
//                    mCommentTextView.setText(mComments.get(i));
//                    mCommentTextView.setY(mDateTextView.getY() + 40);
//                    mCommentTextView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    if (i <= mComplectingPosition) {
                        mTextView!!.setTypeface(null, Typeface.BOLD)
                        mTextView!!.setTextColor(
                            if (i == mComplectingPosition) ContextCompat.getColor(
                                context, R.color.privacyDashboard_accent_blue
                            ) else mComplectedTextColor
                        )
                        mDateTextView!!.setTypeface(null, Typeface.NORMAL)
                        mDateTextView!!.setTextColor(mComplectedTextColor)
                        //                        mCommentTextView.setTypeface(null, Typeface.BOLD);
//                        mCommentTextView.setTextColor(mComplectedTextColor);
                    } else {
                        mTextView!!.setTextColor(mUnComplectedTextColor)
                        mDateTextView!!.setTextColor(mUnComplectedTextColor)
                        //                        mCommentTextView.setTextColor(mUnComplectedTextColor);
                    }
                    mTextContainer!!.addView(mTextView)
                    mTextContainer!!.addView(mDateTextView)
                    //                    mTextContainer.addView(mCommentTextView);
                }
            }
        }
    }

    //    private int mCommentTextSize = 10;//default textSize
    //    private TextView mCommentTextView;
    init {
        init()
    }
}