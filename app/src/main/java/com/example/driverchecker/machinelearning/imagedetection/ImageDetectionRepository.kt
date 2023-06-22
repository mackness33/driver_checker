package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.*
import androidx.camera.core.ImageProxy
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.MLRepository
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream


class ImageDetectionRepository (localUri: String? = null, remoteUri: String? = null, classificationJson: String?) : MLRepository<IImageDetectionData, IImageDetectionBox, ImageDetectionArrayListOutput>() {

    init {
        local = ImageDetectionLocalRepository(YOLOModel(localUri, classificationJson))
        remote = ImageDetectionRemoteRepository(ImageDetectionRemoteModel(remoteUri))
    }

//    constructor (localUri: String? = null, remoteUri: String? = null, classificationJson: String?) : this (localUri, remoteUri) {
//
//    }

    suspend fun instantClassification (path:String) : ImageDetectionArrayListOutput? {
        return instantClassification(ImageDetectionBaseInput(BitmapFactory.decodeFile(path)))
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

//        fun getInstance(localUri: String?, remoteUri: String?): ImageDetectionRepository =
//            INSTANCE ?: ImageDetectionRepository(localUri, remoteUri)

        fun getInstance(localUri: String?, remoteUri: String?, classificationJson: String?): ImageDetectionRepository =
            INSTANCE ?: ImageDetectionRepository(localUri, remoteUri, classificationJson)

    }

    override val repositoryScope: CoroutineScope
        get() = TODO("Not yet implemented")
}