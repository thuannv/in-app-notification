package com.github.thuannv.inappnotification

import android.view.View
import androidx.annotation.LayoutRes

class Params {

    var animationDuration: Long = 100L

    var contentView: View? = null

    @LayoutRes
    var contentViewLayoutRes: Int = 0

    var swipeListener: SwipeListener? = null
}