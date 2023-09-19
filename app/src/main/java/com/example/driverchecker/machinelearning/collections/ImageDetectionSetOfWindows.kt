package com.example.driverchecker.machinelearning.collections

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.BasicImageDetectionWindow
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.machinelearning.helpers.windows.IImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

open class ImageDetectionSetOfWindows :
    ClassificationWindowsMutableCollection<IImageDetectionFullOutput<String>, String> {
    override var groups: Set<String> = emptySet()
        protected set
    protected var availableWindows: MutableMap<IWindowSettings, BasicImageDetectionWindow> = mutableMapOf()
    protected var selectedWindows: MutableSet<BasicImageDetectionWindow> = mutableSetOf()
        protected set

    override fun initialize(availableSettings: IMultipleWindowSettings) {
        settings = availableSettings

        try {
            val mAvailableWindows: MutableMap<IWindowSettings, BasicImageDetectionWindow> = mutableMapOf()
            settings.multipleTypes.forEach { type ->
                settings.multipleWindowsFrames.forEach { frames ->
                    settings.multipleWindowsThresholds.forEach { threshold ->
                        availableWindows.putIfAbsent(
                            WindowSettings(frames, threshold, type),
                            BasicImageDetectionWindow(frames, threshold, groups)
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }

    var inactiveWindows: Set<BasicImageDetectionWindow> = emptySet()
        get() = selectedWindows.minus(activeWindows)
        protected set
    var activeWindows: Set<BasicImageDetectionWindow> = emptySet()
        protected set


    override val confidence: Float
        get() = 0.0f
    override val lastResult: IImageDetectionFullOutput<String>?
        get() = if (activeWindows.isEmpty()) null else (activeWindows.last().lastResult as IImageDetectionFullOutput<String>)
    override var hasAcceptedLast: Boolean = false
        get() = if (activeWindows.isEmpty()) false else activeWindows.fold(false) { last, current -> last || current.hasAcceptedLast }
        protected set
    override var totEvaluationsDone: Int = 0
        get() = activeWindows.first().totEvaluationsDone
        protected set
    override var settings: IMultipleWindowSettings =
        Settings(emptyList(), emptyList(), emptyList(), 0.0f)
        protected set


    /*  WINDOWS  */
    override fun updateSettings(newSettings: IMultipleWindowSettings) {
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


    override fun isSatisfied(): Boolean {
        val satisfiedWindows = mutableSetOf<BasicImageDetectionWindow>()
        var currentIsSatisfied: Boolean

        val areAllSatisfied = activeWindows.fold(true) { lastResult, currentWindow ->
            currentIsSatisfied = currentWindow.isSatisfied()
            if (currentIsSatisfied) satisfiedWindows.add(currentWindow)

            lastResult && currentIsSatisfied
        }

        activeWindows = activeWindows.minus(satisfiedWindows)

        return areAllSatisfied
    }

    @OptIn(ExperimentalTime::class)
    override fun next(element: IImageDetectionFullOutput<String>, offset: Double?) {
        activeWindows.forEach { it.next(element, offset) }
    }

    override fun clean() {
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
        return selectedWindows.associate { it.getData() }
    }

    override fun getFinalResults(): IClassificationFinalResult<String> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: String
        var finalConfidence: Float = 0.0f
        selectedWindows.forEach { window ->
            finalGroupWindow = window.getFinalGroup()
            finalConfidence += window.confidence
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }

        finalConfidence /= selectedWindows.size
        return ClassificationFinalResult(
            finalConfidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,
            getData()
        )
    }


    override fun updateGroups(newGroups: Set<String>) {
        groups = newGroups

        availableWindows.forEach { it.value.updateGroups(newGroups) }
    }


    // TODO("NEED TO BE UPDATE")  finalResults need to return another type of results
    override fun getOldFinalResults(): IMachineLearningFinalResultStats {
        return selectedWindows.first().getOldFinalResults()
    }

    // TODO("NEED TO BE UPDATE") metrics need to return another type of metrics
    override fun getOldMetrics(): IWindowOldMetrics {
        return selectedWindows.first().getOldMetrics()
    }

    // TODO("NEED TO BE UPDATE") also this one will require a different type of metrics
    override fun getOldFullMetrics(): Pair<IWindowOldMetrics, IAdditionalMetrics?> {
        return selectedWindows.first().getOldFullMetrics()
    }


    /*  SET  */
//    override fun contains(element: IMachineLearningWindow<I>): Boolean {
//        return availableWindows.contains(element)
//    }
//
//    override fun containsAll(elements: Collection<IMachineLearningWindow<E>>): Boolean {
//        return windows.containsAll(elements)
//    }
//
//    override fun isEmpty(): Boolean {
//        return windows.isEmpty()
//    }
//
//    override fun iterator(): MutableIterator<IMachineLearningWindow<E>> {
//        return mWindows.iterator()
//    }
}
