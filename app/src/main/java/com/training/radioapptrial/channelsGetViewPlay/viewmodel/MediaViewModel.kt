package com.training.radioapptrial.channelsGetViewPlay.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE
import com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY
import com.training.radioapptrial.channelsGetViewPlay.factory.MediaSourceAbstractFactory
import com.training.radioapptrial.channelsGetViewPlay.listener.PlayerListener
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.radioforegroundservice.service.RadioService
import com.training.radioapptrial.radioforegroundservice.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
class MediaViewModel
@Inject
constructor(
    application: Application,
    val mediaPlayer: ExoPlayer
) : AndroidViewModel(application) {
    var isPlaying = false
    var isFailure = false
    var isServiceActive = false
    var playerEvents: PlayerListener

    private val _playerState: MutableLiveData<Int> =
        MutableLiveData(-1)

    val playerState: MutableLiveData<Int>
        get() = _playerState


    init {
        playerEvents = PlayerListener(::emitState)
        mediaPlayer.addListener(playerEvents)
    }

    fun playNewStation(channel: RadioChannelModel) {
        isPlaying = true
        isFailure = false

        if(isServiceActive){
            stopService()
        }

        isServiceActive = true

        mediaPlayer.stop()
        mediaPlayer.setMediaSource(
            MediaSourceAbstractFactory().getMediaSourceFactoy(
                Uri.parse(
                    channel.uri
                )
            )
        )
        mediaPlayer.prepare()
        startService(channel)
    }

    private fun startService(channel: RadioChannelModel){
        Intent(getApplication(), RadioService::class.java).apply {

            putExtra("played_channel", channel)
            putExtra("played_channel_name", channel.name)
            putExtra("played_channel_img", channel.image_url)

            setAction(ACTION_PAUSE)
            ContextCompat.startForegroundService(getApplication(), this)
        }
    }

    fun pausePlayer() {
        if (!isFailure) {
            isPlaying = false
            mediaPlayer.pause()
        }
    }

    fun resumePlayer() {
        if (!isFailure) {
            isPlaying = true
            mediaPlayer.play()
        }
    }

    private fun emitState(state: Int) {
        _playerState.postValue(state)
    }

    fun stopService(){
        mediaPlayer.stop()
        val serviceIntent = Intent(getApplication(), RadioService::class.java)
        getApplication<Application>().stopService(serviceIntent)
    }


}