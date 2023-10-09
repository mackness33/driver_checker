package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag


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

data class SingleWindowSettings<S> (
    override val size: Int,
    override val threshold: Float,
    override val groups: Set<S>,
    override val tag: IWindowTag? = null
) : IClassificationSingleWindowSettings<S>