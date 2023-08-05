package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

open class ClassificationWindow<E : WithConfAndGroups<S>, S> (override val size: Int = 3, override val threshold: Float = 0.15f, supergroups: Set<S>) :
    MachineLearningWindow<E>(), IClassificationWindow<E, S> {

    protected val mSupergroupCounter: MutableMap<S, Int> = supergroups.associateWith { 0 }.toMutableMap()

    override val supergroupCounter: Map<S, Int> = supergroups.associateWith { 0 }.toMutableMap()

    override fun next (element: E) {
        if (element.groups.isEmpty()) {
            hasAcceptedLast = false
            return
        }

        if (!mSupergroupCounter.keys.containsAll(element.groups)) throw Throwable("The value found is not part of the classification")

        mSupergroupCounter.putAll(element.groups.associateWith { group -> mSupergroupCounter[group]!!.inc() })

        super.next(element)
    }

    override fun update () {
        if (window.size == 0) {
            confidence = 0.0f
            mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
        }

        confidence = (supergroupCounter.values.max() / window.size).toFloat()
    }

    override fun clean () {
        super.clean()
        mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
    }

    override fun getFinalResults() : WithConfAndSuper<S> {
        return ClassificationFinalResult(confidence, supergroupCounter.maxWith { o1, o2 -> o2.value.compareTo(o1.value) }.key)
    }
}