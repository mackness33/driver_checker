package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.IAdditionalMetrics
import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResultStats
import com.example.driverchecker.machinelearning.data.IMachineLearningItem
import com.example.driverchecker.machinelearning.data.IWindowMetrics
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.WindowFactory
import com.example.driverchecker.utils.ISettings
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

open class MachineLearningWindowMutableSet<E : IMachineLearningItem, W : IMachineLearningWindow<E>> : MutableSet<W>,
    MachineLearningWindowsSet<E, W> {
    var factory: Map<String, WindowFactory<E>> = emptyMap()
        protected set

    private val mWindows: MutableSet<W> = mutableSetOf()
    val windows: Set<W>
        get() = mWindows

    override var inactiveWindows: Set<W> = emptySet()
        get() = windows.minus(activeWindows)
        protected set
    override var activeWindows: Set<W> = emptySet()
        protected set

    var totalTime: Double = 0.0
        protected set
    var totalWindows: Int = 0
        protected set
    var type: String = "Set of Machine Learning Windows"
        protected set

    override val lastResult: E?
        get() = if (activeWindows.isEmpty()) null else activeWindows.last().lastResult
    override var hasAcceptedLast: Boolean = false
        protected set
    override var totEvaluationsDone: Int = 0
        get() = activeWindows.first().totEvaluationsDone
        protected set
    override var threshold: Float = 0.0f
        protected set

    /*  WINDOWS  */
    @OptIn(ExperimentalTime::class)
    // Moment when I'm going to create the different types of Windows to add to the set, based on the settings
    override fun initialize(settings: ISettings, newStart: TimeSource.Monotonic.ValueTimeMark?) {
        mWindows.forEach { it.initialize(settings, newStart) }
    }

    override fun isSatisfied(): Boolean {
        val satisfiedWindows = mutableSetOf<W>()
        var currentIsSatisfied = false

        val areAllSatisfied = activeWindows.fold(true) { lastResult, currentWindow ->
            currentIsSatisfied = currentWindow.isSatisfied()
            if (currentIsSatisfied) satisfiedWindows.add(currentWindow)

            lastResult && currentIsSatisfied
        }

         activeWindows = activeWindows.minus(satisfiedWindows)

        return areAllSatisfied
    }

    override fun next(element: E) {
        activeWindows.forEach { it.next(element) }
    }

    override fun clean() {
        mWindows.forEach { it.clean() }
    }

    // TODO("NEED TO BE UPDATE")  finalResults need to return another type of results
    override fun getFinalResults(): IMachineLearningFinalResultStats {
        return first().getFinalResults()
    }

    // TODO("NEED TO BE UPDATE") metrics need to return another type of metrics
    override fun getMetrics(): IWindowMetrics {
        return first().getMetrics()
    }

    // TODO("NEED TO BE UPDATE") also this one will require a different type of metrics
    override fun getFullMetrics(): Pair<IWindowMetrics, IAdditionalMetrics?> {
        return first().getFullMetrics()
    }

    @OptIn(ExperimentalTime::class)
    override fun updateStart(newStart: TimeSource.Monotonic.ValueTimeMark) {
        mWindows.forEach { it.updateStart(newStart) }
    }

    /*  SET  */
    override fun contains(element: W): Boolean {
        return windows.contains(element)
    }

    override fun containsAll(elements: Collection<W>): Boolean {
        return windows.containsAll(elements)
    }

    override fun isEmpty(): Boolean {
        return windows.isEmpty()
    }

    override val size: Int
        get() = windows.size

    /*  MUTABLE SET  */
    override fun iterator(): MutableIterator<W> {
        return mWindows.iterator()
    }

    override fun add(element: W): Boolean {
        return mWindows.add(element)
    }

    override fun remove(element: W): Boolean {
        return mWindows.remove(element)
    }

    // Bulk Modification Operations

    override fun addAll(elements: Collection<W>): Boolean {
        return mWindows.addAll(elements)
    }
    override fun removeAll(elements: Collection<W>): Boolean {
        return mWindows.removeAll(elements.toSet())
    }
    override fun retainAll(elements: Collection<W>): Boolean {
        return mWindows.retainAll(elements.toSet())
    }

    override fun clear() {
        mWindows.clear()
    }
}
