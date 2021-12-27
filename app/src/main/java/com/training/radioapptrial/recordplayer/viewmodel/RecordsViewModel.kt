package com.training.radioapptrial.recordplayer.viewmodel

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import androidx.lifecycle.ViewModel
import com.training.radioapptrial.channelrecorder.util.Constants.OUTPUT_PATH
import com.training.radioapptrial.recordplayer.model.ChannelMediaRecordModel
import com.training.radioapptrial.recordplayer.util.ExternalStorageHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.media.AudioManager
import android.media.MediaPlayer.OnPreparedListener

import android.os.Build
import java.io.File
import java.io.FileInputStream
import java.lang.Exception


@HiltViewModel
class RecordsViewModel
@Inject constructor(
    var mediaPlayer: MediaPlayer
) : ViewModel() {


    fun getRecordList(context: Context): List<ChannelMediaRecordModel>{
        val recList = ExternalStorageHelper.getFiles(context, OUTPUT_PATH)
        val actualRecordsList = mutableListOf<ChannelMediaRecordModel>()
        for (item in recList){
            val pathFolders = item.absolutePath.split("/")
            val name = pathFolders[pathFolders.size-1]
            val path = item.absolutePath
            val duration = 0L

            actualRecordsList.add(ChannelMediaRecordModel(name, duration, path))
        }
        return actualRecordsList
    }

    fun playNewRecord(context: Context,record: ChannelMediaRecordModel){
        mediaPlayer.release()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                    .build()
            )
        }
        mediaPlayer.apply {
            val str = FileInputStream(File(record.path))
            setDataSource(str.fd)
            prepare()
            start()
        }
    }

    fun pauseRecord(){
        mediaPlayer.pause()
    }

    fun resumeRecord(){
        mediaPlayer.start()
    }

    fun releasePlayer(){
        mediaPlayer.release()
    }
}