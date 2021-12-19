package com.training.radioapptrial.channelrecorder.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.training.radioapptrial.channelrecorder.utils.AudioFileBuilder
import com.training.radioapptrial.channelrecorder.utils.Constants.NOTIFICATION_MESSAGE
import com.training.radioapptrial.channelrecorder.utils.Constants.RECORD_CHANNEL_ID
import com.training.radioapptrial.channelrecorder.utils.Constants.RECORD_CHANNEL_NAME
import com.training.radioapptrial.channelrecorder.utils.Constants.RECORD_NOTIFICATION_ID
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.ui.MainActivity
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class ChannelRecordingService: Service() {

    lateinit var channel: RadioChannelModel
    var streamBytes = mutableListOf<Byte>()


    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForegroundService() {


        serviceScope.launch {
            downloadStream()
        }



        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            RECORD_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationBuilder =
            PlayerNotificationManager.makeStatusNotification(NOTIFICATION_MESSAGE, pendingIntent, this, RECORD_CHANNEL_ID, RECORD_CHANNEL_NAME)

        serviceScope.launch {

        }

        startForeground(RECORD_NOTIFICATION_ID, notificationBuilder.build())
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("here"," RecordService: onStartCommand() is called ")

        super.onStartCommand(intent, flags, startId)

        channel = intent?.getSerializableExtra("played_channel") as RadioChannelModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService()
        else
            startForeground(2, Notification())

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        AudioFileBuilder.writeToFile(applicationContext, streamBytes.toByteArray())

        val notificationIntent = Intent()
        val pendingIntent = PendingIntent.getActivity(
            this,
            RECORD_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )
        val notificationBuilder =
            PlayerNotificationManager.makeStatusNotification(NOTIFICATION_MESSAGE, pendingIntent, this, RECORD_CHANNEL_ID, RECORD_CHANNEL_NAME)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private suspend fun downloadStream() {
        val client = OkHttpClient()

        val request = Request.Builder()
            .url(channel.uri)
            .header("Content-type", "application/octet-stream")
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")

            while (!response.body!!.source().exhausted()) {
                val byteArray = ByteArray(8192)
                val count2 = response.body!!.source().read(byteArray)

                for(i in 0.. (count2-1)){
                    streamBytes.add(byteArray[i])
                }
            }

        }
    }
}