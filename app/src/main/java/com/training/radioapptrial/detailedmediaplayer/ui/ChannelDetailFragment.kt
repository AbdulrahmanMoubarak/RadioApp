package com.training.radioapptrial.detailedmediaplayer.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.training.radioapptrial.R
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelrecorder.service.ChannelRecordBroadcastReciever
import com.training.radioapptrial.channelrecorder.service.ChannelRecordingService
import com.training.radioapptrial.channelrecorder.ui.RecordTimeDialogFragment
import com.training.radioapptrial.channelrecorder.utils.Constants.START_SERVICE_ACTION
import com.training.radioapptrial.channelrecorder.utils.Constants.STOP_SERVICE_ACTION
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detailed_media_player.*
import kotlinx.android.synthetic.main.detailed_media_player.view.*
import kotlinx.android.synthetic.main.fragment_channel_detail.*
import kotlinx.android.synthetic.main.fragment_record_time_dialog.view.*
import kotlinx.android.synthetic.main.mini_media_player.view.*
import java.time.Duration

@AndroidEntryPoint
class ChannelDetailFragment : Fragment() {

    private var channel: RadioChannelModel? = null
    private var replay: Boolean? = null
    private val mediaViewModel: MediaViewModel by viewModels()
    private var isPlaying = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            channel = it.getSerializable("played_channel") as RadioChannelModel
            replay = it.getBoolean("replay")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_channel_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        channel?.let { detailedPlayer.loadMedia(it) }

        replay?.let {
            if(it) {
                channel?.let { mediaViewModel.playNewStation(it) }
            }
        }
        detailedPlayer.play()

        detailedPlayer.detail_buttonPlayPause.setOnClickListener {
            if (isPlaying) {
                isPlaying = false
                detailedPlayer.pause()
                mediaViewModel.pausePlayer()
            } else {
                isPlaying = true
                detailedPlayer.play()
                mediaViewModel.resumePlayer()
            }
        }

        detailedPlayer.resize.setOnClickListener {
            toMiniPlayer(channel)
        }

        detailedPlayer.recordButton.setOnClickListener {
            val dialog = RecordTimeDialogFragment(::onDialogConfirm)
            dialog.show(requireActivity().supportFragmentManager,"record start time and duration")
        }

    }

    fun toMiniPlayer(channel:RadioChannelModel?){
        val bundle = Bundle().apply {
            putSerializable("played_channel", channel)
        }
        findNavController().navigate(R.id.action_channelDetailFragment_to_fragmentRadioChannels, bundle)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun onDialogConfirm(calendar: Calendar, duration: Int){
        val alarm = requireActivity().getSystemService(Context.ALARM_SERVICE) as? AlarmManager

        alarm?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            getAlarmIntent()
        )

        calendar.add(Calendar.MINUTE, duration)
        alarm?.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            getAlarmStopIntent()
        )


        Toast.makeText(
            MainApplication.getAppContext(),
            "Recorder set successfully",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun getAlarmIntent(): PendingIntent {
        val intent = Intent(context, ChannelRecordBroadcastReciever::class.java).apply {
            setAction(START_SERVICE_ACTION)
            //putExtra("played_channel", channel)
        }
            return PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
    }

    private fun getAlarmStopIntent(): PendingIntent {
        val intent = Intent(context, ChannelRecordBroadcastReciever::class.java).apply {
            setAction(STOP_SERVICE_ACTION)
            //putExtra("played_channel", channel)
        }
        return PendingIntent.getBroadcast(requireContext(), 0, intent, 0)
    }

    private fun getAlarmServiceIntent_noBroadcast(): PendingIntent{
        val intent = Intent(context, ChannelRecordingService::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PendingIntent.getForegroundService(requireContext(), 0, intent, 0)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
    }

}