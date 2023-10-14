package com.example.driverchecker.machinelearning.data

import androidx.room.ColumnInfo

interface IWindowSettingsOld {
    val windowFrames: Int
    val windowThreshold: Float
    val type: String
}

interface IMultipleWindowSettingsOld {
    val multipleWindowsFrames: List<Int>
    val multipleWindowsThresholds: List<Float>
    val multipleTypes: List<String>
}

interface IModelSettings {
    val modelThreshold: Float
}

interface ISettingsOld : IMultipleWindowSettingsOld, IModelSettings

data class WindowSettingsOld (
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "type") override val type: String
) : IWindowSettingsOld {
    constructor(copy: IWindowSettingsOld?) : this (
        copy?.windowFrames ?: 0,
        copy?.windowThreshold ?: 0.0f,
        copy?.type ?: ""
    )

    constructor() : this (0, 0.0f, "")
}

class SettingsException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)
