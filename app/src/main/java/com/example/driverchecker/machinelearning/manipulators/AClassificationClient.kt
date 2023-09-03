package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S>
    : AMachineLearningClient<I, O, FR> (), IClassificationClient<I, O, FR, S> {
    // LIVE DATA
    override val output: LiveData<FR?>
        get() = mOutput

    override var classifier: IClassifier<S>? = null
        protected set

    protected val mMetricsPerGroup = mutableMapOf<S, MutableLiveData<Pair<Int, Int>>>()
    override val metricsPerGroup: Map<S, LiveData<Pair<Int, Int>>> = mMetricsPerGroup

    override val evaluationListener: ClassificationListener<S> = EvaluationClassificationListener()


    // INNER CLASSES
    protected open inner class EvaluationClassificationListener :
        ClassificationListener<S>,
        EvaluationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            super.onLiveEvaluationReady(state)
            mMetricsPerGroup.clear()
            classifier = null
            Log.d("ClassificationClient - EvaluationClassificationListener", "READY: ${state.isReady} with index ${mPartialResultEvent.value} but array.size is ${evaluatedItemsArray.size}")
        }


        override suspend fun onLiveEvaluationStart() {}

        override suspend fun onLiveClassificationStart(state: LiveClassificationState.Start<S>) {
            super.onLiveEvaluationStart()
            classifier = state.classifier
            mMetricsPerGroup.putAll(state.classifier.supergroups.keys.associateWith { MutableLiveData(Pair(0, 0)) })
            Log.d("ClassificationClient - EvaluationClassificationListener", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<S>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
        }

        override suspend fun onLiveClassificationLoading(state: LiveClassificationState.Loading<S>) {
            super.onLiveEvaluationLoading(LiveEvaluationState.Loading(state.index, state.partialResult))
            try {
                if (state.partialResult != null && state.partialResult.groups.isNotEmpty() && mMetricsPerGroup.keys.containsAll(state.partialResult.groups.keys)) {
                    val partialResult: O = state.partialResult as O
                    for (group in partialResult.groups) {
                        val loadedValue = 1 to group.value.size
                        val newValue: Pair<Int, Int> =
                            if (mMetricsPerGroup[group.key]?.value == null)
                                loadedValue
                            else
                                mMetricsPerGroup[group.key]!!.value!!.apply {
                                    (this.first + loadedValue.first) to (this.second + loadedValue.second)
                                }

                        mMetricsPerGroup[group.key]?.postValue(newValue)
                    }
                }
            } catch (e : Throwable) {
                Log.e("ClassificationClient - EvaluationClassificationListener", "Client couldn't load the partial result properly", e)
            } finally {
                Log.d("ClassificationClient - EvaluationClassificationListener", "LOADING: ${state.partialResult} for the ${mPartialResultEvent.value} time")
            }
        }
    }
}