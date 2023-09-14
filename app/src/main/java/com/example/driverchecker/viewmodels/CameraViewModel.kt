package com.example.driverchecker.viewmodels

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.database.EvaluationRepository
import com.example.driverchecker.database.PartialEntity
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.manipulators.IClassificationClient
import com.example.driverchecker.machinelearning.manipulators.ImageDetectionClient
import com.example.driverchecker.utils.AtomicValue
import com.example.driverchecker.utils.DeferredLiveData
import com.example.driverchecker.utils.StateLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel (private val imageDetectionRepository: ImageDetectionFactoryRepository, private val evaluationRepository: EvaluationRepository) : BaseViewModel<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>>(imageDetectionRepository) {
    override val evaluationClient: IClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>, String> = ImageDetectionClient()

    val passengerInfo: StateLiveData<Triple<Int, Int, Int>?>?
        get() = evaluationClient.metricsPerGroup.liveMetrics["passenger"]

    val driverInfo: StateLiveData<Triple<Int, Int, Int>?>?
        get() = evaluationClient.metricsPerGroup.liveMetrics["driver"]

    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()

    private val mColoredOutputs: MutableList<Map<String, Set<Int>>> = mutableListOf()
    val coloredOutputs: List<Map<String, Set<Int>>>
        get() = mColoredOutputs

    val classificationGroups: StateLiveData<Set<String>>
        get() = evaluationClient.groups

    val areMetricsObservable: LiveData<Boolean>
        get() = evaluationClient.areMetricsObservable

    val metricsPerGroup: Map<String, Triple<Int, Int, Int>?>
        get() = evaluationClient.lastMetricsPerGroup

    private val mSaveImages = DeferredLiveData<List<Bitmap>?>(null, viewModelScope.coroutineContext)
    val saveImages: StateLiveData<List<Bitmap>?>
        get() = mSaveImages.value

    private val mAwaitImagesPaths = DeferredLiveData<List<String?>?>(null, viewModelScope.coroutineContext)
    private val mAwaitEndInsert = DeferredLiveData<Long?>(null, viewModelScope.coroutineContext)
    val awaitEndInsert: LiveData<Long?>
        get() = mAwaitEndInsert.asLiveData

    val currentState: AtomicValue<LiveEvaluationStateInterface?>
        get() = evaluationClient.currentState

    fun usePaths (paths: List<String?>) {
        mAwaitImagesPaths.complete(paths)
    }

    fun save(name: String) = viewModelScope.launch {
        if (evaluationClient.finalResult.lastValue != null) {
            if (mSaveImages.isCompleted()) update(name) else insert(name)
        }
    }

    fun insert(name: String) = viewModelScope.launch {
        mAwaitEndInsert.deferredAwait()
        mSaveImages.complete(evaluationClient.lastResultsList.map { it.input.input })

        val evalId = evaluationRepository.insertEvaluation(evaluationClient.finalResult.lastValue!!, name)
        evaluationRepository.insertAllMetrics(metricsPerGroup, evalId)

        mAwaitImagesPaths.await()
        evaluationRepository.insertAllPartialsAndItems(evaluationClient.lastResultsList, evalId, mAwaitImagesPaths.value.lastValue)

        mAwaitEndInsert.complete(evalId)
    }


    fun update(name: String) = viewModelScope.launch {
        if (mAwaitEndInsert.value.lastValue != null && mAwaitEndInsert.value.lastValue!! > 0)
            evaluationRepository.updateById(mAwaitEndInsert.value.lastValue!!, name)
    }

    suspend fun produceImage (image: ImageProxy) = viewModelScope.launch {
        (evaluationClient as ImageDetectionClient).produceImage(image)
        image.close()
    }

    override fun resetShown () {
        super.resetShown()
        mSaveImages.deferredAwait()
    }

    fun ready () = runBlocking {
        evaluationClient.ready()
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<String>,
        EvaluationListener {
        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            super.onLiveEvaluationReady(state)
            mColoredOutputs.clear()
        }

        override suspend fun onLiveEvaluationStart() {}

        override suspend fun onLiveClassificationStart(state: LiveClassificationState.Start<String>) {
            super.onLiveEvaluationStart()
        }

        override suspend fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {}

        override suspend fun onLiveClassificationLoading(state: LiveClassificationState.Loading<String>) {
            super.onLiveEvaluationLoading(LiveEvaluationState.Loading(state.index, state.partialResult))

            if (state.partialResult?.groups != null) {
                mColoredOutputs.add(state.partialResult.groups.mapValues { entry ->
                    entry.value.map { classification -> classification.internalIndex }.toSet()
                })
            }
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override suspend fun onLiveClassificationEnd(state: LiveClassificationState.End<String>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))

            if (state.finalResult != null) {
//                metricsPerGroup = evaluationClient.metricsPerGroup.metrics
                mSaveImages.reset()
                mAwaitImagesPaths.reset()
                mAwaitEndInsert.reset()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        imageDetectionRepository.removeClient()
    }

    fun onResultsViewed () {
        mShowResults.reset()
    }

    init {
        evaluationListener.listen(viewModelScope, evaluationState)
        evaluationClient.listen(viewModelScope, evaluationState)
        imageDetectionRepository.addClient(evaluationClient.clientState)
    }
}
