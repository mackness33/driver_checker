package com.example.driverchecker.machinelearning.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.factories.IWindowFactory
import com.example.driverchecker.machinelearning.windows.factories.ImageDetectionWindowFactory
import com.example.driverchecker.machinelearning.windows.singles.ISingleWindow
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.MutableCompletableData
import kotlinx.coroutines.CoroutineScope

abstract class AMultipleWindows<E, W : ISingleWindow<E>, S : ISingleWindowSettings>(scope: CoroutineScope) :
    IMultipleWindows<E> {
    /* MULTIPLE */
    // TODO: improve windows management
    protected abstract val factory: IWindowFactory<E, S, W>
    protected var isFinalResultBuilt: MutableCompletableData<Nothing?> = DeferrableData(null, scope.coroutineContext)

    protected abstract var currentWindows: Map<S, W>
    override var inactiveWindows: Set<W> = emptySet()
        get() = currentWindows.values.toSet().minus(activeWindows)
        protected set
    override var activeWindows: Set<W> = emptySet()
        protected set

    /* WINDOW */
    override val lastResult: E?
        get() = if (activeWindows.isEmpty()) null else (activeWindows.last().lastResult)
    override var hasAcceptedLast: Boolean = false
        get() = if (activeWindows.isEmpty()) false else activeWindows.fold(false) { last, current -> last || current.hasAcceptedLast }
        protected set
    override var totalElements: Int = 0
        get() = activeWindows.first().totalElements
        protected set

    init {
        isFinalResultBuilt.complete(null)
    }


    /*  WINDOWS  */
    override fun <M : IMultipleWindowSettings> update (newSettings: M) {
        // get the list of settings as a set and get all the windows that are not part of the current ones
        // TODO: try catch?
        val listOfNewSettings = newSettings.asListOfSettings().toSet() as Set<S>
        Log.d("MULTIPLE WINDOWS", listOfNewSettings.toString())
        val newWindows = factory.createMapOfWindow(listOfNewSettings.minus(currentWindows.keys))

        // remove the windows not part of the new settings and add the one that are not there
        currentWindows = currentWindows
            .minus(currentWindows.keys.minus(listOfNewSettings))
            .plus(newWindows)

        activeWindows = currentWindows.values.toSet()
    }

    // TODO: complete the single window with a completable deferred that and the mutable with a
    // multiple semaphore
    override fun isSatisfied(): Boolean {
        val satisfiedWindows = mutableSetOf<W>()

        val areAllSatisfied = activeWindows.fold(true) { lastResult, currentWindow ->
            val currentIsSatisfied = currentWindow.isSatisfied()
            if (currentIsSatisfied) satisfiedWindows.add(currentWindow)

            lastResult && currentIsSatisfied
        }

        if (areAllSatisfied)
            isFinalResultBuilt.reset()

        satisfiedWindows.forEach { win -> onWindowSatisfied(win)}
        // minus assign
        activeWindows = activeWindows.minus(satisfiedWindows)

        return areAllSatisfied
    }

    protected abstract fun onWindowSatisfied(window: W)

    override fun next(element: E, timeOffset: Double?) {
        activeWindows.forEach { it.next(element, timeOffset) }
    }

    override suspend fun clean() {
        isFinalResultBuilt.await()
        currentWindows.forEach { (_, window) -> window.clean() }
        activeWindows = currentWindows.values.toSet()
    }
}
