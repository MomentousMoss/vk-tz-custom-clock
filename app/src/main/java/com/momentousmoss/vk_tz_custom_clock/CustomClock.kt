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
import java.util.*
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

const val FPS : Long = 1000 / 60
const val DEFAULT_FACE_RADIUS = 0f
const val DEFAULT_TEXT_DRAW = false
const val DEFAULT_TEXT_PADDING = 0f
const val DEFAULT_TEXT_SIZE = 12f
const val DEFAULT_TEXT_SIZE_CENTER_OFFSET_MULTIPLIER = 0f
const val DEFAULT_HAND_LENGTH = 80
const val DEFAULT_HAND_WIDTH = 10f
const val DEFAULT_COLOR = Color.BLACK

const val NUMBER_SIZE = 12
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
    var numberTextColor: Int = DEFAULT_COLOR
    var numberTextSize: Float = DEFAULT_TEXT_SIZE
    var numberTextSizeCenterOffsetMultiplier : Float = DEFAULT_TEXT_SIZE_CENTER_OFFSET_MULTIPLIER

    var defaultHandPaint: Paint
    var secondsHandDrawable: Drawable? = null
    var minutesHandDrawable: Drawable? = null
    var hoursHandDrawable: Drawable? = null

    var secondsHandLength: Int = DEFAULT_HAND_LENGTH
    var secondsHandWidth: Float = DEFAULT_HAND_WIDTH
    var secondsHandColor: Int = DEFAULT_COLOR

    var minutesHandLength: Int = DEFAULT_HAND_LENGTH
    var minutesHandWidth: Float = DEFAULT_HAND_WIDTH
    var minutesHandColor: Int = DEFAULT_COLOR

    var hoursHandLength: Int = DEFAULT_HAND_LENGTH
    var hoursHandWidth: Float = DEFAULT_HAND_WIDTH
    var hoursHandColor: Int = DEFAULT_COLOR

    init {
        context.obtainStyledAttributes(
            attrs, R.styleable.CustomClock, defStyle, 0
        ).apply {
            clockFaceDrawable = getDrawable(R.styleable.CustomClock_clockFaceDrawable)

            numberTextDraw = getBoolean(R.styleable.CustomClock_numberTextDraw, DEFAULT_TEXT_DRAW)
            numberTextPadding = getDimension(R.styleable.CustomClock_numberTextPadding, DEFAULT_TEXT_PADDING)
            numberTextColor = getColor(R.styleable.CustomClock_numberTextColor, DEFAULT_COLOR)
            numberTextSize = getDimension(R.styleable.CustomClock_numberTextSize, DEFAULT_TEXT_SIZE)
            numberTextSizeCenterOffsetMultiplier = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, numberTextSize, context.resources.displayMetrics
            ) / TEXT_CENTER_OFFSET_MULTIPLIER

            secondsHandLength = getInteger(R.styleable.CustomClock_secondsHandLength, DEFAULT_HAND_LENGTH)
            secondsHandWidth = getDimension(R.styleable.CustomClock_secondsHandWidth, DEFAULT_HAND_WIDTH)
            secondsHandColor = getColor(R.styleable.CustomClock_secondsHandColor, DEFAULT_COLOR)
            getDrawable(R.styleable.CustomClock_secondsHandDrawable)?.let { secondsHandDrawable = it }

            minutesHandLength = getInteger(R.styleable.CustomClock_minutesHandLength, DEFAULT_HAND_LENGTH)
            minutesHandWidth = getDimension(R.styleable.CustomClock_minutesHandWidth, DEFAULT_HAND_WIDTH)
            minutesHandColor = getColor(R.styleable.CustomClock_minutesHandColor, DEFAULT_COLOR)
            getDrawable(R.styleable.CustomClock_minutesHandDrawable)?.let { minutesHandDrawable = it }

            hoursHandLength = getInteger(R.styleable.CustomClock_hoursHandLength, DEFAULT_HAND_LENGTH)
            hoursHandWidth = getDimension(R.styleable.CustomClock_hoursHandWidth, DEFAULT_HAND_WIDTH)
            hoursHandColor = getColor(R.styleable.CustomClock_hoursHandColor, DEFAULT_COLOR)
            getDrawable(R.styleable.CustomClock_hoursHandDrawable)?.let { hoursHandDrawable = it }

            recycle()
        }

        numberTextPaint = TextPaint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textAlign = Paint.Align.LEFT
            textSize = numberTextSize
            color = numberTextColor
        }

        defaultHandPaint = Paint().apply {
            style = Paint.Style.FILL
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawClockFace(canvas)
        drawNumbers(canvas)
        drawHands(canvas)

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
            for (number in 1..NUMBER_SIZE) {
                val angle = Math.toRadians((30 * (number - 3)).toDouble())
                val radiusWithTextPadding = clockFaceRadius - numberTextPadding
                val x = (viewWidth / 2 + cos(angle) * radiusWithTextPadding).toFloat()
                val y = (viewHeight / 2 + sin(angle) * radiusWithTextPadding).toFloat()
                val textSizeCenterOffset = numberTextSizeCenterOffsetMultiplier * number.toString().length
                canvas.drawText(number.toString(), x - textSizeCenterOffset, y + textSizeCenterOffset, numberTextPaint)
            }
        }
    }

    private fun drawHands(canvas: Canvas) {
        val calendar : Calendar = Calendar.getInstance()
        drawHoursHand(canvas, calendar)
        drawMinutesHand(canvas, calendar)
        drawSecondsHand(canvas, calendar)
    }

    private fun drawHoursHand(canvas: Canvas, calendar: Calendar) {
        val handCoordinates = getCoordinatesFromDegree(getHandDegree(calendar, Calendar.HOUR), hoursHandLength)
        drawHand(canvas, handCoordinates, hoursHandWidth, hoursHandColor)
    }

    private fun drawMinutesHand(canvas: Canvas, calendar: Calendar) {
        val handCoordinates = getCoordinatesFromDegree(getHandDegree(calendar, Calendar.MINUTE), minutesHandLength)
        drawHand(canvas, handCoordinates, minutesHandWidth, minutesHandColor)
    }

    private fun drawSecondsHand(canvas: Canvas, calendar: Calendar) {
        val handCoordinates = getCoordinatesFromDegree(getHandDegree(calendar, Calendar.SECOND), secondsHandLength)
        drawHand(canvas, handCoordinates, secondsHandWidth, secondsHandColor)
    }

    private fun drawHand(
        canvas: Canvas,
        handCoordinates: Pair<Float, Float>,
        handWidth: Float,
        handColor: Int
    ) {
        canvas.drawLine(
            viewWidth / 2f,
            viewHeight / 2f,
            handCoordinates.first,
            handCoordinates.second,
            defaultHandPaint.apply {
                strokeWidth = handWidth
                color = handColor
            }
        )
    }

    private fun getHandDegree(calendar: Calendar, timeType: Int): Int {
        val onePercent = 360 / ( if (Calendar.HOUR == timeType) NUMBER_SIZE.toFloat() else 60f )
        return (onePercent * calendar.get(timeType)).roundToInt()
    }

    private fun getCoordinatesFromDegree(degree: Int, handLength: Int): Pair<Float, Float> {
        val angle = Math.toRadians((degree - 90).toDouble())
        val radiusToHandle = (clockFaceRadius - numberTextPadding) / 100 * handLength
        val x = (viewWidth / 2 + cos(angle) * radiusToHandle).toFloat()
        val y = (viewHeight / 2 + sin(angle) * radiusToHandle).toFloat()
        return Pair(x, y)
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