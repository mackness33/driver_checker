package com.example.driverchecker.utils

data class Settings (
    override val windowFrames: Int,
    override val windowThreshold: Float,
    override val modelThreshold: Float
) : ISettings

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