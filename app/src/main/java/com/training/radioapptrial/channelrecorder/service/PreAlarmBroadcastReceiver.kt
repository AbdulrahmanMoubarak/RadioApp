package com.training.radioapptrial.channelrecorder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.widget.Toast
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.roomdb.RecordingAlarmsDatabase
import com.training.radioapptrial.channelrecorder.roomdb.model.ChannelRecordingAlarmModel
import com.training.radioapptrial.channelrecorder.util.Constants
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PreAlarmBroadcastReceiver : BroadcastReceiver() {
    private var database: RecordingAlarmsDatabase? = null

    init {
        MainApplication.getAppContext()?.let {
            database = RecordingAlarmsDatabase.getInstance(it)
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {

        var actionStr = ""
        intent?.action?.let { actionStr = it }
        if (actionStr.equals(Constants.PREALARM_ACTION)) {
            val backup_channelId = intent?.`package`
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                CoroutineScope(Dispatchers.IO).launch {
                    backup_channelId?.let {
                        database?.findAlarmById(it.toInt())?.collect {
                            val calendar = Calendar.getInstance().apply {
                                timeInMillis = it.time
                            }

                            createRecordingAlarm(
                                calendar,
                                it.duration,
                                it.recordingId
                            )
                        }
                    }
                }

                context?.let {  PlayerNotificationManager.createImmediateNotification(it, "Channel Recording", "Recording starts in 60 minutes") }
            }
        }
    }

    private fun getAlarmIntent(action_type: String, alarm_id: Int): PendingIntent {
        val intent = Intent(
            MainApplication.getAppContext(),
            ChannelRecordBroadcastReciever::class.java
        ).apply {
            action = action_type
            setPackage(alarm_id.toString())
            putExtra("id", alarm_id)
        }
        return PendingIntent.getBroadcast(MainApplication.getAppContext(), 0, intent, 0).apply {

        }
    }

    private fun createRecordingAlarm(calendar: Calendar, duration: Int, recording_id: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            recording_id.let {
                val startPendingIntent = getAlarmIntent(Constants.START_SERVICE_ACTION, it)
                val stopPendingIntent = getAlarmIntent(Constants.STOP_SERVICE_ACTION, it)

                val alarm = MainApplication.getAppContext()
                    ?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

                alarm?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    //AlarmManager.INTERVAL_DAY,
                    startPendingIntent
                )


                calendar.add(Calendar.MINUTE, duration)
                //calendar.add(Calendar.SECOND, 15)  //for testing

                alarm?.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    //AlarmManager.INTERVAL_DAY,
                    stopPendingIntent
                )
            }
        }
    }
}

