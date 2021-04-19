package com.jdevzone.clipper

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.Gravity
import androidx.annotation.Nullable
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.graphics.toRectF


class ClipImage : AppCompatImageView {

    companion object {
        const val CLIP_TYPE_CIRCLE = 1
        const val CLIP_TYPE_PIN = 1
    }

    private val path = Path()
    private val clipRect = Rect()
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        isDither = true
        style = Paint.Style.STROKE
//        xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_IN)
        strokeWidth = 10f
        alpha = 10
        pathEffect = DashPathEffect(floatArrayOf(15f, 15f), 15f)
        color = Color.BLACK
    }

    private var mHorizontalPadding = 0
    private var mVerticalPadding = 0
    private var mClipRadius = 0f
    private var mCornerRadius = 0f
    private var mClipGravity = Gravity.TOP
    private var mClipType = CLIP_TYPE_CIRCLE

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
                if (typedArray.hasValue(R.styleable.ClipImage_ci_clip_radius))
                    mClipRadius =
                        typedArray.getDimension(R.styleable.ClipImage_ci_clip_radius, 0f)
                if (typedArray.hasValue(R.styleable.ClipImage_ci_corner_radius))
                    mCornerRadius =
                        typedArray.getDimension(R.styleable.ClipImage_ci_corner_radius, 0f)
                if (typedArray.hasValue(R.styleable.ClipImage_ci_vertical_padding))
                    mVerticalPadding =
                        typedArray.getDimension(R.styleable.ClipImage_ci_vertical_padding, 0f)
                            .toInt()
                if (typedArray.hasValue(R.styleable.ClipImage_ci_horizontal_padding))
                    mHorizontalPadding =
                        typedArray.getDimension(R.styleable.ClipImage_ci_horizontal_padding, 0f)
                            .toInt()
                if (typedArray.hasValue(R.styleable.ClipImage_ci_clip_gravity))
                    mClipGravity =
                        typedArray.getInt(R.styleable.ClipImage_ci_clip_gravity, Gravity.TOP)

                if (typedArray.hasValue(R.styleable.ClipImage_ci_clip_type))
                    mClipType =
                        typedArray.getInt(R.styleable.ClipImage_ci_clip_type, CLIP_TYPE_CIRCLE)

            } finally {
                typedArray.recycle()
            }

        }
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
            val path2 = if (mClipType == CLIP_TYPE_CIRCLE)
                Path().apply { getCirclePathByGravity() }
            else
                Path().apply { getPinPathByGravity() }
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
                path.lineTo(measuredWidth / 2f, 0f)
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
                path.lineTo(measuredWidth / 2f, measuredHeight.toFloat())
                path.lineTo(
                    (measuredWidth / 2f) + mClipRadius,
                    measuredHeight - (mClipRadius + mVerticalPadding)
                )
                path.close()
                addPath(path)
            }
        }

    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.clipPath(path, Region.Op.INTERSECT)
        super.onDraw(canvas)
        canvas?.drawPath(path, paint)
    }
}