package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.utils.AtomicValue
import com.example.driverchecker.utils.StateLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S>
    : AMachineLearningClient<I, O, FR> (), IClassificationClient<I, O, FR, S> {
    // LIVE DATA
    override val output: StateLiveData<FR?>
        get() = mOutput

    override var classifier: IClassifier<S>? = null
        protected set

//    protected val mMetricsPerGroup = mutableMapOf<S, MutableLiveData<Pair<Int, Int>>>()
    protected val mMetricsPerGroup = ClientMetricsMutableMap<S>()
//    override val metricsPerGroup: Map<S, StateLiveData<Triple<Int, Int, Int>?>> = mMetricsPerGroup.liveMetrics
    override val metricsPerGroup: ClientMetricsMap<S> = mMetricsPerGroup

    protected val mAreMetricsObservable = MutableLiveData(false)
    override val areMetricsObservable: LiveData<Boolean>
        get() = mAreMetricsObservable

    override val evaluationListener: ClassificationListener<S> = EvaluationClassificationListener()

    protected val mGroups: MutableLiveData<Set<S>> = MutableLiveData()
    override val groups: LiveData<Set<S>>
        get() = mGroups


    override val currentState: AtomicValue<LiveEvaluationStateInterface?>
        get() = evaluationListener.currentState


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
            mAreMetricsObservable.postValue(false)
            Log.d("ClassificationClient - EvaluationClassificationListener", "READY: ${state.isReady} with index ${mPartialResultEvent.value} but array.size is ${evaluatedItemsArray.size}")
        }

        override suspend fun onLiveEvaluationStart() {}

        override suspend fun onLiveClassificationStart(state: LiveClassificationState.Start<S>) {
            super.onLiveEvaluationStart()
            classifier = state.classifier
            mGroups.postValue(state.classifier.supergroups.keys)
            mMetricsPerGroup.initialize(state.classifier.supergroups.keys)
            mAreMetricsObservable.postValue(true)
            Log.d("ClassificationClient - EvaluationClassificationListener", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<S>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
        }

        override suspend fun onLiveClassificationLoading(state: LiveClassificationState.Loading<S>) {
            super.onLiveEvaluationLoading(LiveEvaluationState.Loading(state.index, state.partialResult))
            if (state.partialResult != null && state.partialResult.groups.isNotEmpty()) {
                mMetricsPerGroup.add(state.partialResult)
            }
        }
    }
}