package com.github.thuannv.inappnotification.sample

import android.content.Context
import android.os.Handler
import android.view.View
import android.widget.FrameLayout
import com.bumptech.glide.Glide
import com.github.thuannv.inappnotification.sample.model.SampleModel1
import com.github.thuannv.inappnotification.sample.model.SampleModel2
import com.github.thuannv.inappnotification.sample.model.SampleModel3
import com.github.thuannv.inappnotification.utils.layoutInflater
import kotlinx.android.synthetic.main.layout_notification2.view.tvTitle
import kotlinx.android.synthetic.main.layout_notification3.view.*
import kotlinx.android.synthetic.main.layout_notification3.view.icon
import java.util.*


object Scheduler {

    private val handler = Handler()

    private val once = Once()

    private val random = Random()

    private var task: Runnable? = null

    private val data1 = SampleModel1(
        R.drawable.ic_account,
        "Notification",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt."
    )

    private val data2 = SampleModel2(
        "https://cdn.pixabay.com/photo/2016/11/29/05/45/astronomy-1867616__340.jpg",
        "Notification",
    )

    private val data3 = SampleModel3(
        "https://upload.wikimedia.org/wikipedia/commons/thumb/a/a5/Instagram_icon.png/1024px-Instagram_icon.png",
        "Notification",
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt.",
        "19:53"
    )

    fun schedule(context: Context) {
        once.execute {
            task = Runnable {
                val contentView = prepareView(context)
                NotificationManager.notify(contentView)
                task?.apply { handler.postDelayed(this, random.nextInt(5000).toLong()) }
            }
            task?.apply { handler.post(this) }
        }
    }

    private fun prepareView(context: Context): View {
//        return context.layoutInflater().inflate(
//            R.layout.layout_notification,
//            FrameLayout(context),
//            false
//        ).apply {
//            first_line_text.text = data1.title
//            second_line_text.text = data1.description
//            icon.setImageResource(R.drawable.ic_account)
//        }

//        return context.layoutInflater().inflate(
//            R.layout.layout_notification2,
//            FrameLayout(context),
//            false
//        ).apply {
//            tvTitle.text = data1.title
//            Glide.with(imageView)
//                .load(data2.image)
//                .override(imageView.width, imageView.height)
//                .into(imageView)
//        }

        return context.layoutInflater().inflate(
            R.layout.layout_notification3,
            FrameLayout(context),
            false
        ).apply {
            tvTitle.text = data3.title
            tvTime.text = data3.time
            tvDescription.text = data3.description
            Glide.with(icon)
                .load(data3.image)
                .override(icon.width, icon.height)
                .into(icon)
        }
    }
}