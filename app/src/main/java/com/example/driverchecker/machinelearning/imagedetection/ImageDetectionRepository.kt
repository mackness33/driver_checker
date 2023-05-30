package com.example.driverchecker.machinelearning.imagedetection

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.graphics.*
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.text.TextUtils
import androidx.camera.core.ImageProxy
import com.example.driverchecker.FileUtils
import com.example.driverchecker.machinelearning.general.MLRepository
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class ImageDetectionRepository (localUri: String? = null, remoteUri: String? = null) : MLRepository<Bitmap, MLResult>() {
//    constructor(localUri: String? = null, remoteUri: String? = null) : this() {
//        when {
//            localUri != null && remoteUri != null ->
//                initializeRepos(ImageDetectionLocalRepository(ImageDetectionLocalModel(localUri)), ImageDetectionRemoteRepository(ImageDetectionRemoteModel(remoteUri)))
//
//            localUri != null ->
//                initializeLocalRepo(ImageDetectionLocalRepository(ImageDetectionLocalModel(localUri)))
//
//            remoteUri != null ->
//                initializeRemoteRepo(ImageDetectionRemoteRepository(ImageDetectionRemoteModel(remoteUri)))
//
//            else -> {}
//        }
//    }

    init {
        local = ImageDetectionLocalRepository(ImageDetectionLocalModel(localUri))
        remote = ImageDetectionRemoteRepository(ImageDetectionRemoteModel(remoteUri))
    }

    suspend fun instantClassification (path:String) : MLResult? {
        val bm = BitmapFactory.decodeFile(path)
        // the bitmap MUST BE SCALED, if it is too big the application is going ot crash
        val bmScaled = Bitmap.createScaledBitmap(bm, 500, (bm.height*500)/bm.width, true)
        return this.instantClassification(bmScaled)
    }

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val yBuffer = image.planes[0].buffer // Y
        val vuBuffer = image.planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    companion object {
        @Volatile private var INSTANCE: ImageDetectionRepository? = null

        fun getInstance(localUri: String?, remoteUri: String?): ImageDetectionRepository =
            INSTANCE ?: ImageDetectionRepository(localUri, remoteUri)

    }
}