package com.blackcar.chauffeur

import android.animation.ObjectAnimator
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.blackcar.chauffeur.databinding.ActivitySplashBinding

/**
 * Branded splash screen.
 *
 * On Android 12+ the system SplashScreen API (see Theme.BlackCarChauffeur.Splash)
 * shows the logo briefly while the process cold-starts. This Activity then shows
 * the same logo full-screen on the brand black background for a consistent
 * experience on every OS version, with a short fade + scale entrance, before
 * handing off to MainActivity which loads the website.
 */
class SplashActivity : AppCompatActivity() {

    private val splashDurationMs = 1400L

    override fun onCreate(savedInstanceState: Bundle?) {
        // Must be called before super.onCreate() and before setContentView().
        installSplashScreen()

        super.onCreate(savedInstanceState)
        val binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.splashLogo.alpha = 0f
        binding.splashLogo.scaleX = 0.9f
        binding.splashLogo.scaleY = 0.9f

        val fadeIn = ObjectAnimator.ofFloat(binding.splashLogo, "alpha", 0f, 1f)
        val scaleX = ObjectAnimator.ofFloat(binding.splashLogo, "scaleX", 0.9f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding.splashLogo, "scaleY", 0.9f, 1f)

        AnimatorSet().apply {
            playTogether(fadeIn, scaleX, scaleY)
            duration = 500
            interpolator = DecelerateInterpolator()
            start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }, splashDurationMs)
    }
}
