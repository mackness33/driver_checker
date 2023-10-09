package com.example.driverchecker.machinelearning.collections

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.BasicImageDetectionWindowOld
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.MutableCompletableData
import kotlinx.coroutines.CoroutineScope

open class ImageDetectionSetOfWindows(scope: CoroutineScope) :
    ClassificationWindowsMutableCollection<IImageDetectionFullOutput<String>, String> {
    override var groups: Set<String> = emptySet()
        protected set
    protected var availableWindows: MutableMap<IWindowSettings, BasicImageDetectionWindowOld> = mutableMapOf()
    protected var selectedWindows: MutableSet<BasicImageDetectionWindowOld> = mutableSetOf()
        protected set

    protected var hasFinalResultBeingCopied: MutableCompletableData<Boolean> = DeferrableData(false, scope.coroutineContext)

    protected var madeAWholeEvaluation: Boolean = false

    override fun initialize(availableSettings: ISettings) {
        settings = availableSettings

        try {
            val mAvailableWindows: MutableMap<IWindowSettings, BasicImageDetectionWindowOld> = mutableMapOf()
            settings.multipleTypes.forEach { type ->
                settings.multipleWindowsFrames.forEach { frames ->
                    settings.multipleWindowsThresholds.forEach { threshold ->
                        availableWindows.putIfAbsent(
                            WindowSettings(frames, threshold, type),
                            BasicImageDetectionWindowOld(frames, threshold, groups)
                        )
                    }
                }
            }
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }

    var inactiveWindows: Set<BasicImageDetectionWindowOld> = emptySet()
        get() = selectedWindows.minus(activeWindows)
        protected set
    var activeWindows: Set<BasicImageDetectionWindowOld> = emptySet()
        protected set


    override val confidence: Float
        get() = 0.0f
    override val lastResult: IImageDetectionFullOutput<String>?
        get() = if (activeWindows.isEmpty()) null else (activeWindows.last().lastResult as IImageDetectionFullOutput<String>)
    override var hasAcceptedLast: Boolean = false
        get() = if (activeWindows.isEmpty()) false else activeWindows.fold(false) { last, current -> last || current.hasAcceptedLast }
        protected set
    override var totalElements: Int = 0
        get() = activeWindows.first().totalElements
        protected set
    override var settings: ISettings =
        Settings(emptyList(), emptyList(), emptyList(), 0.0f)
        protected set


    /*  WINDOWS  */
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

    override fun isSatisfied(): Boolean {
        val satisfiedWindows = mutableSetOf<BasicImageDetectionWindowOld>()
        var currentIsSatisfied: Boolean

        val areAllSatisfied = activeWindows.fold(true) { lastResult, currentWindow ->
            currentIsSatisfied = currentWindow.isSatisfied()
            if (currentIsSatisfied) satisfiedWindows.add(currentWindow)

            lastResult && currentIsSatisfied
        }

        if (areAllSatisfied) {
            madeAWholeEvaluation = true
            print("")
        }

        activeWindows = activeWindows.minus(satisfiedWindows)

        return areAllSatisfied
    }

    override fun next(element: IImageDetectionFullOutput<String>, timeOffset: Double?) {
        activeWindows.forEach { it.next(element, timeOffset) }
    }

    override suspend fun clean() {
        if (madeAWholeEvaluation)
            hasFinalResultBeingCopied.await()

        selectedWindows.forEach { it.clean() }

        hasFinalResultBeingCopied.reset()
        madeAWholeEvaluation = false
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
            finalGroupWindow = window.getFinalGroup()
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

        hasFinalResultBeingCopied.complete(true)

        return fr
    }


    override fun updateGroups(newGroups: Set<String>) {
        groups = newGroups

        availableWindows.forEach { it.value.updateGroups(newGroups) }
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
