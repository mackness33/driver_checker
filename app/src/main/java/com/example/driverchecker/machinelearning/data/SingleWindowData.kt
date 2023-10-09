package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo
import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag


interface ISingleWindowSettings {
    val size: Int
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
    override val groups: Set<S>
) : IClassificationSingleWindowSettings<S>