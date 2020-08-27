package com.github.thuannv.inappnotification

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.PixelFormat
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.widget.FrameLayout
import com.github.thuannv.inappnotification.utils.*
import java.lang.ref.WeakReference
import kotlin.math.abs

@SuppressLint("ViewConstructor")
class InAppNotification @JvmOverloads private constructor(
    context: Context,
    private val params: Params
) : FrameLayout(context) {

    private val gestureDetector: GestureDetector

    private val touchListener: OnTouchListener

    private var isAnimating = false

    private var contentView: View? = null

    private var swipeListener: SwipeListener? = null

    init {
        touchListener = TouchHandler(this)
        gestureDetector = GestureDetector(context, FlingGestureListener(this))
        swipeListener = params.swipeListener
        setupView()
    }

    private fun setupView() {
        contentView = params.contentView ?: LayoutInflater.from(context)
            .inflate(params.contentViewLayoutRes, this, false)

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

    private fun computeLayoutParams(): WindowManager.LayoutParams {
        val wParams = WindowManager.LayoutParams()
        wParams.x = 0
        wParams.y = 0
        wParams.width = WindowManager.LayoutParams.MATCH_PARENT
        wParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        wParams.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        wParams.type = WindowManager.LayoutParams.TYPE_APPLICATION
        wParams.format = PixelFormat.TRANSLUCENT
        wParams.flags = wParams.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
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

    fun dismiss() = windowManager().safelyRemoveView(this)

    fun show() {
        post {
            visibility = INVISIBLE
            val wParams = computeLayoutParams()
            val wm = windowManager()
            wm.safelyAddView(this, wParams)
            if (!isAnimating) {
                isAnimating = true
                val animator = ValueAnimator.ofInt(0, dp(56f))
                animator.duration = params.animationDuration
                animator.interpolator = AccelerateDecelerateInterpolator()
                animator.addUpdateListener {
                    wParams.y = it.animatedValue as Int
                    wm.safelyUpdateView(this, wParams)
                }
                animator.addListener(object: AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        visibility = VISIBLE
                    }
                    override fun onAnimationEnd(animation: Animator?) {
                        isAnimating = false
                    }
                })
                animator.start()
            }
        }
    }

    fun exitToLeft() {
        post {
            if (!isAnimating) {
                isAnimating = true
                val animation = animate()
                animation.duration = 100
                animation.translationX(-0.6f * width)
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
        post {
            if (!isAnimating) {
                val animation = animate()
                animation.duration = params.animationDuration
                animation.translationX(0.6f * width)
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
        post {
            if (!isAnimating) {
                val animation = animate()
                animation.duration = params.animationDuration
                animation.translationY(-0.6f * height)
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

        private val params: Params = Params()

        fun contentView(contentView: View?) = apply {
            this.params.contentView = contentView
            this.params.contentViewLayoutRes = 0
        }

        fun contentView(contentViewLayoutRes: Int = 0) = apply {
            this.params.contentView = null
            this.params.contentViewLayoutRes = contentViewLayoutRes
        }

        fun swipeListener(swipeListener: SwipeListener?) = apply {
            this.params.swipeListener = swipeListener
        }

        fun animationDuration(duration: Long) = apply { params.animationDuration = if (duration > 0) duration else 100L  }

        fun build(): InAppNotification {
            if (params.contentView == null && params.contentViewLayoutRes == 0) {
                throw IllegalArgumentException("ContentView was not set properly")
            }
            return InAppNotification(context, params)
        }
    }

    /**
     * [TouchHandler]
     */
    private class TouchHandler : OnTouchListener {

        private val ref: WeakReference<InAppNotification>

        constructor(inAppNotification: InAppNotification) {
            ref = WeakReference(inAppNotification)
        }

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            return gestureDetector()?.onTouchEvent(event) ?: false
        }

        private fun gestureDetector(): GestureDetector? {
            return ref.get()?.gestureDetector
        }
    }

    /**
     * [FlingGestureListener]
     */
    private class FlingGestureListener : GestureDetector.SimpleOnGestureListener {

        private val ref: WeakReference<InAppNotification>

        constructor(inAppNotification: InAppNotification) : super() {
            ref = WeakReference(inAppNotification)
        }

        override fun onDown(e: MotionEvent?) = true

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent?,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            val view = ref.get() ?: return false;
            var direction: Direction = if (abs(velocityX) > abs(velocityY)) {
                if (velocityX < 0) Direction.LEFT else Direction.RIGHT
            } else {
                if (velocityY < 0) Direction.UP else Direction.DOWN
            }
            view?.swipeListener?.onSwipe(direction)
            return true
        }
    }
}