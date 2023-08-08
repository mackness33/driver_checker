package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.IMachineLearningOutput
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.PartialEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfidence
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningClient<I, O : WithConfidence, FR : WithConfidence> {
    // LIVE DATA

    val hasEnded: LiveData<Boolean?>

    // last result evaluated by the mlRepo
    val lastResult: LiveData<O?>

    // the index of the partialResult
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>

    // array of evaluated items by the mlRepo
    val currentResultsList: List<O>

    fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?)

    fun getOutput () : IMachineLearningOutput<I, O>?

    val output: LiveData<FR?>
}
