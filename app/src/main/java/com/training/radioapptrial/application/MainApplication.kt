package com.training.radioapptrial.application

import android.app.Application
import android.content.Context
import android.content.IntentFilter
import com.training.radioapptrial.channelrecorder.service.ChannelRecordBroadcastReciever
import com.training.radioapptrial.channelrecorder.utils.Constants.START_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.utils.Constants.STOP_SERVICE_ACTION
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