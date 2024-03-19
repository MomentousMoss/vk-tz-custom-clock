package com.momentousmoss.vk_tz_custom_clock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

const val FPS : Long = 1000 / 60
const val DEFAULT_FACE_RADIUS = 0f
const val DEFAULT_TEXT_DRAW = false
const val DEFAULT_TEXT_PADDING = 0f
const val DEFAULT_TEXT_COLOR = Color.BLACK
const val DEFAULT_TEXT_SIZE = 12f
const val DEFAULT_TEXT_SIZE_CENTER_OFFSET_MULTIPLIER = 0f
const val DEFAULT_HAND_LENGTH = -2f
const val DEFAULT_HAND_WIDTH = -2f

const val TEXT_CENTER_OFFSET_MULTIPLIER = 10f

class CustomClock @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var viewWidth: Int = 0
    private var viewHeight: Int = 0

    var clockFaceRadius: Float = DEFAULT_FACE_RADIUS
    var clockFaceDrawable: Drawable? = null

    var numberTextPaint: TextPaint

    var numberTextDraw: Boolean = DEFAULT_TEXT_DRAW
    var numberTextPadding: Float = DEFAULT_TEXT_PADDING
    var numberTextColor: Int = DEFAULT_TEXT_COLOR
    var numberTextSize: Float = DEFAULT_TEXT_SIZE
    var numberTextSizeCenterOffsetMultiplier : Float = DEFAULT_TEXT_SIZE_CENTER_OFFSET_MULTIPLIER

    var secondsHandLength: Float = DEFAULT_HAND_LENGTH
    var secondsHandWidth: Float = DEFAULT_HAND_WIDTH

    var minutesHandLength: Float = DEFAULT_HAND_LENGTH
    var minutesHandWidth: Float = DEFAULT_HAND_WIDTH

    var hoursHandLength: Float = DEFAULT_HAND_LENGTH
    var hoursHandWidth: Float = DEFAULT_HAND_WIDTH

    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.CustomClock, defStyle, 0
        ).apply {
            clockFaceRadius = getDimension(R.styleable.CustomClock_clockFaceRadius, DEFAULT_FACE_RADIUS)
            clockFaceDrawable = getDrawable(R.styleable.CustomClock_clockFaceDrawable)

            numberTextDraw = getBoolean(R.styleable.CustomClock_numberTextDraw, DEFAULT_TEXT_DRAW)
            numberTextPadding = getDimension(R.styleable.CustomClock_numberTextPadding, DEFAULT_TEXT_PADDING)
            numberTextColor = getColor(R.styleable.CustomClock_numberTextColor, DEFAULT_TEXT_COLOR)
            numberTextSize = getDimension(R.styleable.CustomClock_numberTextSize, DEFAULT_TEXT_SIZE)
            numberTextSizeCenterOffsetMultiplier = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, numberTextSize, context.resources.displayMetrics
            ) / TEXT_CENTER_OFFSET_MULTIPLIER

            secondsHandLength = getDimension(R.styleable.CustomClock_secondsHandLength, DEFAULT_HAND_LENGTH)
            secondsHandWidth = getDimension(R.styleable.CustomClock_secondsHandWidth, DEFAULT_HAND_WIDTH)

            minutesHandLength = getDimension(R.styleable.CustomClock_minutesHandLength, DEFAULT_HAND_LENGTH)
            minutesHandWidth = getDimension(R.styleable.CustomClock_minutesHandWidth, DEFAULT_HAND_WIDTH)

            hoursHandLength = getDimension(R.styleable.CustomClock_hoursHandLength, DEFAULT_HAND_LENGTH)
            hoursHandWidth = getDimension(R.styleable.CustomClock_hoursHandWidth, DEFAULT_HAND_WIDTH)

            recycle()
        }


        numberTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
            textSize = numberTextSize
            color = numberTextColor
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawClockFace(canvas)
        drawNumbers(canvas)

        postInvalidateDelayed(FPS)
    }

    private fun drawClockFace(canvas: Canvas) {
        clockFaceDrawable?.let {
            it.setBounds(
                0, 0,
                viewWidth, viewHeight
            )
            it.draw(canvas)
        }
    }

    private fun drawNumbers(canvas: Canvas) {
        if (numberTextDraw) {
            for (number in 1..12) {
                val angle = Math.toRadians((30 * (number - 3)).toDouble())
                val radiusWithTextPadding = clockFaceRadius  - numberTextPadding
                val x = (viewWidth / 2 + cos(angle) * radiusWithTextPadding).toFloat()
                val y = (viewHeight / 2 + sin(angle) * radiusWithTextPadding).toFloat()
                val textSizeCenterOffset = numberTextSizeCenterOffsetMultiplier * number.toString().length
                canvas.drawText(number.toString(), x - textSizeCenterOffset, y + textSizeCenterOffset, numberTextPaint)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        val viewSize = if (h > w) w else h
        super.onSizeChanged(viewSize, viewSize, oldw, oldh)
        viewWidth = viewSize
        viewHeight = viewSize
        if (clockFaceRadius == DEFAULT_FACE_RADIUS) {
            clockFaceRadius = viewSize / 2f
        }
    }
}