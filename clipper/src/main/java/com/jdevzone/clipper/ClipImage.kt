package com.jdevzone.clipper

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toRectF


class ClipImage : AppCompatImageView {

    companion object {
        const val CLIP_TYPE_CIRCLE = 1
        const val CLIP_TYPE_PIN = 2

        const val STROKE_TYPE_SOLID = 11
        const val STROKE_TYPE_DASHED = 12

    }

    //Customisable Property Variables
    private var mHorizontalPadding = 0
    private var mVerticalPadding = 0
    private var mClipRadius = 0f
    private var mCornerRadius = 0f
    private var mClipGravity = Gravity.TOP
    private var mClipType = CLIP_TYPE_CIRCLE

    //Stroke related properties
    private var mStrokeWidth = 0f
    private var mStrokePathPhase = 15f
    private var mStrokePathInterval = 15f
    private var mStrokeColor = Color.BLACK
    private var mStrokeType = STROKE_TYPE_SOLID

    //Drawing elements
    private val path = Path()
    private val clipRect = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    constructor(context: Context) : super(context) {
        initUI()
    }

    constructor(context: Context, @Nullable attrs: AttributeSet) : super(context, attrs) {
        initUI()
        setAttrsValues(attrs)
    }

    private fun setAttrsValues(attrs: AttributeSet) {
        attrs.apply {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.ClipImage)
            try {
                initPropertiesFromAttrs(typedArray)
            } finally {
                typedArray.recycle()
                initStrokePaint()
            }

        }
    }

    private fun initPropertiesFromAttrs(a: TypedArray) {
        mClipRadius = a.safeDimensionValue(R.styleable.ClipImage_ci_clip_radius,0f)
        mCornerRadius = a.safeDimensionValue(R.styleable.ClipImage_ci_corner_radius,0f)
        mVerticalPadding = a.safeDimensionValue(R.styleable.ClipImage_ci_vertical_padding,0f).toInt()
        mHorizontalPadding = a.safeDimensionValue(R.styleable.ClipImage_ci_horizontal_padding,0f).toInt()
        mClipGravity = a.safeIntValue(R.styleable.ClipImage_ci_clip_gravity,Gravity.TOP)
        mClipType = a.safeIntValue(R.styleable.ClipImage_ci_clip_type,CLIP_TYPE_CIRCLE)
        // Stroke Values
        mStrokeWidth = a.safeDimensionValue(R.styleable.ClipImage_ci_stroke_width,0f)
        mStrokePathInterval = a.safeDimensionValue(R.styleable.ClipImage_ci_stroke_path_interval,0f)
        mStrokePathPhase = a.safeDimensionValue(R.styleable.ClipImage_ci_stroke_path_phase,0f)
        mStrokeType = a.safeIntValue(R.styleable.ClipImage_ci_stroke_type,STROKE_TYPE_SOLID)
        val strokeColor = a.safeResourceValue(R.styleable.ClipImage_ci_stroke_color,android.R.color.black)
        mStrokeColor = contextColor(strokeColor)
    }

    constructor(context: Context, @Nullable attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initUI()
        setAttrsValues(attrs)
    }

    private fun initUI() {
        scaleType = ScaleType.CENTER_CROP
    }

    private fun initStrokePaint() {
        paint.apply {
            isDither = true
            style = Paint.Style.STROKE
            strokeWidth = mStrokeWidth
            pathEffect = (mStrokeType == STROKE_TYPE_DASHED).elvis(
                DashPathEffect(
                    floatArrayOf(mStrokePathInterval, mStrokePathInterval),
                    mStrokePathPhase
                ),
                null
            )
            color = mStrokeColor
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
//        Log.e("TAG", "onSizeChanged:  $w, $h, $oldw, $oldh")
        if (w > 0 && h > 0) {
            setClippingRectByGravity()
            val path1 = Path().apply {
                addRoundRect(
                    clipRect.toRectF(),
                    mCornerRadius,
                    mCornerRadius,
                    Path.Direction.CW
                )
            }
            val path2 = (mClipType == CLIP_TYPE_CIRCLE).elvis(
                Path().apply { getCirclePathByGravity() },
                Path().apply { getPinPathByGravity() })

            path1.op(path2, Path.Op.UNION)
            path.addPath(path1)
        }

    }

    private fun setClippingRectByGravity() {
        when (mClipGravity) {
            Gravity.TOP -> clipRect.set(
                mHorizontalPadding,
                mClipRadius.toInt(),
                measuredWidth - mHorizontalPadding,
                measuredHeight - mVerticalPadding
            )
            Gravity.BOTTOM -> clipRect.set(
                mHorizontalPadding,
                mVerticalPadding,
                measuredWidth - mHorizontalPadding,
                measuredHeight - (mVerticalPadding + mClipRadius.toInt())
            )
        }

    }

    private fun Path.getCirclePathByGravity() {
        when (mClipGravity) {
            Gravity.TOP -> addCircle(
                measuredWidth / 2f,
                mClipRadius + mVerticalPadding,
                mClipRadius,
                Path.Direction.CW
            )
            Gravity.BOTTOM -> addCircle(
                measuredWidth / 2f,
                measuredHeight - (mClipRadius + mVerticalPadding),
                mClipRadius,
                Path.Direction.CW
            )
        }
    }

    private fun Path.getPinPathByGravity() {

        when (mClipGravity) {

            Gravity.TOP -> {
                val path = Path()
                path.moveTo((measuredWidth / 2f) - mClipRadius, mClipRadius + mVerticalPadding)
                path.lineTo(measuredWidth / 2f, mVerticalPadding.toFloat())
                path.lineTo((measuredWidth / 2f) + mClipRadius, mClipRadius + mVerticalPadding)
                path.close()
                addPath(path)
            }
            Gravity.BOTTOM -> {
                val path = Path()
                path.moveTo(
                    (measuredWidth / 2f) - mClipRadius,
                    measuredHeight - (mClipRadius + mVerticalPadding)
                )
                path.lineTo(measuredWidth / 2f, measuredHeight.toFloat() - mVerticalPadding)
                path.lineTo(
                    (measuredWidth / 2f) + mClipRadius,
                    measuredHeight - (mClipRadius + mVerticalPadding)
                )
                path.close()
                addPath(path)
            }
        }

    }

    @Suppress("DEPRECATION")
    override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(path, Region.Op.INTERSECT)
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }


