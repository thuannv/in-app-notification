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
class BigNotificationInfo(
    private val image: String? = null,
    private val title: String? = null,
    context: Context? = null,
    autoDismiss: Boolean = false,
    autoDismissMillis: Long = 0
) : InAppNotificationInfo(context, autoDismiss, autoDismissMillis) {

    override fun getView(): View? {
        return context?.let { ctx ->
            ctx.layoutInflater()
                .inflate(R.layout.layout_big_notification, FrameLayout(ctx), false)
                .also { view ->
                    view.findViewById<TextView>(R.id.tvTitle)?.text = title
                    view.findViewById<ImageView>(R.id.imageView)?.apply {
                        Glide.with(this)
                            .load(image)
                            .override(dp(64f), dp(64f))
                            .into(this)
                    }
                    view.setOnTouchListener { _, event ->
                        event?.let { ev ->
                            when (ev.action) {
                                MotionEvent.ACTION_UP -> Log.e("BigNotification", "up")
                                MotionEvent.ACTION_DOWN -> Log.e("BigNotification", "down")
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
        private var title: String? = null,
        private var image: String? = null,
        private var context: Context? = null,
        private var autoDismiss: Boolean = false,
        private var autoDismissMillis: Long = 0
    ) {

        fun context(context: Context?) = apply { this.context = context }

        fun autoDismiss(autoDismiss: Boolean) = apply { this.autoDismiss = autoDismiss }

        fun autoDismissMillis(autoDismissMillis: Long) =
            apply { this.autoDismissMillis = autoDismissMillis }

        fun title(title: String?) = apply { this.title = title }

        fun image(image: String?) = apply { this.image = image }

        fun build() =
            BigNotificationInfo(image, title, context, autoDismiss, autoDismissMillis)
    }
}