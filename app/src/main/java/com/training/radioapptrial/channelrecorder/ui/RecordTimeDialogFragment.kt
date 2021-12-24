package com.training.radioapptrial.channelrecorder.ui

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.training.radioapptrial.R
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.service.ChannelRecordBroadcastReciever
import com.training.radioapptrial.channelrecorder.util.RecordDialogInputManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_record_time_dialog.view.*

@AndroidEntryPoint
class RecordTimeDialogFragment(var onConfirm: (Calendar, Int) -> Unit) : DialogFragment() {

    @RequiresApi(Build.VERSION_CODES.N)
    private var startRecordingTime = Calendar.getInstance()
    private var duration = 0


    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        val view = layoutInflater.inflate(R.layout.fragment_record_time_dialog, null)

        builder.setView(view).setTitle(getString(R.string.pickRecordTime))
            .setNegativeButton(getString(R.string.cancle)) { dialogInterface, i ->
            }.setPositiveButton(R.string.confirm) { dialogInterface, i ->

                duration = RecordDialogInputManager().validateDialogInput(
                    view.editTextStart.text.toString() ,
                    view.editTextDuration.text.toString()
                )

                if(duration > 0) {
                    duration = view.editTextDuration.text.toString().toInt()
                    onConfirm(startRecordingTime, duration)
                }
            }
        view.pick_btn.setOnClickListener {
            val timepicker = TimePickerDialog(
                requireActivity(),
                { timePicker, hour, min ->

                    view.editTextStart.setText(RecordDialogInputManager().convertClockToText(hour, min))

                    startRecordingTime.set(
                        Calendar.HOUR_OF_DAY, hour
                    )
                    startRecordingTime.set(
                        Calendar.MINUTE, min
                    )

                    startRecordingTime.set(
                        Calendar.SECOND, 0
                    )
                },
                0,
                0,
                true
            )
            timepicker.show()
        }

        return builder.create()
    }



}