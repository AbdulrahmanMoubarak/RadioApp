package com.training.radioapptrial.channelrecorder.util

import android.content.Context
import android.net.Uri
import android.util.Log
import com.training.radioapptrial.channelrecorder.util.Constants.OUTPUT_PATH
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object AudioFileBuilder {

    @Throws(FileNotFoundException::class)
    fun writeToFile(applicationContext: Context, byteArray:ByteArray, channelName: String): Uri {

        Log.d("here", "writeToFile: " + byteArray[0])
        val name = String.format("${channelName}-%s.wav", getCurrentDateTime())
        val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
        if (!outputDir.exists()) {
            outputDir.mkdirs() // should succeed
        }
        val outputFile = File(outputDir, name)
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(outputFile)
            out.write(byteArray)

        } finally {
            out?.let {
                try {
                    it.close()
                } catch (ignore: IOException) {
                }

            }
        }
        return Uri.fromFile(outputFile)
    }

    private fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat(format, locale)
        return formatter.format(this)
    }

    private fun getCurrentDateTime(): Date {
        return Calendar.getInstance().time
    }
}