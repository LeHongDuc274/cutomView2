package com.example.balls.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.widget.GridLayout
import android.widget.LinearLayout
import com.example.balls.R
import kotlinx.coroutines.*
import java.util.*

class BouncyBalls : GridLayout {

    private var listBalls = mutableListOf<Ball>()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        var ballsCount = 0
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BouncyBalls,
            0, 0
        ).apply {
            try {
                ballsCount = getInt(R.styleable.BouncyBalls_ballsCount, DEFAULT_BALLS_COUNT)
                columnCount = ballsCount
                rowCount = 1
            } finally {
                recycle()
            }
        }
        initBalls(ballsCount)
    }

    private fun initBalls(ballsCount: Int) {
        for (i in 0..ballsCount - 1) {
            val ball = Ball(pos = i, numBalls = ballsCount,colorBall =getRandomColor(), context = context)
            addView(ball)
            listBalls.add(ball)
        }
        listBalls[0].setCallback { newY ->
            listBalls.forEach { ball ->
                startChainBouncy(ball,newY)
            }
        }
    }

    private fun startChainBouncy(ball: Ball,newY : Int){
        CoroutineScope(Dispatchers.Default).launch {
            delay(TIME_CHAIN_STEP_DELAY * ball.pos)
            if (ball.pos > 0) {
                ball.moveBalls(newY)
            }
        }
    }
    private fun getRandomColor(): Int{
        listColor.shuffle()
        return listColor[0]
    }
    companion object{
        const val TIME_CHAIN_STEP_DELAY = 100L
        const val DEFAULT_BALLS_COUNT = 5
        val listColor = mutableListOf<Int>(Color.BLUE,Color.GREEN,Color.BLACK,Color.GRAY,Color.RED,Color.YELLOW)
    }
}