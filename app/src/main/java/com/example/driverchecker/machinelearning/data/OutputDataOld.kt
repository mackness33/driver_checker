package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.ClassificationItemMutableList
import com.example.driverchecker.machinelearning.collections.MachineLearningItemListOld

// ---------------------------------- MACHINE LEARNING ----------------------------------
interface IMachineLearningOutputStatsOld : WithConfidence

interface IMachineLearningOutputOld<E : IMachineLearningItem> : IMachineLearningOutputStatsOld {
    val listItems: MachineLearningItemListOld<E>
}

data class MachineLearningOutputOld <E : WithConfidence> (
    override val listItems: MachineLearningItemListOld<E>,
) : IMachineLearningOutputOld<E> {
    override val confidence: Float = listItems.confidence
}

interface IOldMachineLearningFullOutput<I, E : IMachineLearningFullItem> : IMachineLearningOutputOld<E>, WithInput<I>

data class MachineLearningFullOutputOld <I, E : WithConfidence> (
    override val listItems: MachineLearningItemListOld<E>,
    override val input: I,
) : IOldMachineLearningFullOutput<I, E> {
    override val confidence: Float = listItems.confidence
}




// ---------------------------------- CLASSIFICATION ----------------------------------
interface IClassificationOutputStatsOld<S> : IMachineLearningOutputStatsOld, WithGroups<S>

interface IClassificationOutputOld<E : IClassificationItem<S>, S> : IMachineLearningOutputOld<E>, IClassificationOutputStatsOld<S> {
    override val listItems: ClassificationItemMutableList<E, S>
}

data class ClassificationOutputStatsOld<S> (
    override val confidence: Float,
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
) : IClassificationOutputStatsOld<S> {
}


data class ClassificationOutputOld<E : IClassificationItem<S>, S> (
    override val listItems: ClassificationItemMutableList<E, S>
) : IClassificationOutputOld<E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}

interface IClassificationFullOutputOld<I, E : IClassificationFullItem<S>, S> : IOldMachineLearningFullOutput<I, E>, IClassificationOutputOld<E, S>
data class ClassificationFullOutputOld<I, E : IClassificationFullItem<S>, S> (
    override val input: I,
    override val listItems: ClassificationItemMutableList<E, S>,
) : IClassificationFullOutputOld<I, E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}



// ---------------------------------- IMAGE DETECTION ----------------------------------
typealias IImageDetectionOutputStatsOld<S> = IClassificationOutputStatsOld<S>
typealias IImageDetectionOutputOld<S> = IClassificationOutputOld<IImageDetectionItem<S>, S>
typealias ImageDetectionOutputOld<S> = ClassificationOutputOld<IImageDetectionItem<S>, S>

typealias IImageDetectionFullOutputOld<S> = IClassificationFullOutputOld<IImageDetectionInputOld, IImageDetectionFullItem<S>, S>
typealias ImageDetectionFullOutputOld<S> = ClassificationFullOutputOld<IImageDetectionInputOld, IImageDetectionFullItem<S>, S>
