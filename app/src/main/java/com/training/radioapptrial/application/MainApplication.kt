package com.training.radioapptrial.application

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication: Application() {

    companion object{
        private lateinit var app: Application
        fun getAppContext(): Context? {
            return app.applicationContext
        }

        fun getApplication(): Application {
            return app
        }
    }

    override fun onCreate() {
        super.onCreate()
        app = this
    }
}