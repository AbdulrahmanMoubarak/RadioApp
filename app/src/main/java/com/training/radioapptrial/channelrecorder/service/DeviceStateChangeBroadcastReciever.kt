package com.training.radioapptrial.channelrecorder.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_BOOT_COMPLETED
import android.content.Intent.ACTION_LOCKED_BOOT_COMPLETED
import android.util.Log
import android.widget.Toast
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.roomdb.RecordingAlarmsDatabase
import com.training.radioapptrial.channelrecorder.util.Constants
import com.training.radioapptrial.radioforegroundservice.exoplayer.PlayerNotificationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DeviceStateChangeBroadcastReciever: BroadcastReceiver() {

    private var database: RecordingAlarmsDatabase? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.i("here", "onReceive: Recieved")
        Log.d("here", "onReceive: Recieved")

        context?.let {  PlayerNotificationManager.createImmediateNotification(it, "Recording failed", "Internet connection error") }
        
        if(action.equals(ACTION_BOOT_COMPLETED) || action.equals(ACTION_LOCKED_BOOT_COMPLETED)){
            context?.let {
                database = RecordingAlarmsDatabase.getInstance(it)
            }



            /*
            CoroutineScope(Dispatchers.Main).launch {
                database?.getAllAlarms()?.collect {
                    for(recording in it){
                        val startPendingIntent = getAlarmIntent(Constants.START_SERVICE_ACTION, recording.recordingId)
                        val stopPendingIntent = getAlarmIntent(Constants.STOP_SERVICE_ACTION, recording.recordingId)

                        val alarm = MainApplication.getAppContext()
                            ?.getSystemService(Context.ALARM_SERVICE) as? AlarmManager

                        alarm?.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            recording.time,
                            AlarmManager.INTERVAL_DAY,
                            startPendingIntent
                        )


                        alarm?.setRepeating(
                            AlarmManager.RTC_WAKEUP,
                            recording.time.plus(recording.duration*60*1000),
                            AlarmManager.INTERVAL_DAY,
                            stopPendingIntent
                        )
                    }

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            MainApplication.getAppContext(),
                            "Alarms reset successfully !!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            */
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