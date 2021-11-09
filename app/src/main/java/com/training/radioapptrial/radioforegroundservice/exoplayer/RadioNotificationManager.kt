package com.training.radioapptrial.radioforegroundservice.exoplayer

import android.app.PendingIntent
import android.content.Context
import android.graphics.Bitmap
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import coil.ImageLoader
import coil.request.ImageRequest
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_CHANNEL_ID
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_ID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class RadioNotificationManager(
    private val context: Context,
    sessionToken: MediaSessionCompat.Token,
    notificationListener:PlayerNotificationManager.NotificationListener,
) {
    private val notificationScope = CoroutineScope(Dispatchers.Main + Job())
    private val notficationManeger: PlayerNotificationManager
    init {
        val mediaController = MediaControllerCompat(context, sessionToken)
        notficationManeger = PlayerNotificationManager.Builder(
            context,
            NOTIFICATION_ID,
            NOTIFICATION_CHANNEL_ID,
            DescriptionAdapter(mediaController)
        ).setNotificationListener(notificationListener).build()
    }

    fun showNotification(player: Player){
        notficationManeger.setPlayer(player)
    }

    private inner class DescriptionAdapter(
        private val mediaController: MediaControllerCompat
        ): PlayerNotificationManager.MediaDescriptionAdapter{

        override fun getCurrentContentTitle(player: Player): CharSequence {
            return mediaController.metadata.description.title.toString()
        }

        override fun createCurrentContentIntent(player: Player): PendingIntent? {
            return mediaController.sessionActivity
        }

        override fun getCurrentContentText(player: Player): CharSequence? {
            return mediaController.metadata.description.subtitle.toString()
        }

        override fun getCurrentLargeIcon(
            player: Player,
            callback: PlayerNotificationManager.BitmapCallback
        ): Bitmap? {
            val imageLoader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data("https://www.example.com/image.jpg")
                .allowConversionToBitmap(true)
                .build()
            notificationScope.launch {
                val drawable = imageLoader.execute(request).drawable
            }
            return null
        }

    }
}

//public Builder(Context context,int notificationId,String channelId,MediaDescriptionAdapter mediaDescriptionAdapter)