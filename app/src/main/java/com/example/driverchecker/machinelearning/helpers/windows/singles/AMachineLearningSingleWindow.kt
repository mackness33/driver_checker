package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag

abstract class AMachineLearningSingleWindow<E : IMachineLearningOutput> constructor(
    initialSettings: IMachineLearningSingleWindowSettings,
    internalTag: IWindowTag,
) : ASingleWindow<E>(initialSettings, internalTag), IMachineLearningSingleWindow<E> {
    override var threshold: Float = initialSettings.threshold
        protected set
    override var confidence: Float = 0.0f
        protected set
    override val settings: IWindowSettingsOld
        get() = WindowSettingsOld(size, threshold, "NoTag")

    protected var sumOfConfidencePerWindowDone: Float = 0.0f
    protected val averageTime: Double
        get() = totalTime/totalWindows
    protected val averageConfidence: Float
        get() = sumOfConfidencePerWindowDone/totalWindows


    /* FUNCTIONS */
    protected fun windowIsFull() : Boolean = window.size == size

    override fun isSatisfied() : Boolean {
      return (windowIsFull() && threshold <= confidence)
    }

    override fun update () {
        if (windowIsFull())
            confidence = window.fold(0.0f) { acc, next -> acc + next.stats.confidence } / window.size
    }

    override fun postUpdate () {
        if (windowIsFull()) sumOfConfidencePerWindowDone += confidence
        super.postUpdate()
    }

    override suspend fun clean () {
        super.clean()
        confidence = 0f
        sumOfConfidencePerWindowDone = 0.0f
    }

    /* DATA */
    override fun getData(): Pair<IWindowBasicData, IAdditionalMetrics?> {
        return getMetrics() to getAdditionalMetrics()
    }

    override fun getAdditionalMetrics(): IAdditionalMetrics? {
        return null
    }

    override fun updateSettings(newSettings: IWindowSettingsOld) {
        size = newSettings.windowFrames
        threshold = newSettings.windowThreshold
    }
}