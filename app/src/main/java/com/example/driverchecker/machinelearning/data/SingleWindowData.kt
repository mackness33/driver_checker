package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag


interface ISingleWindowSettings {
    val size: Int
    val tag: IWindowTag?
}

interface IMachineLearningSingleWindowSettings : ISingleWindowSettings {
    val threshold: Float
}

interface IClassificationSingleWindowSettingsOld<S> : IMachineLearningSingleWindowSettings {
    val groups: Set<S>
}

interface IOffsetSingleWindowSettings : IMachineLearningSingleWindowSettings {
    val offset: Int
}

data class SingleWindowSettings<S> (
    override val size: Int,
    override val threshold: Float,
    override val groups: Set<S>,
    override val tag: IWindowTag? = null
) : IClassificationSingleWindowSettingsOld<S>


data class OffsetSingleWindowSettings (
    override val size: Int,
    override val threshold: Float,
    override val tag: IWindowTag? = null,
    override val offset: Int = 0
) : IOffsetSingleWindowSettings

data class SingleMachineLearningWindowSettings (
    override val size: Int,
    override val threshold: Float,
    override val tag: IWindowTag? = null
) : IMachineLearningSingleWindowSettings

interface IClassificationSingleWindowSettings<S> {
    val groups: Set<S>
}

data class ClassificationSingleWindowSettings<S> (
    override val groups: Set<S> = setOf(),
) : IClassificationSingleWindowSettings<S>