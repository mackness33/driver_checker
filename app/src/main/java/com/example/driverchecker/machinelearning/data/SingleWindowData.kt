package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag


interface ISingleWindowSettings {
    val size: Int
    val tag: IWindowTag?
}

interface IMachineLearningSingleWindowSettings : ISingleWindowSettings {
    val threshold: Float
}

interface IClassificationSingleWindowSettings<S> : IMachineLearningSingleWindowSettings {
    val groups: Set<S>
}

interface IOffsetSingleWindowSettings<S> : IClassificationSingleWindowSettings<S> {
    val offset: Int
}

data class SingleWindowSettings<S> (
    override val size: Int,
    override val threshold: Float,
    override val groups: Set<S>,
    override val tag: IWindowTag? = null
) : IClassificationSingleWindowSettings<S>


data class OffsetSingleWindowSettings<S> (
    override val size: Int,
    override val threshold: Float,
    override val groups: Set<S>,
    override val tag: IWindowTag? = null,
    override val offset: Int = 0
) : IOffsetSingleWindowSettings<S>

data class SingleMachineLearningWindowSettings (
    override val size: Int,
    override val threshold: Float,
    override val tag: IWindowTag? = null
) : IMachineLearningSingleWindowSettings

interface IClassificationWindowSettings<S> {
    val groups: Set<S>
}