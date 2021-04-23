package com.jdevzone.clipper

import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat

fun View.dpToPx(@Dimension(unit = Dimension.DP) dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        resources.displayMetrics
    ).toInt()
}

fun View.contextColor(@ColorRes color: Int) = ContextCompat.getColor(context, color)