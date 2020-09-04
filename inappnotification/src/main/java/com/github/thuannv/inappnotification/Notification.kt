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
import kotlin.math.abs


@SuppressLint("ViewConstructor")
class Notification @JvmOverloads private constructor(
    context: Context,
    private val info: NotificationInfo
) : FrameLayout(context) {

//    private val gestureDetector: GestureDetector

    private var isAnimating = false

    private var contentView: View? = null

    private var swipeListener: SwipeListener? = null

    private val uiHandler = Handler(Looper.getMainLooper())

    private var isScrolling = false

    private var prevEventX = -1f

    private var prevEventY = -1f

    private var dx = 0f

    private var dy = 0f

    private var touchSlop: Int

    private var minimumFlingVelocity: Int

    private var maximumFlingVelocity: Int

    private var velocityTracker: VelocityTracker? = null

    private var direction = Direction.NONE


    init {
        val vc = ViewConfiguration.get(context)
        touchSlop = vc.scaledTouchSlop
        minimumFlingVelocity = vc.scaledMinimumFlingVelocity
        maximumFlingVelocity = vc.scaledMaximumFlingVelocity
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

    private fun resetState() {
        isScrolling = false
        direction = Direction.NONE
        velocityTracker?.recycle()
        velocityTracker = null
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (isBackPressed(event)) {
            dismiss()
            return true
        }
        return super.dispatchKeyEvent(event)
    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        if (isAnimating) {
            return false
        }

        return event?.let { ev ->
            if (velocityTracker == null) {
                velocityTracker = VelocityTracker.obtain()
            }
            velocityTracker?.addMovement(ev)

            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    prevEventX = ev.rawX
                    prevEventY = ev.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    dx = ev.rawX - prevEventX
                    dy = ev.rawY - prevEventY

                    prevEventX = ev.rawX
                    prevEventY = ev.rawY

                    if (abs(dx) > abs(dy)) {
                        if (abs(dx) > touchSlop) {
                            direction = if (dx < 0) {
                                Direction.LEFT
                            } else {
                                Direction.RIGHT
                            }
                            x += dx

                            isScrolling = true
                            return false
                        }
                    } else {
                        if (abs(dy) > touchSlop) {
                            direction = if (dy < 0) {
                                Direction.UP
                            } else {
                                Direction.DOWN
                            }
                            isScrolling = true
                            return false
                        }
                    }
                }

                MotionEvent.ACTION_UP -> {
                    if (isScrolling) {
                        when(direction) {
                            Direction.LEFT -> exitToLeft()
                            Direction.RIGHT -> exitToRight()
                            Direction.UP -> exitToTop()
                            else -> {}
                        }
                        resetState()
                        return false
                    }
                    velocityTracker?.apply {
                        computeCurrentVelocity(1000, maximumFlingVelocity.toFloat())
                        val pointerId = ev.getPointerId(0)
                        val vx = getXVelocity(pointerId)
                        val vy = getYVelocity(pointerId)
                        if (abs(vx) > minimumFlingVelocity || abs(vx) > maximumFlingVelocity) {
                            if (vx > 0) {
                                exitToRight()
                            } else {
                                exitToLeft()
                            }
                            resetState()
                            return false
                        }
                        if (abs(vy) > minimumFlingVelocity || abs(vy) > maximumFlingVelocity) {
                            if (vy < 0) {
                                exitToTop()
                                resetState()
                                return true
                            }
                        }
                    }
                }

                MotionEvent.ACTION_CANCEL -> {
                    if (isScrolling) {
                        isScrolling = false
                        direction = Direction.NONE
                        velocityTracker?.recycle()
                        velocityTracker = null
                        return false
                    }
                }
            }
            super.onInterceptTouchEvent(ev)
        } ?: false
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
                animation.alpha(0.0f)
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
                animation.alpha(0.0f)
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
                animation.alpha(0.0f)
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
}