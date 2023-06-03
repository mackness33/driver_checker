package com.example.driverchecker.machinelearning.general.local

import android.util.Log
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class MLLocalRepository <Data, Result> (protected open val model: MLLocalModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    protected var window: MLWindow<Result> = MLWindow()
    protected val _evalState: MutableStateFlow<LiveEvaluationStateInterface<Result>> = MutableStateFlow(LiveEvaluationState.Ready(false))
    protected var liveClassificationJob: Job? = null
    val subscribingScope = CoroutineScope(SupervisorJob())

    override val evalState: StateFlow<LiveEvaluationStateInterface<Result>>?
            get() = _evalState.asStateFlow()

    init {
        initializeProgressState()
    }

    override suspend fun instantClassification(input: Data): Result? {
        return withContext(Dispatchers.Default) { model?.processAndEvaluate(input) }
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
        jobClassification(input, scope).join()

        return window.lastResult
    }

    protected fun initializeProgressState () {
        if (model?.isLoaded == true) {
            _evalState.value = LiveEvaluationState.Ready(true)
        }
    }

    protected fun jobClassification (input: Flow<Data>, scope: CoroutineScope): Job {
        return subscribingScope.launch(Dispatchers.Default) {
            /*
             * Just for easier visualization of the differences all the windows operation are
             * part of the flow. Instead the update of the state it is made outside of it.
             * There wouldn't be any problem to merge these two in either the catch and onCompletion
             * of the flow or in the try/catch/finally.
             */
            if (_evalState.compareAndSet(LiveEvaluationState.Ready(true), LiveEvaluationState.Start(null))) {
                _evalState.value = LiveEvaluationState.Loading(null)
                model?.processAndEvaluatesStream(input)
                    ?.onEach {
                        if (it == null) throw Error("The result is null")

                        window.next(it)
                        // TODO: subscribingScope.launch {è
//                        _evalState.value = LiveEvaluationState.Loading(window.lastResult!!)
                        // TODO: Pass the metrics and Result
                        if (window.isSatisfied())
                            cancel()
                        else
                            _evalState.update { _ -> LiveEvaluationState.Loading(window.lastResult) }
                    }
                    ?.flowOn(Dispatchers.Default)
                    ?.cancellable()
                    ?.catch { cause ->
                        Log.d("FlowClassificationOutside", "Just caught this, ${cause.message}")
                    }
                    ?.onCompletion { cause ->
                        Log.d("FlowClassificationWindow", "finally finished")
//                        if (cause is CancellationException) {
//                            _evalState.value = LiveEvaluationState.End(null, window.lastResult)
//                        } else {
//                            _evalState.value = LiveEvaluationState.End(cause, window.lastResult)
//                        }
                        _evalState.value = LiveEvaluationState.End(
                            if (cause !is CancellationException) cause else null,
                            window.lastResult
                        )

                        window.clean()
                    }
                    ?.collect()
            } else {
                _evalState.value = LiveEvaluationState.End(Throwable("The stream is not ready yet"), window.lastResult)
                initializeProgressState()
            }
        }
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<Data>,
        scope: CoroutineScope
    ) {
        if (_evalState.value == LiveEvaluationState.Ready(true)) {
            liveClassificationJob = jobClassification(input.buffer(2), scope)
        }
    }

    override suspend fun onStopLiveClassification() {
        liveClassificationJob?.cancel()
    }

    // TODO: let the window take as parameter a handler (interface of callbacks to manage state and etc)
    protected open class MLWindow<Result> (val size: Int = 5, val confidence_threshold: Float = 80F) {
        protected val window : MutableList<Result> = mutableListOf()

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
sealed interface LiveEvaluationStateInterface<out Result> {}

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState<out Result> : LiveEvaluationStateInterface<Result> {
    data class Ready(val isReady: Boolean) : LiveEvaluationState<Nothing>()
    data class Loading<Result>(var partialResult: Result?) : LiveEvaluationState<Result>()
    data class Start(val info: Nothing?) : LiveEvaluationState<Nothing>()
    data class End<Result>(val exception: Throwable?, val result: Result?) : LiveEvaluationState<Result>()
}
