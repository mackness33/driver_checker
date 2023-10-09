package com.example.driverchecker.machinelearning.helpers.windows.helpers

sealed interface IWindowTag {
    val name : String
}

sealed interface IOffsetWindowTag : IWindowTag

sealed class ImageDetectionTag : IWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object MultipleGroupImageDetectionTag : ImageDetectionTag() {
    override val name: String
        get() = "Multiple Group Per Image"
}

object SingleGroupImageDetectionTag : ImageDetectionTag() {
    override val name: String
        get() = "Single Group Per Image"
}

sealed class OffsetImageDetectionWindowTag : ImageDetectionTag(), IOffsetWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object HomogenousOffsetImageDetectionTag : ImageDetectionTag() {
    override val name: String
        get() = "Homogenous Image Detection"
}