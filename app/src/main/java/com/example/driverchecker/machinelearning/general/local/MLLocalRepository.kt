package com.example.driverchecker.machinelearning.general.local

import android.util.Log
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.MLModelInterface
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.Arrays.asList
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

abstract class MLLocalRepository <Data, Result> (protected open val model: MLLocalModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {

    private var modelThreadPool: ThreadPoolExecutor? = null
    private var modelEvalQueue: LinkedBlockingQueue<Runnable>? = null

    private val CORE_POOL_SIZE = 2
    private val MAX_POOL_SIZE = 3
    private val KEEP_ALIVE_TIME = 500L

    protected val window: MLWindow<Result> = MLWindow<Result>()

//    init {
//        modelEvalQueue = LinkedBlockingQueue<Runnable>()
//
//        modelThreadPool = ThreadPoolExecutor(
//            CORE_POOL_SIZE, MAX_POOL_SIZE,
//            KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, modelEvalQueue
//        )
//    }

    override suspend fun instantClassification(input: Data): Result? {
        return withContext(Dispatchers.Default) {
            model?.processAndEvaluate(input)
        }
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
//        TODO("Not yet implemented")
//        return null
//        return classificationThroughWindow(input.asFlow(), MLWindow<Result>())
        return classificationThroughWindow(input, MLWindow<Result>())
    }

    protected suspend fun classificationThroughWindow (input: List<Data>, window: MLWindow<Result>) : Result? {
        return withContext(Dispatchers.Default) {
            var result: Result? = null

            try {
                for (data in input) {
                    val instantResult : Result? = model?.processAndEvaluate(data)

                    if (instantResult == null) throw Error("The result is null")

                    window.add(instantResult!!)
                    // TODO: Pass the metrics and Result
                    if (window.isSatisfied()) {
                        result = window.partialResult
                        break;
                    }
                }

            } catch (e: Throwable) {
                Log.d("FlowClassificationOutside", "Just caught this, ${e.toString()}")
            } finally {
                Log.d("FlowClassificationWindow", "finally finished")
            }

            return@withContext result
        }
    }

    protected suspend fun classificationThroughWindowFlow (input: Flow<Data>, window: MLWindow<Result>) : Result? {
        var result: Result? = null
        val realRes =  withContext(Dispatchers.Default) {
            try {
                input
                    .map { data ->
                        model?.processAndEvaluate(data)
                    }
                    .cancellable()
                    .collect {
                        if (it == null) cancel()

                        window.add(it!!)
                        // TODO: Pass the metrics and Result
                        if (window.isSatisfied()) {
                            result = window.partialResult
                            cancel()
                        }
                    }
            } catch (e: Throwable) {
                Log.d("FlowClassificationOutside", "Just caught this, ${e.toString()}")
            } finally {
                Log.d("FlowClassificationWindow", "finally finished")
            }

            return@withContext result
        }

        return realRes
    }

    protected fun evaluateAsStream(input: List<Data>) : Flow<Result?> {
        return input.asFlow()
            .map { bitmap ->
                model?.processAndEvaluate(bitmap)
            }
            .cancellable()
    }

    protected open class MLWindow<Result> (val size: Int = 5, val confidence_threshold: Float = 80F) {
        protected val window : MutableList<Result> = mutableListOf<Result>()

        var confidence: Float = 0F
            protected set

        fun totalNumber() : Int = if (window.size > size) window.size else 0

        fun isSatisfied() : Boolean = confidence_threshold <= confidence

        var partialResult : Result? = null
            protected set

        fun add (element: Result) {
            window.add(element)

            if (window.size > size)
                window.removeFirst()

            calculate(element)
        }

        fun clean () {
            window.clear()
        }

        // Is gonna return the confidence and other metrics
        open fun calculate (element: Result) : Unit? = null
    }

//    protected interface MLWindowInterface<Result> {
//        var confidence: Float
//        var partialResult : Result?
//
//        fun totalNumber() : Int
//        fun isSatisfied() : Boolean
//        fun add (element: Result)
//        fun clean ()
//        // Is gonna return the confidence and other metrics
//        fun calculate (element: Result) : Unit? = null
//    }

//    protected fun streamEvaluations (video: List<Data>) : Flow<Result> = flow {
//        for ()
//    }

    fun updateLocalModel (path: String) {
        model?.loadModel(path)
    }

    fun getCompleteCount():StringBuffer{
        val count =  modelThreadPool?.taskCount
        val completeCount = modelThreadPool?.completedTaskCount
        return StringBuffer("Complete count : $completeCount/$count")

    }
}