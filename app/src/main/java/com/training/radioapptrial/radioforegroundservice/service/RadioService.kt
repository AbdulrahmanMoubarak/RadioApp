package com.training.radioapptrial.radioforegroundservice.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.*
import android.support.v4.media.MediaBrowserCompat
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.training.radioapptrial.R
import com.training.radioapptrial.channelsGetViewPlay.factory.MediaSourceAbstractFactory
import com.training.radioapptrial.channelsGetViewPlay.listener.PlayerListener
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.ui.MainActivity
import com.training.radioapptrial.channelsGetViewPlay.util.MessageConstants
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import com.training.radioapptrial.radioforegroundservice.util.Constants
import com.training.radioapptrial.radioforegroundservice.util.Constants.CHANNEL_ID
import com.training.radioapptrial.radioforegroundservice.util.Constants.CHANNEL_NAME
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_ID
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_TITLE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val SERVICE_TAG = "RadioService"

@AndroidEntryPoint
class RadioService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var mediaPlayer: ExoPlayer
    lateinit var channel: RadioChannelModel
    var playerEvents: PlayerListener? = null

    private val job = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreate() {
        super.onCreate()
        val intentFilter = IntentFilter().apply {
            addAction(Constants.NOTIFICATION_ACTION_EXIT)
            addAction(Constants.NOTIFICATION_ACTION_PLAY)
        }
    }

    @SuppressLint("RemoteViewLayout")
    private fun startForegroundService(channel: RadioChannelModel, pause: Boolean) {

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.putExtra("played_channel", channel)


        val pendingIntent = PendingIntent.getActivity(
            this,
            NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationView = RemoteViews(packageName, R.layout.radio_notification)

        val notificationBuilder = PlayerNotificationManager.makePlayingStatusNotification(
            channel.name,
            pendingIntent,
            this,
            CHANNEL_ID,
            CHANNEL_NAME,
            NOTIFICATION_TITLE,
            notificationView,
            channel,
            pause
        )
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent?.getSerializableExtra("played_channel")?.let {
            channel = it as RadioChannelModel
        }

        var willPause = false

        mediaPlayer.setMediaSource(
            MediaSourceAbstractFactory().getMediaSourceFactoy(
                Uri.parse(
                    channel.uri
                )
            )
        )
        mediaPlayer.prepare()

        if(Constants.NOTIFICATION_ACTION_PLAY.equals(intent?.action)){
            mediaPlayer.pause()
            willPause = true
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(channel, willPause)
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

    override fun onBind(intent: Intent?): IBinder? {
        val messenger = Messenger(MessageHandler())
        return messenger.binder
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        serviceScope.cancel()
    }

    private inner class MessageHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MessageConstants.PLAY -> {
                    mediaPlayer.play()
                }

                MessageConstants.PAUSE -> {
                    mediaPlayer.pause()
                }

                MessageConstants.SET_LISTENER -> {
                    if (playerEvents == null) {
                        playerEvents = msg.obj as PlayerListener
                        playerEvents?.let { mediaPlayer.addListener(it) }
                    }
                }

                MessageConstants.PLAY_PAUSE_NOTIFICATION -> {
                    if (mediaPlayer.isPlaying) {
                        mediaPlayer.pause()
                    } else {
                        mediaPlayer.play()
                    }
                }

                MessageConstants.EXIT_NOTIFICATION -> {
                    stopSelf()
                }
            }
        }
    }



}