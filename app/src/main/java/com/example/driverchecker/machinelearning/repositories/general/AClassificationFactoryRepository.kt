package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.collections.ClassificationWindowsMutableCollection
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.producers.AProducer
import com.example.driverchecker.machinelearning.helpers.producers.ILiveEvaluationProducer
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

    override var window: IClassificationWindow<O, S> = ClassificationWindow(4, 0.5f, model?.classifier?.supergroups!!.keys, "ClassificationWindow")

    abstract override var model: IClassificationModel<I, O, S>?

    abstract override val collectionOfWindows: ClassificationWindowsMutableCollection<O, S>

    override val evaluationStateProducer: ILiveEvaluationProducer<LiveEvaluationStateInterface> = LiveClassificationProducer()

    override val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
        get() = evaluationStateProducer.sharedFlow

    init {
    }

    override fun initialize() {
        super.initialize()
        collectionOfWindows.updateGroups(model?.classifier?.supergroups?.keys ?: emptySet())
        evaluationStateProducer.tryEmitReady(false)
    }

    @OptIn(ExperimentalTime::class)
    override fun jobEvaluation(input: Flow<I>, newSettings: IOldSettings): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            try {
//                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true) && model != null && model?.classifier != null) {
                if (evaluationStateProducer.isLast(LiveEvaluationState.Ready(true)) && model != null && model?.classifier != null) {
                    mEvaluationFlowState.emit(LiveClassificationState.Start(
                        (model as IClassificationModel<I, O, S>).classifier.maxClassesInGroup(),
                        (model as IClassificationModel<I, O, S>).classifier)
                    )
                    evaluationStateProducer.emitStart()
                    timer.markStart()

//                    model?.updateThreshold(newSettings.modelThreshold)
                    oldSettings = newSettings
                    oldTimer.markStart()
                    window.initialize(
                        newSettings, oldTimer.start!!,
                        (model as IClassificationModel<I, O, S>).classifier.supergroups.keys
                    )

                    flowEvaluation(input, ::cancel)?.collect()
                } else
                    throw Throwable("The stream is not ready yet")
            } catch (e : Throwable) {
                mEvaluationFlowState.emit(LiveEvaluationState.OldEnd(e, null))
                evaluationStateProducer.emitErrorEnd(e)
                triggerReadyState()
            }
        }
    }



    override suspend fun onCompletionEvaluation (cause: Throwable?) {
        Log.d("ACClassification", "finally finished")
        if (cause != null && cause !is CorrectCancellationException) {
            Log.e("ACClassification", "Just caught this: ${cause.message}", cause)
            mEvaluationFlowState.emit(
                LiveClassificationState.End<String>(cause, null)
            )
            evaluationStateProducer.emitErrorEnd(cause)
        } else {
            oldTimer.markEnd()
            mEvaluationFlowState.emit(
                LiveClassificationState.End(null, collectionOfWindows.getFinalResults())
            )
            evaluationStateProducer.emitSuccessEnd()
        }

        oldTimer.reset()
        oldSettings = null
        timer.reset()
        window.clean()
        collectionOfWindows.clean()
    }


    override suspend fun onEachEvaluation (
        postProcessedResult: O,
        onConditionSatisfied: (CancellationException) -> Unit
    ) {
        timer.markEnd()
        window.next(postProcessedResult, timer.diff())
        collectionOfWindows.next(postProcessedResult, timer.diff())
        timer.markStart()

        if (collectionOfWindows.hasAcceptedLast) {
            mEvaluationFlowState.emit(
                LiveClassificationState.Loading(collectionOfWindows.totEvaluationsDone, collectionOfWindows.lastResult)
            )
            evaluationStateProducer.emitLoading()
        }

        if (collectionOfWindows.isSatisfied())
            onConditionSatisfied(CorrectCancellationException())
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