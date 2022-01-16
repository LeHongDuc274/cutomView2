package com.example.balls.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import com.example.balls.R
import com.example.balls.customview.Ball.Companion.DEFAULT_SPEED
import kotlinx.coroutines.*
import java.lang.Math.abs

class BouncyBalls : View {

    private var listBalls = mutableListOf<Ball>()
    private var ballsCount = DEFAULT_BALLS_COUNT
    private var initCoordinatesX = 0
    private var initCoordinatesY = 0
    private var mWidth = 0
    private var mHeight = 0
    private var radius = 0F
    private var state: Int = STATE_AUTO_ANI
    private var jobMoveTo: Job? = null
    private var jobFollow: Job? = null
    private var jobBouncy: Job? = null
    private var jobReset: Job? = null
    val mPaintBall = Paint().apply {
        setColor(Color.RED)
        style = Paint.Style.FILL_AND_STROKE
        strokeWidth = 10F
        strokeCap = Paint.Cap.ROUND
        isAntiAlias = true
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.BouncyBalls,
            0, 0
        ).apply {
            try {
                ballsCount = getInt(R.styleable.BouncyBalls_ballsCount, DEFAULT_BALLS_COUNT)
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        initCoordinatesY = mHeight / 2
        initCoordinatesX = mWidth / 2
        radius = mWidth / 3F / ballsCount
        initBalls(ballsCount)
        setMeasuredDimension(mWidth, mHeight)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        listBalls.forEach { ball ->
            drawBalls(canvas, ball)
        }
        startAutoBoucyBalls()
        invalidate()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            ACTION_DOWN -> {
                jobBouncy?.cancel()
                jobMoveTo?.cancel()
                jobFollow?.cancel()
                jobReset?.cancel()
                // jobMoveDamping?.cancel()
                state = STATE_FOLLOW_ANI
                listBalls.forEach { ball ->
                    moveTo(ball.pos, ball.mX, ball.mY, event.x.toInt(), event.y.toInt())
                }

            }
            ACTION_MOVE -> {
                startFollowAni(event.x.toInt(), event.y.toInt())
            }
            ACTION_UP -> {
                state = STATE_RESET_ANI
                reset()
            }
        }
        return true
    }

    private fun initBalls(ballsCount: Int) {
        listBalls.clear()
        for (i in 0..ballsCount - 1) {
            val centerXY = caculCenterWithPossition(i)
            val ball = Ball(
                pos = i,
                numBalls = ballsCount,
                colorBall = getRandomColor(),
                centerXY.first,
                centerXY.second
            )
            listBalls.add(ball)
        }
    }

    private fun startFollowAni(newX: Int, newY: Int) {
        listBalls[0].mX = newX
        listBalls[0].mY = newY
        jobFollow = CoroutineScope(Dispatchers.Default).launch {
            for (i in 1..ballsCount - 1) {
                val ball = listBalls[i]
                delay(TIME_CHAIN_STEP_DELAY * 2)
                ball.mX = newX
                ball.mY = newY
                //     moveWithDamping(ball,newX,newY)
                invalidate()
            }
//            listBalls.forEach { ball ->
//
//            }
        }
    }
//    private var jobMoveDamping : Job? = null
//    private fun moveWithDamping(ball: Ball, newX: Int, newY: Int) {
//       jobMoveDamping =  CoroutineScope(Dispatchers.Default).launch {
//            for (i in 1..4) {
//                val stepX = (newX - ball.mX) / 10
//                val stepY = (newY - ball.mY) / 10
//                Log.e("tag2", "${ball.pos} $newX  ${ball.mX} $stepX")
//                delay(200L)
//                ball.mX += (stepX*(5-i))
//                ball.mY += (stepY*(5-i))
//                invalidate()
//            }
//            delay(50L)
//            ball.mX = newX
//            ball.mY = newY
//        }
//    }

    private fun drawBalls(canvas: Canvas?, ball: Ball) {
        mPaintBall.setColor(ball.colorBall)
        canvas?.drawCircle(
            ball.mX.toFloat(), ball.mY.toFloat(), radius, mPaintBall
        )
    }

    private fun startAutoBoucyBalls() {
        //change direction of ball by y -> auto change ballY
        if (state == STATE_AUTO_ANI) {
            val firstBall = listBalls[0]
            firstBall.mY += DEFAULT_SPEED * (firstBall.direction)
            if (abs(firstBall.mY - 3 * initCoordinatesY / 4.toFloat()) < DEFAULT_SPEED)
                firstBall.direction = 1
            if (abs(firstBall.mY - 5 * initCoordinatesY / 4) < DEFAULT_SPEED)
                firstBall.direction = -1
            //follow chain
            for (i in 1 until listBalls.size) {
                val ball = listBalls[i]
                val y = listBalls[i - 1].mY
                jobBouncy = CoroutineScope(Dispatchers.Default).launch {
                    delay(TIME_CHAIN_STEP_DELAY)
                    ball.mY = y
                }
            }
        }
    }

    private fun moveTo(pos: Int, oldX: Int, oldY: Int, newX: Int, newY: Int) {
        val stepX = (newX - oldX) / 10
        val stepY = (newY - oldY) / 10
        jobMoveTo = CoroutineScope(Dispatchers.Default).launch {
            for (i in 0..8) {
                delay(MOVE_STEP_DELAY)
                listBalls[pos].mX += stepX
                listBalls[pos].mY += stepY
            }
            listBalls[pos].mX = newX
            listBalls[pos].mY = newY
        }
    }

    private fun reset() {
        if (state == STATE_RESET_ANI) {
            jobReset = CoroutineScope(Dispatchers.Default).launch {
                jobFollow?.cancelAndJoin()
                delay(RESET_TIME)
                listBalls.forEach { ball ->
                    moveTo(ball.pos, ball.mX, ball.mY, ball.centerX, ball.centerY)
                }
                jobMoveTo?.join()
                state = STATE_AUTO_ANI
            }
        }
    }

    private fun getRandomColor(): Int {
        listColor.shuffle()
        return listColor[0]
    }

    private fun caculCenterWithPossition(pos: Int): Pair<Int, Int> {
        val cX = mWidth - (2 * pos + 1) * (mWidth / 2 / ballsCount)
        val cY = mHeight / 2
        return Pair(cX, cY)
    }

    companion object {
        const val STATE_AUTO_ANI = 1
        const val STATE_FOLLOW_ANI = 2
        const val STATE_RESET_ANI = 3
        const val TIME_CHAIN_STEP_DELAY = 50L
        const val DEFAULT_BALLS_COUNT = 5
        const val DEFAULT_SPEED = 25
        const val RESET_TIME = 1000L
        const val MOVE_STEP_DELAY = 20L
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
