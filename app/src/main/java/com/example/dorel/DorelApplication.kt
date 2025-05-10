package com.example.dorel

import android.app.Application
import com.google.firebase.FirebaseApp

class DorelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
    }
}
