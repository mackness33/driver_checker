package com.example.driverchecker.utils

import androidx.room.ColumnInfo

data class Settings (
    @ColumnInfo(name = "window_frames") override val windowFrames: Int,
    @ColumnInfo(name = "window_threshold") override val windowThreshold: Float,
    @ColumnInfo(name = "model_threshold") override val modelThreshold: Float
) : ISettings {
    constructor(copy: ISettings?) : this (
        copy?.windowFrames ?: 0,
        copy?.windowThreshold ?: 0.0f,
        copy?.modelThreshold ?: 0.0f
    )

    constructor() : this (0, 0.0f, 0.0f)
}

interface ISettings {
    val windowFrames: Int
    val windowThreshold: Float
    val modelThreshold: Float
}

class SettingsException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)

sealed interface IPage

sealed class Page : IPage {
    object Camera : Page()
    object Result : Page()
}