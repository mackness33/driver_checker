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

    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()


    // FUNCTIONS
    // handling the clearing of the main array
    override fun clearPartialResults () {
        super.clearPartialResults()
        arrayClassesPredictions.clear()
        mPassengerInfo.postValue(Pair(0, 0))
        mDriverInfo.postValue(Pair(0, 0))
    }

    // INNER CLASSES
    protected open inner class EvaluationClassificationListener :
        ClassificationListener<String>,
        EvaluationListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationStart() {}

        override suspend fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
            Log.d("LiveClassificationState", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override suspend fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
            Log.d("LiveClassificationState", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}