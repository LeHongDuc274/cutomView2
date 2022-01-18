package com.example.balls

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SpringBallActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spring_ball)
        findViewById<Button>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}