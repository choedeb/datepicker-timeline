package com.github.badoualy.datepicker

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat

/** Simple view to draw a colored and sized dot  */
class DotView : View {
    private var paint: Paint? = null
    private var size = -1

    @JvmOverloads
    constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        init()
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint!!.style = Paint.Style.FILL
        paint!!.color = Color.WHITE
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (size == -1) {
            // Default size if full view
            size = w
        }
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawCircle(measuredWidth / 2.toFloat(), measuredHeight / 2.toFloat(), size / 2.toFloat(), paint!!)
    }

    fun setColor(@ColorInt color: Int) {
        paint!!.color = color
        invalidate()
    }

    fun setColorRes(@ColorRes colorRes: Int) {
        setColor(ContextCompat.getColor(context, colorRes))
    }

    fun setCircleSize(size: Int) {
        this.size = size
        invalidate()
    }

    fun setCircleSizeDp(sizeInDp: Int) {
        setCircleSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeInDp.toFloat(), resources.displayMetrics).toInt())
    }
}