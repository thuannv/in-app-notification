package com.github.thuannv.inappnotification

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import com.github.thuannv.inappnotification.utils.safelyAddView
import com.github.thuannv.inappnotification.utils.safelyRemoveView
import com.github.thuannv.inappnotification.utils.safelyUpdateView
import com.github.thuannv.inappnotification.utils.windowManager
import java.lang.ref.WeakReference
import kotlin.math.abs


@SuppressLint("ViewConstructor")
class Notification @JvmOverloads private constructor(
    context: Context,
    private val info: NotificationInfo
) : FrameLayout(context) {

    private val gestureDetector: GestureDetector

    private val tapDetector: TapDetector

    private val touchListener: OnTouchListener

    private var isAnimating = false

    private var contentView: View? = null

    private var swipeListener: SwipeListener? = null

    private val uiHandler = Handler(Looper.getMainLooper())

    init {
        tapDetector = TapDetector(ViewConfiguration.get(context).scaledTouchSlop)
        gestureDetector = GestureDetector(context, FlingGestureListener(this))
        touchListener = TouchHandler(this)
        swipeListener = info.swipeListener
        setupView()
    }

    private fun setupView() {
        contentView = info.contentView ?: LayoutInflater.from(context)
            .inflate(info.contentViewLayoutRes, this, false)

        contentView?.apply {
            val lp = if (layoutParams == null) {
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            } else {
                layoutParams as LayoutParams
            }
            lp.gravity = Gravity.CENTER
            setOnTouchListener(touchListener)
            addView(this, lp)
        }
    }

    private fun computeFlags(flags: Int): Int {
        val computedFlags = flags and (
                WindowManager.LayoutParams.FLAG_IGNORE_CHEEK_PRESSES or
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or
                        WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                ).inv()

        return computedFlags or
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
    }

    private fun computeLayoutParams(): WindowManager.LayoutParams {
        val wParams = WindowManager.LayoutParams()
        wParams.x = info.x
        wParams.y = 0
        wParams.width = WindowManager.LayoutParams.MATCH_PARENT
        wParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        wParams.gravity = Gravity.TOP or Gravity.START
        wParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL
        wParams.format = PixelFormat.TRANSLUCENT
        wParams.token = info.contentView?.applicationWindowToken
        wParams.flags = computeFlags(wParams.flags)
        return wParams
    }

    private fun isBackPressed(event: KeyEvent?): Boolean {
        return event?.let { it.action == KeyEvent.ACTION_DOWN && it.keyCode == KeyEvent.KEYCODE_BACK }
            ?: false
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (isBackPressed(event)) {
            dismiss()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    fun dismiss() {
        windowManager().safelyRemoveView(this)
        removeView(contentView)
    }

    fun show() {
        val view = this
        uiHandler.post {
            view.visibility = INVISIBLE
            val wm = windowManager()
            wm.safelyAddView(view, computeLayoutParams())
            if (!isAnimating) {
                isAnimating = true
                val animator = ValueAnimator.ofInt(0, info.y)
                animator.duration = info.enterAnimationDuration
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.addUpdateListener {
                    val y = it.animatedValue as Int
                    view.alpha = 1.0f * y / info.y

                    val wmParams = view.layoutParams as WindowManager.LayoutParams
                    wmParams.y = y
                    wm.safelyUpdateView(view, wmParams)
                }
                animator.addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        view.alpha = 0.25f
                        view.visibility = VISIBLE
                    }

                    override fun onAnimationEnd(animation: Animator?) {
                        view.alpha = 1.0f
                        isAnimating = false
                    }
                })
                animator.start()
            }
        }
    }

    fun exitToLeft() {
        uiHandler.post {
            if (!isAnimating) {
                isAnimating = true
                val animation = animate()
                animation.duration = info.exitAnimationDuration
                animation.translationX(-0.6f * width)
                animation.alpha(0.25f)
                animation.interpolator = AccelerateInterpolator()
                animation.withEndAction {
                    dismiss()
                    isAnimating = false
                }
                animation.start()
            }
        }
    }

    fun exitToRight() {
        uiHandler.post {
            if (!isAnimating) {
                val animation = animate()
                animation.duration = info.exitAnimationDuration
                animation.translationX(0.6f * width)
                animation.alpha(0.25f)
                animation.interpolator = AccelerateInterpolator()
                animation.withEndAction {
                    dismiss()
                    isAnimating = false
                }
                animation.start()
            }
        }

    }

    fun exitToTop() {
        uiHandler.post {
            if (!isAnimating) {
                val animation = animate()
                animation.duration = info.exitAnimationDuration
                animation.translationY(-0.6f * height)
                animation.alpha(0.25f)
                animation.interpolator = AccelerateInterpolator()
                animation.withEndAction {
                    dismiss()
                    isAnimating = false
                }
                animation.start()
            }
        }
    }

    /**
     * [Builder]
     */
    data class Builder(val context: Context) {

        private val info = NotificationInfo()

        fun contentView(contentView: View?) = apply {
            this.info.contentView = contentView
            this.info.contentViewLayoutRes = 0
        }

        fun contentView(contentViewLayoutRes: Int = 0) = apply {
            this.info.contentView = null
            this.info.contentViewLayoutRes = contentViewLayoutRes
        }

        fun swipeListener(swipeListener: SwipeListener?) = apply {
            this.info.swipeListener = swipeListener
        }

        fun exitAnimationDuration(duration: Long) =
            apply { info.exitAnimationDuration = if (duration > 0) duration else 100L }

        fun enterAnimationDuration(duration: Long) =
            apply { info.enterAnimationDuration = if (duration > 0) duration else 150L }

        fun x(x: Int) = apply { info.x = x }

        fun y(y: Int) = apply { info.y = y }

        fun build(): Notification {
            if (info.contentView == null && info.contentViewLayoutRes == 0) {
                throw IllegalArgumentException("ContentView was not set")
            }
            return Notification(context, info)
        }
    }

    /**
     * [FlingGestureListener]
     */
    private class FlingGestureListener(notification: Notification) :
        GestureDetector.SimpleOnGestureListener() {

        private val ref: WeakReference<Notification> = WeakReference(notification)

        override fun onDown(e: MotionEvent?) = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val view = ref.get() ?: return false
            var swipeDirection: Direction = if (abs(velocityX) > abs(velocityY)) {
                if (velocityX < 0) Direction.LEFT else Direction.RIGHT
            } else {
                if (velocityY < 0) Direction.UP else Direction.DOWN
            }
            view.swipeListener?.onSwipe(swipeDirection)
            return true
        }
    }

    /**
     * [TouchHandler]
     */
    private class TouchHandler(notification: Notification) : OnTouchListener {

        private val ref: WeakReference<Notification> = WeakReference(notification)

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            val isTap = tapDetector()?.detect(event) ?: false
            if (isTap) {
                v?.performClick()
                return true
            }

            val isFling = gestureDetector()?.onTouchEvent(event) ?: false
            if (isFling) {
                return true
            }

            return event?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        false
                    }
                    MotionEvent.ACTION_MOVE -> {
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        false
                    }
                    else -> false
                }
            } ?: false
        }

        private fun gestureDetector() = ref.get()?.gestureDetector

        private fun tapDetector() = ref.get()?.tapDetector
    }

    /**
     * [TapDetector]
     */
    private class TapDetector(
        val scaledTouchSlop: Int,
        val tapTimeout: Int = ViewConfiguration.getTapTimeout()
    ) {

        private var prevX = INVALID_POSITION

        private var prevY = INVALID_POSITION

        private var prevTouchTimeMillis: Long = 0

        fun detect(event: MotionEvent?): Boolean {
            return event?.let {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        prevX = event.rawX.toInt()
                        prevY = event.rawY.toInt()
                        prevTouchTimeMillis = System.currentTimeMillis()
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - prevTouchTimeMillis <= tapTimeout) {
                            val dx = event.rawX.toInt() - prevX
                            val dy = event.rawY.toInt() - prevY
                            abs(dx) <= scaledTouchSlop && abs(dy) <= scaledTouchSlop
                        } else {
                            false
                        }
                    }
                    else -> false
                }
            } ?: false
        }

        companion object {
            const val INVALID_POSITION: Int = -1
        }
    }
}