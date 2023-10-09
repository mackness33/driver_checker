package com.example.driverchecker.machinelearning.helpers.windows.helpers

sealed interface IWindowTag {
    val name : String
}

sealed interface IOffsetWindowTag : IWindowTag

sealed class ImageDetectionTag : IWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object HomogenousImageDetectionTag : ImageDetectionTag() {
    override val name: String
        get() = "Homogenous Image Detection"
}

sealed class OffsetImageDetectionWindowTag : ImageDetectionTag(), IOffsetWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object HomogenousOffsetImageDetectionTag : ImageDetectionTag() {
    override val name: String
        get() = "Homogenous Image Detection"
}