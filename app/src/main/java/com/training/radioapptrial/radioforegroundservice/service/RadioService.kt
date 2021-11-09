package com.training.radioapptrial.radioforegroundservice.service

import android.app.PendingIntent
import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.training.radioapptrial.channelsGetViewPlay.factory.MediaSourceAbstractFactory
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.radioforegroundservice.exoplayer.RadioNotificationManager
import com.training.radioapptrial.radioforegroundservice.exoplayer.callback.RadioPlayerNotificationListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import javax.inject.Inject

private const val SERVICE_TAG = "RadioService"

@AndroidEntryPoint
class RadioService(): MediaBrowserServiceCompat() {

    companion object{
        var currentChannel: RadioChannelModel? = null
    }

    @Inject
    lateinit var mediaSourceAbstractFactory: MediaSourceAbstractFactory

    @Inject
    lateinit var exoplayer: ExoPlayer

    private lateinit var radionNotificationManager: RadioNotificationManager

    val serviceJob = Job()
    val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false


    override fun onCreate() {
        super.onCreate()
        val activityIntent = packageManager?.getLaunchIntentForPackage(packageName)?.let {
            PendingIntent.getActivity(this, 0, it, 0)
        }
        mediaSession = MediaSessionCompat(this, SERVICE_TAG).apply {
            setSessionActivity(activityIntent)
            isActive = true
        }

        sessionToken = mediaSession.sessionToken

        radionNotificationManager = RadioNotificationManager(this, mediaSession.sessionToken, RadioPlayerNotificationListener(this))

        mediaSessionConnector = MediaSessionConnector(mediaSession)
        mediaSessionConnector.setPlayer(exoplayer)
    }

    private fun preparePlayer(){
        currentChannel?.let {
            exoplayer.setMediaSource(
                mediaSourceAbstractFactory.getMediaSourceFactoy(
                    Uri.parse(
                        it.uri
                    )
                )
            )
            exoplayer.prepare()
        }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {
        return null
    }


    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {

    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}