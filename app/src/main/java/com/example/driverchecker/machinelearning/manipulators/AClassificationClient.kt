package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMap
import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMutableMap
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AClassificationClient<I, O : IClassificationOutputStatsOld<S>, FR : IClassificationFinalResult<S>, S>
    : AMachineLearningClient<I, O, FR> (), IClassificationClient<I, O, FR, S> {
    // LIVE DATA
    override val finalResult: ObservableData<FR?>
        get() = mFinalResult

    override var classifier: IClassifier<S>? = null
        protected set

//    protected val mMetricsPerGroup = mutableMapOf<S, MutableLiveData<Pair<Int, Int>>>()
    protected val mMetricsPerGroup = ClassificationMetricsMutableMap<S>()
//    override val metricsPerGroup: Map<S, StateLiveData<Triple<Int, Int, Int>?>> = mMetricsPerGroup.liveMetrics
    override val metricsPerGroup: ClassificationMetricsMap<S> = mMetricsPerGroup

    protected val mAreMetricsObservable = MutableLiveData(false)
    override val areMetricsObservable: LiveData<Boolean>
        get() = mAreMetricsObservable

    override var lastMetricsPerGroup: Map<S, Triple<Int, Int, Int>?> = emptyMap()
        protected set

    override val evaluationListener: ClassificationListener<S> = EvaluationClassificationListener()

    protected val mGroups: MutableObservableData<Set<S>> = StatefulData(emptySet())
    override val groups: ObservableData<Set<S>>
        get() = mGroups


    // INNER CLASSES
    protected open inner class EvaluationClassificationListener :
        ClassificationListener<S>,
        EvaluationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun collectStates (state: LiveEvaluationStateInterface) {
            try {
                super.genericCollectStates(state)

                when (state) {
                    is LiveClassificationState.Start<*> -> onLiveClassificationStart(state as LiveClassificationState.Start<S>)
                    is LiveClassificationState.Loading<*> -> onLiveClassificationLoading(state as LiveClassificationState.Loading<S>)
                    is LiveClassificationState.End<*> -> onLiveClassificationEnd(state as LiveClassificationState.End<S>)
                    else -> super.collectStates(state)
                }
            } catch (e : Throwable) {
                Log.d("ClassificationListener", "Bad cast to Start<S> or End<S>", e)
            }
        }

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
//            lastMetricsPerGroup = state.finalResult?.data?.entries?.first()?.value?.groupMetrics ?: emptyMap()
//            lastClassificationData = state.finalResult?.data ?: emptyMap()
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
        }

        override suspend fun onLiveClassificationLoading(state: LiveClassificationState.Loading<S>) {
            super.onLiveEvaluationLoading(LiveEvaluationState.Loading(state.index, state.partialResult))
            if (state.partialResult != null && state.partialResult.groups.isNotEmpty() && !mHasEnded.value) {
                mMetricsPerGroup.add(state.partialResult)
            }
        }
    }
}