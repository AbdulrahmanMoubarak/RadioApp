package com.training.radioapptrial.channelsGetViewPlay.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.SimpleExoPlayer
import com.training.radioapptrial.channelsGetViewPlay.factory.MediaSourceAbstractFactory
import com.training.radioapptrial.channelsGetViewPlay.listener.PlayerListener
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import dagger.hilt.android.lifecycle.HiltViewModel
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