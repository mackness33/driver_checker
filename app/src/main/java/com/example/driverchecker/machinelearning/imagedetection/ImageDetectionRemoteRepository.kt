package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository

class ImageDetectionRemoteRepository (override val model: MLRemoteModel<Bitmap, MLResult<Float>>?) : MLRemoteRepository<Bitmap, MLResult<Float>>(model) {}