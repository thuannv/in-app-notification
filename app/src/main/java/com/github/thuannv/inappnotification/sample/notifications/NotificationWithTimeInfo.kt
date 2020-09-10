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
class NotificationWithTimeInfo(
    private val image: String = "",
    private val title: String = "",
    private val description: String = "",
    private val time: String = "",
    context: Context? = null,
    autoDismiss: Boolean = false,
    autoDismissMillis: Long = 0,
) : InAppNotificationInfo(context, autoDismiss, autoDismissMillis) {

    override fun getView(): View? {
        return context?.let { ctx ->
            ctx.layoutInflater()
                .inflate(R.layout.layout_notification_with_time, FrameLayout(ctx), false)
                .also { view ->
                    view.findViewById<TextView>(R.id.tvTitle)?.text = title
                    view.findViewById<TextView>(R.id.tvTime)?.text = time
                    view.findViewById<TextView>(R.id.tvDescription)?.text = description
                    view.findViewById<ImageView>(R.id.icon)?.apply {
                        Glide.with(this)
                            .load(image)
                            .override(dp(64f), dp(64f))
                            .into(this)
                    }
                    view.setOnTouchListener { _, event ->
                        event?.let { ev ->
                            when (ev.action) {
                                MotionEvent.ACTION_UP -> Log.e("TimeNotification", "up")
                                MotionEvent.ACTION_DOWN -> Log.e("TimeNotification", "down")
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
        private var image: String = "",
        private var title: String = "",
        private var description: String = "",
        private var time: String = "",
        private var context: Context? = null,
        private var autoDismiss: Boolean = false,
        private var autoDismissMillis: Long = 0
    ) {

        fun image(image: String) = apply { this.image = image }

        fun title(title: String) = apply { this.title = title }

        fun description(desc: String) = apply { this.description = desc }

        fun time(time: String) = apply { this.time = time }

        fun context(context: Context?) = apply { this.context = context }

        fun autoDismiss(autoDismiss: Boolean) = apply { this.autoDismiss = autoDismiss }

        fun autoDismissMillis(autoDismissMillis: Long) =
            apply { this.autoDismissMillis = autoDismissMillis }

        fun build() = NotificationWithTimeInfo(
            image,
            title,
            description,
            time,
            context,
            autoDismiss,
            autoDismissMillis
        )
    }
}