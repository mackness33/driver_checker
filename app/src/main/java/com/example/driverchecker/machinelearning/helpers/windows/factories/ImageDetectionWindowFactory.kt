package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IImageDetectionFullOutput
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.IWindowSettings
import com.example.driverchecker.machinelearning.helpers.windows.IWindow
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

class ImageDetectionWindowFactory {
    fun createWindow (initialSettings: IWindowSettings,
                    supergroup: Set<String>
    ): ImageDetectionSingleWindow? {
        return when (initialSettings.type) {
            "BasicImageDetectionWindow" -> ImageDetectionSingleWindow.buildWindow(initialSettings, supergroup)
            // TODO: throw NonExistentWindowFactoryException
            else -> null
        }
    }

    fun createMapWindow (initialSettings: IWindowSettings,
                      supergroup: Set<String>
    ): Map<IWindowSettings, ImageDetectionSingleWindow?> {
//        return when (initialSettings.type) {
//            "BasicImageDetectionWindow" -> ImageDetectionSingleWindow.buildWindow(initialSettings, supergroup)
//            else -> null
//        }
        TODO("Create a map of windows based on the multipleSettings in input")
    }
}

class NonExistentWindowFactoryException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)
class FailedParameterCastFactoryException(override val message: String?, override val cause: Throwable?) : Throwable(message, cause)

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

sealed class OffsetWindowTag : IOffsetWindowTag {
    override val name: String
        get() = "Basic Image Detection"
}

object HomogenousOffsetImageDetectionTag : ImageDetectionTag() {
    override val name: String
        get() = "Homogenous Image Detection"
}