package com.training.radioapptrial.radioforegroundservice.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import androidx.annotation.RequiresApi
import androidx.media.MediaBrowserServiceCompat
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.ui.MainActivity
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import com.training.radioapptrial.radioforegroundservice.util.Constants.CHANNEL_ID
import com.training.radioapptrial.radioforegroundservice.util.Constants.CHANNEL_NAME
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_ID
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

private const val SERVICE_TAG = "RadioService"

@AndroidEntryPoint
class RadioService : MediaBrowserServiceCompat() {

    lateinit var channel: RadioChannelModel

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + job)

    //MediaSession
    //private lateinit var mediaSessionCompat:MediaSessionCompat
    //private lateinit var mediaSessionConnector: MediaSessionConnector


    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startForegroundService(message: String) {

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("played_channel", channel)


        //val uniqueInt = (System.currentTimeMillis() and 0xfffffff).toInt()

        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notificationBuilder =
            PlayerNotificationManager.makeStatusNotification(message, pendingIntent, this, CHANNEL_ID, CHANNEL_NAME, NOTIFICATION_TITLE)

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.getSerializableExtra("played_channel")?.let {
            channel = it as RadioChannelModel
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(channel.name)
        else
            startForeground(NOTIFICATION_ID, Notification())

        return START_NOT_STICKY
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return BrowserRoot("root", rootHints)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}