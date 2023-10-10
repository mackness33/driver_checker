package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.MachineLearningItemList
import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- MACHINE LEARNING ----------------------------------
typealias IMachineLearningFinalResultStats = WithConfidence

interface IMachineLearningNewFinalResult : IMachineLearningFinalResultStats, WithWindowInfo

data class MachineLearningNewFinalResult (
    override val confidence: Float, override val data: Map<IWindowBasicData, IAdditionalMetrics?>,
) : IMachineLearningFinalResult {
    constructor(main: IMachineLearningFinalResultStats, data: Map<IWindowBasicData, IAdditionalMetrics?>) : this (
        main.confidence, data
    )
}


interface IMachineLearningFinalResult : IMachineLearningFinalResultStats, WithWindowData

data class MachineLearningFinalResult (
    override val confidence: Float, override val data: Map<IWindowBasicData, IAdditionalMetrics?>,
) : IMachineLearningFinalResult {
    constructor(main: IMachineLearningFinalResultStats, data: Map<IWindowBasicData, IAdditionalMetrics?>) : this (
        main.confidence, data
    )
}

interface IOldMachineLearningFinalResult : IMachineLearningFinalResultStats, WithOldMetrics, WithOldSettings

data class OldMachineLearningFinalResult (
    override val confidence: Float,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IOldMachineLearningFinalResult {

    constructor(main: IMachineLearningFinalResultStats, settings: IOldSettings?, metrics: IOldMetrics?) : this (
        main.confidence, settings, metrics
    )
}

// ---------------------------------- FULL OUTPUT ----------------------------------
interface IOldMachineLearningFullFinalResult : IOldMachineLearningFinalResult

data class OldMachineLearningFullFinalResult (
    override val confidence: Float,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null
) : IOldMachineLearningFullFinalResult







interface IClassificationFinalResultStats<S> : IMachineLearningFinalResultStats, WithSupergroup<S>


interface IClassificationNewFinalResult<S> : IClassificationFinalResultStats<S>, IMachineLearningNewFinalResult, WithGroupData<S>

data class ClassificationNewFinalResult<S>(
    override val confidence: Float,
    override val supergroup: S,
    override val data: List<IWindowBasicData>,
    override val additionalMetrics: List<IGroupMetrics<S>>,
) : IClassificationNewFinalResult<S> {
    constructor(main: IClassificationFinalResultStats<S>, info: List<IWindowBasicData>, additional: List<IGroupMetrics<S>>) : this (
        main.confidence, main.supergroup, info, additional
    )

    constructor(copy: IClassificationNewFinalResult<S>) : this (
        copy.confidence, copy.supergroup, copy.data, copy.additionalMetrics
    )
}

interface IClassificationFinalResult<S> : IClassificationFinalResultStats<S>, IMachineLearningFinalResult, WithGroupsData<S>, IModelSettings

data class ClassificationFinalResult<S>(
    override val confidence: Float,
    override val supergroup: S,
    override val data: Map<IWindowBasicData, IGroupMetrics<S>?>,
    override val modelThreshold: Float,
) : IClassificationFinalResult<S> {
    constructor(main: IClassificationFinalResultStats<S>, data: Map<IWindowBasicData, IGroupMetrics<S>?>, modelThreshold: Float) : this (
        main.confidence, main.supergroup, data.toMutableMap(), modelThreshold
    )

    constructor(copy: IClassificationFinalResult<S>) : this (
        copy.confidence, copy.supergroup, copy.data.toMutableMap(), copy.modelThreshold
    )
}



interface IOldClassificationFinalResult<S> : IOldMachineLearningFinalResult, IClassificationFinalResultStats<S>


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

interface IOldClassificationFullFinalResult<S> : IOldMachineLearningFullFinalResult, IOldClassificationFinalResult<S>

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







typealias IOldImageDetectionFinalResult<S> = IOldClassificationFinalResult<S>
typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>

typealias ImageDetectionFinalResultOld<S> = ClassificationFinalResultOld<S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>

// ---------------------------------- FULL OUTPUT ----------------------------------


typealias IOldImageDetectionFullFinalResult<S> = IOldClassificationFullFinalResult<S>

typealias ImageDetectionFullFinalResultOld<S> = ClassificationFullFinalResultOld<S>
