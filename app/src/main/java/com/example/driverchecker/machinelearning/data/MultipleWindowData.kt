package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo
import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag


interface IMultipleWindowSettings {
    val sizes: Set<Int>
    val tags: Set<IWindowTag>?
}

interface IMachineLearningMultipleWindowSettings : IMultipleWindowSettings {
    val thresholds: Set<Float>
}

interface IClassificationMultipleWindowSettings<S> : IMachineLearningMultipleWindowSettings {
    val groups: Set<S>
}

data class MultipleWindowSettings<S> (
    override val sizes: Set<Int>,
    override val tags: Set<IWindowTag>?,
    override val thresholds: Set<Float>,
    override val groups: Set<S>,
) : IClassificationMultipleWindowSettings<S> {
    fun asListOfSettings () : List<IClassificationSingleWindowSettings<S>> {
        val outputSettingsList: MutableList<IClassificationSingleWindowSettings<S>> = mutableListOf()
        this.tags?.forEach { type ->
            this.sizes.forEach { frames ->
                this.thresholds.forEach { threshold ->
                     outputSettingsList.add(SingleWindowSettings(
                        frames,
                        threshold,
                        this.groups,
                        SingleGroupImageDetectionTag
                     ))
                }
            }
        }

        return outputSettingsList
    }
}