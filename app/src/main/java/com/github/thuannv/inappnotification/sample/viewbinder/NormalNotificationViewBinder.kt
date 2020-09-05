package com.github.thuannv.inappnotification.sample.viewbinder

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.thuannv.inappnotification.sample.R
import com.github.thuannv.inappnotification.sample.model.NormalNotificationModel
import com.github.thuannv.inappnotification.utils.dp

class NormalNotificationViewBinder(private val model: NormalNotificationModel) :
    ViewBinder<NormalNotificationModel> {
    override fun bindView(view: View?) {
        view?.apply {
            findViewById<TextView>(R.id.first_line_text)?.text = model.title
            findViewById<TextView>(R.id.second_line_text)?.text = model.description
            findViewById<ImageView>(R.id.icon)?.apply {
                Glide.with(this)
                    .load(R.drawable.ic_account)
                    .override(dp(64f), dp(64f))
                    .into(this)
            }
            setOnClickListener { Log.e("NormalNotification", "Clicked") }
            setOnLongClickListener {
                Log.e("NormalNotification", "Long Clicked")
                true
            }
            setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_UP -> {
                        Log.e("NormalNotification", "up")
                    }
                    MotionEvent.ACTION_DOWN -> {
                        Log.e("NormalNotification", "down")
                    }
                }
                false
            }
        }
    }
}