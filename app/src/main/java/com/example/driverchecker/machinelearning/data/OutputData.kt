package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.ClassificationItemMutableList
import com.example.driverchecker.machinelearning.collections.MachineLearningItemList
import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- MACHINE LEARNING ----------------------------------
interface IMachineLearningOutputStats : WithConfidence

interface IMachineLearningOutput<E : IMachineLearningItem> : IMachineLearningOutputStats {
    val listItems: MachineLearningItemList<E>
}

data class MachineLearningOutput <E : WithConfidence> (
    override val listItems: MachineLearningItemList<E>,
) : IMachineLearningOutput<E> {
    override val confidence: Float = listItems.confidence
}

interface IMachineLearningFullOutput<I, E : IMachineLearningFullItem> : IMachineLearningOutput<E>, WithInput<I>

data class MachineLearningFullOutput <I, E : WithConfidence> (
    override val listItems: MachineLearningItemList<E>,
    override val input: I,
) : IMachineLearningFullOutput<I, E> {
    override val confidence: Float = listItems.confidence
}




// ---------------------------------- CLASSIFICATION ----------------------------------
interface IClassificationOutputStats<S> : IMachineLearningOutputStats, WithGroups<S>

interface IClassificationOutput<E : IClassificationItem<S>, S> : IMachineLearningOutput<E>, IClassificationOutputStats<S> {
    override val listItems: ClassificationItemMutableList<E, S>
}

data class ClassificationOutputStats<S> (
    override val confidence: Float,
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
) : IClassificationOutputStats<S> {
}


data class ClassificationOutput<E : IClassificationItem<S>, S> (
    override val listItems: ClassificationItemMutableList<E, S>
) : IClassificationOutput<E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}

interface IClassificationFullOutput<I, E : IClassificationFullItem<S>, S> : IMachineLearningFullOutput<I, E>, IClassificationOutput<E, S>
data class ClassificationFullOutput<I, E : IClassificationFullItem<S>, S> (
    override val input: I,
    override val listItems: ClassificationItemMutableList<E, S>,
) : IClassificationFullOutput<I, E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}



// ---------------------------------- IMAGE DETECTION ----------------------------------
typealias IImageDetectionOutputStats<S> = IClassificationOutputStats<S>
typealias IImageDetectionOutput<S> = IClassificationOutput<IImageDetectionItem<S>, S>
typealias ImageDetectionOutput<S> = ClassificationOutput<IImageDetectionItem<S>, S>

typealias IImageDetectionFullOutput<S> = IClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias ImageDetectionFullOutput<S> = ClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
