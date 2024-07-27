package com.example.floppybird

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var playBtn: Button
    private lateinit var gameView: FlappyBirdView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playBtn = findViewById(R.id.playButton)
        val layout = findViewById<RelativeLayout>(R.id.main)

        playBtn.setOnClickListener{
            gameView = FlappyBirdView(this)
            layout.removeAllViews()
            layout.addView(gameView)
        }
    }
}