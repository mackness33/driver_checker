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
import java.util.*

class ImageDetectionClient : AClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IClassificationFinalResult<String>, String>() {
    override val evaluationListener: ClassificationListener<String> = EvaluationImageDetectionListener()
    private var index: Int = 0
    
    // FUNCTIONS
    suspend fun produceImage (imgProxy: ImageProxy) {
        val iii = ImageDetectionInput(ImageDetectionUtils.imageProxyToBitmap(imgProxy), ++index)
        iii.rotate(-90f)

        produceInput(iii)
    }

    suspend fun produceImage (bitmap: Bitmap) {
        produceInput(ImageDetectionInput(bitmap, ++index))
    }

    // INNER CLASSES
    private inner class EvaluationImageDetectionListener :
        EvaluationClassificationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            super.onLiveEvaluationReady(state)
            index = 0
        }

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            super.onLiveClassificationEnd(state)

            if (state.finalResult != null)
                mFinalResult.postValue(ClassificationFinalResult(state.finalResult))
            Log.d("ImageDetectionClient - EvaluationImageDetectionListener", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}