package com.example.driverchecker.data

import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import androidx.navigation.fragment.findNavController
import com.example.driverchecker.R
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.manipulators.IClassificationClient
import com.example.driverchecker.machinelearning.manipulators.ImageDetectionClient
import kotlinx.coroutines.*
import kotlinx.coroutines.android.awaitFrame
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel (private val imageDetectionRepository: ImageDetectionFactoryRepository? = null) : BaseViewModel<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>>(imageDetectionRepository) {
    override val evaluationClient: IClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>, String> = ImageDetectionClient()

    val passengerInfo: LiveData<Pair<Int, Int>>?
        get() = evaluationClient.metricsPerGroup["passenger"]

    val driverInfo: LiveData<Pair<Int, Int>>?
        get() = evaluationClient.metricsPerGroup["driver"]

    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()

    private val mColoredOutputs: MutableList<Map<String, Set<Int>>> = mutableListOf()
    val coloredOutputs: List<Map<String, Set<Int>>>
        get() = mColoredOutputs

    val classificationGroups: LiveData<Set<String>>
        get() = evaluationClient.groups

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            (evaluationClient as ImageDetectionClient).produceImage(image)
            image.close()
        }
    }

    fun ready () {
        runBlocking {
            evaluationClient.ready()
        }
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<String>,
        EvaluationListener {
        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationStart() {}

        override suspend fun onLiveClassificationStart(state: LiveClassificationState.Start<String>) {
            super.onLiveEvaluationStart()
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override suspend fun onLiveClassificationEnd(state: LiveClassificationState.End<String>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
        }

        override suspend fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {}

        override suspend fun onLiveClassificationLoading(state: LiveClassificationState.Loading<String>) {
            super.onLiveEvaluationLoading(LiveEvaluationState.Loading(state.index, state.partialResult))

            if (state.partialResult?.groups != null)
                mColoredOutputs.add(state.partialResult.groups.mapValues { entry ->
                    entry.value.map { classification -> classification.internalIndex }.toSet()
                })
        }

        override suspend fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            super.onLiveEvaluationReady(state)
            mColoredOutputs.clear()
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageDetectionRepository?.removeClient()
    }

    fun onResultsViewed () {
        mShowResults.reset()
    }


    init {
        evaluationListener.listen(viewModelScope, evaluationState)
        evaluationClient.listen(viewModelScope, evaluationState)
        imageDetectionRepository?.addClient(evaluationClient.clientState)
    }
}

sealed interface IPage

sealed class Page : IPage {
    object Camera : Page()
    object Result : Page()
}