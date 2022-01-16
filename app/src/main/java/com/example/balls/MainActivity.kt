package com.example.balls

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.MotionEvent.*
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import androidx.dynamicanimation.animation.SpringForce.DAMPING_RATIO_LOW_BOUNCY
import androidx.dynamicanimation.animation.SpringForce.STIFFNESS_LOW
import com.example.balls.databinding.ActivityMainBinding
import android.view.ViewTreeObserver
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import com.example.balls.customview.BouncyBalls
import kotlinx.coroutines.*
import java.lang.Math.abs


class MainActivity : AppCompatActivity() {

    companion object {
        const val DELTA_PIXELS = 300
        const val UPDATE_TIME_MS = 30L
        const val RESET_TIME_MS = 2000L
        const val STEP_UPDATE_VALUE_PIXELS = 50
    }

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!
    private val listAnimX = arrayListOf<SpringAnimation>()
    private val listAnimY = arrayListOf<SpringAnimation>()
    private val listView = arrayListOf<ImageView>()
    private val beginLocal = arrayListOf<Pair<Int, Int>>()
    private var centerX = 0
    private var centerY = 0
    private var jobAutoAnim: Job? = null
    private var jobReset: Job? = null
    private var auto = true

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bindingView()
        initAnim()
        setupAnim()
        startAutoAnim()
    }

    private fun startAutoAnim() {
        jobAutoAnim = CoroutineScope(Dispatchers.Main).launch {
            jobReset?.join()
            var curX = beginLocal[0].first
            var curY = beginLocal[0].second
            val minY = beginLocal[0].second - DELTA_PIXELS
            //3 * beginLocal[0].second / 4.toFloat()
            val maxY = beginLocal[0].second + DELTA_PIXELS
            //5 * beginLocal[0].second / 4
            var direction = -1
            while (isActive) {
                delay(UPDATE_TIME_MS)
                //curX += 50
                curY += STEP_UPDATE_VALUE_PIXELS * direction
                listAnimY[0].animateToFinalPosition(curY.toFloat())
                if (curY == minY || curY == maxY)
                    direction = direction * -1
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupAnim() {
        addAnimListenner()
        val firstView = listView[0]
        binding.rootLayout.setOnTouchListener { view, motionEvent ->
            when (motionEvent.action) {
                ACTION_DOWN -> {
                    auto = false
                    jobAutoAnim?.cancel()
                    centerX = firstView.width / 2
                    centerY = firstView.height / 2
                    listAnimX[0].animateToFinalPosition(motionEvent.x - centerX)
                    listAnimY[0].animateToFinalPosition(motionEvent.y - centerY)
                    Toast.makeText(this, motionEvent.y.toString(), Toast.LENGTH_SHORT).show()
                }

                ACTION_MOVE -> {
                    listAnimX[0].animateToFinalPosition(motionEvent.x - centerX)
                    listAnimY[0].animateToFinalPosition(motionEvent.y - centerY)
                }
                ACTION_UP -> {
                    // removeAnimXListenner()
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
            return@setOnTouchListener true
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

    private fun removeAnimXListenner() {
        for (i in 0 until listAnimX.size - 1) {
            listAnimX[i].addUpdateListener(null)
        }
    }

    private fun bindingView() {
        listView.apply {
            add(binding.iv1)
            add(binding.iv2)
            add(binding.iv3)
            add(binding.iv4)
            add(binding.iv5)
        }
        val rawX = resources.displayMetrics.widthPixels / 5 / 2
        val rawY = resources.displayMetrics.heightPixels / 2
        repeat(5) { i ->
            beginLocal.add(Pair(rawX * (9 - 2 * i), rawY))
        }
    }

    private fun initAnim() {
        listView.forEach { view ->
            listAnimX.add(createSpringAnim(view, DynamicAnimation.X))
            listAnimY.add(createSpringAnim(view, DynamicAnimation.Y))
        }
    }

    private fun createSpringAnim(
        view: View,
        property: DynamicAnimation.ViewProperty
    ): SpringAnimation {
        return SpringAnimation(view, property).setSpring(SpringForce().apply {
            stiffness = STIFFNESS_LOW
            dampingRatio = DAMPING_RATIO_LOW_BOUNCY
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}