/*--------------------------------- Public Setters ---------------------------------*/

    fun setHorizontalPadding(@Dimension value: Int) {
        mHorizontalPadding = dpToPx(value)
        invalidate()
    }

    fun setVerticalPadding(@Dimension value: Int) {
        mVerticalPadding = dpToPx(value)
        invalidate()
    }

    fun setClipRadius(@Dimension value: Int) {
        mClipRadius = dpToPx(value).toFloat()
        invalidate()
    }

    fun setCornerRadius(@Dimension value: Int) {
        mCornerRadius = dpToPx(value).toFloat()
        invalidate()
    }

    fun setClipGravity(gravity: Int) {
        if (gravity in arrayOf(Gravity.TOP, Gravity.BOTTOM)) {
            mClipGravity = gravity
            invalidate()
        }
    }

    fun setClipType(clipType: Int) {
        if (clipType in arrayOf(CLIP_TYPE_CIRCLE, CLIP_TYPE_CIRCLE)) {
            mClipType = clipType
            invalidate()
        }
    }

    fun setStrokeType(strokeType: Int) {
        if (strokeType in arrayOf(STROKE_TYPE_DASHED, STROKE_TYPE_SOLID)) {
            mStrokeType = strokeType
            initStrokePaint()
            invalidate()
        }
    }

    fun setStrokeWidth(@Dimension value: Int) {
        mStrokeWidth = dpToPx(value).toFloat()
        initStrokePaint()
        invalidate()
    }

    fun setStrokePathInterval(@Dimension value: Int) {
        mStrokePathInterval = dpToPx(value).toFloat()
        initStrokePaint()
        invalidate()
    }

    fun setStrokePathPhase(@Dimension value: Int) {
        mStrokePathPhase = dpToPx(value).toFloat()
        initStrokePaint()
        invalidate()
    }

    fun setStrokeColor(@ColorRes value: Int) {
        mStrokeColor = contextColor(value)
        initStrokePaint()
        invalidate()
    }


}