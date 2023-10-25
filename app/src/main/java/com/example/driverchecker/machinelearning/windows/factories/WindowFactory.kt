package com.example.driverchecker.machinelearning.windows.factories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.singles.IClassificationSingleWindow
import com.example.driverchecker.machinelearning.windows.singles.IMachineLearningSingleWindow
import com.example.driverchecker.machinelearning.windows.singles.ISingleWindow
import com.example.driverchecker.machinelearning.windows.singles.ImageDetectionSingleWindow

/* DEFINITIONS */
interface IWindowFactory<E, S : ISingleWindowSettings, W : ISingleWindow<E>> {
    fun createWindow (initialSettings: S): W
    fun createMapOfWindow (collectionOfSettings: Set<S>): Map<S, W>
}

interface IMachineLearningWindowFactory <
        E : IMachineLearningOutput,
        S : IMachineLearningSingleWindowSettings,
        W : IMachineLearningSingleWindow<E>
        > : IWindowFactory<E, S, W>


interface IClassificationWindowFactory <
        E : IClassificationOutput<G>,
        S : IClassificationSingleWindowSettings<G>,
        W : IClassificationSingleWindow<E, G>,
        G
        > : IMachineLearningWindowFactory<E, S, W>

typealias IImageDetectionWindowFactory = IClassificationWindowFactory<
        IImageDetectionOutput<String>,
        IClassificationSingleWindowSettings<String>,
        ImageDetectionSingleWindow,
        String
        >

interface IImageDetectionWindowFactory2 <G> : IClassificationWindowFactory<
        IClassificationOutput<G>,
        IClassificationSingleWindowSettings<G>,
        IClassificationSingleWindow<IClassificationOutput<G>, G>,
        G
        >