package com.example.driverchecker.machinelearning.helpers

import android.util.Log
import com.example.driverchecker.machinelearning.data.WithIndex
import java.util.*

interface IEvaluationsMap <C, I: C, O: C> {
    val partials: Map<I, O?>
    val inputs: List<I>
    val outputs: List<O?>
}

interface IMutableEvaluationsMap <C, I: C, O: C> : IEvaluationsMap<C, I, O> {
    fun offerInput(input: I)
    fun submitOutput(output: O?) : Boolean
    fun clear()
}

abstract class AEvaluationsMap <C : WithIndex, I: C, O: C> : IMutableEvaluationsMap<C, I, O>{
    protected val inputQueue: Queue<I> = LinkedList()
    protected val mPartialsMap: MutableMap<I, O?> = mutableMapOf()
    override val partials: Map<I, O?> = mPartialsMap
    override val inputs: List<I> = mPartialsMap.keys.toList()
    override val outputs: List<O?> = mPartialsMap.values.toList()

    override fun offerInput(input: I) {
        inputQueue.offer(input)
        Log.d("EvalMap", "input with: ${input.index}")
    }

    override fun submitOutput(output: O?) : Boolean {
        Log.d("EvalMap", "inputSize: ${inputs.size} and top: ${inputQueue.peek()}")
        Log.d("EvalMap", "output with: ${output?.index}")
        if (inputQueue.isEmpty()) return false

        val input = inputQueue.poll()!!

        if (checkInputWithOutput(input, output)) {
            mPartialsMap[input] = output
            return true
        }

        return false
    }

    override fun clear () {
        inputQueue.clear()
        mPartialsMap.clear()
    }

    protected abstract fun checkInputWithOutput (input: C, output: C?) : Boolean
}


//class AllEvaluationsMap <C, I: C, O: C> : AEvaluationsMap<C, I, O> () {
//    override fun checkInputWithOutput (input: C, output: C?) : Boolean = true
//}

open class NotNullEvaluationsMap <C: WithIndex, I: C, O: C> : AEvaluationsMap<C, I, O> () {
    override fun checkInputWithOutput (input: C, output: C?) : Boolean {
        return output != null && output.index == output.index
    }
}