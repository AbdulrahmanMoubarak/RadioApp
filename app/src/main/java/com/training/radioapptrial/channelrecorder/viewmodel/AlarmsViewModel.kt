package com.training.radioapptrial.channelrecorder.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.roomdb.RecordingAlarmsDatabase
import com.training.radioapptrial.channelrecorder.roomdb.model.ChannelRecordingAlarmModel
import com.training.radioapptrial.channelrecorder.service.ChannelRecordBroadcastReciever
import com.training.radioapptrial.channelrecorder.service.PreAlarmBroadcastReceiver
import com.training.radioapptrial.channelrecorder.util.Constants.ALARM_PREFIRING_TIME_MILLIS
import com.training.radioapptrial.channelrecorder.util.Constants.PREALARM_ACTION
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AlarmsViewModel
@Inject
constructor() : ViewModel() {

    private var database: RecordingAlarmsDatabase? = null

    init {
        MainApplication.getAppContext()?.let {
            database = RecordingAlarmsDatabase.getInstance(it)
        }
    }


    fun createRecordingAlarm(calendar: Calendar, duration: Int, channel: RadioChannelModel) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            CoroutineScope(Dispatchers.IO).launch {
                database?.insertRecordingAlarm(
                    ChannelRecordingAlarmModel(
                        0,
                        calendar.timeInMillis,
                        duration,
                        channel.name,
                        channel.uri
                    )
                )?.collect {
                    val recording_id = it

                    recording_id.let {
                        val preAlarmPendingIntent =getPreAlarmIntent(it)

                        val alarm = MainApplication.getAppContext()
                            ?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

                        alarm?.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            calendar.timeInMillis - ALARM_PREFIRING_TIME_MILLIS,
                            AlarmManager.INTERVAL_DAY,
                            preAlarmPendingIntent
                        )


                        calendar.add(Calendar.MINUTE, duration)
                        //calendar.add(Calendar.SECOND, 15)  //for testing


                        withContext(Dispatchers.Main) {
                            Toast.makeText(
                                MainApplication.getAppContext(),
                                "Recorder set successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
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

    private fun getPreAlarmIntent( alarm_id: Int):PendingIntent{

        val intent = Intent(
            MainApplication.getAppContext(),
            PreAlarmBroadcastReceiver::class.java
        ).apply {
            action =  PREALARM_ACTION
            setPackage(alarm_id.toString())
        }

        return PendingIntent.getBroadcast(
            MainApplication.getAppContext(),
            0,
            intent,
            0
        )
    }
}