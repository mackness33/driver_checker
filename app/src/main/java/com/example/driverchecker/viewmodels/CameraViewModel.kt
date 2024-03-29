package com.example.driverchecker.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.database.ImageDetectionDatabaseRepository
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ASettingsStateListener
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.manipulators.IClassificationClient
import com.example.driverchecker.machinelearning.manipulators.ImageDetectionClient
import com.example.driverchecker.utils.BitmapUtils
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.utils.PreferencesRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow

class CameraViewModel (
    private val modelRepository: ImageDetectionFactoryRepository,
    private val databaseRepository: ImageDetectionDatabaseRepository,
    private val preferencesRepository: PreferencesRepository
) : BaseViewModel<IImageDetectionInput, IImageDetectionOutput<String>, IClassificationFinalResult<String>>(
    modelRepository
) {
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

    val listOfCompletedEvaluation: Map<IImageDetectionInput, IImageDetectionOutput<String>?>
        get() = evaluationClient.lastEvaluationsMap.filter { entry -> entry.value != null }

    private val mSaveImages = DeferrableData<List<Bitmap>?>(null, viewModelScope.coroutineContext)
    val saveImages: LiveData<List<Bitmap>?>
        get() = mSaveImages.liveData

    private val mAwaitImagesPaths = DeferrableData<List<String?>?>(null, viewModelScope.coroutineContext)
    private val mAwaitEndInsert = DeferrableData<Long?>(null, viewModelScope.coroutineContext)
    val awaitEndInsert: LiveData<Long?>
        get() = mAwaitEndInsert.liveData

    private val settingsListener = SettingsListener(viewModelScope, preferencesRepository.preferencesFlow)
    var modelSettings: SettingsState.ModelSettings? = null
        private set

    val currentState: ObservableData<LiveEvaluationStateInterface?>
        get() = evaluationClient.currentState

    fun usePaths (paths: List<String?>) {
        mAwaitImagesPaths.complete(paths)
    }

    fun save(name: String, context: Context) = viewModelScope.launch(Dispatchers.Default) {
        if (evaluationClient.finalResult.value != null) {
            if (mSaveImages.isCompleted()) update(name) else insert(name, context)
        }
    }

    fun insert(name: String, context: Context) = viewModelScope.launch(Dispatchers.Default) {
        mAwaitEndInsert.deferredAwait()
        val mapWithoutNullOutputs = evaluationClient.lastEvaluationsMap.filter { entry -> entry.value != null }

        val images = mapWithoutNullOutputs.keys.toList().map { key -> key.input }

        val insertEvaluation = async {
            databaseRepository.insertFinalResult(
                evaluationClient.finalResult.value!!,
                name,
                preferencesRepository.activePreferences["model"] as SettingsState.ModelSettings
            )
        }

        val paths = async {
            if (images != null)
                BitmapUtils.saveMultipleBitmapInStorage(images, context)
            else
                emptyList()
        }

        val evaluationId = insertEvaluation.await()

        databaseRepository.insertAllPartialsAndItems(
            evaluationClient.lastEvaluationsMap.values.filterNotNull(),
            evaluationId,
            paths.await()
        )

        mAwaitEndInsert.complete(evaluationId)
    }

    private fun saveImages (images: List<Bitmap>?, context: Context) : List<String?> {
        if (images != null) {
            return BitmapUtils.saveMultipleBitmapInStorage(
                images,
                context
            )
        }

        return emptyList()
    }

    fun updateModelThreshold (modelThreshold: Float) {
        modelRepository.updateModelThreshold(modelThreshold)
    }


    fun update(name: String) = viewModelScope.launch {
        if (mAwaitEndInsert.value != null && mAwaitEndInsert.value!! > 0)
            databaseRepository.updateById(mAwaitEndInsert.value!!, name)
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

    protected open inner class SettingsListener : ASettingsStateListener {
        constructor () : super()

        constructor (scope: CoroutineScope, modelFlow: SharedFlow<SettingsStateInterface>) :
                super(scope, modelFlow)

        override suspend fun collectStates (state: SettingsStateInterface) {
            super.collectStates(state)
        }

        override suspend fun onModelSettingsChange(state: SettingsState.ModelSettings) {
//            model?.updateThreshold(state.threshold)
            modelSettings = state
            Log.d("SettingsListener", "Model settings changed with threshold:${state.threshold}")
        }

        override suspend fun onWindowSettingsChange(state: SettingsState.WindowSettings) {
            Log.d("SettingsListener", "Window settings changed with ${state}")
        }

        override suspend fun onFullSettingsChange(state: SettingsState.FullSettings) {
//            model?.updateThreshold(state.modelSettings.threshold)
            modelSettings = state.modelSettings
            Log.d("SettingsListener", "Full settings changed with ${state}")
        }

        override suspend fun onNoSettingsChange() {
            Log.d("SettingsListener", "No settings changed")
        }
    }

    override fun onCleared() {
        super.onCleared()
        modelRepository.removeClient()
    }

    fun onResultsViewed () {
        mShowResults.reset()
    }

    init {
        modelRepository
        evaluationListener.listen(viewModelScope, evaluationState)
        evaluationClient.listen(viewModelScope, evaluationState)
        modelRepository.addClient(evaluationClient.clientState)
        modelRepository.setSettingsFlow(preferencesRepository.preferencesFlow)
    }
}
