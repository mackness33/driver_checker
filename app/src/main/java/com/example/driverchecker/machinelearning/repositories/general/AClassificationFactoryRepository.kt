package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.collections.ClassificationWindowsMutableCollection
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.producers.AProducer
import com.example.driverchecker.machinelearning.helpers.producers.ILiveEvaluationProducer
import com.example.driverchecker.machinelearning.helpers.producers.IReactiveSemaphore
import com.example.driverchecker.machinelearning.helpers.windows.ClassificationWindow
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.repositories.IClassificationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collect
import kotlin.time.ExperimentalTime


abstract class AClassificationFactoryRepository<I, O : IClassificationOutputStats<S>, FR : IClassificationFinalResult<S>, S>
    : AMachineLearningFactoryRepository<I, O, FR>, IClassificationRepository<I, O, FR, S> {
    constructor(repositoryScope: CoroutineScope) : super(repositoryScope)

    constructor(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope) : super(modelName, modelInit, repositoryScope)

    abstract override var model: IClassificationModel<I, O, S>?

    abstract override val collectionOfWindows: ClassificationWindowsMutableCollection<O, S>

    override val evaluationStateProducer: ILiveEvaluationProducer<LiveEvaluationStateInterface> = LiveClassificationProducer()

    override val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
        get() = evaluationStateProducer.sharedFlow

    init {
    }

    override fun initialize(semaphores: Set<String>) {
        super.initialize(semaphores)
        collectionOfWindows.updateGroups(model?.classifier?.supergroups?.keys ?: emptySet())
    }

    protected open inner class LiveClassificationProducer :
        LiveEvaluationProducer () {
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
                    collectionOfWindows.totEvaluationsDone, collectionOfWindows.lastResult
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
}