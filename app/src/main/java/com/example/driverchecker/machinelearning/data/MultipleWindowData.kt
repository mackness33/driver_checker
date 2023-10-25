package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import com.example.driverchecker.machinelearning.windows.helpers.SingleGroupTag


interface IMultipleWindowSettings {
    val sizes: Set<Int>
    val tags: Set<IWindowTag>?

    fun asListOfSettings () : List<ISingleWindowSettings>
}

interface IMachineLearningMultipleWindowSettings : IMultipleWindowSettings {
    val thresholds: Set<Float>

    override fun asListOfSettings () : List<IMachineLearningSingleWindowSettings> {
        val outputSettingsList: MutableList<IMachineLearningSingleWindowSettings> = mutableListOf()
        this.tags?.forEach { type ->
            this.sizes.forEach { frames ->
                this.thresholds.forEach { threshold ->
                    outputSettingsList.add(SingleMachineLearningWindowSettings(
                        frames,
                        threshold,
                        type
                    ))
                }
            }
        }

        return outputSettingsList
    }
}

interface IClassificationMultipleWindowSettings<S> : IMachineLearningMultipleWindowSettings {
    val groups: Set<S>

    override fun asListOfSettings () : List<IClassificationSingleWindowSettings<S>> {
        val outputSettingsList: MutableList<IClassificationSingleWindowSettings<S>> = mutableListOf()
        this.tags?.forEach { type ->
            this.sizes.forEach { frames ->
                this.thresholds.forEach { threshold ->
                    outputSettingsList.add(SingleWindowSettings(
                        frames,
                        threshold,
                        this.groups,
                        type
                    ))
                }
            }
        }

        return outputSettingsList
    }
}

data class MultipleWindowSettings<S> (
    override val sizes: Set<Int>,
    override val tags: Set<IWindowTag>?,
    override val thresholds: Set<Float>,
    override val groups: Set<S>,
) : IClassificationMultipleWindowSettings<S> {
    constructor (stateSettings: SettingsState.WindowSettings, groups: Set<S>) : this(
        stateSettings.sizes,
        stateSettings.tags,
        stateSettings.thresholds,
        groups
    )
}