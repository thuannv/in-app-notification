package com.github.thuannv.inappnotification.sample

import android.app.Activity
import android.app.Application
import android.os.Handler
import android.os.Looper
import com.github.thuannv.inappnotification.Direction
import com.github.thuannv.inappnotification.Notification
import com.github.thuannv.inappnotification.SwipeListener
import com.github.thuannv.inappnotification.utils.ActivityLifecycleCallbacksAdapter
import com.github.thuannv.inappnotification.utils.Once
import com.github.thuannv.inappnotification.utils.actionBarHeight
import com.github.thuannv.inappnotification.utils.statusBarHeight
import java.lang.ref.WeakReference

object InAppNotificationManager {

    private var activityRef: WeakReference<Activity>? = null

    private var notification: Notification? = null

    private val uiHandler = Handler(Looper.getMainLooper())

    private val AUTO_DISMISS_TASK = Runnable { dismissWithAnimationToTop() }

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

    private fun notifyInternal(info: InAppNotificationInfo) {
        val contentView = info.getView() ?: return
        val activity = activityRef?.get() ?: return
        if (activity.isFinishing) {
            return
        }

        cancelAutoDismiss()

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

        if (info.autoDismiss) {
            uiHandler.postDelayed(AUTO_DISMISS_TASK, info.autoDismissMillis)
        }
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
    fun notify(info: InAppNotificationInfo?) {
        info?.getView()?.apply { uiHandler.post { notifyInternal(info) } }
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
            else -> {
            }
        }
    }

    @JvmStatic
    fun current() = notification

    @JvmStatic
    fun cancelAutoDismiss() {
        uiHandler.removeCallbacks(AUTO_DISMISS_TASK)
    }
}