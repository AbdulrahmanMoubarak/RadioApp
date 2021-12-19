package com.training.radioapptrial.channelrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.utils.Constants.START_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.utils.Constants.STOP_SERVICE_ACTION
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel

class ChannelRecordBroadcastReciever : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {

        var actionStr = ""

        p1?.action?.let { actionStr = it }

        //var channel = p1?.getSerializableExtra("played_channel")
        var channel = RadioChannelModel("", "","https://9090streaming.mobtada.com/9090FMEGYPT",1,"s","")
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

    }
}