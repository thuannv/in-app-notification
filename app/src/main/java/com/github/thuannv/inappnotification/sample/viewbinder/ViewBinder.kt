package com.github.thuannv.inappnotification.sample.viewbinder

import android.view.View

interface ViewBinder<T> {
    fun bindView(view: View?)
}