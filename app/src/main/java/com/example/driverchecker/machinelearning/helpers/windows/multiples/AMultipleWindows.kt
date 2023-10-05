package com.example.driverchecker.machinelearning.helpers.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.ImageDetectionSingleWindow
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.MutableCompletableData
import kotlinx.coroutines.CoroutineScope

open class AMultipleWindows(scope: CoroutineScope) :
    IClassificationMultipleWindows<IImageDetectionFullOutput<String>, String> {
    /* MULTIPLE */
    // TODO: improve windows management
    protected var availableWindows: MutableMap<IWindowSettings, ImageDetectionSingleWindow> = mutableMapOf()
    protected var selectedWindows: MutableSet<ImageDetectionSingleWindow> = mutableSetOf()
    protected var isFinalResultBuilt: MutableCompletableData<Nothing?> = DeferrableData(null, scope.coroutineContext)
    override var inactiveWindows: Set<ImageDetectionSingleWindow> = emptySet()
        get() = selectedWindows.minus(activeWindows)
        protected set
    override var activeWindows: Set<ImageDetectionSingleWindow> = emptySet()
        protected set

    /* WINDOW */
    override val lastResult: IImageDetectionFullOutput<String>?
        get() = if (activeWindows.isEmpty()) null else (activeWindows.last().lastResult)
    override var hasAcceptedLast: Boolean = false
        get() = if (activeWindows.isEmpty()) false else activeWindows.fold(false) { last, current -> last || current.hasAcceptedLast }
        protected set
    override var totalElements: Int = 0
        get() = activeWindows.first().totalElements
        protected set
    override var settings: ISettings =
        Settings(emptyList(), emptyList(), emptyList(), 0.0f)
        protected set

    /* MACHINE LEARNING */
    override val confidence: Float
        get() = 0.0f

    /* CLASSIFICATION */
    override var groups: Set<String> = emptySet()
        protected set


    /*  WINDOWS  */
    override fun initialize(availableSettings: ISettings) {
        settings = availableSettings
        isFinalResultBuilt.complete(null)

        try {
            val mAvailableWindows: MutableMap<IWindowSettings, ImageDetectionSingleWindow> = mutableMapOf()
            settings.multipleTypes.forEach { type ->
                settings.multipleWindowsFrames.forEach { frames ->
                    settings.multipleWindowsThresholds.forEach { threshold ->
                        val tempSettings: IWindowSettings = WindowSettings(frames, threshold, type)
                        availableWindows.putIfAbsent(
                            tempSettings,
                            ImageDetectionSingleWindow(tempSettings, groups)
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }

    override fun updateSettings(newSettings: ISettings) {
        settings = newSettings

        try {
            var tempSetting: IWindowSettings
            newSettings.multipleTypes.forEach { type ->
                newSettings.multipleWindowsFrames.forEach { frames ->
                    newSettings.multipleWindowsThresholds.forEach { threshold ->
                        tempSetting = WindowSettings(frames, threshold, type)
                        if (availableWindows.containsKey(tempSetting)) selectedWindows.add(availableWindows[tempSetting]!!)
                    }
                }
            }

            activeWindows = selectedWindows
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }

    // TODO: complete the single window with a completable deferred that and the mutable with a
    // multiple semaphore
    override fun isSatisfied(): Boolean {
        val satisfiedWindows = mutableSetOf<ImageDetectionSingleWindow>()

        val areAllSatisfied = activeWindows.fold(true) { lastResult, currentWindow ->
            val currentIsSatisfied = currentWindow.isSatisfied()
            if (currentIsSatisfied) satisfiedWindows.add(currentWindow)

            lastResult && currentIsSatisfied
        }

        if (areAllSatisfied)
            isFinalResultBuilt.reset()

        activeWindows = activeWindows.minus(satisfiedWindows)

        return areAllSatisfied
    }

    override fun next(element: IImageDetectionFullOutput<String>, timeOffset: Double?) {
        activeWindows.forEach { it.next(element, timeOffset) }
    }

    override suspend fun clean() {
        isFinalResultBuilt.await()
        selectedWindows.forEach { it.clean() }
        activeWindows = selectedWindows
    }


    /* DATA */
    override fun getMetrics(): List<IWindowBasicData> {
        return selectedWindows.map { it.getMetrics() }
    }

    override fun getAdditionalMetrics(): List<IGroupMetrics<String>?> {
        return selectedWindows.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<String>?> {
        val listOfData = selectedWindows.map { it.getData() }
        return listOfData.toMap()
    }

    override fun getFinalResults(): IClassificationFinalResult<String> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: String
        var finalConfidence: Float = 0.0f
        selectedWindows.forEach { window ->
            // TODO: group must change
            finalGroupWindow = "Change"
            finalConfidence += window.confidence
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }

        finalConfidence /= selectedWindows.size
        val fr = ClassificationFinalResult(
            finalConfidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,
            getData().toMutableMap(),
            settings.modelThreshold
        )

        isFinalResultBuilt.complete(null)

        return fr
    }


    override fun updateGroups(newGroups: Set<String>) {
        groups = newGroups

        availableWindows.forEach { it.value.updateGroups(newGroups) }
    }

    override val type: String
        get() = "MultipleWindows"
}
