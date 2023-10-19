package com.example.driverchecker.machinelearning.windows.helpers

sealed interface IWindowTag {
    val name : String
}

sealed interface IOffsetWindowTag : IWindowTag

sealed class ImageDetectionTag : IWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object MultipleGroupTag : ImageDetectionTag() {
    override val name: String
        get() = "Multiple Group Per Image"
}

object SingleGroupTag : ImageDetectionTag() {
    override val name: String
        get() = "Single Group Per Image"
}

sealed class OffsetTag : ImageDetectionTag(), IOffsetWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object SingleGroupOffsetTag : OffsetTag() {
    override val name: String
        get() = "Single Group With Offset"
}

object MultipleGroupOffsetTag : OffsetTag() {
    override val name: String
        get() = "Multiple Group With Offset"
}