package com.example.driverchecker.machinelearning.collections

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow

open class ClassificationSetOfWindows<E : IClassificationOutputStats<String>> :
    ClassificationWindowsMutableCollection<E, String>, MachineLearningSetOfWindows<E> () {
    override var groups: Set<String> = emptySet()
        protected set

    /*  WINDOWS  */
    override fun updateSettings(newSettings: IMultipleWindowSettings) {
        settings = newSettings

        try {
            newSettings.multipleTypes.forEach { type ->
                newSettings.multipleWindowsFrames.forEach { frames ->
                    newSettings.multipleWindowsThresholds.forEach { threshold ->
                        if (factory.containsKey(type))
                            mWindows.add(
                                factory[type]?.buildClassificationWindow(
                                    frames,
                                    threshold,
                                    groups
                                ) as IClassificationWindow<E, String>
                            )
                    }
                }
            }

            activeWindows = mWindows
        } catch (e: Throwable) {
            Log.e("WindowMutableSet", e.message.toString(), e)
        }
    }

    /* DATA */
    override fun getMetrics(): List<IWindowBasicData> {
        return mWindows.map { it.getMetrics() }
    }

    override fun getAdditionalMetrics(): List<IGroupMetrics<S>?> {
        return mWindows.map { (it as IClassificationWindow<E, S>).getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<S>?> {
        return mWindows.associate { (it as IClassificationWindow<E, S>).getData() }
    }

    override fun getFinalResults(): IClassificationFinalResult<S> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: S
        mWindows.forEach { window ->
            finalGroupWindow = (window as IClassificationWindow<E, S>).getFinalGroup()
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }

        return ClassificationFinalResult(
            confidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,
            getData()
        )
    }


    override fun updateGroups(newGroups: Set<S>) {
        groups = newGroups

        mWindows.forEach { (it as IClassificationWindow<E, S>).updateGroups(newGroups) }
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
}
