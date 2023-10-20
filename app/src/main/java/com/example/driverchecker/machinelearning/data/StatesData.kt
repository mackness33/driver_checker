package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag
import kotlinx.coroutines.flow.SharedFlow

// ---------------------------------- SEALED CLASSES/INTERFACES ----------------------------------

sealed interface LiveEvaluationStateInterface

sealed class LiveEvaluationState : LiveEvaluationStateInterface {
    data class Ready(val isReady: Boolean) : LiveEvaluationStateInterface
    data class Loading(val index: Int, val partialResult: IMachineLearningOutput?) : LiveEvaluationStateInterface
    object Start : LiveEvaluationStateInterface
    data class End(val exception: Throwable?, val finalResult: IMachineLearningFinalResult?) : LiveEvaluationStateInterface
}

sealed interface PartialEvaluationStateInterface

sealed class PartialEvaluationState : PartialEvaluationStateInterface {
    data class Insert(val index: Int) : PartialEvaluationState()
    object Clear : PartialEvaluationState()
    object Init : PartialEvaluationState()
}



sealed interface ClientStateInterface

sealed class ClientState : ClientStateInterface {
    object Ready : ClientState()
    data class UpdateSettings(val settings: ISettingsOld) : ClientState()
    data class Start<E>(val input: SharedFlow<E>) : ClientState()
    data class Stop(val cause: ExternalCancellationException) : ClientState()
}



sealed interface SettingsStateInterface

sealed class SettingsState : SettingsStateInterface {
    data class ModelSettings (val threshold: Float, val uoiThreshold: Float) : SettingsState()
    data class WindowSettings (
        val types: Set<IWindowTag?>,
        val thresholds: Set<Float?>,
        val sizes: Set<Int?>,
        val offsets: Set<Int?>?
    ) : SettingsState()
    data class FullSettings (val modelSettings: ModelSettings, val windowSettings: WindowSettings) : SettingsState()
    object NoSettings : SettingsState()
}




sealed interface LiveClassificationStateInterface : LiveEvaluationStateInterface

sealed class LiveClassificationState : LiveEvaluationState(), LiveClassificationStateInterface {
    data class Start<S>(val maxClassesPerGroup: Int, val classifier: IClassifier<S>) : LiveClassificationStateInterface
    data class Loading<S>(val index: Int, val partialResult: IClassificationOutput<S>?) : LiveClassificationStateInterface
    data class End<S>(val exception: Throwable?, val finalResult: IClassificationFinalResult<S>?) : LiveClassificationStateInterface
}