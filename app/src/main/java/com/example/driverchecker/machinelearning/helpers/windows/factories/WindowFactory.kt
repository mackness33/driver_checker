package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

/* DEFINITIONS */
interface IWindowFactory<E, S : ISingleWindowSettings, M : IMultipleWindowSettings, W : ISingleWindow<E>> {
    fun createWindow (initialSettings: S): W
    fun createMapOfWindow (settings: M): Map<S, W>
    fun createMapOfWindow (collectionOfSettings: Set<S>): Map<S, W>
}

interface IMachineLearningWindowFactory <
        E : IMachineLearningOutputStats,
        S : IMachineLearningSingleWindowSettings,
        M : IMachineLearningMultipleWindowSettings,
        W : IMachineLearningSingleWindow<E>
        > : IWindowFactory<E, S, M, W>


interface IClassificationWindowFactory <
        E : IClassificationOutputStats<G>,
        S : IClassificationSingleWindowSettings<G>,
        M : IClassificationMultipleWindowSettings<G>,
        W : IClassificationSingleWindow<E, G>,
        G
        > : IMachineLearningWindowFactory<E, S, M, W>

interface IImageDetectionWindowFactory <G> : IClassificationWindowFactory<
        IImageDetectionFullOutput<G>,
        IClassificationSingleWindowSettings<G>,
        IClassificationMultipleWindowSettings<G>,
        IClassificationSingleWindow<IImageDetectionFullOutput<G>, G>,
        G
    >