package com.github.thuannv.inappnotification.sample.notifications

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.github.thuannv.inappnotification.sample.InAppNotificationInfo
import com.github.thuannv.inappnotification.sample.R
import com.github.thuannv.inappnotification.utils.dp
import com.github.thuannv.inappnotification.utils.layoutInflater

@SuppressLint("ClickableViewAccessibility")
class NormalNotificationInfo(
    private val title: String,
    private val description: String,
    context: Context? = null,
    autoDismiss: Boolean = false,
    autoDismissMillis: Long = 0,
) : InAppNotificationInfo(context, autoDismiss, autoDismissMillis) {

    override fun getView(): View? {
        return context?.let { ctx ->
            ctx.layoutInflater()
                .inflate(R.layout.layout_notification, FrameLayout(ctx), false)
                .also { view ->
                    view.findViewById<TextView>(R.id.first_line_text)?.text = title
                    view.findViewById<TextView>(R.id.second_line_text)?.text = description
                    view.findViewById<ImageView>(R.id.icon)?.apply {
                        Glide.with(this)
                            .load(R.drawable.ic_account)
                            .override(dp(64f), dp(64f))
                            .into(this)
                    }
                    view.setOnTouchListener { _, event ->
                        event?.let { ev ->
                            when (ev.action) {
                                MotionEvent.ACTION_UP -> Log.e("NormalNotification", "up")
                                MotionEvent.ACTION_DOWN -> Log.e("NormalNotification", "down")
                            }
                            true
                        } ?: false
                    }
                }
        }
    }

    /**
     * [Builder]
     */
    class Builder(
        private var title: String = "",
        private var description: String = "",
        private var context: Context? = null,
        private var autoDismiss: Boolean = false,
        private var autoDismissMillis: Long = 0
    ) {

        fun title(title: String) = apply { this.title = title }

        fun description(desc: String) = apply { this.description = desc }

        fun context(context: Context?) = apply { this.context = context }

        fun autoDismiss(autoDismiss: Boolean) = apply { this.autoDismiss = autoDismiss }

        fun autoDismissMillis(autoDismissMillis: Long) =
            apply { this.autoDismissMillis = autoDismissMillis }

        fun build() =
            NormalNotificationInfo(title, description, context, autoDismiss, autoDismissMillis)
    }
}