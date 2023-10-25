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

interface IOffsetMultipleWindowSettings<S> : IClassificationMultipleWindowSettings<S> {
    val offsets: Set<Int>

    override fun asListOfSettings () : List<IOffsetSingleWindowSettings<S>> {
        val outputSettingsList: MutableList<IOffsetSingleWindowSettings<S>> = mutableListOf()
        this.tags?.forEach { type ->
            this.sizes.forEach { frames ->
                this.thresholds.forEach { threshold ->
                    this.offsets.forEach { offset ->
                        outputSettingsList.add(
                            OffsetSingleWindowSettings(
                                frames,
                                threshold,
                                this.groups,
                                type,
                                offset
                            )
                        )
                    }
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

data class OffsetMultipleWindowSettings<S> (
    override val sizes: Set<Int>,
    override val tags: Set<IWindowTag>?,
    override val thresholds: Set<Float>,
    override val groups: Set<S>,
    override val offsets: Set<Int>,
) : IOffsetMultipleWindowSettings<S> {
    constructor (stateSettings: SettingsState.WindowSettings, groups: Set<S>) : this(
        stateSettings.sizes,
        stateSettings.tags,
        stateSettings.thresholds,
        groups,
        setOf()
    )
}