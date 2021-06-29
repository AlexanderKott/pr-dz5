package dz.mydz

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
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
            invalidate()
        }

    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributes, R.styleable.StatesView){
            textPaint.textSize = getDimension(R.styleable.StatesView_textSize, 1F)
            lineWidth = getDimensionPixelSize(R.styleable.StatesView_lineWith, 1)
            paint.strokeWidth = lineWidth.toFloat()
            colors = listOf(
                getColor(R.styleable.StatesView_color1, getRandomColor()),
                getColor(R.styleable.StatesView_color2, getRandomColor()),
                getColor(R.styleable.StatesView_color3, getRandomColor()),
                getColor(R.styleable.StatesView_color4, getRandomColor()),
            )
        }
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
         radius = min(w, h) / 2F  - lineWidth / 2F
         center = PointF(w / 2F, h / 2F)
         circle = RectF(center.x - radius, center.y - radius,
        center.x + radius, center.y + radius)
    }




    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()){
            return
        }

        var startAngle = -90F

        val calculatedData = getValues(data)

        calculatedData.forEachIndexed { index, it ->
            val angle =  it * 360F
            paint.color =  colors.getOrElse(index) { getRandomColor() }
            canvas.drawArc(circle, startAngle, angle, false,  paint)
            startAngle += angle
        }

        if (calculatedData[0] > 0) {
            paint.color = colors.getOrElse(0) { getRandomColor() }
            startAngle = -90F
            canvas.drawArc(circle, startAngle, -1F, false, paint)
        }

        canvas.drawText(
            "%.2f%%".format(calculatedData.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
    }

    private fun View.getInPixels(dp : Int): Int{
        return  if (dp == 0) 0 else ceil(resources.displayMetrics.density * dp).toInt()
    }

    private fun getRandomColor() : Int = Random.nextInt(0xFF000000.toInt(), 0xFFFFFFFF.toInt())
}