package com.example.doculink

import android.app.Application
import com.google.firebase.FirebaseApp

class DocuLinkApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}
