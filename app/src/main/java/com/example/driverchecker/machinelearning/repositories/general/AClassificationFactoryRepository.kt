package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ASettingsStateListener
import com.example.driverchecker.machinelearning.helpers.producers.ILiveEvaluationProducer
import com.example.driverchecker.machinelearning.windows.multiples.IClassificationMultipleWindows
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.repositories.IClassificationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.SharedFlow


abstract class AClassificationFactoryRepository<I, O : IClassificationOutput<S>, FR : IClassificationFinalResult<S>, S>
    : AMachineLearningFactoryRepository<I, O, FR>, IClassificationRepository<I, O, FR, S> {
    constructor(repositoryScope: CoroutineScope) : super(repositoryScope)
    constructor(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope) : super(modelName, modelInit, repositoryScope)

    abstract override var model: IClassificationModel<I, O, S>?
    abstract override val collectionOfWindows: IClassificationMultipleWindows<O, S>


    override val evaluationStateProducer: ILiveEvaluationProducer<LiveEvaluationStateInterface> = LiveClassificationProducer()
    override val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
        get() = evaluationStateProducer.sharedFlow


    override fun initialize(semaphores: Set<String>) {
        super.initialize(semaphores)
//        collectionOfWindows.updateGroups(model?.classifier?.supergroups?.keys ?: emptySet())
    }

    protected open inner class LiveClassificationProducer : LiveEvaluationProducer () {
        override suspend fun emitStart() {
            emit(
                LiveClassificationState.Start(
                    (model as IClassificationModel<I, O, S>).classifier.maxClassesInGroup(),
                    (model as IClassificationModel<I, O, S>).classifier
                )
            )
        }

        override suspend fun emitLoading() {
            emit(
                LiveClassificationState.Loading (
                    collectionOfWindows.totalElements, collectionOfWindows.lastResult
                )
            )
        }

        override suspend fun emitErrorEnd(cause: Throwable) {
            emit(LiveClassificationState.End<S>(cause, null))
        }

        override suspend fun emitSuccessEnd() {
            emit(LiveClassificationState.End(null, collectionOfWindows.getFinalResults()))
        }
    }

    protected open inner class ClassificationSettingsListener : SettingsListener {
        constructor () : super()

        constructor (scope: CoroutineScope, modelFlow: SharedFlow<SettingsStateInterface>) :
                super(scope, modelFlow)

        override suspend fun onWindowSettingsChange(state: SettingsState.WindowSettings) {
            collectionOfWindows.update(MultipleWindowSettings(
                state, model?.classifier?.supergroups?.keys!!
            ))

            Log.d("SettingsListener", "Window settings changed with ${state}")
        }

        override suspend fun onFullSettingsChange(state: SettingsState.FullSettings) {
            model?.updateThreshold(state.modelSettings.threshold)
            collectionOfWindows.update(MultipleWindowSettings(
                state.windowSettings, model?.classifier?.supergroups?.keys!!
            ))
            Log.d("SettingsListener", "Full settings changed with ${state}")
        }
    }
}