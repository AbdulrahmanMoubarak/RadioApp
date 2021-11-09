package com.training.radioapptrial.channelsGetViewPlay.listener

import android.media.session.PlaybackState
import android.util.Log
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.audio.AudioAttributes

class PlayerListener(
    val onErrorEvent: () -> Unit = {},
    val onLoadingEvent: (Boolean) -> Unit = {}
): Player.Listener {

    override fun onPlayerError(error: PlaybackException) {
        onLoadingEvent(false)
        onErrorEvent()
    }

    override fun onAudioAttributesChanged(audioAttributes: AudioAttributes) {
        super.onAudioAttributesChanged(audioAttributes)
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        if(playbackState == PlaybackState.STATE_BUFFERING){
            onLoadingEvent(true)
        } else if(playbackState == PlaybackState.STATE_PLAYING){
            onLoadingEvent(false)
        }
        Log.d("here", "onPlaybackStateChanged: $playbackState")
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        onLoadingEvent(isLoading)
    }

}