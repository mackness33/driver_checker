package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class AClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S> : AMachineLearningClient<I, O, FR> (), IClassificationClient<I, O, FR, S> {
    // LIVE DATA
    protected val mPassengerInfo = MutableLiveData(Pair(0, 0))
    override val passengerInfo: LiveData<Pair<Int, Int>>
        get() = mPassengerInfo

    protected val mDriverInfo = MutableLiveData(Pair(0, 0))
    override val driverInfo: LiveData<Pair<Int, Int>>
        get() = mDriverInfo

    override val output: LiveData<FR?>
        get() = mOutput


    // VARIABLES
    // REFACTOR: move this array/function to the mlRepo
    // It doesn't count two same object on the same output
    protected val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    override val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions

    protected val mMetricsPerGroup = mutableMapOf<S, Pair<Int, Int>>()
    val metricsPerGroup: Map<S, Pair<Int, Int>> = mMetricsPerGroup

    override val evaluationListener: ClassificationListener<S> = EvaluationClassificationListener()


    // FUNCTIONS
    // handling the clearing of the main array
    override fun clearPartialResults () {
        super.clearPartialResults()
        arrayClassesPredictions.clear()
        mPassengerInfo.postValue(Pair(0, 0))
        mDriverInfo.postValue(Pair(0, 0))
        mMetricsPerGroup.clear()
    }

    // INNER CLASSES
    protected open inner class EvaluationClassificationListener :
        ClassificationListener<S>,
        EvaluationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationStart() {}

        override suspend fun onLiveClassificationStart(state: LiveClassificationState.Start<S>) {
            super.onLiveEvaluationStart()
            mMetricsPerGroup.putAll(state.supergroups.associateWith { Pair(0, 0) })
            Log.d("LiveClassificationState", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<S>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
            Log.d("LiveClassificationState", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }

        override suspend fun onLiveClassificationLoading(state: LiveClassificationState.Loading<S>) {
            super.onLiveEvaluationLoading(LiveEvaluationState.Loading(state.index, state.partialResult))
            // for each supergroup found update the metrics associated with
            // REFACTOR: possible optimize solution
            state.partialResult?.groups?.forEach {
                group -> mMetricsPerGroup.merge(group.key, 1 to group.value)
                    { newValue: Pair<Int, Int>, oldValue: Pair<Int, Int> ->
                        (newValue.first + oldValue.first) to (newValue.second + oldValue.second)
                    }
            }
        }
    }
}