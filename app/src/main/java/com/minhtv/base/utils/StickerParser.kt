package com.draw.animation.utils

import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.draw.animation.models.flatModels.sticker.File
import com.draw.animation.models.flatModels.sticker.Sticker
import com.draw.animation.models.flatModels.sticker.StickerPack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.Channels

object StickerParser {

    const val BIN_URL = "https://cdn.widodc.com/stickerpack_games/v4_picture_116_android___.bin"

    fun loadStickerData(context: Context, url: String = BIN_URL) {  //just using for pre download ...
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val binFile = java.io.File(context.cacheDir, "sticker_cache.bin")
                val buffer = if (binFile.exists()) {
                    if (true) return@launch
                    Log.d("STICKER_VM", "Reading from cache...")
                    ByteBuffer.wrap(binFile.readBytes()).order(ByteOrder.LITTLE_ENDIAN)
                } else {
                    Log.d("STICKER_VM", "Downloading from URL...")
                    downloadStickerFile(url, binFile)
                    ByteBuffer.wrap(binFile.readBytes()).order(ByteOrder.LITTLE_ENDIAN)
                }

            } catch (e: Exception) {
                Log.e("STICKER_VM", "Exception", e)
            }
        }
    }
    private suspend fun downloadStickerFile(url: String, destinationFile: java.io.File): Boolean = withContext(Dispatchers.IO) {
        val request = Request.Builder().url(url).build()
        var response: Response? = null
        val client = OkHttpClient()
        try {
            response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext false

            response.body?.byteStream()?.use { input ->
                FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }

            true
        } catch (e: IOException) {
            Log.e("DOWNLOAD", "Download error: ${e.message}", e)
            false
        } finally {
            response?.close()
        }
    }
}
