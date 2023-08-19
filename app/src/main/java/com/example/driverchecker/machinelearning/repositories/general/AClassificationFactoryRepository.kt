package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.ClassificationWindow
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.repositories.IClassificationRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

abstract class AClassificationFactoryRepository<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S>
    : AMachineLearningFactoryRepository<I, O, FR>, IClassificationRepository<I, O, FR, S> {
    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit)

    override var window: IClassificationWindow<O, S> = ClassificationWindow(2, 0.5f, model?.classifier?.superclasses!!.keys)

    abstract override var model: IClassificationModel<I, O, S>?

    override fun jobEvaluation (input: Flow<I>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            try {
                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true) && model != null && model?.classifier != null) {
                    mEvaluationFlowState.emit(LiveClassificationState.Start(
                        (model as IClassificationModel<I, O, S>).classifier.maxClassesInGroup(),
                        (model as IClassificationModel<I, O, S>).classifier.superclasses.keys.toList())
                    )

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
            mEvaluationFlowState.emit(
                LiveClassificationState.End<S>(cause, null)
            )
        } else {
            mEvaluationFlowState.emit(
                LiveClassificationState.End(null, window.getFinalResults())
            )
        }

        window.clean()

//        mEvaluationFlowState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
    }

    override suspend fun onEachEvaluation (
        postProcessedResult: O,
        onConditionSatisfied: (CancellationException) -> Unit
    ) {
        Log.d("JobClassification", "finally finished")
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