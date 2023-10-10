package com.example.driverchecker.machinelearning.helpers.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.ISingleWindow
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.MutableCompletableData
import kotlinx.coroutines.CoroutineScope

abstract class AMultipleWindows<E, W : ISingleWindow<E>, S : ISingleWindowSettings>(scope: CoroutineScope) :
    IMultipleWindows<E> {
    /* MULTIPLE */
    // TODO: improve windows management
//    protected abstract val availableWindows: MutableMap<IWindowSettingsOld, W>
//    protected abstract val selectedWindows: MutableSet<W>
    protected abstract val currentWindows: MutableMap<S, W>
    protected var isFinalResultBuilt: MutableCompletableData<Nothing?> = DeferrableData(null, scope.coroutineContext)
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
    override var settings: ISettingsOld =
        SettingsOld(emptyList(), emptyList(), emptyList(), 0.0f)
        protected set


    /*  WINDOWS  */
    override fun updateSettings(newSettings: ISettingsOld) {
//        settings = newSettings
//
//        try {
//            var tempSetting: IWindowSettingsOld
//            newSettings.multipleTypes.forEach { type ->
//                newSettings.multipleWindowsFrames.forEach { frames ->
//                    newSettings.multipleWindowsThresholds.forEach { threshold ->
//                        tempSetting = WindowSettingsOld(frames, threshold, type)
//                        if (availableWindows.containsKey(tempSetting)) selectedWindows.add(availableWindows[tempSetting]!!)
//                    }
//                }
//            }
//
//            activeWindows = selectedWindows
//        } catch (e: Throwable) {
//            Log.e("WindowMutableSet", e.message.toString(), e)
//        }
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

        // minus assign
        activeWindows = activeWindows.minus(satisfiedWindows)

        return areAllSatisfied
    }

    override fun next(element: E, timeOffset: Double?) {
        activeWindows.forEach { it.next(element, timeOffset) }
    }

    override suspend fun clean() {
        isFinalResultBuilt.await()
//        selectedWindows.forEach { it.clean() }
        currentWindows.forEach { (_, window) -> window.clean() }
        activeWindows = currentWindows.values.toSet()
    }
}
