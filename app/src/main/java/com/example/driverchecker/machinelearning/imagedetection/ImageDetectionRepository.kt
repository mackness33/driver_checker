package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.*
import androidx.camera.core.ImageProxy
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.MLRepository
import kotlinx.coroutines.CoroutineScope
import java.io.ByteArrayOutputStream


class ImageDetectionRepository (localUri: String? = null, remoteUri: String? = null, classificationJson: String?) : MLRepository<IImageDetectionData, IImageDetectionBox, String, ImageDetectionArrayListOutput<String>>() {

    init {
        local = ImageDetectionLocalRepository(YOLOModel(localUri, classificationJson))
        remote = ImageDetectionRemoteRepository(ImageDetectionRemoteModel(remoteUri))
    }

//    suspend fun instantClassification (path:String) : ImageDetectionArrayListOutput<String>? {
//        return instantClassification(ImageDetectionBaseInput(BitmapFactory.decodeFile(path)))
//    }

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