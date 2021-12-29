package com.example.balls.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import kotlinx.coroutines.*

class Ball(
    val pos: Int = 0,
    val numBalls: Int = 5,
    val colorBall: Int = Color.GREEN,
    val centerX : Int,
    val centerY : Int
)
{
    var mX: Int
    var mY: Int
    var direction = 1
    init {
        mX = centerX
        mY = centerY
    }


    private val speed = DEFAULT_SPEED

    var callBack: ((Int,Int) -> Unit)? = null
    private var radius: Float = 0F

    private var job: Job? = null
//    private fun reset(currenY: Int) {
//        val direction = if (currenY > initCoordinatesY) -1 else 1
//        job = CoroutineScope(Dispatchers.Default).launch {
//            delay(TIME_RESET_DELAY)
//            while (Math.abs(mY - initCoordinatesY) >= speed) {
//                mY += speed * direction
//                callBack?.invoke(mY,mX)
//                delay(TIME_STEP_DELAY)
//            }
//            mY += Math.abs(mY - initCoordinatesY) * direction
//            callBack?.invoke(mY,mX)
//        }
//    }

    fun setCallback(action: (Int,Int) -> Unit) {
        callBack = action
    }

    fun moveBalls(newX: Int,newY: Int) {
        mY = newY
        mX = newX
    }

    companion object {
        const val TIME_RESET_DELAY = 3000L
        const val TIME_STEP_DELAY = 10L
        const val DEFAULT_SPEED = 20
    }
}
