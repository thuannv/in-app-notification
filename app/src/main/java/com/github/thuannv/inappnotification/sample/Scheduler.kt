package com.github.thuannv.inappnotification.sample

import android.content.Context
import android.os.Handler
import com.github.thuannv.inappnotification.sample.notifications.BigNotificationInfo
import com.github.thuannv.inappnotification.sample.notifications.NormalNotificationInfo
import com.github.thuannv.inappnotification.sample.notifications.NotificationWithTimeInfo
import com.github.thuannv.inappnotification.utils.Once
import java.util.*


object Scheduler {

    private val handler = Handler()

    private val once = Once()

    private val random = Random()

    private var task: Runnable? = null

    private fun build(context: Context, notificationType: Int): InAppNotificationInfo? {
        return when(notificationType) {
            0 -> NormalNotificationInfo.Builder()
                .title("Notification")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.")
                .context(context)
                .autoDismiss(true)
                .autoDismissMillis(15000L)
                .build()

            1 -> BigNotificationInfo.Builder()
                .title("Notification")
                .image("https://cdn.pixabay.com/photo/2016/11/29/05/45/astronomy-1867616__340.jpg")
                .context(context)
                .autoDismiss(true)
                .autoDismissMillis(15000L)
                .build()

            2 -> NotificationWithTimeInfo.Builder()
                .title("Notification")
                .description("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.")
                .image("https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/1024px-Instagram_icon.png")
                .time("19:53")
                .context(context)
                .autoDismiss(true)
                .autoDismissMillis(15000L)
                .build()

            else -> null
        }
    }

    fun schedule(context: Context) {
        once.execute {
            task = Runnable {
                InAppNotificationManager.notify(build(context, random.nextInt(3)))
                task?.apply { handler.postDelayed(this, random.nextInt(5000).toLong()) }
            }
            task?.apply { handler.post(this) }
        }
    }
}