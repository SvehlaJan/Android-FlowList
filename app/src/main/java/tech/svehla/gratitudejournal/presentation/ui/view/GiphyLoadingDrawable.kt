package tech.svehla.gratitudejournal.presentation.ui.view

import android.animation.ValueAnimator
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable

/**
 * Original from https://github.com/Giphy/giphy-android-sdk/blob/6d5a52b148fff3b2122130781d6de9d0ad7456c3/app/src/main/java/com/giphy/sdk/uidemo/LoadingDrawable.kt
 */
class GiphyLoadingDrawable(val shape: Shape) : Drawable(), ValueAnimator.AnimatorUpdateListener {
    val paint = Paint().apply {
        color = Color.RED
        isAntiAlias = true
        style = Paint.Style.FILL
    }
    val animator = ValueAnimator.ofFloat(20.0f, 60f)
    var currentSize = 50f

    init {
        animator.addUpdateListener(this)
        animator.duration = 500
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = ValueAnimator.INFINITE
        animator.start()
    }
    override fun draw(p0: Canvas) {
        if (!animator.isRunning) {
        }
        when (shape) {
            Shape.Circle ->
                p0.drawCircle(bounds.width() / 2f, bounds.height() / 2f, currentSize, paint)
            Shape.Rect ->
                p0.drawRect((bounds.width() - currentSize) / 2f, (bounds.height() - currentSize) / 2f, (bounds.width() + currentSize) / 2f, (bounds.height() + currentSize) / 2f, paint)
        }
    }

    override fun setAlpha(p0: Int) {
    }

    override fun getOpacity(): Int {
        return PixelFormat.TRANSPARENT
    }

    override fun setColorFilter(p0: ColorFilter?) {
        paint.colorFilter = p0
    }

    enum class Shape {
        Rect,
        Circle
    }

    override fun onAnimationUpdate(p0: ValueAnimator?) {
        currentSize = p0?.animatedValue as Float
        invalidateSelf()
    }
}