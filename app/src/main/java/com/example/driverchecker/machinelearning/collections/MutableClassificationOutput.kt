package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*

open class MutableClassificationOutput<E : IClassificationItem<G>, G> (
    override val index: Int, collection: Collection<E>? = null,
): IMutableClassificationOutput<E, G>, MutableMachineLearningOutput<E>(index, collection) {
    override val stats: IClassificationOutputStats<G>
        get() = ClassificationStats(calculateConfidence(), groups)

    protected val groups: MutableMap<G, MutableSet<IMutableClassificationWithMetrics<G>>> = mutableMapOf()

    protected open fun putClassification (element: E) {
        // Check if the map contains the group
        if (!groups.containsKey(element.classification.supergroup)) {
            // if not add a new group with the classification
            groups[element.classification.supergroup] = mutableSetOf(
                MutableClassificationWithMetrics(element.classification)
            )
        } else {
            // find the item
            val item = groups[element.classification.supergroup]!!.find { classMetric ->
                classMetric.externalIndex == element.classification.externalIndex
            }

            if (item == null) {
                // if not found add it to the map
                groups[element.classification.supergroup]!!.add(
                    MutableClassificationWithMetrics(element.classification)
                )
            } else {
                // If found increase the metric for the objects found
                item.inc()
            }
        }
//        when {
//            !groups.containsKey(element.classification.supergroup) -> {
//                groups[element.classification.supergroup] = mutableSetOf(
//                    MutableClassificationWithMetrics(element.classification)
//                )
//            }
//            groups[element.classification.supergroup]!!.find { classMetric ->
//                classMetric.externalIndex == element.classification.externalIndex
//            } == null -> {
//                groups[element.classification.supergroup]!!.add(
//                    MutableClassificationWithMetrics(element.classification)
//                )
//            }
//            else -> {
//                groups[element.classification.supergroup]!!.find { it.externalIndex == element.classification.externalIndex }?.inc()
//            }
//        }
    }

    // NOT TESTED
    protected open fun removeClassification (element: E) {
        if (groups[element.classification.supergroup]?.find { classMetric ->
                classMetric.externalIndex == element.classification.externalIndex
            } != null) {

            val group = groups[element.classification.supergroup]!!
//            group[element.classification.externalIndex].dec()
            val classification = group.toList()[element.classification.externalIndex]

            classification.dec()

            if (classification.objectsFound <= 0)
                group.remove(element.classification)

            if (group.isEmpty())
                groups.remove(element.classification.supergroup)

//            if (groups[element.classification.supergroup]!!.toList()[element.classification.externalIndex].objectsFound <= 0)
//                groups[element.classification.supergroup]!!.remove(element.classification)
//
//            if (groups[element.classification.supergroup].isNullOrEmpty())
//                groups.remove(element.classification.supergroup)
        }
    }

    override fun push(item: E) : Boolean {
        val result = super.push(item)
        if (result)
            putClassification(item)

        return result
    }

    override fun getImmutable() = ClassificationOutput (this)
}
