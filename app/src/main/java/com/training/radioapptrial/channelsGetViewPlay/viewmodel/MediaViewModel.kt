package com.training.radioapptrial.channelsGetViewPlay.viewmodel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.graphics.Color
import android.net.Uri
import android.os.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PAUSE
import com.google.android.exoplayer2.ui.PlayerNotificationManager.ACTION_PLAY
import com.training.radioapptrial.application.MainApplication
import com.training.radioapptrial.channelsGetViewPlay.factory.MediaSourceAbstractFactory
import com.training.radioapptrial.channelsGetViewPlay.listener.PlayerListener
import com.training.radioapptrial.channelsGetViewPlay.model.RadioChannelModel
import com.training.radioapptrial.channelsGetViewPlay.util.MessageConstants
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
) : AndroidViewModel(application) {
    var isPlaying = false
    var isFailure = false
    var isServiceActive = false
    var playerEvents: PlayerListener
    private val serviceIntent = Intent(getApplication(), RadioService::class.java)

    private var messenger: Messenger? = null

    init {
        playerEvents = PlayerListener(::emitState)
    }

    private val serviceConnection = object: ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            messenger = Messenger(p1)
            messenger?.send(Message().apply {
                this.what = MessageConstants.SET_LISTENER
                this.obj = playerEvents
            })
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            messenger = null
        }
    }

    private val _playerState: MutableLiveData<Int> =
        MutableLiveData(-1)

    val playerState: MutableLiveData<Int>
        get() = _playerState

    fun playNewStation(channel: RadioChannelModel) {
        isPlaying = true
        isFailure = false

        if(isServiceActive){
            stopService()
        }
        isServiceActive = true
        startService(channel)
    }

    private fun startService(channel: RadioChannelModel){
        serviceIntent.apply {
            putExtra("played_channel", channel)
            putExtra("played_channel_name", channel.name)
            putExtra("played_channel_img", channel.image_url)
        }
        ContextCompat.startForegroundService(getApplication(), serviceIntent)
        MainApplication.getAppContext()?.bindService(serviceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND)
    }

    fun pausePlayer() {
        MainApplication.getAppContext()?.bindService(serviceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND)
        if (!isFailure) {
            isPlaying = false
            messenger?.send(Message().apply {
                this.what = MessageConstants.PAUSE

            })
        }
    }

    fun resumePlayer() {
        MainApplication.getAppContext()?.bindService(serviceIntent, serviceConnection, Context.BIND_NOT_FOREGROUND)
        if (!isFailure) {
            isPlaying = true
            messenger?.send(Message.obtain(null, MessageConstants.PLAY,0,0,0,))
        }
    }

    private fun emitState(state: Int) {
        _playerState.postValue(state)
    }

    fun stopService(){
        val serviceIntent = Intent(getApplication(), RadioService::class.java)
        getApplication<Application>().stopService(serviceIntent)
        messenger?.let {
            getApplication<Application>().unbindService(serviceConnection)
        }
    }
}