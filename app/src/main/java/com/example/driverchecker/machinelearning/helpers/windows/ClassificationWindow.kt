package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

open class ClassificationWindow<Result : WithConfAndClas<S>, S> (override val size: Int = 3, override val threshold: Float = 0.15f, supergroups: Set<S>) :
    MachineLearningWindow<Result>(), IClassificationWindow<Result, S> {

    protected val _supergroupCounter: MutableMap<S, Int> = supergroups.associateWith { 0 }.toMutableMap()

    override val supergroupCounter: Map<S, Int> = _supergroupCounter

    override fun next (element: Result) {
        if (!_supergroupCounter.containsKey(element.classification.supergroup)) throw Throwable("The value found is not part of the classification")

        _supergroupCounter[element.classification.supergroup] = _supergroupCounter[element.classification.supergroup]!!.inc()
    }

    override fun update () {
        if (window.size == 0) {
            confidence = 0.0f
        }

        confidence = (supergroupCounter.values.max() / window.size).toFloat()
    }

    override fun clean () {
        super.clean()
        _supergroupCounter.clear()
    }

    override fun getFinalResults() : IClassificationFinalResult<S> {
        return ClassificationFinalResult(confidence, supergroupCounter.maxWith { o1, o2 -> o2.value.compareTo(o1.value) }.key)
    }
}