package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IImageDetectionFullOutput
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.IWindowSettingsOld
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

/* DEFINITIONS */
interface IWindowFactory<E> {

    fun buildWindow(initialSettings: IWindowSettingsOld): ISingleWindow<E>
}

interface IMachineLearningWindowFactory<E : IMachineLearningOutputStats> : IWindowFactory<E> {

    override fun buildWindow(initialSettings: IWindowSettingsOld): IMachineLearningSingleWindow<E>
}

interface IClassificationWindowFactory<E : IClassificationOutputStats<S>, S> : IMachineLearningWindowFactory<E> {

    override fun buildWindow(initialSettings: IWindowSettingsOld): IClassificationSingleWindow<E, S>
    fun buildWindow(initialSettings: IWindowSettingsOld, supergroup: Set<String>): IClassificationSingleWindow<E, S>
}

typealias IImageDetectionWindowFactory2 = IClassificationWindowFactory<IImageDetectionFullOutput<String>, String>


/* ABSTRACT */
abstract class AWindowFactory<E> : IWindowFactory<E> {

    abstract override fun buildWindow(initialSettings: IWindowSettingsOld): ISingleWindow<E>
}

abstract class AMachineLearningWindowFactory<E : IMachineLearningOutputStats> : AWindowFactory<E>(), IMachineLearningWindowFactory<E> {

    abstract override fun buildWindow(initialSettings: IWindowSettingsOld): IMachineLearningSingleWindow<E>
}

abstract class AClassificationWindowFactory2<E : IClassificationOutputStats<S>, S> : AMachineLearningWindowFactory<E>(), IClassificationWindowFactory<E, S> {

    abstract override fun buildWindow(initialSettings: IWindowSettingsOld): IClassificationSingleWindow<E, S>
}

typealias AImageDetectionWindowFactory2 = AClassificationWindowFactory2<IImageDetectionFullOutput<String>, String>