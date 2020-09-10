package com.github.thuannv.inappnotification.sample

import android.content.Context
import android.view.View

abstract class InAppNotificationInfo(
    val context: Context? = null,
    val autoDismiss: Boolean = false,
    val autoDismissMillis: Long = 0
) {

    abstract fun getView(): View?
}