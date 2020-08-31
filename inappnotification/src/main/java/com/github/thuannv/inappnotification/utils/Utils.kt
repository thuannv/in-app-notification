
@file:JvmName("Utils")
package com.github.thuannv.inappnotification.utils

import android.app.Activity
import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager

fun Context.windowManager() =  getSystemService(Context.WINDOW_SERVICE) as WindowManager

fun Context.layoutInflater(): LayoutInflater {
    return if (this is Activity) {
        this.layoutInflater
    } else {
        LayoutInflater.from(this)
    }
}

fun Context.dp(value: Float) = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, resources.displayMetrics).toInt()

fun Context.statusBarHeight(): Int {
    var statusBarHeight = dp(24f)
    val statusBarHeightResourceID = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (statusBarHeightResourceID > 0) {
        statusBarHeight = resources.getDimensionPixelSize(statusBarHeightResourceID)
    }
    return statusBarHeight
}

fun Activity.actionBarHeight() = actionBar?.height ?: dp(56f)

fun View.dp(value: Float) = context.dp(value)

fun View.windowManager() = context.windowManager()

fun WindowManager.safelyRemoveView(v: View?) = try {
    v?.apply { removeViewImmediate(v) }
} catch (e: Exception) {}


fun WindowManager.safelyAddView(v: View?, layoutParams: WindowManager.LayoutParams) = try {
    v?.apply { addView(v, layoutParams) }
} catch (e: Exception) {}

fun WindowManager.safelyUpdateView(v: View?, layoutParams: WindowManager.LayoutParams) = try {
    v?.apply { updateViewLayout(v, layoutParams) }
} catch (e: Exception) {}