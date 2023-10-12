package com.example.unsplashcl

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.HiltAndroidApp


const val ACCESS_TOKEN = "access_token"
const val REFRESH_TOKEN = "refresh_token"

@HiltAndroidApp
class App: Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel() {
        val name = "Test notification channel"
        val descriptionText = "This is a simple description"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object{
        const val NOTIFICATION_CHANNEL_ID = "test_channel_id"
    }
}