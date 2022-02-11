package com.training.radioapptrial.detailedmediaplayer.ui

import android.content.Context
import android.icu.util.Calendar
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.training.radioapptrial.R
import com.training.radioapptrial.channelrecorder.ui.RecordTimeDialogFragment
import com.training.radioapptrial.channelrecorder.viewmodel.AlarmsViewModel
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detailed_media_player.view.*
import kotlinx.android.synthetic.main.fragment_channel_detail.*
import android.os.Build

import android.os.PowerManager
import android.widget.Toast
import android.content.Context.POWER_SERVICE

import androidx.core.content.ContextCompat.getSystemService

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.common.reflect.Reflection.getPackageName
import com.training.radioapptrial.detailedmediaplayer.util.Constants.APP_IN_BATTERY_OPTIMIZATION
import com.training.radioapptrial.detailedmediaplayer.util.Constants.DEVICE_ABLE_TO_RECORD
import com.training.radioapptrial.detailedmediaplayer.util.Constants.DEVICE_IN_POWER_SAVING_MODE


@AndroidEntryPoint
class ChannelDetailFragment : Fragment() {

    private var channel: RadioChannelModel? = null
    private var replay: Boolean? = null
    private val mediaViewModel: MediaViewModel by viewModels()
    private val recorderViewModel: AlarmsViewModel by viewModels()
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

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        channel?.let { detailedPlayer.loadMedia(it) }

        replay?.let {
            if (it) {
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
            val state = checkBatteryOptimization()
            if(state == DEVICE_ABLE_TO_RECORD) {
                val dialog = RecordTimeDialogFragment(::onDialogConfirmVm)
                dialog.show(
                    requireActivity().supportFragmentManager,
                    "record start time and duration"
                )
            } else if (state == APP_IN_BATTERY_OPTIMIZATION){
                val dialog = AlertDialog.Builder(requireContext()).apply {
                    setTitle("Channel recording alarm failed!")
                    setMessage("App is in battery optimiztion, please change the state to be able to set the alarm.")
                    setPositiveButton("OK"){dialogInterface, i ->
                        val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        startActivity(intent)
                    }
                    setNegativeButton("Cancel"){dialogInterface, i ->
                    }
                }

                dialog.show()
            }
        }

    }

    fun toMiniPlayer(channel: RadioChannelModel?) {
        val bundle = Bundle().apply {
            putSerializable("played_channel", channel)
        }
        findNavController().navigate(
            R.id.action_channelDetailFragment_to_fragmentRadioChannels,
            bundle
        )
    }

    private fun onDialogConfirmVm(calendar: Calendar, duration: Int) {
        channel?.let { recorderViewModel.createRecordingAlarm(calendar, duration, it) }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkBatteryOptimization(): Int {
        val packageName = requireActivity().getPackageName()
        val pm = requireActivity().getSystemService(POWER_SERVICE) as PowerManager

        if (pm.isPowerSaveMode()) {
            Toast.makeText(
                requireContext(),
                "Can't set recorder if device is in power saving mode",
                Toast.LENGTH_SHORT
            ).show()

            return DEVICE_IN_POWER_SAVING_MODE
        }

        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            return APP_IN_BATTERY_OPTIMIZATION
        }

        return DEVICE_ABLE_TO_RECORD
    }

}