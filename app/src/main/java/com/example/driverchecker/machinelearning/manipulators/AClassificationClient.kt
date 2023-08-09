package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener

abstract class AClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S> : MachineLearningClient<I, O, FR> (), IClassificationClient<I, O, FR, S> {
    // LIVE DATA
    protected val mPassengerInfo = MutableLiveData(Pair(0, 0))
    override val passengerInfo: LiveData<Pair<Int, Int>>
        get() = mPassengerInfo

    protected val mDriverInfo = MutableLiveData(Pair(0, 0))
    override val driverInfo: LiveData<Pair<Int, Int>>
        get() = mDriverInfo

    // REFACTOR: move this array/function to the mlRepo
    protected val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    override val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions


    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()

    // FUNCTIONS

    abstract override fun getOutput () : IClassificationFinalResult<S>

    override val output: LiveData<FR?>
        get() = mOutput

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
        EvaluationListener() {
        override fun onLiveEvaluationStart() {}

        override fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
            Log.d("LiveClassificationState", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
            Log.d("LiveClassificationState", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}