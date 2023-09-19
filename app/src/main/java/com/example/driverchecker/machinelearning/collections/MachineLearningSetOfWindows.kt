package com.example.driverchecker.machinelearning.collections

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.BasicImageDetectionWindow
import com.example.driverchecker.machinelearning.helpers.windows.IImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

open class MachineLearningSetOfWindows<E : IMachineLearningOutputStats> :
    MachineLearningWindowsMutableCollection<E, IMachineLearningWindow<E>> {
    var factory: Map<String, IImageDetectionWindowFactory> = emptyMap()
        protected set

    init {
        factory = mapOf("BasicImageDetectionWindow" to BasicImageDetectionWindow.Factory)
    }

    protected val mWindows: MutableSet<IMachineLearningWindow<E>> = mutableSetOf()
    val windows: Set<IMachineLearningWindow<E>>
        get() = mWindows

    override var inactiveWindows: Set<IMachineLearningWindow<E>> = emptySet()
        get() = windows.minus(activeWindows)
        protected set
    override var activeWindows: Set<IMachineLearningWindow<E>> = emptySet()
        protected set


    override val confidence: Float
        get() = 0.0f
    override val lastResult: E?
        get() = if (activeWindows.isEmpty()) null else activeWindows.last().lastResult
    override var hasAcceptedLast: Boolean = false
        protected set
    override var totEvaluationsDone: Int = 0
        get() = activeWindows.first().totEvaluationsDone
        protected set
    override var settings: IMultipleWindowSettings =
        Settings(emptyList(), emptyList(), emptyList(), 0.0f)
        protected set

    override val size: Int
        get() = windows.size

    /*  WINDOWS  */
    override fun updateSettings(newSettings: IMultipleWindowSettings) {
        settings = newSettings

        try {
            newSettings.multipleTypes.forEach { type ->
                newSettings.multipleWindowsFrames.forEach { frames ->
                    newSettings.multipleWindowsThresholds.forEach { threshold ->
                        if (factory.containsKey(type))
                            mWindows.add(
                                factory[type]?.buildMachineLearningWindow(
                                    frames,
                                    threshold
                                ) as IMachineLearningWindow<E>
                            )
                    }
                }
            }

            activeWindows = mWindows
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }


    override fun isSatisfied(): Boolean {
        val satisfiedWindows = mutableSetOf<IMachineLearningWindow<E>>()
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
    override fun next(element: E, offset: Double?) {
        activeWindows.forEach { it.next(element, offset) }
    }

    override fun clean() {
        mWindows.forEach { it.clean() }
    }


    /* DATA */
    override fun getMetrics(): List<IWindowBasicData> {
        return mWindows.map { it.getMetrics() }
    }

    override fun getAdditionalMetrics(): List<IAdditionalMetrics?> {
        return mWindows.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IAdditionalMetrics?> {
        return mWindows.associate { it.getData() }
    }

    override fun getFinalResults(): IMachineLearningFinalResult {
        return MachineLearningFinalResult(
            confidence,
            getData()
        )
    }


    // TODO("NEED TO BE UPDATE")  finalResults need to return another type of results
    override fun getOldFinalResults(): IMachineLearningFinalResultStats {
        return first().getOldFinalResults()
    }

    // TODO("NEED TO BE UPDATE") metrics need to return another type of metrics
    override fun getOldMetrics(): IWindowOldMetrics {
        return first().getOldMetrics()
    }

    // TODO("NEED TO BE UPDATE") also this one will require a different type of metrics
    override fun getOldFullMetrics(): Pair<IWindowOldMetrics, IAdditionalMetrics?> {
        return first().getOldFullMetrics()
    }


    /*  SET  */
    override fun contains(element: IMachineLearningWindow<E>): Boolean {
        return windows.contains(element)
    }

    override fun containsAll(elements: Collection<IMachineLearningWindow<E>>): Boolean {
        return windows.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return windows.isEmpty()
    }


    /*  MUTABLE SET  */
    override fun iterator(): MutableIterator<IMachineLearningWindow<E>> {
        return mWindows.iterator()
    }

    override fun add(element: IMachineLearningWindow<E>): Boolean {
        return mWindows.add(element)
    }

    override fun remove(element: IMachineLearningWindow<E>): Boolean {
        return mWindows.remove(element)
    }

    // Bulk Modification Operations

    override fun addAll(elements: Collection<IMachineLearningWindow<E>>): Boolean {
        return mWindows.addAll(elements)
    }

    override fun removeAll(elements: Collection<IMachineLearningWindow<E>>): Boolean {
        return mWindows.removeAll(elements.toSet())
    }

    override fun retainAll(elements: Collection<IMachineLearningWindow<E>>): Boolean {
        return mWindows.retainAll(elements.toSet())
    }

    override fun clear() {
        mWindows.clear()
    }
}
