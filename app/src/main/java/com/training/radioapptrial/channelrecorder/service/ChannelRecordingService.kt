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
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.roomdb.model.ChannelRecordingAlarmModel
import com.training.radioapptrial.channelrecorder.util.AudioFileBuilder
import com.training.radioapptrial.channelrecorder.util.Constants.NOTIFICATION_MESSAGE
import com.training.radioapptrial.channelrecorder.util.Constants.RECORDING_NOTIFICATION_TITLE
import com.training.radioapptrial.channelrecorder.util.Constants.RECORD_CHANNEL_ID
import com.training.radioapptrial.channelrecorder.util.Constants.RECORD_CHANNEL_NAME
import com.training.radioapptrial.channelrecorder.util.Constants.RECORD_NOTIFICATION_ID
import com.training.radioapptrial.channelrecorder.util.StreamDownloader
import com.training.radioapptrial.channelsGetViewPlay.ui.MainActivity
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import okhttp3.Request
import java.net.SocketException
import java.net.SocketTimeoutException

class ChannelRecordingService : Service() {

    lateinit var recording: ChannelRecordingAlarmModel
    var streamBytes = mutableListOf<Byte>()
    var successful = true
    var channelName = ""

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + job)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForegroundService(channelName: String) {
        this.channelName = channelName
        downloadStream()

        val notificationIntent = Intent(this, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this,
            RECORD_NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationBuilder =
            PlayerNotificationManager.makeStatusNotification(
                NOTIFICATION_MESSAGE + channelName,
                pendingIntent,
                this,
                RECORD_CHANNEL_ID,
                RECORD_CHANNEL_NAME,
                RECORDING_NOTIFICATION_TITLE
            )

        startForeground(RECORD_NOTIFICATION_ID, notificationBuilder.build())
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("here", " RecordService: onStartCommand() is called ")

        super.onStartCommand(intent, flags, startId)

        recording = intent?.getSerializableExtra("recording") as ChannelRecordingAlarmModel

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(recording.channel_name)
        else
            startForeground(2, Notification())

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (successful) {
            MainApplication.getAppContext()?.let {
                PlayerNotificationManager.createImmediateNotification(
                    it,
                    "Finished recording",
                    "Record saved successfully. Tab to view"
                )
            }
            AudioFileBuilder.writeToFile(applicationContext, streamBytes.toByteArray(), channelName)
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun downloadStream() {
        val streamDownloader = StreamDownloader()

        val request = Request.Builder()
            .url(recording.channel_url)
            .header("Content-type", "application/octet-stream")
            .build()

        serviceScope.launch {
            try {
                streamDownloader.startDownloadingStream(request).collect {
                    saveByteArrayToRecordList(it)
                }
            } catch (e: SocketTimeoutException) {
                successful = false
                MainApplication.getAppContext()?.let {
                    PlayerNotificationManager.createImmediateNotification(
                        it,
                        "Recording inturrupted",
                        "Network connection timed out"
                    )
                }
                stopSelf()
            } catch (e: SocketException) {
                successful = false
                MainApplication.getAppContext()?.let {
                    PlayerNotificationManager.createImmediateNotification(
                        it,
                        "Recording inturrupted",
                        "Network connection error"
                    )
                }
                stopSelf()
            }
        }
    }

    private fun saveByteArrayToRecordList(byteArray: ByteArray) {
        val iterator = byteArray.iterator()
        while (iterator.hasNext()) {
            streamBytes.add(iterator.next())
        }
    }


}