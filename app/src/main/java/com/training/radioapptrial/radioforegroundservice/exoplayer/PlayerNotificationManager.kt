package com.training.radioapptrial.radioforegroundservice.exoplayer

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.training.radioapptrial.R
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.service.ChannelRecordBroadcastReciever
import com.training.radioapptrial.channelrecorder.util.Constants
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.radioforegroundservice.service.RadioServiceBroadcastReceiver
import com.training.radioapptrial.radioforegroundservice.util.Constants.CHANNEL_ID
import com.training.radioapptrial.radioforegroundservice.util.Constants.CHANNEL_NAME
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_ACTION_EXIT
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_ACTION_PLAY
import com.training.radioapptrial.radioforegroundservice.util.Constants.NOTIFICATION_TITLE
import com.training.radioapptrial.radioforegroundservice.util.Constants.VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
import com.training.radioapptrial.recordplayer.ui.RecordListActivity

object PlayerNotificationManager {

    fun makeStatusNotification(
        message: String,
        pendingIntent: PendingIntent,
        context: Context,
        channel_id: String,
        channel_name: String,
        title: String
    )
            : NotificationCompat.Builder {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = channel_name
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channel_id, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }

        // Create the notification
        val builder = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.drawable.ic_radio_svgrepo_com)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(null)
            .setSilent(true)


        pendingIntent.let {
            builder.setContentIntent(pendingIntent)
        }

        return builder;

        // Show the notification
        //NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }

    fun createImmediateNotification(
        context: Context,
        title: String,
        message: String,
    ) {
        val builder = NotificationCompat.Builder(
            MainApplication.getApplication(),
            Constants.RECORD_CHANNEL_ID
        )

            .setSmallIcon(R.drawable.ic_radio_svgrepo_com)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setContentIntent(PendingIntent.getActivity(
                MainApplication.getAppContext(),
                0,
                Intent(MainApplication.getAppContext(), RecordListActivity::class.java),
            0
            ))

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }

    fun makePlayingStatusNotification(
        message: String,
        pendingIntent: PendingIntent,
        context: Context,
        channel_id: String,
        channel_name: String,
        title: String,
        view: RemoteViews,
        radioChannel: RadioChannelModel,
        pause: Boolean
    )
            : NotificationCompat.Builder {
        // Make a channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            val name = channel_name
            val description = VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channel_id, name, importance)
            channel.description = description

            // Add the channel
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.createNotificationChannel(channel)
        }
        //Create actions pending intents to send broadcasts
        val exitPendingIntent = PendingIntent.getBroadcast(
            MainApplication.getAppContext(),
            0,
            Intent(
                MainApplication.getAppContext(),
                RadioServiceBroadcastReceiver::class.java
            ).apply {
                setAction(NOTIFICATION_ACTION_EXIT)
                putExtra("played_channel", radioChannel)
            },
            0
        )

        val playPendingIntent = PendingIntent.getBroadcast(
            MainApplication.getAppContext(),
            0,
            Intent(
                MainApplication.getAppContext(),
                RadioServiceBroadcastReceiver::class.java
            ).apply {
                setAction(NOTIFICATION_ACTION_PLAY)
                putExtra("played_channel", radioChannel)
            },
            0
        )

        // Create the notification

        if(pause){
            view.setImageViewResource(R.id.notification_play, R.drawable.ic_play_svgrepo_com)
        }
        view.setTextViewText(R.id.notification_title, message)
        view.setOnClickPendingIntent(R.id.notification_exit, exitPendingIntent)
        view.setOnClickPendingIntent(R.id.notification_play, playPendingIntent)
        val builder = NotificationCompat.Builder(context, channel_id)
            .setSmallIcon(R.drawable.ic_radio_svgrepo_com)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVibrate(null)
            .setSilent(true)
            .setCustomBigContentView(view)

        pendingIntent.let {
            builder.setContentIntent(pendingIntent)
        }

        return builder;

        // Show the notification
        //NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build())
    }
}