package com.example.driverchecker.viewmodels

import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.GenericListener
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.machinelearning.manipulators.IMachineLearningClient
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import com.example.driverchecker.utils.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<I, O : WithConfidence, FR : WithConfidence> (private var machineLearningRepository: IMachineLearningFactory<I, O, FR>? = null): ViewModel(){
    // SHARED FLOWS
    // progress flow of the evaluation by the mlRepository
    val evaluationState: SharedFlow<LiveEvaluationStateInterface>?
        get() = machineLearningRepository?.evaluationFlowState


    // VARIABLES
    protected open val evaluationListener: MachineLearningListener = EvaluationListener()

    protected abstract val evaluationClient: IMachineLearningClient<I, O, FR>

    open val settings: ISettings
        get() = evaluationClient.settings


    // LIVE DATA
    // last result evaluated by the mlRepo
    val lastResult: LiveData<O?>
        get() = evaluationClient.lastResult

    // bool to check if the mlRepo is evaluating
    protected val mIsEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = mIsEvaluating

    // bool to check if the button to start/stop the evaluation of mlRepo is enable
    protected val mIsEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
        get() = mIsEnabled

    // the index of the partialResult
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>
        get () = evaluationClient.partialResultEvent

    // array of evaluated items by the mlRepo
    val evaluatedItemsList: List<O>
        get() = evaluationClient.currentResultsList

    val lastItemsList: List<O>
        get() = evaluationClient.lastResultsList

    val finalResult: StateLiveData<FR?>
        get() = evaluationClient.finalResult

    protected val mShowResults = DeferredLiveData(false, viewModelScope.coroutineContext)
    val showResults: StateLiveData<Boolean?>
        get() = mShowResults.value

    protected val mActualPage: AtomicLiveData<IPage?> = AtomicLiveData(null)
    val actualPage: LiveData<IPage?>
        get() = mActualPage.asLiveData



    // FUNCTIONS
    fun setActualPage (nextPage: IPage) {
        runBlocking {
            mActualPage.update(nextPage)
        }
    }

    open fun resetShown () {
        mShowResults.reset()
    }

    // enabling the button to start/stop the evaluation of the ml
    fun enable (enable: Boolean) {
        mIsEnabled.value = enable
    }

    // start/stop the evaluation of the ml
    fun evaluate (record: Boolean) {
        mIsEvaluating.value = record
    }

    fun saveSettings (newSettings: ISettings) {
        evaluationClient.updateSettings(newSettings)
    }

    // update of the live classification of the mlRepo
    fun updateLiveClassification (stopEvent: Boolean = false) {
        runBlocking(Dispatchers.Default) {
            when {
                mIsEvaluating.value == null -> {}
                !(mIsEvaluating.value!! || stopEvent) -> {
                    evaluationClient.start()
                }
                mIsEvaluating.value!! -> {
                    evaluationClient.stop()
                }
            }
        }
    }


    // INNER CLASSES
    protected open inner class EvaluationListener : MachineLearningListener, GenericListener<LiveEvaluationStateInterface> {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            mIsEvaluating.postValue(false)
            mIsEnabled.postValue(state.isReady)
        }

        override suspend fun onLiveEvaluationStart() {
            // add the partialResult to the resultsArray
            mShowResults.deferredAwait()
            mIsEvaluating.postValue(true)
            mIsEnabled.postValue(true)
        }

        override suspend fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {}

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {
            // update the UI with the text of the class
            // save to the database the result with bulk of 10 and video
            mIsEvaluating.postValue(false)
            mIsEnabled.postValue(false)

            when {
                state.exception != null -> evaluationClient.ready()
                state.finalResult != null -> mShowResults.complete(mActualPage.value != null && mActualPage.value != Page.Result)
            }
        }
    }
}