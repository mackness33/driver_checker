package com.example.driverchecker.machinelearning.models

import com.example.driverchecker.machinelearning.helpers.producers.AAtomicProducer
import com.example.driverchecker.machinelearning.helpers.producers.AProducer
import com.example.driverchecker.machinelearning.helpers.producers.IModelStateProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

abstract class MachineLearningModel<I, R>  (scope: CoroutineScope) : IMachineLearningModel<I, R> {
    protected val mIsLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected val loadingMap: Map<String, Boolean> = mutableMapOf()
    final override val modelScope: CoroutineScope = scope
    protected open val modelStateProducer: IModelStateProducer<Boolean> = ModelStateProducer()
    override val isLoaded: SharedFlow<Boolean>
        get() = modelStateProducer.sharedFlow
//        get() = mIsLoaded
    override var threshold = 0.05f // score above which a detection is generated
        protected set


    override fun processAndEvaluate (input: I): R? {
        val data: I = preProcess(input)
        val result: R = evaluateData(data)
        return postProcess(result)
    }

    override fun processAndEvaluatesStream (input: Flow<I>): Flow<R>? {
        return input
//            .buffer()
            .map { data -> preProcess(data)}
            .map { preProcessedInput -> evaluateData(preProcessedInput)}
            .map { output -> postProcess(output)}
    }

    protected abstract fun evaluateData (input: I) : R
    protected abstract fun preProcess (data: I) : I
    protected abstract fun postProcess (output: R) : R

    override fun updateThreshold (newThreshold: Float) {
        threshold = newThreshold
    }

    protected open inner class ModelStateProducer :
        AAtomicProducer<Boolean>(1, 1, BufferOverflow.DROP_OLDEST, modelScope),
        IModelStateProducer<Boolean>
    {
        protected val readyMap : MutableMap<String, Boolean> = mutableMapOf()

        protected suspend fun updateState () {
            val result = readyMap.values.fold(true) { last, current -> last && current }
            if (!isLast(result)) {
                emit(result)
            }
        }

        protected fun tryUpdateState () {
            val result = readyMap.values.fold(true) { last, current -> last && current }
            if (!isLast(result)) {
                tryEmit(result)
            }
        }

        override fun modelReady(isReady: Boolean) = runBlocking {
            readyMap["model"] = isReady
            updateState()
        }

        override fun initialize () {
            readyMap["model"] = false
            tryEmit(false)
//            tryUpdateState()
        }
    }
}