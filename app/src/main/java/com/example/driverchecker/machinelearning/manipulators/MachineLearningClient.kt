package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.utils.AtomicLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class MachineLearningClient<I, O : WithConfidence, FR : WithConfidence> : IMachineLearningClient<I, O, FR>{
    // LIVE DATA

    protected val mHasEnded = AtomicLiveData(100, false)
    override val hasEnded: LiveData<Boolean?>
        get() = mHasEnded.asLiveData

    // last result evaluated by the mlRepo
    protected val mLastResult: MutableLiveData<O?> = MutableLiveData(null)
    override val lastResult: LiveData<O?>
        get() = mLastResult

    // the index of the partialResult
    protected val mPartialResultEvent: MutableLiveData<PartialEvaluationStateInterface> = MutableLiveData(PartialEvaluationState.Init)
    override val partialResultEvent: LiveData<PartialEvaluationStateInterface>
        get () = mPartialResultEvent

    // array of evaluated items by the mlRepo
    protected val evaluatedItemsArray =
        MachineLearningResultArrayList<O>()
    override val currentResultsList: List<O>
        get() = evaluatedItemsArray


    // LISTENERS

    protected open val evaluationListener: MachineLearningListener = EvaluationListener()


//    abstract var output: IMachineLearningOutput<D, R>?

    override fun getOutput () : IMachineLearningOutput<I, O>? {
        return MachineLearningOutput(evaluatedItemsArray, 1.0f)
    }

    protected open val mOutput: MutableLiveData<FR?> = MutableLiveData(null)
    override val output: LiveData<FR?>
        get() = mOutput


    // FUNCTIONS

    // handling the add of a partial result to the main array
    protected open fun insertPartialResult (partialResult: O) {
        evaluatedItemsArray.add(partialResult)
    }

    // handling the clearing of the main array
    protected open fun clearPartialResults () {
        evaluatedItemsArray.clear()
        mHasEnded.tryUpdate(false)
    }

    override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?) {
        evaluationListener.listen(scope, evaluationFlow)
    }

    // INNER CLASSES
    protected open inner class EvaluationListener : MachineLearningListener {
        override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?) {
            scope.launch(Dispatchers.Default) {
                evaluationFlow?.collect {state -> evaluationListener.collectLiveEvaluations(state)}
            }
        }

        override fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            clearPartialResults()
            mPartialResultEvent.postValue(PartialEvaluationState.Clear)
            mLastResult.postValue(null)
            Log.d("LiveEvaluationState", "READY: ${state.isReady} with index ${mPartialResultEvent.value} but array.size is ${evaluatedItemsArray.size}")
        }

        override fun onLiveEvaluationStart() {
            // add the partialResult to the resultsArray
            mLastResult.postValue(null)
            Log.d("LiveEvaluationState", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {
            // add the partialResult to the resultsArray
            if (state.partialResult != null) {
                val partialResult: O = state.partialResult as O
                insertPartialResult(partialResult)
                mPartialResultEvent.postValue(PartialEvaluationState.Insert(evaluatedItemsArray.size))
                mLastResult.postValue(partialResult)
                Log.d("LiveEvaluationState", "LOADING: ${state.partialResult} for the ${mPartialResultEvent.value} time")
            }
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {
            // update the UI with the text of the class
            // save to the database the result with bulk of 10 and video
            mHasEnded.tryUpdate(state.finalResult != null)
            Log.d("LiveEvaluationState", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}
