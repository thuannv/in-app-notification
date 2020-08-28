package com.github.thuannv.inappnotification.sample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.thuannv.inappnotification.Direction
import com.github.thuannv.inappnotification.InAppNotification
import com.github.thuannv.inappnotification.SwipeListener
import com.github.thuannv.inappnotification.utils.dp
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var notification: InAppNotification? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusBarHeight = dp(24f)
        val actionbarHeight = actionBar?.height ?: dp(56f)
        button_toggle_view.setOnClickListener {
            if (notification == null) {
                notification = InAppNotification.Builder(this)
                    .x(0)
                    .y(statusBarHeight + actionbarHeight)
                    .contentView(R.layout.layout_notification)
                    .enterAnimationDuration(50)
                    .exitAnimationDuration(100)
                    .swipeListener(object: SwipeListener {
                        override fun onSwipe(direction: Direction) {
                            when (direction) {
                                Direction.LEFT -> {
                                    notification?.exitToLeft()
                                    notification = null
                                }
                                Direction.RIGHT -> {
                                    notification?.exitToRight()
                                    notification = null
                                }
                                Direction.UP -> {
                                    notification?.exitToTop()
                                    notification = null
                                }
                            }
                        }
                    })
                    .build()
                notification?.show()

                button_toggle_view.text = "Dismiss Notification"
            } else {
                notification?.exitToTop()
                notification = null

                button_toggle_view.text = "Show Notification"
            }
        }
    }

    override fun onStop() {
        notification?.dismiss()
        notification = null
        super.onStop()
    }
}