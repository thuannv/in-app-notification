package com.github.thuannv.inappnotification.sample

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.github.thuannv.inappnotification.Direction
import com.github.thuannv.inappnotification.Notification
import com.github.thuannv.inappnotification.SwipeListener
import com.github.thuannv.inappnotification.utils.ActivityLifecycleCallbacksAdapter
import com.github.thuannv.inappnotification.utils.actionBarHeight
import com.github.thuannv.inappnotification.utils.statusBarHeight
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicBoolean

class Once {
    private val isExecuted = AtomicBoolean(false)
    fun execute(block: () -> Unit) {
        if (isExecuted.compareAndSet(false, true)) {
            return block()
        }
    }
}


object NotificationManager {

    private var activityRef: WeakReference<Activity>? = null

    private var notification: Notification? = null

    private val uiHandler = Handler(Looper.getMainLooper())

    private val once = Once()

    @JvmStatic
    private fun dismissWithAnimationToLeft() {
        notification?.exitToLeft()
        notification = null
    }

    @JvmStatic
    private fun dismissWithAnimationToRight() {
        notification?.exitToRight()
        notification = null
    }

    @JvmStatic
    private fun dismissWithAnimationToTop() {
        notification?.exitToTop()
        notification = null
    }

    private fun notifyInternal(contentView: View?) {
        val activity = activityRef?.get()
        if (activity == null || activity.isFinishing) {
            return
        }
        dismissInternal()

        notification = Notification.Builder(activity)
            .x(0)
            .y(activity.statusBarHeight() + activity.actionBarHeight())
            .contentView(contentView)
            .enterAnimationDuration(500)
            .exitAnimationDuration(250)
            .swipeListener(object : SwipeListener {
                override fun onSwipe(swipeDirection: Direction) {
                    dismiss(swipeDirection)
                }
            })
            .build()

        notification?.show()
    }

    private fun dismissInternal() {
        notification?.dismiss()
        notification = null
    }

    @JvmStatic
    fun init(application: Application) {
        once.execute {
            application.registerActivityLifecycleCallbacks(
                object : ActivityLifecycleCallbacksAdapter() {
                    override fun onActivityResumed(activity: Activity) {
                        activityRef = WeakReference(activity)
                    }

                    override fun onActivityPaused(activity: Activity) {
                        dismiss()
                        activityRef = null
                    }
                }
            )
        }
    }

    @JvmStatic
    fun notify(@LayoutRes layoutResId: Int = 0) {
        if (layoutResId == 0) {
            return
        }
        activityRef?.get()?.apply {
            val contentView = layoutInflater.inflate(
                layoutResId,
                window.decorView as ViewGroup,
                false
            )
            notify(contentView)
        }
    }

    @JvmStatic
    fun notify(contentView: View?) {
        if (contentView == null) {
            return
        }
        uiHandler.post { notifyInternal(contentView) }
    }

    @JvmStatic
    fun dismiss() {
        uiHandler.post { dismissInternal() }
    }

    @JvmStatic
    fun dismiss(direction: Direction) {
        when (direction) {
            Direction.LEFT -> dismissWithAnimationToLeft()
            Direction.RIGHT -> dismissWithAnimationToRight()
            Direction.UP -> dismissWithAnimationToTop()
        }
    }

    @JvmStatic
    fun current() = notification
}