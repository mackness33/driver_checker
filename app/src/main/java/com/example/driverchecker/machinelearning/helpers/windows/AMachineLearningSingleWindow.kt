package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

abstract class AMachineLearningSingleWindow<E : IMachineLearningOutputStats> constructor(
    initialSettings: IWindowSettings? = null
) : ASingleWindow<E> (initialSettings?.windowFrames ?: 0), IMachineLearningSingleWindow<E> {
    override var threshold: Float = initialSettings?.windowThreshold ?: 0.0f
        protected set
    override var confidence: Float = 0.0f
        protected set
    override val settings: IWindowSettings
        get() = WindowSettings(size, threshold, type)

    protected var sumOfConfidencePerWindowDone: Float = 0.0f
    protected val averageTime: Double
        get() = totalTime/totalWindows
    protected val averageConfidence: Float
        get() = sumOfConfidencePerWindowDone/totalWindows


    /* FUNCTIONS */
    override fun isSatisfied() : Boolean {
      return (window.size == size && threshold <= confidence)
    }

    override fun update () {
        confidence = window.fold(0.0f) { acc, next -> acc + next.confidence } / window.size
    }

    override fun postUpdate () {
        if (window.size == size) sumOfConfidencePerWindowDone += confidence
        postUpdate()
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

    override fun updateSettings(newSettings: IWindowSettings) {
        size = newSettings.windowFrames
        threshold = newSettings.windowThreshold
    }
}