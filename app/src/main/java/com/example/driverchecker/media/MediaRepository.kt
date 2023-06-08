package com.example.driverchecker.media

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.DisplayMetrics
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class MediaRepository : FrameExtractorInterface {
    val video: MutableList<Bitmap>? = mutableListOf()

    suspend fun extractVideo (path: String) {
        val frameExtractor = FrameExtractor(this)
        return withContext(Dispatchers.Default) {
            try {
                frameExtractor.extractFrames(path)
            } catch (exception: Exception) {
                exception.printStackTrace()
            }
        }
    }

    override fun onCurrentFrameExtracted(currentFrame: Frame) {
        val imageBitmap = fromBufferToBitmap(currentFrame.byteBuffer, currentFrame.width, currentFrame.height)

        // TODO: Manage case with resulted bitmap null or empty
        if (imageBitmap != null) {
            video?.add(imageBitmap)
        }
    }

    override fun onAllFrameExtracted(processedFrameCount: Int, processedTimeMs: Long) {
        Log.d("MediaRepository", "Save: $processedFrameCount frames in: $processedTimeMs ms.")
    }

    /**
     * Get bitmap from ByteBuffer
     */
    fun fromBufferToBitmap(buffer: ByteBuffer, width: Int, height: Int): Bitmap? {
        val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        buffer.rewind()
        result.copyPixelsFromBuffer(buffer)
        val transformMatrix = Matrix()
        val outputBitmap = Bitmap.createBitmap(result, 0, 0, result.width, result.height, transformMatrix, false)
        outputBitmap.density = DisplayMetrics.DENSITY_DEFAULT
        return outputBitmap
    }

}