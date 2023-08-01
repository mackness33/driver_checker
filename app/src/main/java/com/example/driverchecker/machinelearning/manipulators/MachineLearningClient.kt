package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import com.example.driverchecker.utils.AtomicLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class MachineLearningClient<D, R : WithConfidence> (machineLearningRepo: IMachineLearningFactory<D, R>? = null) : IMachineLearningClient<D, R>{
    // LIVE DATA

    protected val _hasEnded = AtomicLiveData(100, false)
    override val hasEnded: LiveData<Boolean?>
        get() = _hasEnded.asLiveData

    // last result evaluated by the mlRepo
    protected val _lastResult: MutableLiveData<R?> = MutableLiveData(null)
    override val lastResult: LiveData<R?>
        get() = _lastResult

    // the index of the partialResult
    protected val _partialResultEvent: MutableLiveData<PartialEvaluationStateInterface> = MutableLiveData(PartialEvaluationState.Init)
    override val partialResultEvent: LiveData<PartialEvaluationStateInterface>
        get () = _partialResultEvent

    // array of evaluated items by the mlRepo
    protected val evaluatedItemsArray = ArrayList<R>()
    override val evaluatedItemsList: List<R>
        get() = evaluatedItemsArray


    // LISTENERS

    protected open val evaluationListener: MachineLearningListener<D, R> = EvaluationListener()


    // FUNCTIONS

    // handling the add of a partial result to the main array
    protected open fun insertPartialResult (partialResult: R) {
        evaluatedItemsArray.add(partialResult)
    }

    // handling the clearing of the main array
    protected open fun clearPartialResults () {
        evaluatedItemsArray.clear()
        _hasEnded.tryUpdate(false)
    }

    override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface<R>>?) {
        evaluationListener.listen(scope, evaluationFlow)
    }

    // INNER CLASSES
    protected open inner class EvaluationListener : MachineLearningListener<D, R> {
        override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface<R>>?) {
            scope.launch(Dispatchers.Default) {
                evaluationFlow?.collect {state -> evaluationListener.collectLiveEvaluations(state)}
            }
        }

        override fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            clearPartialResults()
            _partialResultEvent.postValue(PartialEvaluationState.Clear)
            _lastResult.postValue(null)
            Log.d("LiveEvaluationState", "READY: ${state.isReady} with index ${_partialResultEvent.value} but array.size is ${evaluatedItemsArray.size}")
        }

        override fun onLiveEvaluationStart() {
            // add the partialResult to the resultsArray
            _lastResult.postValue(null)
            Log.d("LiveEvaluationState", "START: ${_partialResultEvent.value} initialIndex")
        }

        override fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading<R>) {
            // add the partialResult to the resultsArray
            if (state.partialResult != null) {
                insertPartialResult(state.partialResult)
                _partialResultEvent.postValue(PartialEvaluationState.Insert(evaluatedItemsArray.size))
                _lastResult.postValue(state.partialResult)
                Log.d("LiveEvaluationState", "LOADING: ${state.partialResult} for the ${_partialResultEvent.value} time")
            }
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End<R>) {
            // update the UI with the text of the class
            // save to the database the result with bulk of 10 and video
            Log.d("LiveEvaluationState", "END: ${state.result} for the ${_partialResultEvent.value} time")
        }
    }
}
