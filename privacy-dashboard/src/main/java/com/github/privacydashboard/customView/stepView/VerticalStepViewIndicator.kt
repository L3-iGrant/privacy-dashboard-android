package com.github.privacydashboard.customView.stepView

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.github.privacydashboard.R

class VerticalStepViewIndicator @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) :
    View(context, attrs, defStyle) {
    private val TAG_NAME = this.javaClass.simpleName

    //define default height
    private val defaultStepIndicatorNum =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40f, resources.displayMetrics)
            .toInt()
    private var mCompletedLineHeight //define completed line height
            = 0f

    /**
     *  get circle radius
     *
     * @return
     */
    var circleRadius //define circle radius
            = 0f
        private set
    private var mCompleteIcon //define default completed icon
            : Drawable? = null
    private var mAttentionIcon //define default underway icon
            : Drawable? = null
    private var mDefaultIcon //define default unCompleted icon
            : Drawable? = null
    private var mCenterX
            = 0f
    private var mLeftY = 0f
    private var mRightY = 0f
    private var mStepNum = 0 //there are currently few step
    private var mLinePadding //define the spacing between the two circles
            = 0f
    private var mCircleCenterPointPositionList //define all of circles center point list
            : MutableList<Float>? = null
    private var mUnCompletedPaint //define mUnCompletedPaint
            : Paint? = null
    private var mCompletedPaint //define mCompletedPaint
            : Paint? = null
    private var mUnCompletedLineColor = ContextCompat.getColor(
        getContext(),
        R.color.privacyDashboard_stepview_uncompleted
    ) //define mUnCompletedLineColor
    private var mCompletedLineColor = Color.WHITE //define mCompletedLineColor
    private var mEffects: PathEffect? = null
    private var mComplectingPosition //underway position
            = 0
    private var mPath: Path? = null
    private var mOnDrawListener: OnDrawIndicatorListener? = null
    private var mRect: Rect? = null
    private var mHeight //this view dynamic height
            = 0
    private var mIsReverseDraw //is reverse draw this view;
            = false

    /**
     * Set up monitoring
     *
     * @param onDrawListener
     */
    fun setOnDrawListener(onDrawListener: OnDrawIndicatorListener?) {
        mOnDrawListener = onDrawListener
    }

    /**
     * init
     */
    private fun init() {
        mPath = Path()
        mEffects = DashPathEffect(floatArrayOf(8f, 8f, 8f, 8f), 1f)
        mCircleCenterPointPositionList = ArrayList() //初始化
        mUnCompletedPaint = Paint()
        mCompletedPaint = Paint()
        mUnCompletedPaint!!.isAntiAlias = true
        mUnCompletedPaint!!.color = mUnCompletedLineColor
        mUnCompletedPaint!!.style = Paint.Style.STROKE
        mUnCompletedPaint!!.strokeWidth = 2f
        mCompletedPaint!!.isAntiAlias = true
        mCompletedPaint!!.color = mCompletedLineColor
        mCompletedPaint!!.style = Paint.Style.STROKE
        mCompletedPaint!!.strokeWidth = 2f
        mUnCompletedPaint!!.pathEffect = mEffects
        mCompletedPaint!!.style = Paint.Style.FILL

        //set mCompletedLineHeight
        mCompletedLineHeight = 0.05f * defaultStepIndicatorNum
        //set mCircleRadius
        circleRadius = 0.28f * defaultStepIndicatorNum
        //set mLinePadding
        mLinePadding = 0.85f * defaultStepIndicatorNum
        mCompleteIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_stepview_completed)
        mAttentionIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_stepview_current)
        mDefaultIcon =
            ContextCompat.getDrawable(context, R.drawable.ic_stepview_uncompleted)
        mIsReverseDraw = true //default draw
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.i(TAG_NAME, "onMeasure")
        var width = defaultStepIndicatorNum
        mHeight = 0
        if (mStepNum > 0) {
            //dynamic measure VerticalStepViewIndicator height
            mHeight =
                (paddingTop + paddingBottom + circleRadius * 2 * mStepNum + (mStepNum - 1) * mLinePadding).toInt()
        }
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = Math.min(width, MeasureSpec.getSize(widthMeasureSpec))
        }
        setMeasuredDimension(width, mHeight)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        Log.i(TAG_NAME, "onSizeChanged")
        mCenterX = (width / 2).toFloat()
        mLeftY = mCenterX - mCompletedLineHeight / 2
        mRightY = mCenterX + mCompletedLineHeight / 2
        for (i in 0 until mStepNum) {
            //reverse draw VerticalStepViewIndicator
            if (mIsReverseDraw) {
                mCircleCenterPointPositionList!!.add(mHeight - (circleRadius + i * circleRadius * 2 + i * mLinePadding))
            } else {
                mCircleCenterPointPositionList!!.add(circleRadius + i * circleRadius * 2 + i * mLinePadding)
            }
        }
        /**
         * set listener
         */
        if (mOnDrawListener != null) {
            mOnDrawListener!!.ondrawIndicator()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        Log.i(TAG_NAME, "onDraw")
        if (mOnDrawListener != null) {
            mOnDrawListener!!.ondrawIndicator()
        }
        mUnCompletedPaint!!.color = mUnCompletedLineColor
        mCompletedPaint!!.color = mCompletedLineColor

        //------------------------------draw line-----------------------------------------------
        for (i in 0 until mCircleCenterPointPositionList!!.size - 1) {
            //ComplectedXPosition
            val preComplectedXPosition = mCircleCenterPointPositionList!![i]
            //ComplectedXPosition
            val afterComplectedXPosition = mCircleCenterPointPositionList!![i + 1]
            if (i < mComplectingPosition)
            {
                if (mIsReverseDraw) {
                    canvas.drawRect(
                        mLeftY,
                        afterComplectedXPosition + circleRadius - 10,
                        mRightY,
                        preComplectedXPosition - circleRadius + 10,
                        mCompletedPaint!!
                    )
                } else {
                    canvas.drawRect(
                        mLeftY,
                        preComplectedXPosition + circleRadius - 10,
                        mRightY,
                        afterComplectedXPosition - circleRadius + 10,
                        mCompletedPaint!!
                    )
                }
            } else {
                if (mIsReverseDraw) {
                    mPath!!.moveTo(mCenterX, afterComplectedXPosition + circleRadius)
                    mPath!!.lineTo(mCenterX, preComplectedXPosition - circleRadius)
                    canvas.drawPath(mPath!!, mUnCompletedPaint!!)
                } else {
                    mPath!!.moveTo(mCenterX, preComplectedXPosition + circleRadius)
                    mPath!!.lineTo(mCenterX, afterComplectedXPosition - circleRadius)
                    canvas.drawPath(mPath!!, mUnCompletedPaint!!)
                }
            }
        }
        //-----------------------------draw line-----------------------------------------------

        //---------------------------draw icon-----------------------------------------------
        for (i in mCircleCenterPointPositionList!!.indices) {
            val currentComplectedXPosition = mCircleCenterPointPositionList!![i]
            mRect = Rect(
                (mCenterX - circleRadius).toInt(),
                (currentComplectedXPosition - circleRadius).toInt(),
                (mCenterX + circleRadius).toInt(),
                (currentComplectedXPosition + circleRadius).toInt()
            )
            if (i < mComplectingPosition) {
                mCompleteIcon!!.bounds = mRect!!
                mCompleteIcon!!.draw(canvas)
            } else if (i == mComplectingPosition && mCircleCenterPointPositionList!!.size != 1) {
                mCompletedPaint!!.color = Color.WHITE
                canvas.drawCircle(
                    mCenterX, currentComplectedXPosition, circleRadius * 1.1f,
                    mCompletedPaint!!
                )
                mAttentionIcon!!.bounds = mRect!!
                mAttentionIcon!!.draw(canvas)
            } else {
                mDefaultIcon!!.bounds = mRect!!
                mDefaultIcon!!.draw(canvas)
            }
        }
        //---------------------------draw icon-----------------------------------------------
    }

    /**
     * Get the location of all dots
     *
     * @return
     */
    val circleCenterPointPositionList: List<Float>?
        get() = mCircleCenterPointPositionList

    /**
     * Set the number of process steps
     *
     * @param stepNum 流程步数
     */
    fun setStepNum(stepNum: Int) {
        mStepNum = stepNum
        requestLayout()
    }

    /**
     * set linePadding proportion
     *
     * @param linePaddingProportion
     */
    fun setIndicatorLinePaddingProportion(linePaddingProportion: Float) {
        mLinePadding = linePaddingProportion * defaultStepIndicatorNum
    }

    /**
     * Set ongoing position
     *
     * @param complectingPosition
     */
    fun setComplectingPosition(complectingPosition: Int) {
        mComplectingPosition = complectingPosition
        requestLayout()
    }

    /**
     * Set the color of unfinished lines
     *
     * @param unCompletedLineColor
     */
    fun setUnCompletedLineColor(unCompletedLineColor: Int) {
        mUnCompletedLineColor = unCompletedLineColor
    }

    /**
     * Set the color of completed lines
     *
     * @param completedLineColor
     */
    fun setCompletedLineColor(completedLineColor: Int) {
        mCompletedLineColor = completedLineColor
    }

    /**
     * is reverse draw
     */
    fun reverseDraw(isReverseDraw: Boolean) {
        mIsReverseDraw = isReverseDraw
        invalidate()
    }

    /**
     * Set default image
     *
     * @param defaultIcon
     */
    fun setDefaultIcon(defaultIcon: Drawable?) {
        mDefaultIcon = defaultIcon
    }

    /**
     * Setup completed picture
     *
     * @param completeIcon
     */
    fun setCompleteIcon(completeIcon: Drawable?) {
        mCompleteIcon = completeIcon
    }

    /**
     * Set up a picture in progress
     *
     * @param attentionIcon
     */
    fun setAttentionIcon(attentionIcon: Drawable?) {
        mAttentionIcon = attentionIcon
    }

    interface OnDrawIndicatorListener {
        fun ondrawIndicator()
    }

    init {
        init()
    }
}