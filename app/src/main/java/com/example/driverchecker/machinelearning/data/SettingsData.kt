package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo


/* NEW */
interface WithSettings {
    val settings: ISettings
}

interface IWindowSettings {
    val windowFrames: Int
    val windowThreshold: Float
    val type: String
}

interface IMultipleWindowSettings {
    val multipleWindowsFrames: List<Int>
    val multipleWindowsThresholds: List<Float>
    val multipleTypes: List<String>
}

interface IModelSettings {
    val modelThreshold: Float
}

interface ISettings : IMultipleWindowSettings, IModelSettings

data class WindowSettings (
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "type") override val type: String
) : IWindowSettings {
    constructor(copy: IWindowSettings?) : this (
        copy?.windowFrames ?: 0,
        copy?.windowThreshold ?: 0.0f,
        copy?.type ?: ""
    )

    constructor() : this (0, 0.0f, "")
}


data class Settings (
    override val multipleWindowsFrames: List<Int>,
    override val multipleWindowsThresholds: List<Float>,
    override val multipleTypes: List<String>,
    override val modelThreshold: Float
) : ISettings {
    constructor(copyMultipleWindowSettings: IMultipleWindowSettings, copyModelSettings: IModelSettings) : this (
        copyMultipleWindowSettings.multipleWindowsFrames,
        copyMultipleWindowSettings.multipleWindowsThresholds,
        copyMultipleWindowSettings.multipleTypes,
        copyModelSettings.modelThreshold
    )
}

/* OLD */
interface WithOldSettings {
    val settings: IOldSettings?
}

data class OldSettings (
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "model_threshold") override val modelThreshold: Float
) : IOldSettings {
    constructor(copy: IOldSettings?) : this (
        copy?.windowFrames ?: 0,
        copy?.windowThreshold ?: 0.0f,
        copy?.modelThreshold ?: 0.0f
    )

    constructor() : this (0, 0.0f, 0.0f)
}

interface IOldSettings {
    val windowFrames: Int
    val windowThreshold: Float
    val modelThreshold: Float
}

class SettingsException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)
