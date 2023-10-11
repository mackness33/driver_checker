package com.example.driverchecker.machinelearning.data

// ---------------------------------- MACHINE LEARNING ----------------------------------
typealias IMachineLearningFinalResultStats = WithConfidence

interface IMachineLearningFinalResult : IMachineLearningFinalResultStats, WithWindowData

data class MachineLearningFinalResult (
    override val confidence: Float, override val data: Map<IWindowBasicData, IAdditionalMetrics?>,
) : IMachineLearningFinalResult {
    constructor(main: IMachineLearningFinalResultStats, data: Map<IWindowBasicData, IAdditionalMetrics?>) : this (
        main.confidence, data
    )
}





interface IClassificationFinalResultStats<S> : IMachineLearningFinalResultStats, WithSupergroup<S>

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





typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>
