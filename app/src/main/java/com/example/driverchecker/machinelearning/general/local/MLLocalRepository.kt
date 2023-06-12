package com.example.driverchecker.machinelearning.general.local

import android.util.Log
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

abstract class MLLocalRepository <Data, Result> (protected open val model: MLLocalModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    protected var window: MLWindow<Result> = MLWindow()
    protected val _analysisProgressState: MutableStateFlow<LiveEvaluationStateInterface<Result>> = MutableStateFlow(LiveEvaluationState.Ready(false))
    protected var liveClassificationJob: Job? = null

    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val analysisProgressState: StateFlow<LiveEvaluationStateInterface<Result>>?
        get() = _analysisProgressState.asStateFlow()

    init {
        listenOnLoadingState()
    }

    protected fun listenOnLoadingState () {
        repositoryScope.launch {
            model?.isLoaded?.collect { state ->
                _analysisProgressState.compareAndSet(LiveEvaluationState.Ready(!state), LiveEvaluationState.Ready(state))
            }
        }
    }

    override suspend fun instantClassification(input: Data): Result? {
        var result: Result? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
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

    protected fun jobClassification (input: Flow<Data>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (_analysisProgressState.compareAndSet(LiveEvaluationState.Ready(true), LiveEvaluationState.Start(null))) {
                model?.processAndEvaluatesStream(input)
                    ?.onEach {
                        if (it == null) throw Error("The result is null")

                        window.next(it)
                        // TODO: Pass the metrics and Result
                        if (window.isSatisfied())
                            cancel()
                        else
                            _analysisProgressState.update { _ -> LiveEvaluationState.Loading(
                                window.index,
                                window.lastResult
                            ) }

                        Log.d("JobClassification", "Checked: ${window.index} with ${window.lastResult}")
                    }
                    ?.cancellable()
                    ?.catch { cause ->
                        Log.e("JobClassification", "Just caught this, ${cause.message}, cause")
                    }
                    ?.onCompletion { cause ->
                        Log.d("JobClassification", "finally finished")
                        _analysisProgressState.update { _ ->
                            LiveEvaluationState.End(
                                if (cause !is CancellationException) cause else null,
                                window.lastResult
                            )
                        }

                        window.clean()
                    }
                    ?.collect()

            } else {
                _analysisProgressState.update { _ -> LiveEvaluationState.End(Throwable("The stream is not ready yet"), null)}
            }
        }
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<Data>,
        scope: CoroutineScope
    ) {
        if (_analysisProgressState.value == LiveEvaluationState.Ready(true)) {
            liveClassificationJob = jobClassification(input.buffer(2), scope)
            liveClassificationJob?.invokeOnCompletion {
                _analysisProgressState.update { _ -> LiveEvaluationState.Ready(model?.isLoaded?.value ?: false)}
            }
        }
    }

    override suspend fun onStopLiveClassification() {
        liveClassificationJob?.cancel()
    }

    // TODO: let the window take as parameter a handler (interface of callbacks to manage state and etc)
    protected open class MLWindow<Result> (val size: Int = 5, val confidence_threshold: Float = 80f) {
        protected val window : MutableList<Result> = mutableListOf()

        var confidence: Float = 0f
            protected set

        var index: Int = 0
            protected set

        fun totalNumber() : Int = if (window.size >= size) window.size else 0

        fun isSatisfied() : Boolean = (window.size == size && confidence_threshold <= confidence)

        var lastResult : Result? = null
            protected set

        fun next (element: Result) {
            window.add(element)

            if (window.size > size)
                window.removeFirst()

            lastResult = element
            index++

            confidenceCalculation()
            metricsCalculation()
        }

        fun clean () {
            window.clear()
            confidence = 0f
            index = 0
        }

        open fun confidenceCalculation () {
            confidence += 5f
        }

        // Is gonna return the confidence and other metrics
        open fun metricsCalculation () {}
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
    data class Loading<Result>(val index: Int, val partialResult: Result?) : LiveEvaluationState<Result>()
    data class Start(val info: Nothing?) : LiveEvaluationState<Nothing>()
    data class End<Result>(val exception: Throwable?, val result: Result?) : LiveEvaluationState<Result>()
}
