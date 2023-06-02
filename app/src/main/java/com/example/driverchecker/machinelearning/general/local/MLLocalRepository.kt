package com.example.driverchecker.machinelearning.general.local

import android.graphics.Bitmap
import android.util.Log
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class MLLocalRepository <Data, Result> (protected open val model: MLLocalModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    protected var window: MLWindow<Result> = MLWindow()
    protected val _evalState: MutableStateFlow<LiveEvaluationStateInterface<Result>> = MutableStateFlow(LiveEvaluationStateInterface.Ready(false))
    protected var liveClassificationJob: Job? = null

    val evalState: StateFlow<LiveEvaluationStateInterface<Result>>
            get() = _evalState.asStateFlow()

    init {
        if (model?.isLoaded == true) {
            _evalState.value = LiveEvaluationStateInterface.Ready(true)
        }
    }

    override suspend fun instantClassification(input: Data): Result? {
        return withContext(Dispatchers.Default) {
            model?.processAndEvaluate(input)
        }
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
        return withContext(Dispatchers.Default) {
            var result: Result? = null

            try {
                for (data in input) {
                    val instantResult : Result = model?.processAndEvaluate(data) ?: throw Error("The result is null")

                    window.next(instantResult!!)
                    // TODO: Pass the metrics and Result
                    if (window.isSatisfied()) {
                        result = window.lastResult
                        break;
                    }
                }

            } catch (e: Throwable) {
                Log.d("FlowClassificationOutside", "Just caught this, ${e.message}")
            } finally {
                Log.d("FlowClassificationWindow", "finally finished")
                window.clean()
            }

            return@withContext result
        }
    }

    override suspend fun continuousClassification(input: Flow<Data>, scope: CoroutineScope): Result? {
        val job: Job = jobClassification(input, scope)

        job.join()

        return window.lastResult
    }

    protected fun jobClassification (input: Flow<Data>, scope: CoroutineScope): Job {
        return scope.launch {
            try {

                /*
                 * Just for easier visualization of the differences all the windows operation are
                 * part of the flow. Instead the update of the state it is made outside of it.
                 * There wouldn't be any problem to merge these two in either the catch and onCompletion
                 * of the flow or in the try/catch/finally.
                 */
                model?.processAndEvaluatesStream(input)
                    ?.cancellable()
                    ?.collect {
                        if (it == null) cancel()

                        window.next(it!!)

                        _evalState.value = LiveEvaluationStateInterface.Loading(window.lastResult!!)
                        // TODO: Pass the metrics and Result
                        if (window.isSatisfied())
                            cancel()
                    }
            } catch (e: Throwable) {
                Log.d("FlowClassificationOutside", "Just caught this, ${e.message}")
                if (e !is CancellationException)
                    _evalState.value = LiveEvaluationStateInterface.Error(e)
            } finally {
                Log.d("FlowClassificationWindow", "finally finished")
                _evalState.value = LiveEvaluationStateInterface.End(window.lastResult!!)
                window.clean()
            }
        }
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<Data>,
        scope: CoroutineScope
    ) {
        if (_evalState.value == LiveEvaluationStateInterface.Ready(true)) {
            liveClassificationJob = jobClassification(input.buffer(2), scope)
        }
    }

    override suspend fun onStopLiveClassification() {
        liveClassificationJob?.cancel()
    }

    // TODO: let the window take as parameter a handler (interface of callbacks to manage state and etc)
    protected open class MLWindow<Result> (val size: Int = 5, val confidence_threshold: Float = 80F) {
        protected val window : MutableList<Result> = mutableListOf<Result>()

        var confidence: Float = 0F
            protected set

        fun totalNumber() : Int = if (window.size > size) window.size else 0

        fun isSatisfied() : Boolean = confidence_threshold <= confidence

        var lastResult : Result? = null
            protected set

        fun next (element: Result) {
            window.add(element)

            if (window.size > size)
                window.removeFirst()

            lastResult = element
            metricsCalculation()
        }

        fun clean () {
            window.clear()
            confidence = 0F
        }

        // Is gonna return the confidence and other metrics
        open fun metricsCalculation () {
            confidence += 5F
        }
    }

    fun updateLocalModel (path: String) {
        model?.loadModel(path)
    }
}


// Represents different states for the LatestNews screen
sealed interface LiveEvaluationStateInterface<out Result> {
    data class Ready(val isReady: Boolean) : LiveEvaluationStateInterface<Nothing>
    data class Loading<Result>(val partialResult: Result) : LiveEvaluationStateInterface<Result>
    data class Error(val exception: Throwable) : LiveEvaluationStateInterface<Nothing>
    data class End<Result>(val result: Result) : LiveEvaluationStateInterface<Result>
}

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState<out Result> : LiveEvaluationStateInterface<Result> {
    data class Ready(val isReady: Boolean) : LiveEvaluationState<Nothing>()
    data class Loading<Result>(val partialResult: Result) : LiveEvaluationState<Result>()
    data class Error(val exception: Throwable) : LiveEvaluationState<Nothing>()
    data class End<Result>(val result: Result) : LiveEvaluationState<Result>()
}
