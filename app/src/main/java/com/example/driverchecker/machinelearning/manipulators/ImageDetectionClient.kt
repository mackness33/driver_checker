package com.example.driverchecker.machinelearning.manipulators

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

class ImageDetectionClient : AClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>, String>() {
    override val evaluationListener: ClassificationListener<String> = EvaluationImageDetectionListener()
    override val output: LiveData<IImageDetectionFinalResult<String>?>
        get() = mOutput

    // FUNCTIONS

    suspend fun produceImage (imgProxy: ImageProxy) {
        produceInput(ImageDetectionInput(ImageDetectionUtils.imageProxyToBitmap(imgProxy)))
    }

    suspend fun produceImage (bitmap: Bitmap) {
        produceInput(ImageDetectionInput(bitmap))
    }

    // INNER CLASSES
    private inner class EvaluationImageDetectionListener :
        EvaluationClassificationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            super.onLiveClassificationEnd(state)

            if (state.finalResult != null)
                mOutput.postValue(ImageDetectionFinalResult(state.finalResult.confidence, state.finalResult.supergroup))

            Log.d("ImageDetectionClient - EvaluationImageDetectionListener", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}