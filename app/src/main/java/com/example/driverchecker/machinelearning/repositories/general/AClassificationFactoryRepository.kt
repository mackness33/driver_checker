package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.ClassificationWindow
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.repositories.IClassificationRepository
import com.example.driverchecker.utils.ISettings
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime


abstract class AClassificationFactoryRepository<I, O : IClassificationOutputStats<S>, FR : IClassificationFinalResult<S>, S>
    : AMachineLearningFactoryRepository<I, O, FR>, IClassificationRepository<I, O, FR, S> {
    constructor(repositoryScope: CoroutineScope) : super(repositoryScope)

    constructor(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope) : super(modelName, modelInit, repositoryScope)

    override var window: IClassificationWindow<O, S> = ClassificationWindow(4, 0.5f, model?.classifier?.supergroups!!.keys)

    abstract override var model: IClassificationModel<I, O, S>?

    override fun jobEvaluation(input: Flow<I>, settings: ISettings): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            try {
                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true) && model != null && model?.classifier != null) {
                    mEvaluationFlowState.emit(LiveClassificationState.Start(
                        (model as IClassificationModel<I, O, S>).classifier.maxClassesInGroup(),
                        (model as IClassificationModel<I, O, S>).classifier)
                    )

                    window.updateSettings(settings)
                    model?.updateThreshold(settings.modelThreshold)

                    timer.markStart()
                    flowEvaluation(input, ::cancel)?.collect()
                } else
                    throw Throwable("The stream is not ready yet")
            } catch (e : Throwable) {
                mEvaluationFlowState.emit(LiveEvaluationState.End(e, null))
                triggerReadyState()
            }
        }
    }

    override suspend fun onCompletionEvaluation (cause: Throwable?) {
        Log.d("ACClassification", "finally finished")
        if (cause != null && cause !is CorrectCancellationException) {
            Log.e("ACClassification", "Just caught this: ${cause.message}", cause)
            mEvaluationFlowState.emit(
                LiveClassificationState.End<S>(cause, null)
            )
        } else {
            timer.markEnd()
            mEvaluationFlowState.emit(
                LiveClassificationState.End(
                    null,
                    ClassificationFinalResult(
                        window.getFinalResults(),
                        null,
                        MachineLearningMetrics(timer.diff() ?: 0.0, window.totalWindowsDone())
                    )
                )
            )
        }

        timer.reset()
        window.clean()
    }

    override suspend fun onEachEvaluation (
        postProcessedResult: O,
        onConditionSatisfied: (CancellationException) -> Unit
    ) {
        window.next(postProcessedResult)

        if (window.hasAcceptedLast) {
            mEvaluationFlowState.emit(
                LiveClassificationState.Loading(window.totEvaluationsDone, window.lastResult)
            )
        }

        // TODO: Pass the metrics and R
        if (window.isSatisfied())
            onConditionSatisfied(CorrectCancellationException())
    }
}