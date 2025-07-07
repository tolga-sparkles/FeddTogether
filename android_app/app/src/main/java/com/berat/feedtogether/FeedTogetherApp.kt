package com.berat.feedtogether

import android.app.Application
import com.google.firebase.FirebaseApp

class FeedTogetherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 