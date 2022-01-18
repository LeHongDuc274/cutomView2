package com.example.balls.springview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.View

internal class BallView(
    val pos: Int,
    val size: Int,
    context: Context?
) : View(context) {
    private var paint = Paint().apply {
        isAntiAlias = true
    }
    private var normalColor: Int = Color.RED
    private var radius: Float = 0F

    init {
        radius = size / 3F
        Log.e("cell1", " ${size}")
        setMeasuredDimension(size, size)
        normalColor = listColor.apply {
            shuffle()
        }[0]
    }

    override fun onDraw(canvas: Canvas?) {
        drawView(canvas, normalColor)
    }

    private fun drawView(canvas: Canvas?, color: Int) {
        //draw backGround
        var centerX = width / 2
        var centerY = height / 2
        Log.e("cell", "${centerX} ${centerY}")
        //draw circle
        paint.color = color
        paint.style = Paint.Style.FILL
        canvas?.drawCircle(centerX.toFloat(), centerY.toFloat(), radius, paint)
    }
    companion object{
        val listColor = mutableListOf<Int>(
            Color.BLUE,
            Color.GREEN,
            Color.BLACK,
            Color.GRAY,
            Color.RED,
            Color.YELLOW
        )
    }
}