package dz.mydz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.animation.addListener
import androidx.core.content.withStyledAttributes
import kotlin.math.ceil
import kotlin.math.min
import kotlin.random.Random


class StatesView @JvmOverloads constructor(
    context: Context,
    attributes: AttributeSet? = null
) : View(context, attributes) {

    private var lineWidth = 0
    private var center = PointF(0F, 0F)
    private var radius = 0F


    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = lineWidth.toFloat()
        strokeCap = Paint.Cap.ROUND
        // strokeJoin = Paint.Join.MITER //ROUND
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textSize = getInPixels(20).toFloat()
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private var circle = RectF(0F, 0F, 0F, 0F)
    var data: Array<Float> = emptyArray<Float>()
        set(value) {
            field = value
            calculatedData = getValues(data)
            update(aduration.toLong())
        }

    private var colors = emptyList<Int>()
    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null

    private fun update(aduration: Long) {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()

            }

            duration = aduration

            if (animationType == 0) {
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        if (lineCount < 3) {
                            lineCount++
                            update(aduration.toLong())
                            if (lineCount > 0) {
                                startAngle += calculatedData[lineCount - 1] * 360F
                            }
                        }

                    }
                })

                duration = aduration / 4
            }

            interpolator = LinearInterpolator()
            start()
        }


    }

    private var animationType = 0
    private  var aduration = 0

    init {
        context.withStyledAttributes(attributes, R.styleable.StatesView) {
            textPaint.textSize = getDimension(R.styleable.StatesView_textSize, 1F)
            lineWidth = getDimensionPixelSize(R.styleable.StatesView_lineWith, 1)
            paint.strokeWidth = lineWidth.toFloat()
            aduration = getInt(R.styleable.StatesView_aduration, 1000)
            animationType = getInteger(R.styleable.StatesView_animType, 0)

            colors = listOf(
                getColor(R.styleable.StatesView_color1, getRandomColor()),
                getColor(R.styleable.StatesView_color2, getRandomColor()),
                getColor(R.styleable.StatesView_color3, getRandomColor()),
                getColor(R.styleable.StatesView_color4, getRandomColor()),
            )
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth / 2F
        center = PointF(w / 2F, h / 2F)
        circle = RectF(
            center.x - radius, center.y - radius,
            center.x + radius, center.y + radius
        )
    }


    private var lineCount = 0
    private var startAngle = -90F
    private lateinit var calculatedData: Array<Float>

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) {
            return
        }

        if (animationType == 0) {
            methodTwo(canvas)
        } else {
            methodOne(canvas)
        }

        firstLineFix(canvas)
        drawText(canvas)
    }

    private fun methodOne(canvas: Canvas) {
        var startAngle3 = -90F
        calculatedData.forEachIndexed { index, it ->
            val angle = it * 360F
            paint.color = colors.getOrElse(index) { getRandomColor() }
            canvas.drawArc(circle, startAngle3, angle * progress, false, paint)
            startAngle3 += angle
        }
    }

    private fun methodTwo(canvas: Canvas) {
        var startAngle2 = -90F

        if (lineCount > 0) {
            for (i in 0 until lineCount) {
                val angle = calculatedData[i] * 360F
                paint.color = colors.getOrElse(i) { getRandomColor() }
                canvas.drawArc(circle, startAngle2, angle, false, paint)
                startAngle2 += angle
            }
        }

        val angle = calculatedData[lineCount] * 360F
        paint.color = colors.getOrElse(lineCount) { getRandomColor() }
        canvas.drawArc(circle, startAngle, angle * progress, false, paint)
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(
            "%.2f%%".format(calculatedData.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun firstLineFix(canvas: Canvas) {
        if (calculatedData[0] > 0) {
            paint.color = colors.getOrElse(0) { getRandomColor() }
            canvas.drawArc(circle, -90F, -1F, false, paint)
        }
    }

    private fun View.getInPixels(dp: Int): Int {
        return if (dp == 0) 0 else ceil(resources.displayMetrics.density * dp).toInt()
    }

    private fun getRandomColor(): Int = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}