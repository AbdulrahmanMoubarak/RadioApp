package com.training.radioapptrial.detailedmediaplayer.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.training.radioapptrial.R
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import kotlinx.android.synthetic.main.detailed_media_player.view.*
import kotlinx.android.synthetic.main.fragment_channel_detail.*
import kotlinx.android.synthetic.main.mini_media_player.view.*

class ChannelDetailFragment : Fragment() {

    private var channel: RadioChannelModel? = null
    private var isPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            channel = it.getSerializable("channel") as RadioChannelModel
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

        detailedPlayer.detail_buttonPlayPause.setOnClickListener {
            if (isPlaying) {
                isPlaying = false
                detailedPlayer.pause()
            } else {
                isPlaying = true
                detailedPlayer.play()
            }
        }
    }


}