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


interface IOffsetWindowFactory <
        E : IClassificationOutput<G>,
        S : IOffsetSingleWindowSettings,
        W : IClassificationSingleWindow<E, G>,
        G
    > : IMachineLearningWindowFactory<E, S, W>

interface IClassificationWindowFactory<
        E : IClassificationOutput<G>, S : IMachineLearningSingleWindowSettings,
        W : IClassificationSingleWindow<E, G>, G
    > : IMachineLearningWindowFactory<E,S,W> {
    fun createWindow (
        initialSettings: S,
        initialClassificationSettings: IClassificationSingleWindowSettings<G>
    ): W

    fun createMapOfWindow (
        collectionOfSettings: Set<S>,
        initialClassificationSettings: IClassificationSingleWindowSettings<G>
    ): Map<S, W>
}


interface IClassificationWindowFactoryOld <
        E : IClassificationOutput<G>,
        S : IClassificationSingleWindowSettingsOld<G>,
        W : IClassificationSingleWindow<E, G>,
        G
        > : IMachineLearningWindowFactory<E, S, W>

typealias IImageDetectionWindowFactory = IClassificationWindowFactoryOld<
        IImageDetectionOutput<String>,
        IClassificationSingleWindowSettingsOld<String>,
        ImageDetectionSingleWindow,
        String
        >

interface IImageDetectionWindowFactoryOld2 <G> : IClassificationWindowFactoryOld<
        IClassificationOutput<G>,
        IClassificationSingleWindowSettingsOld<G>,
        IClassificationSingleWindow<IClassificationOutput<G>, G>,
        G
        >