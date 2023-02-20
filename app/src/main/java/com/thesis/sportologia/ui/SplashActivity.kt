package com.thesis.sportologia.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.thesis.sportologia.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


/**
 * Entry point of the app.
 *
 * Splash activity contains only window background, all other initialization logic is placed to
 * [SplashFragment] and [SplashViewModel].
 */
@AndroidEntryPoint
class SplashActivity @Inject constructor() : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashActivityStyle)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }
}
