package com.sibama2024ai

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.progressindicator.LinearProgressIndicator

class Splash  : AppCompatActivity(){

    private lateinit var progressBar: LinearProgressIndicator
    private val SPLASH_TIME_OUT: Long = 3500 // 3 seconds



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        progressBar = findViewById(R.id.progress_bar)

        Handler(Looper.getMainLooper()).postDelayed({
            // Start your app main activity
            progressBar.visibility = View.VISIBLE

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition( R.anim.fade_in, R.anim.fade_out)
            progressBar.visibility = View.GONE
            // close this activity
            finish()
        }, SPLASH_TIME_OUT)
    }

}