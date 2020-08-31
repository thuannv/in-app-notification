package com.github.thuannv.inappnotification.sample

import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.thuannv.inappnotification.Direction
import com.github.thuannv.inappnotification.Notification
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

//    private var notification: Notification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        var statusBarHeight = dp(24f)
//        val idStatusBarHeight = resources.getIdentifier("status_bar_height", "dimen", "android")
//        if (idStatusBarHeight > 0) {
//            statusBarHeight = resources.getDimensionPixelSize(idStatusBarHeight)
//        }
//
//        val actionbarHeight = actionBar?.height ?: dp(56f)
//        button_toggle_view.setOnClickListener {
//            if (notification == null) {
//                notification = Notification.Builder(this)
//                    .x(0)
//                    .y(statusBarHeight + actionbarHeight)
//                    .contentView(R.layout.layout_notification)
//                    .enterAnimationDuration(500)
//                    .exitAnimationDuration(250)
//                    .swipeListener(object : SwipeListener {
//                        override fun onSwipe(swipeDirection: SwipeDirection) {
//                            when (swipeDirection) {
//                                SwipeDirection.LEFT -> {
//                                    notification?.exitToLeft()
//                                    notification = null
//                                }
//                                SwipeDirection.RIGHT -> {
//                                    notification?.exitToRight()
//                                    notification = null
//                                }
//                                SwipeDirection.UP -> {
//                                    notification?.exitToTop()
//                                    notification = null
//                                }
//                            }
//                        }
//                    })
//                    .build()
//                notification?.show()
//            } else {
//                notification?.exitToTop()
//                notification = null
//            }
//        }



        val contentView = layoutInflater.inflate(
            R.layout.layout_notification,
            window.decorView as ViewGroup,
            false)

        button_toggle_view.setOnClickListener {
            if (NotificationManager.current() == null) {
                NotificationManager.notify(contentView)
            } else {
                NotificationManager.dismiss(Direction.RIGHT)
            }
        }
    }
}