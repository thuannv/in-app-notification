package com.github.thuannv.inappnotification.sample

import android.content.Context
import android.os.Handler
import android.widget.FrameLayout
import com.github.thuannv.inappnotification.sample.model.NormalNotificationModel
import com.github.thuannv.inappnotification.sample.model.BigNotificationModel
import com.github.thuannv.inappnotification.sample.model.NotificationWithTimeModel
import com.github.thuannv.inappnotification.sample.viewbinder.BigNotificationViewBinder
import com.github.thuannv.inappnotification.sample.viewbinder.NormalNotificationViewBinder
import com.github.thuannv.inappnotification.sample.viewbinder.NotificationWithTimeViewBinder
import com.github.thuannv.inappnotification.utils.layoutInflater
import java.util.*


object Scheduler {

    private val handler = Handler()

    private val once = Once()

    private val random = Random()

    private var task: Runnable? = null

    fun schedule(context: Context) {
        val data1 = NormalNotificationModel(
            R.drawable.ic_account,
            "Notification",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt."
        )

        val data2 = BigNotificationModel(
            "https://cdn.pixabay.com/photo/2016/11/29/05/45/astronomy-1867616__340.jpg",
            "Notification",
        )

        val data3 = NotificationWithTimeModel(
            "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/1024px-Instagram_icon.png",
            "Notification",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.",
            "19:53"
        )

        val notificationViewBinders = listOf(
            NormalNotificationViewBinder(
                data1
            ),
            BigNotificationViewBinder(
                data2
            ),
            NotificationWithTimeViewBinder(
                data3
            )
        )

        val notificationViews = listOf(
            R.layout.layout_notification,
            R.layout.layout_big_notification,
            R.layout.layout_notification_with_time
        )

        once.execute {
            task = Runnable {
                val randomInt = random.nextInt(notificationViewBinders.size)
                val contentView = context.layoutInflater().inflate(notificationViews[randomInt], FrameLayout(context), false)
                notificationViewBinders.getOrNull(randomInt)?.bindView(contentView)
                NotificationManager.notify(contentView)
                task?.apply {
//                    handler.postDelayed(this, random.nextInt(5000).toLong())
//                    handler.postDelayed(this, 10000L)
                }
            }
            task?.apply { handler.post(this) }
        }
    }
}