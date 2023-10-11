package com.example.driverchecker.machinelearning.data

// ---------------------------------- MACHINE LEARNING ----------------------------------
typealias IMachineLearningFinalResultStatsOld = WithConfidence

interface IMachineLearningFinalResultOld : IMachineLearningFinalResultStatsOld, WithWindowData

data class MachineLearningFinalResultOld (
    override val confidence: Float, override val data: Map<IWindowBasicData, IAdditionalMetrics?>,
) : IMachineLearningFinalResultOld {
    constructor(main: IMachineLearningFinalResultStatsOld, data: Map<IWindowBasicData, IAdditionalMetrics?>) : this (
        main.confidence, data
    )
}




interface IClassificationFinalResultStatsOld<S> : IMachineLearningFinalResultStatsOld, WithSupergroup<S>

interface IClassificationFinalResultOld<S> : IClassificationFinalResultStatsOld<S>, IMachineLearningFinalResultOld, WithGroupsData<S>, IModelSettings

data class ClassificationFinalResultOld<S>(
    override val confidence: Float,
    override val supergroup: S,
    override val data: Map<IWindowBasicData, IGroupMetrics<S>?>,
    override val modelThreshold: Float,
) : IClassificationFinalResultOld<S> {
    constructor(main: IClassificationFinalResultStatsOld<S>, data: Map<IWindowBasicData, IGroupMetrics<S>?>, modelThreshold: Float) : this (
        main.confidence, main.supergroup, data.toMutableMap(), modelThreshold
    )

    constructor(copy: IClassificationFinalResultOld<S>) : this (
        copy.confidence, copy.supergroup, copy.data.toMutableMap(), copy.modelThreshold
    )
}





typealias IImageDetectionFinalResultOld<S> = IClassificationFinalResultOld<S>
typealias ImageDetectionFinalResultOld<S> = ClassificationFinalResultOld<S>
