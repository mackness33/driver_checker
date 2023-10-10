package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.*

/* DEFINITIONS */
interface IWindowFactory<E, S : ISingleWindowSettings, W : ISingleWindow<E>> {
    fun createWindow (initialSettings: S): W
    fun createMapOfWindow (collectionOfSettings: Set<S>): Map<S, W>
}

interface IMachineLearningWindowFactory <
        E : IMachineLearningOutputStats,
        S : IMachineLearningSingleWindowSettings,
        W : IMachineLearningSingleWindow<E>
        > : IWindowFactory<E, S, W>


interface IClassificationWindowFactory <
        E : IClassificationOutputStats<G>,
        S : IClassificationSingleWindowSettings<G>,
        W : IClassificationSingleWindow<E, G>,
        G
        > : IMachineLearningWindowFactory<E, S, W>

typealias IImageDetectionWindowFactory = IClassificationWindowFactory<
        IImageDetectionFullOutput<String>,
        IClassificationSingleWindowSettings<String>,
        ImageDetectionSingleWindow,
        String
        >

interface IImageDetectionWindowFactory2 <G> : IClassificationWindowFactory<
        IImageDetectionFullOutput<G>,
        IClassificationSingleWindowSettings<G>,
        IClassificationSingleWindow<IImageDetectionFullOutput<G>, G>,
        G
    >