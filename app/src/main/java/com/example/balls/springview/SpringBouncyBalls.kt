package com.example.balls.springview

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import android.widget.GridLayout
import android.widget.LinearLayout
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.example.balls.MainActivity
import com.example.balls.R
import com.example.balls.customview.Ball
import com.example.balls.customview.BouncyBalls
import kotlinx.coroutines.*

class SpringBouncyBalls : LinearLayout {
    private var ballsCount = BouncyBalls.DEFAULT_BALLS_COUNT
    private var listBalls = mutableListOf<BallView>()
    private var mWidth = 0
    private var mHeight = 0
    private var listAnimX = arrayListOf<SpringAnimation>()
    private var listAnimY = arrayListOf<SpringAnimation>()
    private var beginLocal = arrayListOf<Pair<Int, Int>>()
    private var auto = true

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        context.theme.obtainStyledAttributes(attrs, R.styleable.SpringBouncyBalls, 0, 0).apply {
            try {
                ballsCount = getInt(
                    R.styleable.SpringBouncyBalls_countBalls,
                    BouncyBalls.DEFAULT_BALLS_COUNT
                )
                Log.e("tag", ballsCount.toString())
            } finally {
                recycle()
            }
        }
        gravity = Gravity.CENTER_VERTICAL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w
        mHeight = h
        initBalls(ballsCount)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            ACTION_DOWN -> {
                auto = false
                jobAutoAnim?.cancel()
                jobReset?.cancel()
                listAnimX[0].animateToFinalPosition(event.x)
                listAnimY[0].animateToFinalPosition(event.y)
            }

            ACTION_MOVE -> {
                listAnimX[0].animateToFinalPosition(event.x)
                listAnimY[0].animateToFinalPosition(event.y)
            }
            ACTION_UP -> {
                jobReset = CoroutineScope(Dispatchers.Main).launch {
                    delay(RESET_TIME_MS)
                    auto = true
                    for (i in 1..listAnimX.size) {
                        delay(UPDATE_TIME_MS)
                        listAnimX[i - 1].animateToFinalPosition(beginLocal[i - 1].first.toFloat())
                        listAnimY[i - 1].animateToFinalPosition(beginLocal[i - 1].second.toFloat())
                    }
                }
                startAutoAnim()
            }
        }

        return true
    }

    private fun initBalls(ballsCount: Int) {
        for (i in 0 until ballsCount) {
            val ballView = BallView(i, mWidth / ballsCount, context)
            listBalls.add(ballView)
            beginLocal.add(caculCenterWithPossition(i))
            // init anim
            listAnimX.add(createSpringAnim(listBalls[i], DynamicAnimation.X))
            listAnimY.add(createSpringAnim(listBalls[i], DynamicAnimation.Y))

        }

        for (i in 1..ballsCount) {
            addView(listBalls[ballsCount - i])
        }
        // onUpdate pos listenner
        addAnimListenner()
        startAutoAnim()
    }

    private var jobAutoAnim: Job? = null
    private var jobReset: Job? = null

    private fun startAutoAnim() {
        jobAutoAnim = CoroutineScope(Dispatchers.Main).launch {
            jobReset?.join()
            var curX = beginLocal[0].first
            var curY = beginLocal[0].second
            val minY = beginLocal[0].second - DELTA_PIXELS
            val maxY = beginLocal[0].second + DELTA_PIXELS
            var direction = -1
            while (isActive) {
                delay(UPDATE_TIME_MS)
                //curX += 50
                curY += STEP_UPDATE_VALUE_PIXELS * direction
                listAnimY[0].animateToFinalPosition(curY.toFloat())
                if (curY < minY || curY > maxY)
                    direction = direction * -1
            }
        }
    }

    private fun addAnimListenner() {
        for (i in 0 until listAnimX.size - 1) {
            listAnimX[i].addUpdateListener { _, value, _ ->
                if (auto == false) {
                    listAnimX[i + 1].animateToFinalPosition(value)
                }
            }
            listAnimY[i].addUpdateListener { _, value, _ ->
                listAnimY[i + 1].animateToFinalPosition(value)
            }
        }
    }

    private fun caculCenterWithPossition(pos: Int): Pair<Int, Int> {
        val cX = mWidth - (2 * pos + 1) * (mWidth / 2 / ballsCount) - mWidth / ballsCount / 3
        val cY = mHeight / 2
        return Pair(cX, cY)
    }

    private fun createSpringAnim(
        view: View,
        property: DynamicAnimation.ViewProperty
    ): SpringAnimation {
        return SpringAnimation(view, property).setSpring(SpringForce().apply {
            stiffness = SpringForce.STIFFNESS_LOW
            dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
        })
    }

    companion object {
        const val DELTA_PIXELS = 300
        const val UPDATE_TIME_MS = 30L
        const val RESET_TIME_MS = 2000L
        const val STEP_UPDATE_VALUE_PIXELS = 50
    }
}