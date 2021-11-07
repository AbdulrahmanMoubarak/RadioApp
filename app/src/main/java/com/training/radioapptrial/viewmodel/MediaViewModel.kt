package com.training.radioapptrial.viewmodel

import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.training.radioapptrial.R
import com.training.radioapptrial.factory.MediaSourceAbstractFactory
import com.training.radioapptrial.listener.PlayerListener
import com.training.radioapptrial.model.RadioChannelModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.android.synthetic.main.fragment_radio_channels.*
import javax.inject.Inject

@HiltViewModel
class MediaViewModel
@Inject
constructor(
    val mediaPlayer: SimpleExoPlayer
): ViewModel() {
    var isPlaying = false
    var isFailure = false
    lateinit var playerEvents: PlayerListener

     fun setPlayerEvents(
        onErrorEvent: () -> Unit = {},
        onLoadingEvent: (Boolean) -> Unit = {}
    ){
        playerEvents = PlayerListener(onErrorEvent, onLoadingEvent)
        mediaPlayer.addListener(playerEvents)
    }

     fun playNewStation(channel: RadioChannelModel) {
        isPlaying = true
        isFailure = false
        mediaPlayer.stop()
        mediaPlayer.setMediaSource(
            MediaSourceAbstractFactory().getMediaSourceFactoy(
                Uri.parse(
                    channel.uri
                )
            )
        )
        mediaPlayer.prepare()
    }

     fun pausePlayer() {
        if(!isFailure) {
            isPlaying = false
            mediaPlayer.pause()
        }
    }

     fun resumePlayer() {
        if(!isFailure) {
            isPlaying = true
            mediaPlayer.play()
        }
    }

}