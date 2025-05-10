package com.example.dorel

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class CustomPieChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint().apply {
        isAntiAlias = true
    }

    private var pieData: List<Float> = emptyList()
    private var pieLabels: List<String> = emptyList()
    private var pieColors: List<Int> = emptyList()

    fun setData(data: List<Float>, labels: List<String>, colors: List<Int>) {
        pieData = data
        pieLabels = labels
        pieColors = colors
        invalidate() // Refresh the chart
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (pieData.isEmpty()) return

        val total = pieData.sum()
        var startAngle = -90f
        val rect = RectF(100f, 100f, width - 100f, height - 100f)

        for (i in pieData.indices) {
            val sweepAngle = (pieData[i] / total) * 360f
            val percentage = (pieData[i] / total) * 100

            // Draw the segment
            paint.color = pieColors[i % pieColors.size] // Cycle colors if more segments than colors
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

            // Draw text labels with percentages
            val midAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val labelX = (rect.centerX() + Math.cos(midAngle) * rect.width() / 3).toFloat()
            val labelY = (rect.centerY() + Math.sin(midAngle) * rect.height() / 3).toFloat()

            paint.color = Color.BLACK
            paint.textSize = 36f
            canvas.drawText(
                "${pieLabels[i]} (${String.format("%.1f", percentage)}%)",
                labelX,
                labelY,
                paint
            )

            startAngle += sweepAngle
        }
    }
}
