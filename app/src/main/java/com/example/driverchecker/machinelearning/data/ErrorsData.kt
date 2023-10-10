package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.MachineLearningItemList
import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- MACHINE LEARNING ----------------------------------
class ExternalCancellationException : CancellationException ()
class InternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()