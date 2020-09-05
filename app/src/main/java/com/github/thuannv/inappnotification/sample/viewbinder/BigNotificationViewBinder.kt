package com.github.thuannv.inappnotification.sample.viewbinder

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.thuannv.inappnotification.sample.R
import com.github.thuannv.inappnotification.sample.model.BigNotificationModel
import com.github.thuannv.inappnotification.utils.dp

class BigNotificationViewBinder(private val model: BigNotificationModel) :
    ViewBinder<BigNotificationModel> {

    override fun bindView(view: View?) {
        view?.apply {
            findViewById<TextView>(R.id.tvTitle)?.text = model.title
            findViewById<ImageView>(R.id.imageView)?.apply {
                Glide.with(this)
                    .load(model.image)
                    .override(dp(64f), dp(64f))
                    .into(this)
            }
            setOnClickListener { Log.e("BigNotification", "Clicked") }
            setOnLongClickListener {
                Log.e("BigNotification", "Long Clicked")
                true
            }
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.e("BigNotification", "up")
                    }
                    MotionEvent.ACTION_DOWN -> {
                        Log.e("BigNotification", "down")
                    }
                }
                false
            }
        }
    }
}