package com.github.thuannv.inappnotification.sample.viewbinder

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.thuannv.inappnotification.sample.R
import com.github.thuannv.inappnotification.sample.model.NotificationWithTimeModel
import com.github.thuannv.inappnotification.utils.dp

class NotificationWithTimeViewBinder(private val model: NotificationWithTimeModel) :
    ViewBinder<NotificationWithTimeModel> {
    override fun bindView(view: View?) {
        view?.apply {
            findViewById<TextView>(R.id.tvTitle)?.text = model.title
            findViewById<TextView>(R.id.tvTime)?.text = model.time
            findViewById<TextView>(R.id.tvDescription)?.text = model.description
            findViewById<ImageView>(R.id.icon)?.apply {
                Glide.with(this)
                    .load(model.image)
                    .override(dp(64f), dp(64f))
                    .into(this)
            }
            setOnClickListener { Log.e("TimNotification", "Clicked") }
            setOnLongClickListener {
                Log.e("TimNotification", "Long Clicked")
                true
            }
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.e("TimNotification", "up")
                    }
                    MotionEvent.ACTION_DOWN -> {
                        Log.e("TimNotification", "down")
                    }
                }
                false
            }
        }
    }
}