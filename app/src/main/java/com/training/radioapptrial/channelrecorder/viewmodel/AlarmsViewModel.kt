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
import com.training.radioapptrial.channelrecorder.util.Constants.START_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.util.Constants.STOP_SERVICE_ACTION
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
                        val startPendingIntent = getAlarmIntent(START_SERVICE_ACTION, it)
                        val stopPendingIntent = getAlarmIntent(STOP_SERVICE_ACTION, it)

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
}