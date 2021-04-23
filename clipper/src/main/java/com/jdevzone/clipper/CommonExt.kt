package com.jdevzone.clipper

import android.content.res.TypedArray

fun <T> Boolean.elvis(ifTrue: T, ifFalse: T): T = if (this) ifTrue else ifFalse


fun TypedArray.safeDimensionValue(value: Int, defValue: Float): Float {
    return if (hasValue(value)) getDimension(value, defValue) else defValue
}

fun TypedArray.safeIntValue(value: Int, defValue: Int): Int {
    return if (hasValue(value)) getInt(value, defValue) else defValue
}

fun TypedArray.safeResourceValue(value: Int, defValue: Int): Int {
    return if (hasValue(value)) getResourceId(value, defValue) else defValue
}