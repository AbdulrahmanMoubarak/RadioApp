package com.training.radioapptrial.factory

import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

class MediaSourceAbstractFactory {
    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)

    fun getMediaSourceFactoy(uri: Uri): MediaSource{
        val type = Util.inferContentType(uri)
        Log.d("here", "getMediaSourceFactoy: $type")
        Log.d("here", "getMediaSourceFactoy: ${uri}")
        when(type){
            C.TYPE_SS -> {
                return SsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            }

            C.TYPE_DASH ->{
                return DashMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            }

            C.TYPE_HLS ->{
                return HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            }

            C.TYPE_RTSP ->{
                return RtspMediaSource.Factory().createMediaSource(MediaItem.fromUri(uri))
            }

            C.TYPE_OTHER ->{
                return ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))
            }

            else ->{
                throw IllegalStateException("Unsupported type: " + type);
            }
        }
    }
}