package com.example.kotlincatchthekennygame

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.core.content.ContextCompat.startActivity
import com.airbnb.lottie.LottieDrawable
import com.example.kotlincatchthekennygame.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityStartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        setupAnim()
    }
    private fun setupAnim() {
        binding.lottieAnimationView.setAnimation(R.raw.anim)
        binding.lottieAnimationView.repeatCount = LottieDrawable.INFINITE
        binding.lottieAnimationView.playAnimation()

        Handler().postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, 1000)
    }

}