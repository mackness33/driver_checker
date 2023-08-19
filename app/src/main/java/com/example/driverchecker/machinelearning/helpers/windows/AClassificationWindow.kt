package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

abstract class AClassificationWindow<E : WithConfAndGroups<S>, S> (
    override val size: Int = 3,
    override val threshold: Float = 0.15f,
    supergroups: Set<S>
) : AMachineLearningWindow<E>(size, threshold), IClassificationWindow<E, S> {

    protected val mSupergroupCounter: MutableMap<S, Int> = supergroups.associateWith { 0 }.toMutableMap()

    override val supergroupCounter: Map<S, Int> = mSupergroupCounter

    override fun next (element: E) {
        if (element.groups.isEmpty()) {
            hasAcceptedLast = false
            return
        }

        if (!mSupergroupCounter.keys.containsAll(element.groups.keys)) throw Throwable("The value found is not part of the classification")

        element.groups.forEach { group ->
            mSupergroupCounter.merge(group.key, group.value) { newValue, oldValue ->
                newValue + oldValue
            }
        }

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
}