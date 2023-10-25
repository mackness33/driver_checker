package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag


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

interface IClassificationMultipleWindowSettingsOld<S> : IMachineLearningMultipleWindowSettings {
    val groups: Set<S>

    override fun asListOfSettings () : List<IClassificationSingleWindowSettingsOld<S>> {
        val outputSettingsList: MutableList<IClassificationSingleWindowSettingsOld<S>> = mutableListOf()
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

interface IOffsetMultipleWindowSettings : IMachineLearningMultipleWindowSettings {
    val offsets: Set<Int>

    override fun asListOfSettings () : List<IOffsetSingleWindowSettings> {
        val outputSettingsList: MutableList<IOffsetSingleWindowSettings> = mutableListOf()
        this.tags?.forEach { type ->
            this.sizes.forEach { frames ->
                this.thresholds.forEach { threshold ->
                    this.offsets.forEach { offset ->
                        outputSettingsList.add(
                            OffsetSingleWindowSettings(
                                frames,
                                threshold,
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

data class MultipleWindowSettingsOld<S> (
    override val sizes: Set<Int>,
    override val tags: Set<IWindowTag>?,
    override val thresholds: Set<Float>,
    override val groups: Set<S>,
) : IClassificationMultipleWindowSettingsOld<S> {
    constructor (stateSettings: SettingsState.WindowSettings, groups: Set<S>) : this(
        stateSettings.sizes,
        stateSettings.tags,
        stateSettings.thresholds,
        groups
    )
}

data class OffsetMultipleWindowSettings (
    override val sizes: Set<Int>,
    override val tags: Set<IWindowTag>?,
    override val thresholds: Set<Float>,
    override val offsets: Set<Int>,
) : IOffsetMultipleWindowSettings {
    constructor (original: IOffsetMultipleWindowSettings) : this(
        original.sizes,
        original.tags,
        original.thresholds,
        original.offsets
    )
}


interface IClassificationMultipleWindowSettings<S>{
    val groups: Set<S>
}

data class ClassificationMultipleWindowSettings<S> (
    override val groups: Set<S>
) : IClassificationMultipleWindowSettings<S>