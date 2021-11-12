package com.training.radioapptrial.channelsGetViewPlay.listener

import android.media.session.PlaybackState
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player

class PlayerListener(
    val emitState: (Int) -> Unit
): Player.Listener {

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        if(isPlaying){
            emitState(PlaybackState.STATE_PLAYING)
        }
    }

    override fun onPlayerError(error: PlaybackException) {
        super.onPlayerError(error)
        emitState(PlaybackState.STATE_ERROR)
    }

    override fun onIsLoadingChanged(isLoading: Boolean) {
        super.onIsLoadingChanged(isLoading)
        if(isLoading){
            emitState(PlaybackState.STATE_BUFFERING)
        }
    }
}