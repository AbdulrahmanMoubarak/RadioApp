package com.training.radioapptrial.channelrecorder.util

import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class StreamDownloader {
    fun startDownloadingStream(request: Request): Flow<ByteArray>{
        val client = OkHttpClient()
        return flow {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) throw IOException("Unexpected code $response")
                while (!response.body!!.source().exhausted()) {
                    val byteArray = ByteArray(8192)
                    val count2 = response.body!!.source().read(byteArray)
                    emit(byteArray)
                }
            }
        }
    }
}