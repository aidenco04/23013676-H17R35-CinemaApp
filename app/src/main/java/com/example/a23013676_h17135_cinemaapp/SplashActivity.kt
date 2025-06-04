package com.example.a23013676_h17135_cinemaapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Link to your custom XML layout: splashactivity.xml
        setContentView(R.layout.splashactivity)

        // Wait 2 seconds, then navigate to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // Prevents back button returning to splash
        }, 2000)
    }
}
