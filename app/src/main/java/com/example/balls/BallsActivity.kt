package com.example.balls

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.balls.customview.Ball

class BallsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balls)
        val rootLayout =findViewById<LinearLayout>(R.id.root_layout)
        val params =rootLayout.layoutParams
        val layoutWidth = params.width
        Log.e("params",layoutWidth.toString())
        //val layoutHeight = params.height
    }
}