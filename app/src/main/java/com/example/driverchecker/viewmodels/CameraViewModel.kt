package com.example.driverchecker.viewmodels

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.database.EvaluationRepository
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.manipulators.IClassificationClient
import com.example.driverchecker.machinelearning.manipulators.ImageDetectionClient
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.ObservableData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel (private val imageDetectionRepository: ImageDetectionFactoryRepository, private val evaluationRepository: EvaluationRepository) : BaseViewModel<IImageDetectionInput, IImageDetectionOutput<String>, IClassificationFinalResult<String>>(imageDetectionRepository) {
    override val evaluationClient: IClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IClassificationFinalResult<String>, String> = ImageDetectionClient()

    val passengerInfo: ObservableData<Triple<Int, Int, Int>>?
        get() = evaluationClient.metricsPerGroup.liveMetrics["passenger"]

    val driverInfo: ObservableData<Triple<Int, Int, Int>>?
        get() = evaluationClient.metricsPerGroup.liveMetrics["driver"]

    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()

    private val mColoredOutputs: MutableList<Map<String, Set<Int>>> = mutableListOf()
    val coloredOutputs: List<Map<String, Set<Int>>>
        get() = mColoredOutputs

    val classificationGroups: ObservableData<Set<String>>
        get() = evaluationClient.groups

    val areMetricsObservable: LiveData<Boolean>
        get() = evaluationClient.areMetricsObservable

    val metricsPerGroup: Map<String, Triple<Int, Int, Int>?>
        get() = evaluationClient.lastMetricsPerGroup

    private val mSaveImages = DeferrableData<List<Bitmap>?>(null, viewModelScope.coroutineContext)
    val saveImages: LiveData<List<Bitmap>?>
        get() = mSaveImages.liveData

    private val mAwaitImagesPaths = DeferrableData<List<String?>?>(null, viewModelScope.coroutineContext)
    private val mAwaitEndInsert = DeferrableData<Long?>(null, viewModelScope.coroutineContext)
    val awaitEndInsert: LiveData<Long?>
        get() = mAwaitEndInsert.liveData

    val currentState: ObservableData<LiveEvaluationStateInterface?>
        get() = evaluationClient.currentState

    fun usePaths (paths: List<String?>) {
        mAwaitImagesPaths.complete(paths)
    }

    fun save(name: String) = viewModelScope.launch {
        if (evaluationClient.finalResult.value != null) {
            if (mSaveImages.isCompleted()) update(name) else insert(name)
        }
    }

    fun insert(name: String) = viewModelScope.launch {
        // TODO: database save

//        mAwaitEndInsert.deferredAwait()
//        mSaveImages.complete(evaluationClient.lastResultsList.map { it.input.input })

//        val evalId = evaluationRepository.insertFinalResult(evaluationClient.finalResult.value!!, name)
//        evaluationRepository.insertAllOldMetrics(metricsPerGroup, evalId)

//        mAwaitImagesPaths.await()
//        evaluationRepository.insertAllPartialsAndItems(evaluationClient.lastResultsList, evalId, mAwaitImagesPaths.value)

//        mAwaitEndInsert.complete(evalId)
    }

    fun updateModelThreshold (modelThreshold: Float) {
        imageDetectionRepository.updateModelThreshold(modelThreshold)
    }


    fun update(name: String) = viewModelScope.launch {
        if (mAwaitEndInsert.value != null && mAwaitEndInsert.value!! > 0)
            evaluationRepository.updateById(mAwaitEndInsert.value!!, name)
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

        override suspend fun collectStates (state: LiveEvaluationStateInterface) {
            try {
                super.genericCollectStates(state)

                when (state) {
                    is LiveClassificationState.Start<*> -> onLiveClassificationStart(state as LiveClassificationState.Start<String>)
                    is LiveClassificationState.Loading<*> -> onLiveClassificationLoading(state as LiveClassificationState.Loading<String>)
                    is LiveClassificationState.End<*> -> onLiveClassificationEnd(state as LiveClassificationState.End<String>)
                    else -> super.collectStates(state)
                }
            } catch (e : Throwable) {
                Log.d("ClassificationListener", "Bad cast to Start<S> or End<S>", e)
            }
        }

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

            if (state.partialResult?.stats?.groups != null) {
                mColoredOutputs.add(state.partialResult.stats.groups.mapValues { entry ->
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
        imageDetectionRepository
        evaluationListener.listen(viewModelScope, evaluationState)
        evaluationClient.listen(viewModelScope, evaluationState)
        imageDetectionRepository.addClient(evaluationClient.clientState)
    }
}
