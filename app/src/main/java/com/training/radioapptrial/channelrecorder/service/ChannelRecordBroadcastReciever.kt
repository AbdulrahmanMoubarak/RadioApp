package com.training.radioapptrial.channelrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.roomdb.RecordingAlarmsDatabase
import com.training.radioapptrial.channelrecorder.util.Constants.START_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.util.Constants.STOP_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.viewmodel.AlarmsViewmodel
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChannelRecordBroadcastReciever : BroadcastReceiver() {

    private var database: RecordingAlarmsDatabase? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        /*

        var actionStr = ""

        p1?.action?.let { actionStr = it }

        var channel = RadioChannelModel("", "", p1?.data.toString(), 1, "s", "")
        val intent = Intent(p0, ChannelRecordingService::class.java).apply {
            putExtra("played_channel", channel)
        }

        if (actionStr.equals(STOP_SERVICE_ACTION)) {
            p0?.stopService(intent)
            Log.d("here", "onReceive: stopping service")
        } else if (actionStr.equals(START_SERVICE_ACTION)) {
            p0?.startService(intent)
            Log.d("here", "onReceive: Service started")
        }

         */

        MainApplication.getAppContext()?.let {
            database = RecordingAlarmsDatabase.getInstance(it)
        }
        var actionStr = ""
        intent?.action?.let { actionStr = it }

        var channelId = intent?.getIntExtra("id", -1)
        val backup_channelId = intent?.`package`

        if(channelId != null && channelId != -1 && backup_channelId != null) {
            channelId = backup_channelId.toInt()
            CoroutineScope(Dispatchers.IO).launch {
                database?.findAlarmById(channelId)?.collect {
                    val recording = it
                    val serviceIntent = Intent(context, ChannelRecordingService::class.java).apply {
                        putExtra("recording", recording)
                    }

                    if (actionStr.equals(STOP_SERVICE_ACTION)) {
                        context?.stopService(serviceIntent)
                        Log.d("here", "onReceive: stopping service")
                    } else if (actionStr.equals(START_SERVICE_ACTION)) {
                        context?.startService(serviceIntent)
                        Log.d("here", "onReceive: Service started")
                    }
                }
            }
        }
    }
}