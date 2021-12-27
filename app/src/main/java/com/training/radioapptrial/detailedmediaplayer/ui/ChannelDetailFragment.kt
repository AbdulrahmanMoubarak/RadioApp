package com.training.radioapptrial.detailedmediaplayer.ui

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
            val dialog = RecordTimeDialogFragment(::onDialogConfirmVm)
            dialog.show(requireActivity().supportFragmentManager,"record start time and duration")
        }

    }

    fun toMiniPlayer(channel:RadioChannelModel?){
        val bundle = Bundle().apply {
            putSerializable("played_channel", channel)
        }
        findNavController().navigate(R.id.action_channelDetailFragment_to_fragmentRadioChannels, bundle)
    }

    private fun onDialogConfirmVm(calendar: Calendar, duration: Int){
        channel?.let {  recorderViewModel.createRecordingAlarm(calendar, duration, it)}
    }


}