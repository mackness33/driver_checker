package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.ClassificationItemMutableList


// ---------------------------------- CLASSES ----------------------------------

// with classification
interface WithClassification<S> {
    val classification: IClassification<S>
}

// with supergroup
interface WithSupergroup<S> {
    val supergroup: S
}

// with supergroup
interface WithGroups<S> {
    val groups: Map<S, Set<IClassificationWithMetrics<S>>>
}



// ---------------------------------- BASIC OUTPUT ----------------------------------
// with classification
interface IClassificationItem<S> : IMachineLearningItem, WithClassification<S> {
    override val classification: IClassification<S>
}

interface IClassificationOutputStats<S> : IMachineLearningOutputStats, WithGroups<S>

interface IClassificationOutput<E : IClassificationItem<S>, S> : IMachineLearningOutput<E>, IClassificationOutputStats<S> {
    override val listItems: ClassificationItemMutableList<E, S>
}

interface IClassificationFinalResultStats<S> : IMachineLearningFinalResultStats, WithSupergroup<S>

interface IClassificationFinalResult<S> : IClassificationFinalResultStats<S>, IMachineLearningFinalResult, WithGroupsData<S>

data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val data: Map<IWindowBasicData, IGroupMetrics<S>?>,
) : IClassificationFinalResult<S> {
    constructor(main: IClassificationFinalResultStats<S>, data: Map<IWindowBasicData, IGroupMetrics<S>?>) : this (
        main.confidence, main.supergroup, data
    )

    constructor(copy: IClassificationFinalResult<S>) : this (
        copy.confidence, copy.supergroup, copy.data
    )
}



interface IOldClassificationFinalResult<S> : IOldMachineLearningFinalResult, IClassificationFinalResultStats<S>


data class ClassificationItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationOutputStats<S> (
    override val confidence: Float,
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
) : IClassificationOutputStats<S> {
    override var time: Double? = null
        private set

    override fun updateTime(newTime: Double?) {
        time = newTime
    }
}

data class ClassificationOutput<E : IClassificationItem<S>, S> (
    override val listItems: ClassificationItemMutableList<E, S>
) : IClassificationOutput<E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
    override var time: Double? = null
        private set

    override fun updateTime(newTime: Double?) {
        time = newTime
    }
}

data class ClassificationFinalResultOld<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IOldClassificationFinalResult<S> {
    constructor(baseResult: IOldClassificationFinalResult<S>) : this(
        baseResult.confidence, baseResult.supergroup, baseResult.settings, baseResult.metrics
    )

    constructor(main: IClassificationFinalResultStats<S>, settings: IOldSettings?, metrics: IOldMetrics?) : this (
        main.confidence, main.supergroup, settings, metrics
    )
}

// ---------------------------------- FULL OUTPUT ----------------------------------

interface IClassificationFullItem<S> : IMachineLearningFullItem, IClassificationItem<S>

interface IClassificationFullOutput<I, E : IClassificationFullItem<S>, S> : IMachineLearningFullOutput<I, E>, IClassificationOutput<E, S>

interface IOldClassificationFullFinalResult<S> : IOldMachineLearningFullFinalResult, IOldClassificationFinalResult<S>


data class ClassificationFullItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationFullOutput<I, E : IClassificationFullItem<S>, S> (
    override val input: I,
    override val listItems: ClassificationItemMutableList<E, S>,
) : IClassificationFullOutput<I, E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
    override var time: Double? = null
        private set

    override fun updateTime(newTime: Double?) {
        time = newTime
    }
}

data class ClassificationFullFinalResultOld<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IOldClassificationFullFinalResult<S> {
    constructor(baseResult: IOldClassificationFinalResult<S>) : this(
        baseResult.confidence, baseResult.supergroup, baseResult.settings, baseResult.metrics
    )
}