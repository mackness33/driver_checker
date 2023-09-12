package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

open class ClassificationWindow<E : WithConfAndGroups<S>, S> (
    size: Int = 3,
    threshold: Float = 0.15f,
    supergroups: Set<S>
) : AClassificationWindow<E, S>(size, threshold, supergroups) {

    override fun getFinalResults() : WithConfAndSuper<S> {
        return ClassificationFinalResult(confidence, supergroupCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key, window)
    }
}