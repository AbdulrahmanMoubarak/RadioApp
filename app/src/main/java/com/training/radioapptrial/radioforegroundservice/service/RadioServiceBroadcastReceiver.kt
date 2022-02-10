package com.training.radioapptrial.radioforegroundservice.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.radioforegroundservice.util.Constants

class RadioServiceBroadcastReceiver : BroadcastReceiver() {
    private val serviceIntent = Intent(MainApplication.getApplication(), RadioService::class.java)

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Constants.NOTIFICATION_ACTION_EXIT -> {
                context?.stopService(serviceIntent)
            }

            Constants.NOTIFICATION_ACTION_PLAY -> {
                val channel = intent.getSerializableExtra("played_channel")
                context?.startService(
                    serviceIntent.apply {
                        action = Constants.NOTIFICATION_ACTION_PLAY
                        putExtra("played_channel", channel)
                    }
                )
            }
        }
    }
}