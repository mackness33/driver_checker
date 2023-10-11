package com.example.driverchecker.machinelearning.manipulators

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.utils.ObservableData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

class ImageDetectionClient : AClassificationClient<IImageDetectionInputOld, IImageDetectionFullOutputOld<String>, IImageDetectionFinalResultOld<String>, String>() {
    override val evaluationListener: ClassificationListener<String> = EvaluationImageDetectionListener()
    override val finalResult: ObservableData<IImageDetectionFinalResultOld<String>?>
        get() = mFinalResult
    
    // FUNCTIONS

    suspend fun produceImage (imgProxy: ImageProxy) {
        produceInput(ImageDetectionInputOld(ImageDetectionUtils.imageProxyToBitmap(imgProxy), null))
    }

    suspend fun produceImage (bitmap: Bitmap) {
        produceInput(ImageDetectionInputOld(bitmap, null))
    }

    // INNER CLASSES
    private inner class EvaluationImageDetectionListener :
        EvaluationClassificationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            super.onLiveClassificationEnd(state)

            if (state.finalResult != null)
                mFinalResult.postValue(ClassificationFinalResultOld(state.finalResult))

            Log.d("ImageDetectionClient - EvaluationImageDetectionListener", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}