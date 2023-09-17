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

interface IClassificationFinalResult<S> : IMachineLearningFinalResult, IClassificationFinalResultStats<S>


data class ClassificationItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationOutputStats<S> (
    override val confidence: Float,
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
) : IClassificationOutputStats<S>

data class ClassificationOutput<E : IClassificationItem<S>, S> (
    override val listItems: ClassificationItemMutableList<E, S>
) : IClassificationOutput<E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}

data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IClassificationFinalResult<S> {
    constructor(baseResult: IClassificationFinalResult<S>) : this(
        baseResult.confidence, baseResult.supergroup, baseResult.settings, baseResult.metrics
    )

    constructor(main: IClassificationFinalResultStats<S>, settings: IOldSettings?, metrics: IOldMetrics?) : this (
        main.confidence, main.supergroup, settings, metrics
    )
}

// ---------------------------------- FULL OUTPUT ----------------------------------

interface IClassificationFullItem<S> : IMachineLearningFullItem, IClassificationItem<S>

interface IClassificationFullOutput<I, E : IClassificationFullItem<S>, S> : IMachineLearningFullOutput<I, E>, IClassificationOutput<E, S>

interface IClassificationFullFinalResult<S> : IMachineLearningFullFinalResult, IClassificationFinalResult<S>


data class ClassificationFullItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationFullOutput<I, E : IClassificationFullItem<S>, S> (
    override val input: I,
    override val listItems: ClassificationItemMutableList<E, S>
) : IClassificationFullOutput<I, E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}

data class ClassificationFullFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IClassificationFullFinalResult<S> {
    constructor(baseResult: IClassificationFinalResult<S>) : this(
        baseResult.confidence, baseResult.supergroup, baseResult.settings, baseResult.metrics
    )
}