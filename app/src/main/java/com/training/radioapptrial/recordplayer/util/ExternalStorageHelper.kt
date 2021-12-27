package com.training.radioapptrial.recordplayer.util

import android.content.Context
import com.training.radioapptrial.channelrecorder.util.Constants
import java.io.File


object ExternalStorageHelper {
    fun getFiles(context: Context, folderName: String): List<File>{
        val outputDir = File(context.filesDir, folderName)
        if(outputDir.listFiles() != null) {
            return outputDir.listFiles().toList()
        } else{
            return listOf<File>()
        }
    }
}