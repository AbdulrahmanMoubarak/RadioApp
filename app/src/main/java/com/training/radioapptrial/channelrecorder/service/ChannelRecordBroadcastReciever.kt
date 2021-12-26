package com.training.radioapptrial.channelrecorder.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.roomdb.RecordingAlarmsDatabase
import com.training.radioapptrial.channelrecorder.util.Constants.START_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.util.Constants.STOP_SERVICE_ACTION
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketAddress

class ChannelRecordBroadcastReciever : BroadcastReceiver() {

    private var database: RecordingAlarmsDatabase? = null

    override fun onReceive(context: Context?, intent: Intent?) {

        MainApplication.getAppContext()?.let {
            database = RecordingAlarmsDatabase.getInstance(it)
        }

        var actionStr = ""
        intent?.action?.let { actionStr = it }

        var channelId = intent?.getIntExtra("id", -1)
        val backup_channelId = intent?.`package`

        if (channelId != null && channelId != -1 && backup_channelId != null) {
            channelId = backup_channelId.toInt()
            if (isOnline()) {
                CoroutineScope(Dispatchers.IO).launch {
                    database?.findAlarmById(channelId)?.collect {
                        val recording = it
                        val serviceIntent =
                            Intent(context, ChannelRecordingService::class.java).apply {
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
            } else {
                context?.let {  PlayerNotificationManager.createImmediateNotification(it, "Recording failed", "Internet connection error") }
            }
        }
    }

    private fun isOnline(): Boolean {
        val runtime = Runtime.getRuntime()
        try {
            val ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8")
            val exitValue = ipProcess.waitFor()
            return exitValue == 0
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        return false
    }

    private fun isOnlineBySocket(): Boolean {
        return try {
            val timeoutMs = 1500
            val sock = Socket()
            val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
            sock.connect(sockaddr, timeoutMs)
            sock.close()
            true
        } catch (e: IOException) {
            false
        }
    }
}