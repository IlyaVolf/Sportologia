package com.thesis.sportologia

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.thesis.sportologia.utils.image_loader.GlideLoaderCreator
import com.thesis.sportologia.utils.image_loader.ImageLoader
import dagger.hilt.android.HiltAndroidApp

/**
 * Entry point of the app should be annotated with [HiltAndroidApp].
 */
@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()

        /*if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }*/

        ImageLoader.loaderCreator = GlideLoaderCreator()
        context = applicationContext
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}
